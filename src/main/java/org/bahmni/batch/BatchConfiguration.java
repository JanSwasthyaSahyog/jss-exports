package org.bahmni.batch;

import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.io.FileUtils;
import org.bahmni.batch.exception.BatchResourceException;
import org.bahmni.batch.exports.*;
import org.bahmni.batch.form.FormListProcessor;
import org.bahmni.batch.form.domain.BahmniForm;
import org.bahmni.batch.form.domain.DateRange;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration extends DefaultBatchConfigurer {

	public static final String FULL_DATA_EXPORT_JOB_NAME = "bahmniExports";

	@Autowired
	private JobBuilderFactory jobBuilders;

	@Autowired
	private FormListProcessor formListProcessor;

	@Autowired
	private ObjectFactory<WideFormatObservationExportStep> wideFormatObservationExportStepFactory;

	@Autowired
	private ObjectFactory<WideFormatInPatientExportStep> wideFormatInPatientExportStepFactory;

	@Value("${templates}")
	private Resource freemarkerTemplateLocation;

	@Autowired
	public JobCompletionNotificationListener jobCompletionNotificationListener;

	@Value("${zipFolder}")
	private Resource zipFolder;

	@Value("${bahmniConfigFolder}")
	private Resource bahmniConfigFolder;

	@Autowired
	private ReportGenerator reportGenerator;

	@Autowired
	StepBuilderFactory stepBuilders;

	@Bean
	public JobExecutionListener listener() {
		return jobCompletionNotificationListener;
	}

	public Job completeDataExport(DateRange dateRange){

		System.out.println("Getting the Job");
		List<BahmniForm> forms = formListProcessor.retrieveAllForms();

		FlowBuilder<FlowJobBuilder> completeDataExport = jobBuilders.get(FULL_DATA_EXPORT_JOB_NAME)
				.incrementer(new RunIdIncrementer()).preventRestart()
				.listener(listener())
		                .flow(beforeStep());


		for (BahmniForm form : forms) {
				WideFormatObservationExportStep wideFormatObservationExportStep = wideFormatObservationExportStepFactory.getObject();
				wideFormatObservationExportStep.setDateRange(dateRange);
				wideFormatObservationExportStep.setForm(form);
				completeDataExport.next(wideFormatObservationExportStep.getStep());
		}

		return completeDataExport.end().build();
	}

	public Job inpatientExport(DateRange dateRange){
		FlowBuilder<FlowJobBuilder> inpatientExport = jobBuilders.get(FULL_DATA_EXPORT_JOB_NAME)
				.incrementer(new RunIdIncrementer()).preventRestart()
				.flow(beforeStep());

		WideFormatInPatientExportStep wideFormatInpatientExportStep = wideFormatInPatientExportStepFactory.getObject();
		wideFormatInpatientExportStep.setDateRange(dateRange);
		inpatientExport.next(wideFormatInpatientExportStep.getStep());

		return inpatientExport.end().build();
	}



	public Step beforeStep() {
		Tasklet tasklet = new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				// TODO Auto-generated method stub
				return RepeatStatus.FINISHED;
			}
		};

		return stepBuilders.get("beforeStep")
				.tasklet(tasklet)
				.build();
	}

	@Bean
	public freemarker.template.Configuration freeMarkerConfiguration() throws IOException {
		freemarker.template.Configuration freemarkerTemplateConfig = new freemarker.template.Configuration(
				freemarker.template.Configuration.VERSION_2_3_22);
		freemarkerTemplateConfig.setDirectoryForTemplateLoading(freemarkerTemplateLocation.getFile());
		freemarkerTemplateConfig.setDefaultEncoding("UTF-8");
		freemarkerTemplateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		return freemarkerTemplateConfig;
	}

	@PreDestroy
	public void generateReport(){
		try {
			File report = new File(bahmniConfigFolder.getFile(),"report.html");
			String reportOutput =  reportGenerator.generateReport();
			FileUtils.writeStringToFile(report,reportOutput);
		}
		catch (IOException e) {
			throw new BatchResourceException("Unable to write the report file ["+zipFolder.getFilename()+"]",e);
		}
	}
}
