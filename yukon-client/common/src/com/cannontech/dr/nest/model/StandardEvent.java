package com.cannontech.dr.nest.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StandardEvent extends CriticalEvent {
    private LoadShaping options;

    @JsonCreator
    public StandardEvent(@JsonProperty("start_time") String startTime, @JsonProperty("duration") String duration,
            @JsonProperty("groups") List<String> groups,
            @JsonProperty("load_shaping_options") LoadShaping options) {
        super(startTime, duration, groups);
        this.options = options;
    }

    @JsonProperty("load_shaping_options")
    public LoadShaping getOptions() {
        return options;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}
