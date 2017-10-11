package com.cannontech.web.tools.device.config.model;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.google.common.base.Joiner;

public class DeviceConfigSummaryFilter {
    public enum LastAction {
        READ, SEND, VERIFY, UNKNOWN
    }

    public enum InSync {
        IN_SYNC, OUT_OF_SYNC
    }

    public enum LastActionStatus {
        SUCCESS, FAILURE, IN_PROGRESS
    }
    
    private List<LastAction> actions;
    private List<InSync> inSync;
    private List<LastActionStatus> statuses;
    private List<DeviceGroup> groups;
    private List<Integer> configurationIds;

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
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        if (groups != null) {
            tsb.append("groups", Joiner.on(",").join(groups.stream().map(g -> g.getFullName()).collect(Collectors.toList())));
        }
        tsb.append("actions", Joiner.on(",").join(actions));
        tsb.append("inSync", Joiner.on(",").join(inSync));
        tsb.append("statuses", Joiner.on(",").join(statuses));
        tsb.append("configurationIds", Joiner.on(",").join(configurationIds));
        return tsb.toString();
    }
}
