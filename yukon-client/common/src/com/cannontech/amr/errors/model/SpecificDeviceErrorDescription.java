package com.cannontech.amr.errors.model;

import com.cannontech.amr.errors.model.DeviceErrorDescription;

public class SpecificDeviceErrorDescription {

    private final DeviceErrorDescription errorDescription;
    private final String resultString;

    public SpecificDeviceErrorDescription(DeviceErrorDescription errorDescription,
                                          String resultString) {
                                            this.errorDescription = errorDescription;
                                            this.resultString = resultString;
    }

    public Integer getErrorCode() {
        return errorDescription.getErrorCode();
    }

    public String getDescription() {
        return errorDescription.getDescription();
    }

    public String getTroubleshooting() {
        return errorDescription.getTroubleshooting();
    }

    public String getCategory() {
        return errorDescription.getCategory();
    }

    public String getPorter() {
        return resultString;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((errorDescription == null) ? 0 : errorDescription.hashCode());
        result = prime * result + ((resultString == null) ? 0 : resultString.hashCode());
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
        SpecificDeviceErrorDescription other = (SpecificDeviceErrorDescription) obj;
        if (errorDescription == null) {
            if (other.errorDescription != null)
                return false;
        } else if (!errorDescription.equals(other.errorDescription))
            return false;
        if (resultString == null) {
            if (other.resultString != null)
                return false;
        } else if (!resultString.equals(other.resultString))
            return false;
        return true;
    }

}
