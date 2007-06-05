package com.cannontech.amr.csr.model;

/**
 * Class used to filter a csr search
 */
public class FilterBy {

    private String filterValue = null;
    private CsrSearchField field = CsrSearchField.PAONAME;

    public FilterBy() {
    }

    public FilterBy(CsrSearchField field, String filterValue) {

        this.field = field;
        this.filterValue = filterValue;

    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public CsrSearchField getField() {
        return field;
    }

    public void setField(CsrSearchField field) {
        this.field = field;
    }

    public String toString() {
        return field.getSearchQueryField() + " LIKE '" + filterValue + "%'";
    }

    /**
     * Method to return a csr friendly string representation of this FilterBy
     * @return Csr friendly string
     */
    public String toCsrString() {
        return field.getCsrString() + " starts with '" + filterValue + "'";
    }
}
