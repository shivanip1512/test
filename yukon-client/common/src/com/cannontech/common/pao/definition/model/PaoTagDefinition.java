package com.cannontech.common.pao.definition.model;

import org.apache.commons.lang.builder.CompareToBuilder;

public class PaoTagDefinition implements Comparable<PaoTagDefinition> {

	private PaoTag tag;
	private Object value;
	
	public PaoTagDefinition(PaoTag feature, Object value) {
		this.tag = feature;
		this.value = value;
	}
	
	public PaoTag getTag() {
		return this.tag;
	}
	
	private String getTagName() {
		return this.tag.name();
	}
	
	public Object getValue() {
        return value;
    }
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tag == null) ? 0 : tag.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        PaoTagDefinition other = (PaoTagDefinition) obj;
        if (tag != other.tag)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    public int compareTo(PaoTagDefinition o) {

		return new CompareToBuilder()
	        .append(getTagName(), o.getTagName())
	        .toComparison();
    }
}
