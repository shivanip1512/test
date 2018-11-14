package com.cannontech.dr.nest.model.v3;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ControlEvent {
    private String startTime;
    private String duration;
    private List<String> groupIds;
    private LoadShapingOptions loadShapingOptions;
    @JsonIgnore
    private Instant start;
    @JsonIgnore
    private Instant stop;
    
    @JsonCreator
    public ControlEvent(String startTime, String duration, List<String> groupIds,
            LoadShapingOptions loadShapingOptions) {
        this(startTime, duration, groupIds);
        this.loadShapingOptions = loadShapingOptions;
    }

    @JsonCreator
    public ControlEvent(String startTime, String duration, List<String> groupIds) {
        this.startTime = startTime;
        this.duration = duration;
        this.groupIds = groupIds;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getDuration() {
        return duration;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public LoadShapingOptions getLoadShapingOptions() {
        return loadShapingOptions;
    }
    
    @JsonIgnore
    public Instant getStop() {
        return stop;
    }
    
    @JsonIgnore
    public void setStop(Instant stop) {
        this.stop = stop;
    }

    @JsonIgnore
    public void setStart(Instant start) {
        this.start = start;
    }
    
    @JsonIgnore
    public Instant getStart() {
        return start;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}
