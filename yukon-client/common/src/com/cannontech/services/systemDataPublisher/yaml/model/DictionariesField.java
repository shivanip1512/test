package com.cannontech.services.systemDataPublisher.yaml.model;

public class DictionariesField {

    private String field;
    private String description;
    private String details;
    private String source;
    private IOTDataType iotType;
    private SystemDataPublisherFrequency frequency;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public SystemDataPublisherFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(SystemDataPublisherFrequency frequency) {
        this.frequency = frequency;
    }

    public IOTDataType getIotType() {
        return iotType;
    }

    public void setIotType(IOTDataType iotType) {
        this.iotType = iotType;
    }

}
