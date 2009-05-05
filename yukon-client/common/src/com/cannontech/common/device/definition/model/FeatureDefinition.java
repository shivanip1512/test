package com.cannontech.common.device.definition.model;

import org.apache.commons.lang.builder.EqualsBuilder;

public class FeatureDefinition implements Comparable<FeatureDefinition> {

	DeviceFeature feature;
	boolean value;
	
	public FeatureDefinition(DeviceFeature feature, boolean value) {
		this.feature = feature;
		this.value = value;
	}
	
	public DeviceFeature getFeature() {
		return this.feature;
	}
	
	public String getFeatureName() {
		return this.feature.name();
	}
	
	public String getFeatureDescription() {
		return this.feature.getDescription();
	}
	
	public boolean isSupported() {
		return this.value;
	}
	
	public boolean equals(Object obj) {
        if (obj instanceof FeatureDefinition == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        FeatureDefinition featureDefinition = (FeatureDefinition) obj;
        return new EqualsBuilder().append(feature, featureDefinition.getFeature())
        						  .append(value, featureDefinition.isSupported())
                                  .isEquals();
    }
	
	public int compareTo(FeatureDefinition o) {

        if (o == null) {
            return 0;
        }
        return getFeatureName().compareTo(o.getFeatureName());
    }
}
