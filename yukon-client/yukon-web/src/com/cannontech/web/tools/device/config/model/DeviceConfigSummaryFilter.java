package com.cannontech.web.tools.device.config.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao.StateSelection;

public class DeviceConfigSummaryFilter {
    private List<DeviceGroup> groups;
    private List<Integer> configurationIds;
    private boolean displayUnassigned;
    private StateSelection selection;

    public List<DeviceGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<DeviceGroup> groups) {
        this.groups = groups;
    }

    public List<Integer> getConfigurationIds() {
        return configurationIds;
    }

    public void setConfigurationIds(List<Integer> configurationIds) {
        this.configurationIds = configurationIds;
    }

    public boolean isDisplayUnassigned() {
        return displayUnassigned;
    }

    public void setDisplayUnassigned(boolean displayUnassigned) {
        this.displayUnassigned = displayUnassigned;
    }

    public StateSelection getSelection() {
        return selection;
    }

    public void setSelection(StateSelection selection) {
        this.selection = selection;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
                + System.getProperty("line.separator");
    }
}
