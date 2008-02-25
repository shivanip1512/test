package com.cannontech.common.point.alarm.model;

public class PointPropertyValue {

	int pointId;
	int pointPropertyCode;
	float floatValue;
	
	public PointPropertyValue() {
		
	}
	
	public PointPropertyValue(int pointId, int attributeId, float floatValue) {
		this.pointId = pointId;
		this.pointPropertyCode = attributeId;
		this.floatValue = floatValue;
	}

	public int getPointId() {
		return pointId;
	}

	public void setPointId(int pointId) {
		this.pointId = pointId;
	}

	public int getPointPropertyCode() {
		return pointPropertyCode;
	}

	public void setPointPropertyCode(int propertyId) {
		this.pointPropertyCode = propertyId;
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
		result = prime * result + pointPropertyCode;
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
		final PointPropertyValue other = (PointPropertyValue) obj;
		if (pointPropertyCode != other.pointPropertyCode)
			return false;
		if (Float.floatToIntBits(floatValue) != Float
				.floatToIntBits(other.floatValue))
			return false;
		if (pointId != other.pointId)
			return false;
		return true;
	}

}
