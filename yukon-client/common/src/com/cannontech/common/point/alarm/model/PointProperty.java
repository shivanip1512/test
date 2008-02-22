package com.cannontech.common.point.alarm.model;

public class PointProperty {

	int pointId;
	int propertyId;
	float floatValue;
	
	public PointProperty() {
		
	}
	
	public PointProperty(int pointId, int attributeId, float floatValue) {
		this.pointId = pointId;
		this.propertyId = attributeId;
		this.floatValue = floatValue;
	}

	public int getPointId() {
		return pointId;
	}

	public void setPointId(int pointId) {
		this.pointId = pointId;
	}

	public int getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(int propertyId) {
		this.propertyId = propertyId;
	}

	public float getFloatValue() {
		return floatValue;
	}

	public void setFloatValue(float floatValue) {
		this.floatValue = floatValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + propertyId;
		result = prime * result + Float.floatToIntBits(floatValue);
		result = prime * result + pointId;
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
		final PointProperty other = (PointProperty) obj;
		if (propertyId != other.propertyId)
			return false;
		if (Float.floatToIntBits(floatValue) != Float
				.floatToIntBits(other.floatValue))
			return false;
		if (pointId != other.pointId)
			return false;
		return true;
	}

}
