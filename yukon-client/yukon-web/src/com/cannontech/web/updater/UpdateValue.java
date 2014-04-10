package com.cannontech.web.updater;

public class UpdateValue {
	private UpdateIdentifier identifier;
    private String value;
    private boolean unavailable = false;
    
    public UpdateValue(UpdateIdentifier identifier, String value) {
        this.identifier = identifier;
        this.value = value;
    }
    
    public UpdateValue(UpdateIdentifier identifier) {
    	this.identifier = identifier;
    	this.unavailable = true;
    }
    
    public String getValue() {
    	if (unavailable) {
    		throw new IllegalStateException("Can't call getValue if unavailable");
    	}
        return value.trim();
    }

    public UpdateIdentifier getIdentifier() {
        return identifier;
    }
    
    public boolean isUnavailable() {
		return unavailable;
	}
   
}
