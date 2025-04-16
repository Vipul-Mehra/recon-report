package com.util;

import java.util.Objects;

public class ReportBean {
    public String etrmId; //Column number 12 in excel
    public String outcome; //Column number 6 in excel

    public String getEtrmId() {
        return etrmId;
    }

    public void setEtrmId(String etrmId) {
        this.etrmId = etrmId;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    @Override
    public String toString() {
        return "ReportBean{" +
                "etrmId='" + etrmId + '\'' +
                ", outcome='" + outcome + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReportBean that = (ReportBean) o;
        return Objects.equals(etrmId, that.etrmId) && Objects.equals(outcome, that.outcome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(etrmId, outcome);
    }
}
