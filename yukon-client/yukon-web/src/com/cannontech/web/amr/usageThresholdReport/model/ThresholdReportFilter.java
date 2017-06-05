package com.cannontech.web.amr.usageThresholdReport.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.device.groups.model.DeviceGroup;

public class ThresholdReportFilter {

    private DeviceGroup group;
    private double thershold;
    private ThresholdDesctiptor thresholdDesctiptor;
    private DataCompleteness dataCompleteness;
    private boolean includeDisabled;

    public ThresholdReportFilter(DeviceGroup group, double thershold, ThresholdDesctiptor thresholdDesctiptor,
            DataCompleteness dataCompleteness, boolean includeDisabled) {
        this.group = group;
        this.thershold = thershold;
        this.thresholdDesctiptor = thresholdDesctiptor;
        this.dataCompleteness = dataCompleteness;
        this.includeDisabled = includeDisabled;
    }

    public DeviceGroup getGroup() {
        return group;
    }

    public double getThershold() {
        return thershold;
    }

    public ThresholdDesctiptor getThresholdDesctiptor() {
        return thresholdDesctiptor;
    }

    public DataCompleteness getDataCompleteness() {
        return dataCompleteness;
    }

    public boolean isIncludeDisabled() {
        return includeDisabled;
    }
    
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("group", group);
        tsb.append("threshold", thresholdDesctiptor.getValue() + " " + thershold);
        tsb.append("dataCompleteness", dataCompleteness);
        tsb.append("includeDisabled", includeDisabled);
        return tsb.toString();
    }
}
