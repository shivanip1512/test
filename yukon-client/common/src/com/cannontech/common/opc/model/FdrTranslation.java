package com.cannontech.common.opc.model;

import java.util.HashMap;
import java.util.Map;


/** 
 * 
 * Base Translation class. Extend for new interfaces to create specific calls instead of the
 * generic getParameters(String).
 *
 */

public class FdrTranslation {

	private int id;
	private FdrDirection direction;
	private FdrInterfaceType interfaceType;
	private FdrInterfaceType destination;
	private String translation;
	
	protected Map<String,String> parameterMap;
	
	public FdrTranslation() {
		super();
		parameterMap = new HashMap<String,String>();
	}
	
	public int getFdrPointId() {
		return id;
	}

	public void setFdrPointId(int id) {
		this.id = id;
	}

	public FdrDirection getDirection() {
		return direction;
	}

	public void setDirection(FdrDirection direction) {
		this.direction = direction;
	}

	public FdrInterfaceType getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(FdrInterfaceType interfaceType) {
		this.interfaceType = interfaceType;
	}

	public FdrInterfaceType getDestination() {
		return destination;
	}

	public void setDestination(FdrInterfaceType destination) {
		this.destination = destination;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
		convertTranslation();
	}
	
	public String getParameter(String param) {
		return parameterMap.get(param);
	}
	
	private void convertTranslation() {
		parameterMap.clear();
		
		String [] parameters = translation.split(";");
		
		for( String paramSet : parameters ) {
			String [] keyValuePair = paramSet.split(":");
			if( (keyValuePair.length == 2) && (keyValuePair[1] != null)) {
				parameterMap.put(keyValuePair[0], keyValuePair[1]);
			}
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((destination == null) ? 0 : destination.hashCode());
		result = prime * result
				+ ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((interfaceType == null) ? 0 : interfaceType.hashCode());
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
		if (id != other.id)
			return false;
		if (interfaceType == null) {
			if (other.interfaceType != null)
				return false;
		} else if (!interfaceType.equals(other.interfaceType))
			return false;
		if (translation == null) {
			if (other.translation != null)
				return false;
		} else if (!translation.equals(other.translation))
			return false;
		return true;
	}
}
