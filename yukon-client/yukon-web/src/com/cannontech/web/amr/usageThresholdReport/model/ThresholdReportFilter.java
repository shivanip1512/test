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
    
    public List<DeviceGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<DeviceGroup> groups) {
        this.groups = groups;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public ThresholdDescriptor getThresholdDescriptor() {
        return thresholdDescriptor;
    }

    public void setThresholdDescriptor(ThresholdDescriptor thresholdDescriptor) {
        this.thresholdDescriptor = thresholdDescriptor;
    }

    public List<DataAvailability> getAvailability() {
        return availability;
    }

    public void setAvailability(List<DataAvailability> availability) {
        this.availability = availability;
    }

    public boolean isIncludeDisabled() {
        return includeDisabled;
    }

    public void setIncludeDisabled(boolean includeDisabled) {
        this.includeDisabled = includeDisabled;
    }
    
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        if (groups != null) {
            tsb.append("groups", Joiner.on(",").join(groups.stream().map(g -> g.getFullName()).collect(Collectors.toList())));
        }
        tsb.append("threshold", threshold);
        tsb.append("availability", Joiner.on(",").join(availability));
        tsb.append("includeDisabled", includeDisabled);
        return tsb.toString();
    }
}
