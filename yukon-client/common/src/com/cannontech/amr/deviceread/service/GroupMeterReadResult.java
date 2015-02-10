package com.cannontech.amr.deviceread.service;

import java.util.Set;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.commands.GroupActionResult;
import com.cannontech.common.pao.attribute.model.Attribute;

public class GroupMeterReadResult extends GroupActionResult {

    private Set<? extends Attribute> attributes;
    private DeviceCollection originalDeviceCollectionCopy;
    
    public Set<? extends Attribute> getAttributes() {
		return attributes;
	}
    
    public void setAttributes(Set<? extends Attribute> attributes) {
		this.attributes = attributes;
	}

    public DeviceCollection getOriginalDeviceCollectionCopy() {
		return originalDeviceCollectionCopy;
	}
    
    public void setOriginalDeviceCollectionCopy(DeviceCollection originalDeviceCollectionCopy) {
		this.originalDeviceCollectionCopy = originalDeviceCollectionCopy;
	}
}