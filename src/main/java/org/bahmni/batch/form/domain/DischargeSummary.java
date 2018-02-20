package org.bahmni.batch.form.domain;

import java.util.Date;

public class DischargeSummary {

    private String admissionIndication;
    private String hospitalCourse;
    private String adviceOnDischarge;
    private Date followupDate;
    private String operativeProcedure;
    private Date dateOfOperation;
    private String surgicalIndication;

    public String getOperativeProcedure() {
        return operativeProcedure;
    }

    public void setOperativeProcedure(String operativeProcedure) {
        this.operativeProcedure = operativeProcedure;
    }

    public Date getDateOfOperation() {
        return dateOfOperation;
    }

    public void setDateOfOperation(Date dateOfOperation) {
        this.dateOfOperation = dateOfOperation;
    }

    public String getSurgicalIndication() {
        return surgicalIndication;
    }

    public void setSurgicalIndication(String surgicalIndication) {
        this.surgicalIndication = surgicalIndication;
    }

    public String getAdmissionIndication() {
        return admissionIndication;
    }

    public void setAdmissionIndication(String admissionIndication) {
        this.admissionIndication = admissionIndication;
    }

    public String getHospitalCourse() {
        return hospitalCourse;
    }

    public void setHospitalCourse(String hospitalCourse) {
        this.hospitalCourse = hospitalCourse;
    }

    public String getAdviceOnDischarge() {
        return adviceOnDischarge;
    }

    public void setAdviceOnDischarge(String adviceOnDischarge) {
        this.adviceOnDischarge = adviceOnDischarge;
    }

    public Date getFollowupDate() {
        return followupDate;
    }

    public void setFollowupDate(Date followupDate) {
        this.followupDate = followupDate;
    }
}
