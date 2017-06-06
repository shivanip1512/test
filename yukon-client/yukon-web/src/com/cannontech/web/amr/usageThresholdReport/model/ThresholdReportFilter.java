package com.cannontech.web.amr.usageThresholdReport.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.device.groups.model.DeviceGroup;

public class ThresholdReportFilter {

    private DeviceGroup group;
    private double threshold;
    private ThresholdDescriptor thresholdDescriptor;
    private DataAvailability availability;
    private boolean includeDisabled;

    public ThresholdReportFilter(DeviceGroup group, double threshold, ThresholdDescriptor thresholdDescriptor,
            DataAvailability availability, boolean includeDisabled) {
        this.group = group;
        this.threshold = threshold;
        this.thresholdDescriptor = thresholdDescriptor;
        this.availability = availability;
        this.includeDisabled = includeDisabled;
    }

    public DeviceGroup getGroup() {
        return group;
    }

    public double getThreshold() {
        return threshold;
    }

    public ThresholdDescriptor getThresholdDescriptor() {
        return thresholdDescriptor;
    }

    public DataAvailability getAvailability() {
        return availability;
    }

    public boolean isIncludeDisabled() {
        return includeDisabled;
    }
    
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("group", group);
        tsb.append("threshold", thresholdDescriptor.getValue() + " " + threshold);
        tsb.append("availability", availability);
        tsb.append("includeDisabled", includeDisabled);
        return tsb.toString();
    }
}
