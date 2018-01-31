package com.cannontech.amr.errors.model;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.common.i18n.MessageSourceAccessor;

public class DeviceErrorDescription {
    private final DeviceError error;
    private final String porter;
    private final String description;
    private final String troubleshooting;
    private final String category;

    public DeviceErrorDescription(DeviceError error, String porter, String description, String troubleshooting) {
        this.error = error;
        this.porter = porter;
        this.description = description;
        this.troubleshooting = troubleshooting;
        this.category = error.getCategory().getDefaultCategory();
    }

    public DeviceErrorDescription(DeviceError error, MessageSourceAccessor accessor) {
        this.error = error;
        this.porter = accessor.getMessage(error.getPorterResolvable());
        this.description = accessor.getMessage(error.getDescriptionResolvable());
        this.troubleshooting = accessor.getMessage(error.getTroubleshootingResolvable());
        this.category = accessor.getMessage(error.getCategory());
    }

    /**
     * Get error code or null if this is the default.
     */
    public Integer getErrorCode() {
        return getError().getCode();
    }

    /**
     * Get a short description of the error.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get an HTML fragment with the troubleshooting steps.
     */
    public String getTroubleshooting() {
        return troubleshooting;
    }

    public String getPorter() {
        return porter;
    }

    public DeviceError getError() {
        return error;
    }
    @Override
    public String toString() {
        return description + "(" + porter + " -- " + getError() + ")";
    }

    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((getError() == null) ? 0 : getError().hashCode());
        return result;
    }
    
    public String getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DeviceErrorDescription other = (DeviceErrorDescription) obj;
        if (getError() == null) {
            if (other.getError() != null)
                return false;
        } else if (!getError().equals(other.getError()))
            return false;
        return true;
    }
}
