package com.cannontech.amr.csr.model;

/**
 * Class used to sort a csr search
 */
public class OrderBy {

    private boolean descending = false;
    private CsrSearchField field = CsrSearchField.PAONAME;

    public OrderBy() {
    }

    public OrderBy(String field, boolean descending) {

        if (field != null && field.length() > 0) {
            this.field = CsrSearchField.valueOf(field);
        }

        this.descending = descending;

    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    public CsrSearchField getField() {
        return field;
    }

    public void setField(CsrSearchField field) {
        this.field = field;
    }

    public String toString() {
        return field.getSearchQueryField() + ((descending) ? " desc" : "") + ((field.equals(CsrSearchField.PAONAME)) ? ""
                : ", " + CsrSearchField.PAONAME.getSearchQueryField());
    }
}
