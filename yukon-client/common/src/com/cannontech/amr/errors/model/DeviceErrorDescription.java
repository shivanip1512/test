package com.cannontech.amr.errors.model;


public class DeviceErrorDescription {

    private Integer errorCode;
    private String category;
    private String porter;
    private String description;
    private String troubleshooting;
    
    public DeviceErrorDescription(Integer errorCode, String category, String porter,
                                  String description, String troubleshooting) {
        super();
        this.errorCode = errorCode;
        this.category = category;
        this.porter = porter;
        this.description = description;
        this.troubleshooting = troubleshooting;
    }
    /**
     * Returns the error code or null if this is the default.
     * @return
     */
    public Integer getErrorCode() {
        return errorCode;
    }

    /**
     * Returns a short description of the error.
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns an HTML fragment with the troubleshooting steps.
     * @return
     */
    public String getTroubleshooting() {
        return troubleshooting;
    }

    public String getCategory() {
		return category;
	}

	public String getPorter() {
		return porter;
	}

    @Override
    public String toString() {
        return description + "(" + porter + " -- " + errorCode + ")";
    }

	@Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((errorCode == null) ? 0 : errorCode.hashCode());
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
        final DeviceErrorDescription other = (DeviceErrorDescription) obj;
        if (errorCode == null) {
            if (other.errorCode != null)
                return false;
        } else if (!errorCode.equals(other.errorCode))
            return false;
        return true;
    }
    
}
