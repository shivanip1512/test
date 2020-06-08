package com.cannontech.common.fdr;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** 
 * Base Translation class. Extend for new interfaces to create specific calls instead of the
 * generic getParameters(String).
 */

public class FdrTranslation {
    @JsonIgnore
	private int pointId;
	private FdrDirection direction;
	private FdrInterfaceType fdrInterfaceType;
	private String translation;
	
    @JsonIgnore
	protected Map<String,String> parameterMap;

    public FdrTranslation() {
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

	/**
	 * This getter is hiding the complexity of what to put in the Destination column in the database.
	 * It requires fdrInterfaceType and paramaterMap to be not null and populated.
	 */
    @JsonIgnore
	public String getDestination() {
	    if (fdrInterfaceType.isDestinationInOptions()) {
	        FdrInterfaceOption destinationOption = fdrInterfaceType.getDestinationOption();	        
	        String value = parameterMap.get(destinationOption.getOptionLabel());
	        return value;
	    } else {
	        return fdrInterfaceType.name();    
	    }
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

    /**
     *  Split translation and build parameter map based on it.
     */
    public void setParameterMap() {
        if (translation != null) {
            String[] parameters = translation.split(";");

            for (String paramSet : parameters) {
                int splitSpot = paramSet.indexOf(":");
                if (splitSpot != -1) {
                    parameterMap.put(paramSet.substring(0, splitSpot), paramSet.substring(splitSpot + 1));
                }
            }
        }
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
