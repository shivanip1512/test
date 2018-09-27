package com.cannontech.dr.nest.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NestUploadError {
    private int row;
    private List<String> header;
    private List<String> fields;
    private List<String> errors;

    @JsonCreator
    public NestUploadError(@JsonProperty("row_number") int row,
            @JsonProperty("header") List<String> header,
            @JsonProperty("fields") List<String> fields,
            @JsonProperty("errors") List<String> errors) {
        this.row = row;
        this.header = header;
        this.fields = fields;
        this.errors = errors;
    }
    
    public int getRow() {
        return row;
    }

    public List<String> getHeader() {
        return header;
    }

    public List<String> getFields() {
        return fields;
    }

    public List<String> getErrors() {
        return errors;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}
