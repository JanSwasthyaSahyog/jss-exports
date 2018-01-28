package org.bahmni.batch.form.domain;

public class Diagnosis {
    private String diagnosis;
    private String certainty;
    private String order;


    public String getCertainty() {
        return certainty;
    }

    public void setCertainty(String certainty) {
        this.certainty = certainty;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    @Override
    public String toString() {
        return this.getDiagnosis() + " ( "+ this.getOrder() + " " + this.getCertainty() + " )";
    }
}
