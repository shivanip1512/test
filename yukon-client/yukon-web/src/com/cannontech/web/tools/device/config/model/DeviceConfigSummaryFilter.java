package com.cannontech.web.tools.device.config.model;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.google.common.base.Joiner;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastAction;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastActionStatus;

public class DeviceConfigSummaryFilter {

    public enum InSync {
        IN_SYNC, OUT_OF_SYNC, UNVERIFIED, NA
    }

    private List<LastAction> actions;
    private List<InSync> inSync;
    private List<LastActionStatus> statuses;
    private List<DeviceGroup> groups;
    //has all ids if "ALL" selected.
    private List<Integer> configurationIds;
    private boolean displayUnassigned;
    private String status;  //change to new enum

    public List<DeviceGroup> getGroups() {
        return groups;
    }
    public void setGroups(List<DeviceGroup> groups) {
        this.groups = groups;
    }
    public List<LastActionStatus> getStatuses() {
        return statuses;
    }
    public void setStatuses(List<LastActionStatus> statuses) {
        this.statuses = statuses;
    }
    public List<InSync> getInSync() {
        return inSync;
    }
    public void setInSync(List<InSync> inSync) {
        this.inSync = inSync;
    }
    public List<LastAction> getActions() {
        return actions;
    }
    public void setActions(List<LastAction> actions) {
        this.actions = actions;
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
    
    public List<DeviceRequestType> getRequestTypes(){
        return actions.stream().map(a -> a.getRequestType()).collect(Collectors.toList());
    }
    
    public boolean contains(InSync option) {
        return inSync == null ? false : inSync.contains(option);
    }

    public boolean contains(LastAction option) {
        return actions == null ? false : actions.contains(option);
    }
    
    public boolean contains(LastActionStatus option) {
        return statuses == null ? false : statuses.contains(option);
    }
   
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        if (groups != null) {
            tsb.append("groups",
                Joiner.on(",").join(groups.stream().map(g -> g.getFullName()).collect(Collectors.toList())));
        }
        if (actions != null) {
            tsb.append("actions", Joiner.on(",").join(actions));
        }
        if (inSync != null) {
            tsb.append("inSync", Joiner.on(",").join(inSync));
        }
        if (statuses != null) {
            tsb.append("statuses", Joiner.on(",").join(statuses));
        }
        if (configurationIds != null) {
            tsb.append("configurationIds", Joiner.on(",").join(configurationIds));
        }
        return tsb.toString();
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
