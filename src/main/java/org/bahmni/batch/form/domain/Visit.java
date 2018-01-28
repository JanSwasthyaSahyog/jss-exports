package org.bahmni.batch.form.domain;

import java.util.Date;

public class Visit {

    private Date visit_date;
    private String visit_type;

    public Date getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(Date visit_date) {
        this.visit_date = visit_date;
    }

    public String getVisit_type() {
        return visit_type;
    }

    public void setVisit_type(String visit_type) {
        this.visit_type = visit_type;
    }
}
