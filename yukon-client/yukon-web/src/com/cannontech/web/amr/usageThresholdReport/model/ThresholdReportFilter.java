package com.cannontech.web.amr.usageThresholdReport.model;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.google.common.base.Joiner;

public class ThresholdReportFilter {

    private List<DeviceGroup> groups;
    private double threshold;
    private ThresholdDescriptor thresholdDescriptor;
    private List<DataAvailability> availability;
    private boolean includeDisabled;

    public ThresholdReportFilter(List<DeviceGroup> groups, double threshold, ThresholdDescriptor thresholdDescriptor,
            List<DataAvailability> availability, boolean includeDisabled) {
        this.groups = groups;
        this.threshold = threshold;
        this.thresholdDescriptor = thresholdDescriptor;
        this.availability = availability;
        this.includeDisabled = includeDisabled;
    }

    public List<DeviceGroup> getGroups() {
        return groups;
    }

    public double getThreshold() {
        return threshold;
    }

    public ThresholdDescriptor getThresholdDescriptor() {
        return thresholdDescriptor;
    }

    public List<DataAvailability> getAvailability() {
        return availability;
    }

    public boolean isIncludeDisabled() {
        return includeDisabled;
    }
    
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("groups", Joiner.on(",").join(groups.stream().map(g -> g.getFullName()).collect(Collectors.toList())));
        tsb.append("threshold", thresholdDescriptor.getValue() + " " + threshold);
        tsb.append("availability", Joiner.on(",").join(availability));
        tsb.append("includeDisabled", includeDisabled);
        return tsb.toString();
    }
}
