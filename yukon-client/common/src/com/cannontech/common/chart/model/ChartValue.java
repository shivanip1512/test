package com.cannontech.common.chart.model;

public class ChartValue {
    private long id = 0;
    private String value = null;
    private String description = null;
    

    public ChartValue() {
    }

    public ChartValue(long id, String value) {
        this.id = id;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
