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
import java.util.*;

@Component
@Scope(value="prototype")
public class HospitalisationProcessor implements ItemProcessor<Hospitalisation, Hospitalisation> {


    private String bedsInHospitalisationSql;

    @Value("classpath:sql/bedsInHospitalisation.sql")
    private Resource bedsInHospitalisation;

    private String diagnosisInVisitSql;

    @Value("classpath:sql/diagnosisInVisit.sql")
    private Resource diagnosisInVisit;

    private String allDiagnosisSql;

    @Value("classpath:sql/allDiagnosisFromAVisitType.sql")
    private Resource allDiagnosis;

    private String opdFollowup1Sql;

    @Value("classpath:sql/nextVisitForPatient.sql")
    private Resource opdFollowup1;

    private String firstRecordingOfBasicObsInFirstWeekSql;

    @Value("classpath:sql/firstRecordingOfBasicObsInFirstWeek.sql")
    private Resource firstRecordingOfBasicObsInFirstWeek;

    private String dischargeSummaryObsSql;

    @Value("classpath:sql/dischargeSummaryObs.sql")
    private Resource dischargeSummaryObs;

    private String opdVisitsSql;

    @Value("classpath:sql/opdVisits.sql")
    private Resource opdVisits;

    private String drugOrdersSql;

    @Value("classpath:sql/drugOrders.sql")
    private Resource drugOrders;

    @Value("${opdVisitTypeId}")
    private Integer opdVisitTypeId;

    @Value("${diagnosisCertaintyConceptId}")
    private Integer diagnosisCertaintyConceptId;

    @Value("${diagnosisOrderConceptId}")
    private Integer diagnosisOrderConceptId;

    @Value("${diagnosisStatusConceptId}")
    private Integer diagnosisStatusConceptId;


	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

    private DateRange dateRange;



    @Override
	public Hospitalisation process(Hospitalisation hospitalisation) throws Exception {
		System.out.println("Processing hospitalisation for person "+hospitalisation.getIdentifier()+
                " for date of admission "+hospitalisation.getAdmissionDate());
		updateHospitalisationWithFirstRecordingOfBasicObs(hospitalisation);
		updateHospitalisationWithBeds(hospitalisation);
		updateHospitalisationWithDiagnosis(hospitalisation);
		updateHospitalisationWithOPDDiagnosis(hospitalisation);
		updateHospitalisationWithDischargeSummaryObs(hospitalisation);
		updateHospitalisationWithNextOPDFollowup(hospitalisation);
		updateHospitalisationWithOPDVisits(hospitalisation);
		updateHospitalisationWithMedication(hospitalisation);
		return hospitalisation;
	}

    private void updateHospitalisationWithOPDDiagnosis(Hospitalisation hospitalisation) {
        Map<String,Object> params = new HashMap<>();
        params.put("patient_id", hospitalisation.getPerson().getId());
        params.put("diagnosis_certainty_concept_id", diagnosisCertaintyConceptId);
        params.put("diagnosis_order_concept_id", diagnosisOrderConceptId);
        params.put("diagnosis_status_concept_id", diagnosisStatusConceptId);
        params.put("visit_type_id", opdVisitTypeId);
        params.put("start_date", dateRange.getStartDateString());
        params.put("end_date", dateRange.getEndDateString());


        List<Diagnosis> diagnosisInHospitalisation = jdbcTemplate.query(allDiagnosisSql, params,
                new BeanPropertyRowMapper<>(Diagnosis.class));
        hospitalisation.setOpdDiagnoses(diagnosisInHospitalisation);

    }

    private void updateHospitalisationWithDischargeSummaryObs(Hospitalisation hospitalisation) {
        Map<String,Object> params = new HashMap<>();
        params.put("start_date", hospitalisation.getAdmissionDate());
        params.put("end_date", hospitalisation.getDischargeDate()!=null ? hospitalisation.getDischargeDate():new Date());
        params.put("patient_id", hospitalisation.getPerson().getId());

        List<DischargeSummary> dischargeSummaries = jdbcTemplate.query(dischargeSummaryObsSql, params,
                new BeanPropertyRowMapper<>(DischargeSummary.class));
        if(!dischargeSummaries.isEmpty()){
            hospitalisation.setDischargeSummary(dischargeSummaries.get(0));
        }
    }

