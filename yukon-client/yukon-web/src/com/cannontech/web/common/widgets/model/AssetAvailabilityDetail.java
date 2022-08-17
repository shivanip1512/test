package com.cannontech.web.common.widgets.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AssetAvailabilityDetail {
    
    private double percentage;
    private int deviceCount;

    public AssetAvailabilityDetail(int deviceCount){
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
            percentage = new BigDecimal(deviceCount).divide(new BigDecimal(total), 10, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)).doubleValue();
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
