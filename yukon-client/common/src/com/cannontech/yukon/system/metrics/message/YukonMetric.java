package com.cannontech.yukon.system.metrics.message;

import java.io.Serializable;

import org.joda.time.DateTime;

public class YukonMetric implements Serializable {

    private static final long serialVersionUID = 1L;
    private Object fieldValue;
    private DateTime timestamp;
    private String attributeName;

    public YukonMetric() {
        super();
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

}