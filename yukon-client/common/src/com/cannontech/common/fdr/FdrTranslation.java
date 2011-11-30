package com.cannontech.common.fdr;

import java.util.HashMap;
import java.util.Map;



/** 
 * 
 * Base Translation class. Extend for new interfaces to create specific calls instead of the
 * generic getParameters(String).
 *
 */

public class FdrTranslation {

	private int pointId;
	private FdrDirection direction;
	private FdrInterfaceType fdrInterfaceType;
	private String destination;
	private String translation;
	
	protected Map<String,String> parameterMap;
	
	public FdrTranslation() {
		super();
		parameterMap = new HashMap<String,String>();
	}
	
	public int getPointId() {
		return pointId;
	}
	
	public void setPointId(int pointId) {
		this.pointId = pointId;
	}

	public FdrDirection getDirection() {
		return direction;
	}

	public void setDirection(FdrDirection direction) {
		this.direction = direction;
	}

	public FdrInterfaceType getFdrInterfaceType() {
		return fdrInterfaceType;
	}

	public void setInterfaceType(FdrInterfaceType interfaceType) {
		this.fdrInterfaceType = interfaceType;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}
	
	public String getParameter(String param) {
		return parameterMap.get(param);
	}
	
	public Map<String, String> getParameterMap() {
        return parameterMap;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((destination == null) ? 0 : destination.hashCode());
		result = prime * result
				+ ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + pointId;
		result = prime * result
				+ ((fdrInterfaceType == null) ? 0 : fdrInterfaceType.hashCode());
		result = prime * result
				+ ((translation == null) ? 0 : translation.hashCode());
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
		final FdrTranslation other = (FdrTranslation) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (direction == null) {
			if (other.direction != null)
				return false;
		} else if (!direction.equals(other.direction))
			return false;
		if (pointId != other.pointId)
			return false;
		if (fdrInterfaceType == null) {
			if (other.fdrInterfaceType != null)
				return false;
		} else if (!fdrInterfaceType.equals(other.fdrInterfaceType))
			return false;
		if (translation == null) {
			if (other.translation != null)
				return false;
		} else if (!translation.equals(other.translation))
			return false;
		return true;
	}
}
