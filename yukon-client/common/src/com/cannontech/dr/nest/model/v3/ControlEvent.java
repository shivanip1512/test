package com.cannontech.dr.nest.model.v3;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

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
    
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }

    public LoadShapingOptions getLoadShapingOptions() {
        return loadShapingOptions;
    }

    public void setLoadShapingOptions(LoadShapingOptions loadShapingOptions) {
        this.loadShapingOptions = loadShapingOptions;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}
