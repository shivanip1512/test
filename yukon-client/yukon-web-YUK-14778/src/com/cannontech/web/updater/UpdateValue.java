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
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + (unavailable ? 1231 : 1237);
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
        UpdateValue other = (UpdateValue) obj;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        if (unavailable != other.unavailable)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
}
