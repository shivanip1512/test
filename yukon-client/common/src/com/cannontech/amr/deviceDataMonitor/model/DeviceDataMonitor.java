package com.cannontech.amr.deviceDataMonitor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.monitors.PointMonitor;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.pao.attribute.model.AttributeStateGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.LazyList;

public class DeviceDataMonitor implements PointMonitor, Serializable, Comparable<DeviceDataMonitor> {
    
    private final static long serialVersionUID = 1L;

    private String groupName;
    
    private Integer id;
    private String name;
    private boolean enabled = true;
    private boolean notifyOnAlarmOnly = false;
    private transient DeviceGroup group;
    private transient StoredDeviceGroup violationGroup;
    private List<DeviceDataMonitorProcessor> processors = LazyList.ofInstance(DeviceDataMonitorProcessor.class);

    public DeviceDataMonitor() {
        // Needed by Spring.
    }

    public DeviceDataMonitor(Integer id, String name, String groupName, DeviceGroup group, boolean enabled,
            List<DeviceDataMonitorProcessor> processors, boolean notifyOnAlarmOnly) {
        this.id = id;
        this.groupName = groupName;
        this.name = name.trim();
        this.enabled = enabled;
        this.processors = processors;
        this.group = group;
        this.notifyOnAlarmOnly = notifyOnAlarmOnly;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isNotifyOnAlarmOnly() {
        return notifyOnAlarmOnly;
    }
    
    public void setNotifyOnAlarmOnly(boolean enabled) {
        notifyOnAlarmOnly = enabled;
    }

    public List<DeviceDataMonitorProcessor> getProcessors() {
        return processors;
    }
    
    public List<DeviceDataMonitorProcessor> getProcessors(BuiltInAttribute attribute) {
        return processors.stream().filter(p -> attribute == p.getAttribute()).collect(Collectors.toList());
    }
    
    public List<DeviceDataMonitorProcessor> getStateProcessors() {
        return processors.stream().filter(p -> p.getStateGroup() != null).collect(Collectors.toList());
    }

    public List<DeviceDataMonitorProcessor> getValueProcessors() {
        return processors.stream().filter(p -> p.getStateGroup() == null).collect(Collectors.toList());
    }
    
    public void setProcessors(List<DeviceDataMonitorProcessor> processors) {
        this.processors = processors;
    }

    @Override
    public MonitorEvaluatorStatus getEvaluatorStatus() {
        return enabled ? MonitorEvaluatorStatus.ENABLED : MonitorEvaluatorStatus.DISABLED;
    }

    public String getViolationsDeviceGroupName() {
        return DeviceGroupUtil.removeInvalidDeviceGroupNameCharacters(name);
    }

    public List<AttributeStateGroup> getAttributeStateGroups() {
        List<AttributeStateGroup> attributeStateGroup = new ArrayList<>();
        for (DeviceDataMonitorProcessor processor : processors) {
            attributeStateGroup.add(new AttributeStateGroup(processor.getAttribute(), processor.getStateGroup()));
        }
        return attributeStateGroup;
    }
    
    public List<BuiltInAttribute> getAttributes() {
        return getProcessors().stream().map(p -> p.getAttribute()).collect(Collectors.toList());
    }
    
    public StoredDeviceGroup getViolationGroup() {
        return violationGroup;
    }

    public void setViolationGroup(StoredDeviceGroup violationGroup) {
        this.violationGroup = violationGroup;
    }

    public DeviceGroup getGroup() {
        return group;
    }

    public void setGroup(DeviceGroup group) {
        this.group = group;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (enabled ? 1231 : 1237);
        result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (notifyOnAlarmOnly ? 1231 : 1237);
        result = prime * result + ((processors == null) ? 0 : processors.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DeviceDataMonitor other = (DeviceDataMonitor) obj;
        if (enabled != other.enabled) {
            return false;
        }
        if (groupName == null) {
            if (other.groupName != null) {
                return false;
            }
        } else if (!groupName.equals(other.groupName)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (notifyOnAlarmOnly != other.notifyOnAlarmOnly) {
            return false;
        }
        if (processors == null) {
            if (other.processors != null) {
                return false;
            }
        } else if (!processors.equals(other.processors)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int compareTo(DeviceDataMonitor deviceDataMonitor) {
        return name.compareToIgnoreCase(deviceDataMonitor.name);
    }
    
    @Override
    public String toString() {
        return String.format("DeviceDataMonitor [groupName=%s, id=%s, name=%s, enabled=%s, notifyOnAlarmOnly=%s]",
                             groupName,
                             id,
                             name,
                             enabled,
                             notifyOnAlarmOnly);
    }
}
