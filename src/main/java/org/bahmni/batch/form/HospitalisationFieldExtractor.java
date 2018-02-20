package org.bahmni.batch.form;

import org.bahmni.batch.form.domain.*;
import org.bahmni.batch.helper.SMUVillageCodeMapping;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HospitalisationFieldExtractor implements FieldExtractor<Hospitalisation>, FlatFileHeaderCallback{

	public static final String DATE_FORMAT = "yyyy-MM-dd";

	@Override
	public Object[] extract(Hospitalisation hospitalisation) {
		List<Object> row = new ArrayList<>();


//		System.out.println("Writing hospitalisation "+hospitalisation.getIdentifier());
		row.add(hospitalisation.getIdentifier());
//		row.add(hospitalisation.getPerson().getName());
		row.add(hospitalisation.getPerson().getAge());
		row.add(hospitalisation.getPerson().getGender());
		row.add(hospitalisation.getPerson().getSmartCardHolder());
		row.add(hospitalisation.getPerson().getSmoking());
		row.add(hospitalisation.getPerson().getAlcohol());
		row.add(hospitalisation.getPerson().getFoodSecurity());
		row.add(hospitalisation.getPerson().getFamilyIncome());
		row.add("To be filled by Tim using Community Health Worker expertise");
		row.add(massageStringValue(hospitalisation.getPerson().getAddress().getVillage()));
		row.add(massageStringValue(hospitalisation.getPerson().getAddress().getTehsil()));
		row.add(massageStringValue(hospitalisation.getPerson().getAddress().getState()));
		row.add(new SimpleDateFormat(DATE_FORMAT).format(hospitalisation.getAdmissionDate()));
		row.add(hospitalisation.getDischargeDate()!= null ? new SimpleDateFormat(DATE_FORMAT).format(hospitalisation.getDischargeDate()) : null);
		row.add(hospitalisation.getLengthOfHospitalisation());
		row.add("System can't determine");
		row.add(massageStringValue(hospitalisation.getDispositionNote()));
		row.add(hospitalisation.getDisposingPerson());
		row.add(hospitalisation.getFirstRecordingOfBasicObsInFirstWeek().getPulse());
		row.add(hospitalisation.getFirstRecordingOfBasicObsInFirstWeek().getSystolic());
		row.add(hospitalisation.getFirstRecordingOfBasicObsInFirstWeek().getDiastolic());
		row.add(hospitalisation.getFirstRecordingOfBasicObsInFirstWeek().getTemperature());
		row.add(hospitalisation.getFirstRecordingOfBasicObsInFirstWeek().getWeight());
		row.add(hospitalisation.getFirstRecordingOfBasicObsInFirstWeek().getHeight());



//        System.out.println("Beds Assignments" +hospitalisation.getBedAssignments().size());

		for (int i = 0; i < 20; i++) {
			row.add(hospitalisation.getNthBedAssignment(i));
            row.add(hospitalisation.getNthBedAssignmentDuration(i));
        }
		row.add(hospitalisation.getBedAssignments().size());
		row.add("System can't determine");
		for (int i = 0; i < 20; i++) {
			row.add(hospitalisation.getNthDiagnois(i));
		}
		row.add(massageStringValue(hospitalisation.getDischargeSummary().getAdmissionIndication()));
		row.add(massageStringValue(hospitalisation.getDischargeSummary().getHospitalCourse()));
		row.add(massageStringValue(hospitalisation.getDischargeSummary().getOperativeProcedure()));
		row.add(hospitalisation.getDischargeSummary().getDateOfOperation()!=null ? new SimpleDateFormat(DATE_FORMAT).format(hospitalisation.getDischargeSummary().getDateOfOperation()): null);
		row.add(massageStringValue(hospitalisation.getDischargeSummary().getSurgicalIndication()));
		row.add(massageStringValue(hospitalisation.getDischargeSummary().getAdviceOnDischarge()));
		row.add(hospitalisation.getDischargeSummary().getFollowupDate()!=null ? new SimpleDateFormat(DATE_FORMAT).format(hospitalisation.getDischargeSummary().getFollowupDate()): null);
		row.add(hospitalisation.getSubsequentOPDVisitDate()!=null ? new SimpleDateFormat(DATE_FORMAT).format(hospitalisation.getSubsequentOPDVisitDate()): null);
		row.add(hospitalisation.daysOfSubsequentOPDVisitFromDischargeDate());

		return row.toArray();
	}

	@Override
	public void writeHeader(Writer writer) throws IOException {
		writer.write(getHeader());
	}

	private String getHeader() {
		StringBuilder sb = new StringBuilder();

		sb.append("Patient Identifier");
//		sb.append(",").append("Patient Name");
		sb.append(",").append("Patient Age");
//		sb.append(",").append("Patient Birth date");
		sb.append(",").append("Patient Gender");
		sb.append(",").append("Smart Card Holder");
		sb.append(",").append("Smoking");
		sb.append(",").append("Alcohol");
		sb.append(",").append("Food Security");
		sb.append(",").append("Family Income");
		sb.append(",").append("Travel Time to JSS (hours)");
		sb.append(",").append("Patient Village");
		sb.append(",").append("Patient Tehsil");
		sb.append(",").append("Patient State");
		sb.append(",").append("Admission Date");
		sb.append(",").append("Discharge date");
		sb.append(",").append("Length of Hospitalisation (Days)");
		sb.append(",").append("Admitting service");
		sb.append(",").append("Disposition Note");
		sb.append(",").append("Disposing Person");
		sb.append(",").append("Pulse");
		sb.append(",").append("Systolic");
		sb.append(",").append("Diastolic");
		sb.append(",").append("Temperature");
		sb.append(",").append("Weight");
		sb.append(",").append("Height");

        for (int i = 0; i < 20; i++) {
            sb.append(",").append(ordinal(i+1)+" Bed assignment");
            sb.append(",").append(ordinal(i+1)+" Bed duration (Days)");
        }
		sb.append(",").append("Total Beds");
		sb.append(",").append("Outcome");
		for (int i = 0; i < 20; i++) {
			sb.append(",").append("Diagnosis ").append(i+1);
		}

		sb.append(",").append("DS - Admission Indication");
		sb.append(",").append("DS - Hospital Course");
		sb.append(",").append("DS - Operative Procedure");
		sb.append(",").append("DS - Date of Operation");
		sb.append(",").append("DS - Surgical Indication");
		sb.append(",").append("DS - Advice On Discharge");
		sb.append(",").append("DS - Follow up date");
		sb.append(",").append("OPD Follow up 1 Date");
		sb.append(",").append("Days from Discharge OPD Follow up 1");

		return sb.toString();
	}

	private String massageStringValue(String text){
		if(StringUtils.isEmpty(text))
			return text;
		return text.replaceAll("\n"," ").replaceAll("\t"," ").replaceAll(","," ");
	}

	private static String ordinal(int i) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];

        }
    }
}
