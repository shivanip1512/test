package com.cannontech.services.systemDataPublisher.yaml.model;

public class DictionariesField {

    private String field;
    private String description;
    private String details;
    private String source;
    private IOTDataType iotType;
    private SystemDataPublisherFrequency frequency;

    public DictionariesField(String field, String description, String details, String source, IOTDataType iotType,
            SystemDataPublisherFrequency frequency) {
        this.field = field;
        this.description = description;
        this.details = details;
        this.source = source;
        this.iotType = iotType;
        this.frequency = frequency;
    }

    public DictionariesField() {
    }

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

    @Override
    public String toString() {
        return String
                .format("DictionariesField [field=%s, description=%s, details=%s, source=%s, iotType=%s, frequency=%s]",
                        field, description, details, source, iotType, frequency);
    }
}
