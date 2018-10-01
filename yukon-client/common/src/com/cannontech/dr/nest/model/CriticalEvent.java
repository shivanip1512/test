package com.cannontech.dr.nest.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CriticalEvent {
    private String startTime;
    private String duration;
    private List<String> groups;
    
    @JsonIgnore
    private Instant start;
    @JsonIgnore
    private Instant stop;

    @JsonCreator
    public CriticalEvent(@JsonProperty("start_time") String startTime, @JsonProperty("duration") String duration,
            @JsonProperty("groups") List<String> groups) {
        this.startTime = startTime;
        this.duration = duration;
        this.groups = groups;
    }

    @JsonProperty("start_time")
    public String getStartTime() {
        return startTime;
    }

    public String getDuration() {
        return duration;
    }

    public List<String> getGroups() {
        return groups;
    }
    
    @JsonIgnore
    public Instant getStart() {
        return start;
    }

    @JsonIgnore
    public void setStart(Instant start) {
        this.start = start;
    }

    @JsonIgnore
    public Instant getStop() {
        return stop;
    }

    @JsonIgnore
    public void setStop(Instant stop) {
        this.stop = stop;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}
