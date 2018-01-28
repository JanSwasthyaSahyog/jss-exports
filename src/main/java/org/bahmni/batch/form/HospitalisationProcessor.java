package org.bahmni.batch.form;

import org.bahmni.batch.BatchUtils;
import org.bahmni.batch.form.domain.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope(value="prototype")
public class HospitalisationProcessor implements ItemProcessor<Hospitalisation, Hospitalisation> {


    private String bedsInHospitalisationSql;

    @Value("classpath:sql/bedsInHospitalisation.sql")
    private Resource bedsInHospitalisation;

    private String diagnosisInVisitSql;

    @Value("classpath:sql/diagnosisInVisit.sql")
    private Resource diagnosisInVisit;

    private String opdFollowup1Sql;

    @Value("classpath:sql/nextVisitForPatient.sql")
    private Resource opdFollowup1;

    @Value("${opdVisitTypeId}")
    private Integer opdVisitTypeId;

    @Value("${diagnosisCertaintyConceptId}")
    private Integer diagnosisCertaintyConceptId;

    @Value("${diagnosisOrderConceptId}")
    private Integer diagnosisOrderConceptId;


	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;



	@Override
	public Hospitalisation process(Hospitalisation hospitalisation) throws Exception {
		System.out.println("Processing hospitalisation for person "+hospitalisation.getIdentifier()+
                " for date of admission "+hospitalisation.getAdmissionDate());
		updateHospitalisationWithBeds(hospitalisation);
		updateHospitalisationWithDiagnosis(hospitalisation);
		updateHospitalisationWithNextOPDFollowup(hospitalisation);
		return hospitalisation;
	}

    private void updateHospitalisationWithNextOPDFollowup(Hospitalisation hospitalisation) {
        Map<String,Object> params = new HashMap<>();
        params.put("reference_visit_id", hospitalisation.getVisitId());
        params.put("patient_id", hospitalisation.getPerson().getId());
        params.put("visit_type_id", opdVisitTypeId);

        List<Visit> visits = jdbcTemplate.query(opdFollowup1Sql, params,
                new BeanPropertyRowMapper<>(Visit.class));
        if(!visits.isEmpty()){
            hospitalisation.setSubsequentOPDVisitDate(visits.get(0).getVisit_date());
        }

    }

    private void updateHospitalisationWithDiagnosis(Hospitalisation hospitalisation) {
        Map<String,Object> params = new HashMap<>();
        params.put("visit_id", hospitalisation.getVisitId());
        params.put("diagnosis_certainty_concept_id", diagnosisCertaintyConceptId);
        params.put("diagnosis_order_concept_id", diagnosisOrderConceptId);

        List<Diagnosis> diagnosisInHospitalisation = jdbcTemplate.query(diagnosisInVisitSql, params,
                new BeanPropertyRowMapper<>(Diagnosis.class));
        hospitalisation.setDiagnoses(diagnosisInHospitalisation);
    }

    private void updateHospitalisationWithBeds(Hospitalisation hospitalisation) throws Exception {
        Map<String,Object> params = new HashMap<>();
        params.put("visit_id", hospitalisation.getVisitId());

        List<Bed> bedsAssignmentsInHospitalisation = jdbcTemplate.query(bedsInHospitalisationSql, params,
                new BeanPropertyRowMapper<>(Bed.class));
        hospitalisation.setBedAssignments(bedsAssignmentsInHospitalisation);
    }


	public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    @PostConstruct
    public void postConstruct(){
        this.diagnosisInVisitSql = BatchUtils.convertResourceOutputToString(diagnosisInVisit);
        this.bedsInHospitalisationSql = BatchUtils.convertResourceOutputToString(bedsInHospitalisation);
        this.opdFollowup1Sql = BatchUtils.convertResourceOutputToString(opdFollowup1);

    }

}