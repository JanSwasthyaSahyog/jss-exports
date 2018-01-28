package org.bahmni.batch.exports;

import org.bahmni.batch.exception.BatchResourceException;
import org.bahmni.batch.form.HospitalisationProcessor;
import org.bahmni.batch.form.HospitalisationFieldExtractor;
import org.bahmni.batch.form.domain.*;
import org.bahmni.batch.helper.FreeMarkerEvaluator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope(value = "prototype")
public class WideFormatInPatientExportStep {

    public static final String FILE_NAME_EXTENSION = ".csv";

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Value("${outputFolder}")
    public Resource outputFolder;

    @Autowired
    private FreeMarkerEvaluator<Map<String,Object>> freeMarkerEvaluator;

    @Autowired
    private ObjectFactory<HospitalisationProcessor> bedsProcessorObjectFactory;

    private DateRange dateRange;

    public void setOutputFolder(Resource outputFolder) {
        this.outputFolder = outputFolder;
    }

    public Step getStep() {
        return stepBuilderFactory.get(getStepName())
                .<Hospitalisation, Hospitalisation>chunk(100)
                .reader(obsReader())
                .processor(observationProcessor())
                .writer(obsWriter())
                .build();
    }

    private JdbcCursorItemReader<Hospitalisation> obsReader() {
        Map<String, Object> input = new HashMap<>();
        input.put("startDate", dateRange.getStartDateString());
        input.put("endDate", dateRange.getEndDateString());
        String sql = freeMarkerEvaluator.evaluate("admissions.ftl", input);
        JdbcCursorItemReader<Hospitalisation> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(sql);
        reader.setRowMapper(new BeanPropertyRowMapper<Hospitalisation>(Hospitalisation.class){
            public Hospitalisation mapRow(ResultSet rs, int i) throws SQLException {
                Hospitalisation hospitalisation = super.mapRow(rs,i);
                Address address = new Address(rs.getString("village"), null, null, null);
                Person person = new Person(rs.getInt("person_id"), null, null, rs.getInt("age"),
                        rs.getString("gender"), address);
                person.setSmartCardHolder(rs.getString("smart_card_holder"));
                hospitalisation.setPerson(person);
                return hospitalisation;
            }
        });
        return reader;
    }

    private CompositeItemProcessor observationProcessor() {
        CompositeItemProcessor<Map<String, Object>, List<Hospitalisation>> compositeProcessor = new CompositeItemProcessor<>();
        List itemProcessors = new ArrayList<>();

        HospitalisationProcessor bedsProcessor = bedsProcessorObjectFactory.getObject();
        itemProcessors.add(bedsProcessor);

        compositeProcessor.setDelegates(itemProcessors);

        return compositeProcessor;
    }

    private FlatFileItemWriter<Hospitalisation> obsWriter() {

        FlatFileItemWriter<Hospitalisation> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(getOutputFile()));

        DelimitedLineAggregator<Hospitalisation> delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.setDelimiter(",");
        HospitalisationFieldExtractor fieldExtractor = new HospitalisationFieldExtractor();
        delimitedLineAggregator.setFieldExtractor(fieldExtractor);

        writer.setLineAggregator(delimitedLineAggregator);
        writer.setHeaderCallback(fieldExtractor);

        return writer;
    }

    private File getOutputFile(){
        File outputFile;

        try {
            outputFile = new File(outputFolder.getFile(),"Hospitalisation_Patient_Data" + FILE_NAME_EXTENSION);
        }
        catch (IOException e) {
            throw new BatchResourceException("Unable to create a file in the outputFolder ["+ outputFolder.getFilename()+"]",e);
        }

        return outputFile;
    }


    public String getStepName() {
        return "Hospitalisation";
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }
}
