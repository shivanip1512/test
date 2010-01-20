package com.cannontech.common.pao.definition.model;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;

public class PaoTagDefinition implements Comparable<PaoTagDefinition> {

	PaoTag tag;
	boolean value;
	
	public PaoTagDefinition(PaoTag feature, boolean value) {
		this.tag = feature;
		this.value = value;
	}
	
	public PaoTag getTag() {
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
        if (obj instanceof PaoTagDefinition == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        PaoTagDefinition featureDefinition = (PaoTagDefinition) obj;
        return new EqualsBuilder().append(tag, featureDefinition.getTag())
        						  .append(value, featureDefinition.isTagTrue())
                                  .isEquals();
    }
	
	public int compareTo(PaoTagDefinition o) {

		return new CompareToBuilder()
	        .append(getTagName(), o.getTagName())
	        .toComparison();
    }
}
