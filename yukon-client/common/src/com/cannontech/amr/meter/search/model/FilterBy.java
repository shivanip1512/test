package com.cannontech.amr.meter.search.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class used to filter a meter search
 */
public class FilterBy {

    private String name = null;
    private String filterValue = null;
    private List<MeterSearchField> fieldList = new ArrayList<MeterSearchField>();

    public FilterBy() {
    }

    public FilterBy(String name, List<MeterSearchField> fieldList) {
        this.name = name;
        this.fieldList = fieldList;
    }

    public FilterBy(String name, MeterSearchField... fieldList) {
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

    public List<MeterSearchField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<MeterSearchField> fieldList) {
        this.fieldList = fieldList;
    }

    public void addFilterField(MeterSearchField field) {
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
        for (MeterSearchField field : fieldList) {

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
     * Method to return a meter search friendly string representation of this FilterBy
     * @return Meter Search friendly string
     */
    public String toSearchString() {

        StringBuffer sb = new StringBuffer();

        if (fieldList.size() == 1) {
            return fieldList.get(0).getMeterSearchString() + " starts with '" + filterValue + "'";
        }

        if (fieldList.size() > 1) {
            sb.append(" (");
        }
        boolean first = true;
        for (MeterSearchField field : fieldList) {

            if (!first) {
                sb.append(" OR ");
            } else {
                first = false;
            }

            sb.append(" " + field.getMeterSearchString() + " starts with '" + filterValue + "' ");
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
