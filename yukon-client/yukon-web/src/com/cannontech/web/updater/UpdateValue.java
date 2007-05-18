package com.cannontech.web.updater;

public class UpdateValue {
    private String fullIdentifier;
    private String value;
    
    
    public UpdateValue(String identifier, String value) {
        this.fullIdentifier = identifier;
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getFullIdentifier() {
        return fullIdentifier;
    }
    public void setFullIdentifier(String identifier) {
        this.fullIdentifier = identifier;
    }

    // Eclipse generated...
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((fullIdentifier == null) ? 0 : fullIdentifier.hashCode());
        result = PRIME * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    // Eclipse generated...
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final UpdateValue other = (UpdateValue) obj;
        if (fullIdentifier == null) {
            if (other.fullIdentifier != null)
                return false;
        } else if (!fullIdentifier.equals(other.fullIdentifier))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
    
}
