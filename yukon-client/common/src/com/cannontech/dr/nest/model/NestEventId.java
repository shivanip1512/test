package com.cannontech.dr.nest.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NestEventId {
    private String id;

    @JsonCreator
    public NestEventId(@JsonProperty String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}
