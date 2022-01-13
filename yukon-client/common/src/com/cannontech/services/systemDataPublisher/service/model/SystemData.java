package com.cannontech.services.systemDataPublisher.service.model;

import java.io.Serializable;

import org.joda.time.DateTime;

/**
 * POJO class to publish system data to message broker.
 */
public class SystemData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String fieldName;
    private Object fieldValue;
    private DateTime timestamp;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
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

    @Override
    public String toString() {
        return String
                .format("SystemData [fieldName=%s, fieldValue=%s, timestamp=%s]",
                        fieldName, fieldValue, timestamp);
    }
}
