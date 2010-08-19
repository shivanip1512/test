package com.cannontech.common.search;

public enum FilterType {

	STATUSPOINT,
	ANALOGPOINT;

	//These are here because the faces pages expect getters and setters.
	public String getName() {
		return this.name();
	}
	
	public void setName() {
		
	}
}