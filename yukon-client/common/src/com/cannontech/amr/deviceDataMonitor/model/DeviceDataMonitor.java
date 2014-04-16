package com.cannontech.amr.deviceDataMonitor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.monitors.PointMonitor;
import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeStateGroup;
import com.cannontech.common.util.LazyList;

public class DeviceDataMonitor implements PointMonitor, Serializable,
		Comparable<DeviceDataMonitor> {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private String groupName = null;
	private boolean enabled = true;
	private List<DeviceDataMonitorProcessor> processors = LazyList
			.ofInstance(DeviceDataMonitorProcessor.class);

	public DeviceDataMonitor() {/* for use by Spring */
	}

	public DeviceDataMonitor(Integer id, String name, String groupName,
			boolean enabled, List<DeviceDataMonitorProcessor> processors) {
		this.id = id;
		this.groupName = groupName;
		this.name = name;
		this.enabled = enabled;
		this.processors = processors;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

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

	public List<DeviceDataMonitorProcessor> getProcessors() {
		return processors;
	}

	public void setProcessors(List<DeviceDataMonitorProcessor> processors) {
		this.processors = processors;
	}

	@Override
	public MonitorEvaluatorStatus getEvaluatorStatus() {
		return this.enabled ? MonitorEvaluatorStatus.ENABLED
				: MonitorEvaluatorStatus.DISABLED;
	}

	public Set<Attribute> getProcessorAttributes() {
		Set<Attribute> attributes = new HashSet<>();
		for (DeviceDataMonitorProcessor processor : processors) {
			attributes.add(processor.getAttribute());
		}
		return attributes;
	}

	public String getViolationsDeviceGroupName() {
		return DeviceGroupUtil
				.removeInvalidDeviceGroupNameCharacters(this.name);
	}

	public List<AttributeStateGroup> getAttributeStateGroups() {
		List<AttributeStateGroup> attributeStateGroup = new ArrayList<>();
		for (DeviceDataMonitorProcessor processor : this.processors) {
			attributeStateGroup.add(new AttributeStateGroup(processor
					.getAttribute(), processor.getStateGroup()));
		}
		return attributeStateGroup;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result
				+ ((groupName == null) ? 0 : groupName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((processors == null) ? 0 : processors.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeviceDataMonitor other = (DeviceDataMonitor) obj;
		if (enabled != other.enabled)
			return false;
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (processors == null) {
			if (other.processors != null)
				return false;
		} else if (!processors.equals(other.processors))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DeviceDataMonitor [id=" + id + ", name=" + name
				+ ", groupName=" + groupName + ", enabled=" + enabled + "]";
	}

	@Override
    public int compareTo(DeviceDataMonitor deviceDataMonitor) {
        return name.compareToIgnoreCase(deviceDataMonitor.getName());
	}
}
