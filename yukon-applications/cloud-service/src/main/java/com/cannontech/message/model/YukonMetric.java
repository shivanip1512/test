package com.cannontech.message.model;

import java.io.Serializable;

import org.joda.time.DateTime;

public class YukonMetric implements Serializable {

    private static final long serialVersionUID = 1L;
    private YukonMetricPointInfo pointInfo;
    private Object value;
    private DateTime timestamp;

    public YukonMetric() {
        super();
    }

    public YukonMetricPointInfo getPointInfo() {
        return pointInfo;
    }

    public void setPointInfo(YukonMetricPointInfo pointInfo) {
        this.pointInfo = pointInfo;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

}