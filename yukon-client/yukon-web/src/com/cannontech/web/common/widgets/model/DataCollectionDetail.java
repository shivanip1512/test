package com.cannontech.web.common.widgets.model;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DataCollectionDetail {
    private double percentage;
    private int deviceCount;

    public DataCollectionDetail(int deviceCount){
        this.deviceCount = deviceCount;
    }
    
    public double getPercentage() {
        return percentage;
    }
    
    public int getDeviceCount() {
        return deviceCount;
    }

    public void calculatePrecentage(int total) {
        if (total != 0) {
            percentage = new BigDecimal((deviceCount / total) * 100).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        }
    }
    
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("deviceCount="+ deviceCount);
        tsb.append("%="+ percentage);
        return tsb.toString();
    }
}
