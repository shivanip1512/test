package com.cannontech.amr.csr.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class used to filter a csr search
 */
public class FilterBy {

    private String name = null;
    private String filterValue = null;
    private List<CsrSearchField> fieldList = new ArrayList<CsrSearchField>();

    public FilterBy() {
    }

    public FilterBy(String name, List<CsrSearchField> fieldList) {
        this.name = name;
        this.fieldList = fieldList;
    }

    public FilterBy(String name, CsrSearchField... fieldList) {
        this.name = name;
        this.fieldList = Arrays.asList(fieldList);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public List<CsrSearchField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<CsrSearchField> fieldList) {
        this.fieldList = fieldList;
    }

    public void addFilterField(CsrSearchField field) {
        this.fieldList.add(field);
    }

    public String toString() {

        StringBuffer sb = new StringBuffer();

        if (fieldList.size() == 1) {
            return fieldList.get(0).getSearchQueryField() + " LIKE ?";
        }

        if (fieldList.size() > 0) {
            sb.append(" (");
        }
        boolean first = true;
        for (CsrSearchField field : fieldList) {

            if (!first) {
                sb.append(" OR ");
            } else {
                first = false;
            }

            sb.append(" " + field.getSearchQueryField() + " LIKE ? ");
        }
        if (fieldList.size() > 0) {
            sb.append(") ");
        }

        return sb.toString();
    }

    /**
     * Method to return a csr friendly string representation of this FilterBy
     * @return Csr friendly string
     */
    public String toCsrString() {

        StringBuffer sb = new StringBuffer();

        if (fieldList.size() == 1) {
            return fieldList.get(0).getCsrString() + " starts with '" + filterValue + "'";
        }

        if (fieldList.size() > 1) {
            sb.append(" (");
        }
        boolean first = true;
        for (CsrSearchField field : fieldList) {

            if (!first) {
                sb.append(" OR ");
            } else {
                first = false;
            }

            sb.append(" " + field.getCsrString() + " starts with '" + filterValue + "' ");
        }
        if (fieldList.size() > 1) {
            sb.append(") ");
        }

        return sb.toString();

    }

    public List<String> getFilterValues() {

        List<String> filterValueList = new ArrayList<String>();
        for (int i = 0; i < fieldList.size(); i++) {
            filterValueList.add(filterValue + "%");
        }

        return filterValueList;
    }
}
