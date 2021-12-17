package com.cannontech.yukon.system.metrics.message;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;

public class YukonMetric implements Serializable {

    private static final long serialVersionUID = 1L;
    private YukonMetricPointInfo pointInfo;
    private Object value;
    @JsonDeserialize(using = DateTimeDeserializer.class) private DateTime timestamp;

    public YukonMetric() {
        super();
    }

    public YukonMetric(YukonMetricPointInfo pointInfo, Object value, DateTime timestamp) {
        super();
        this.pointInfo = pointInfo;
        this.value = value;
        this.timestamp = timestamp;
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

    @Override
    public String toString() {
        return "YukonMetric [pointInfo=" + pointInfo + ", value=" + value + ", timestamp=" + timestamp + "]";
    }

}