    private void updateHospitalisationWithFirstRecordingOfBasicObs(Hospitalisation hospitalisation) {
        Map<String,Object> params = new HashMap<>();
        params.put("start_date", hospitalisation.getAdmissionDate());
        params.put("patient_id", hospitalisation.getPerson().getId());

        List<BasicObs> basicObs = jdbcTemplate.query(firstRecordingOfBasicObsInFirstWeekSql, params,
                new BeanPropertyRowMapper<>(BasicObs.class));
        if(!basicObs.isEmpty()){
            hospitalisation.setFirstRecordingOfBasicObsInFirstWeek(basicObs.get(0));
        }

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

        List<Bed> bedsAssignmentsInHospitalisation = jdbcTemplate.query(bedsInHospitalisationSql, params, new BeanPropertyRowMapper<>(Bed.class));
        //JSS specific fix as the data seems corrupted
        List<Bed> mergedBedAssignments = mergeConsecutiveBedAssignmentsToSameBed(bedsAssignmentsInHospitalisation);
        hospitalisation.setBedAssignments(mergedBedAssignments);
    }

    private List<Bed> mergeConsecutiveBedAssignmentsToSameBed(List<Bed> bedsAssignmentsInHospitalisation) {
        if(bedsAssignmentsInHospitalisation.isEmpty()){
            return bedsAssignmentsInHospitalisation;
        }
        List<Bed> mergedBeds = new ArrayList<>();
        for (Bed bed: bedsAssignmentsInHospitalisation) {
            if(mergedBeds.isEmpty()) {
                mergedBeds.add(bed);
            } else {
                Bed lastMergedBed = mergedBeds.get(mergedBeds.size() - 1);
                if(lastMergedBed.getWard().equals(bed.getWard()) && lastMergedBed.getBedNumber().equals(bed.getBedNumber())){
                    lastMergedBed.setDuration(lastMergedBed.getDuration() + bed.getDuration());
                } else {
                    mergedBeds.add(bed);
                }
            }

        }
        return mergedBeds;
    }

    private void updateHospitalisationWithOPDVisits(Hospitalisation hospitalisation) {
        Map<String,Object> params = new HashMap<>();
        params.put("patient_id", hospitalisation.getPerson().getId());
        params.put("visit_type_id", opdVisitTypeId);
        params.put("start_date", dateRange.getStartDateString());
        params.put("end_date", dateRange.getEndDateString());

        List<Visit> opdVisits = jdbcTemplate.query(opdVisitsSql, params,
                new BeanPropertyRowMapper<>(Visit.class));
            hospitalisation.setOpdVisits(opdVisits);

    }

    private void updateHospitalisationWithMedication(Hospitalisation hospitalisation) {
        Map<String,Object> params = new HashMap<>();
        params.put("patient_id", hospitalisation.getPerson().getId());
        params.put("order_date", hospitalisation.getDischargeDate());

        List<DrugOrder> drugOrders = jdbcTemplate.query(drugOrdersSql, params,
                new BeanPropertyRowMapper<>(DrugOrder.class));
        hospitalisation.setMedicationAtDischarge(drugOrders);

    }



    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    @PostConstruct
    public void postConstruct(){
        this.diagnosisInVisitSql = BatchUtils.convertResourceOutputToString(diagnosisInVisit);
        this.allDiagnosisSql = BatchUtils.convertResourceOutputToString(allDiagnosis);
        this.bedsInHospitalisationSql = BatchUtils.convertResourceOutputToString(bedsInHospitalisation);
        this.opdFollowup1Sql = BatchUtils.convertResourceOutputToString(opdFollowup1);
        this.firstRecordingOfBasicObsInFirstWeekSql = BatchUtils.convertResourceOutputToString(firstRecordingOfBasicObsInFirstWeek);
        this.dischargeSummaryObsSql = BatchUtils.convertResourceOutputToString(dischargeSummaryObs);
        this.opdVisitsSql = BatchUtils.convertResourceOutputToString(opdVisits);
        this.drugOrdersSql = BatchUtils.convertResourceOutputToString(drugOrders);

    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }
}
