package com.cannontech.message.model;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.cannontech.azure.model.IOTDataType;

public class SystemData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String fieldName;
    private Object fieldValue;
    private DateTime timestamp;
    private IOTDataType iotDataType;

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

    public IOTDataType getIotDataType() {
        return iotDataType;
    }

    public void setIotDataType(IOTDataType iotDataType) {
        this.iotDataType = iotDataType;
    }

    @Override
    public String toString() {
        return "SystemData [fieldName=" + fieldName + ", fieldValue=" + fieldValue + ", timestamp=" + timestamp + ", iotDataType="
                + iotDataType + "]";
    }

}
