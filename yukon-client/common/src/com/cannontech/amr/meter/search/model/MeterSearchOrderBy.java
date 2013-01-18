package com.cannontech.amr.meter.search.model;

/**
 * Class used to sort a meter search
 */
public class MeterSearchOrderBy {

    private boolean descending = false;
    private MeterSearchField field = MeterSearchField.PAONAME;

    public MeterSearchOrderBy() {
    }

    public MeterSearchOrderBy(String field, boolean descending) {

        if (field != null && field.length() > 0) {
            this.field = MeterSearchField.valueOf(field);
        }

        this.descending = descending;

    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    public MeterSearchField getField() {
        return field;
    }

    public void setField(MeterSearchField field) {
        this.field = field;
    }

    public String toString() {
        return field.getSearchQueryField() + ((descending) ? " desc" : "") + ((field.equals(MeterSearchField.PAONAME)) ? ""
                : ", " + MeterSearchField.PAONAME.getSearchQueryField());
    }
}
