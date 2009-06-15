package com.cannontech.common.device.definition.model;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;

public class DeviceTagDefinition implements Comparable<DeviceTagDefinition> {

	DeviceTag tag;
	boolean value;
	
	public DeviceTagDefinition(DeviceTag feature, boolean value) {
		this.tag = feature;
		this.value = value;
	}
	
	public DeviceTag getTag() {
		return this.tag;
	}
	
	public String getTagName() {
		return this.tag.name();
	}
	
	public String getTagDescription() {
		return this.tag.getDescription();
	}
	
	public boolean isTagTrue() {
		return this.value;
	}
	
	public boolean equals(Object obj) {
        if (obj instanceof DeviceTagDefinition == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        DeviceTagDefinition featureDefinition = (DeviceTagDefinition) obj;
        return new EqualsBuilder().append(tag, featureDefinition.getTag())
        						  .append(value, featureDefinition.isTagTrue())
                                  .isEquals();
    }
	
	public int compareTo(DeviceTagDefinition o) {

		return new CompareToBuilder()
	        .append(getTagName(), o.getTagName())
	        .toComparison();
    }
}
