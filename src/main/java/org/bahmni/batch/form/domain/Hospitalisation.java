package org.bahmni.batch.form.domain;

import java.util.Date;
import java.util.List;

public class Hospitalisation {

    private Integer visitId;
    private Date admissionDate;
    private Date dischargeDate;
    private Integer lengthOfHospitalisation;
    private String identifier;
    private Person person;
    private List<Bed> bedAssignments;
    private List<Diagnosis> diagnoses;
    private Date subsequentOPDVisitDate;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getVisitId() {
        return visitId;
    }

    public void setVisitId(Integer visitId) {
        this.visitId = visitId;
    }

    public Date getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(Date admissionDate) {
        this.admissionDate = admissionDate;
    }

    public Date getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(Date dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public List<Bed> getBedAssignments() {
        return bedAssignments;
    }

    public void setBedAssignments(List<Bed> bedAssignments) {
        this.bedAssignments = bedAssignments;
    }

    public String getNthBedAssignment(int i){
        if(i >= this.bedAssignments.size()){
            return "N/A";
        }
        Bed bed = this.bedAssignments.get(i);
        return bed.getWard() + "-" + bed.getBedNumber();
    }

    public String getNthBedAssignmentDuration(int i) {
        if(i >= this.bedAssignments.size()){
            return "N/A";
        }
        Bed bed = this.bedAssignments.get(i);
        return String.valueOf(bed.getDuration());
    }


    public Integer getLengthOfHospitalisation() {
        return lengthOfHospitalisation;
    }

    public void setLengthOfHospitalisation(Integer lengthOfHospitalisation) {
        this.lengthOfHospitalisation = lengthOfHospitalisation;
    }

    public List<Diagnosis> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(List<Diagnosis> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public String getNthDiagnois(int i) {
        if(i >= this.diagnoses.size()){
            return "N/A";
        }
        Diagnosis diagnosis = this.diagnoses.get(i);
        return "\""+diagnosis.toString()+"\"";
    }

    public Date getSubsequentOPDVisitDate() {
        return subsequentOPDVisitDate;
    }

    public void setSubsequentOPDVisitDate(Date subsequentOPDVisitDate) {
        this.subsequentOPDVisitDate = subsequentOPDVisitDate;
    }

    public String daysOfSubsequentOPDVisitFromDischargeDate() {
        if(dischargeDate == null){
            return "N/A";
        }
        if(subsequentOPDVisitDate == null){
            return String.valueOf(diffInDays(new Date(), dischargeDate));
        }
        return String.valueOf(diffInDays(subsequentOPDVisitDate, dischargeDate));

    }

    private int diffInDays(Date d1, Date d2){
        return (int)((d1.getTime() - d2.getTime()) / (1000 * 60 * 60 * 24));
    }
}
