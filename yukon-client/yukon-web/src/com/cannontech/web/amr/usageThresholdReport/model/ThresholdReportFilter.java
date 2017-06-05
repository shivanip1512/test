package com.cannontech.web.amr.usageThresholdReport.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.device.groups.model.DeviceGroup;

public class ThresholdReportFilter {

    private DeviceGroup group;
    private double thershold;
    private ThresholdDescriptor thresholdDescriptor;
    private DataCompleteness dataCompleteness;
    private boolean includeDisabled;

    public ThresholdReportFilter(DeviceGroup group, double thershold, ThresholdDescriptor thresholdDescriptor,
            DataCompleteness dataCompleteness, boolean includeDisabled) {
        this.group = group;
        this.thershold = thershold;
        this.thresholdDescriptor = thresholdDescriptor;
        this.dataCompleteness = dataCompleteness;
        this.includeDisabled = includeDisabled;
    }

    public DeviceGroup getGroup() {
        return group;
    }

    public double getThershold() {
        return thershold;
    }

    public ThresholdDescriptor getThresholdDescriptor() {
        return thresholdDescriptor;
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
        tsb.append("threshold", thresholdDescriptor.getValue() + " " + thershold);
        tsb.append("dataCompleteness", dataCompleteness);
        tsb.append("", includeDisabled);
        return tsb.toString();
    }
}
