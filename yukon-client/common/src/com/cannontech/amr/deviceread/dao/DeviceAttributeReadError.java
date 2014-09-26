package com.cannontech.amr.deviceread.dao;

import org.springframework.context.MessageSourceResolvable;

public class DeviceAttributeReadError {
	private DeviceAttributeReadErrorType type;
    private MessageSourceResolvable summary;
    private MessageSourceResolvable detail;
    
    public DeviceAttributeReadError(DeviceAttributeReadErrorType type,
            MessageSourceResolvable summary, MessageSourceResolvable detail) {
        this.type = type;
        this.summary = summary;
        this.detail = detail;
    }

    public DeviceAttributeReadError(DeviceAttributeReadErrorType type,
            MessageSourceResolvable summary) {
        this.type = type;
        this.summary = summary;
    }
    
    public DeviceAttributeReadErrorType getType() {
        return type;
    }
    
    public MessageSourceResolvable getSummary() {
        return summary;
    }
    
    public MessageSourceResolvable getDetail() {
        return detail;
    }
    
    @Override
    public String toString() {
        return type.name() + ": " + summary + " (" + detail + ")";
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((detail == null) ? 0 : detail.hashCode());
		result = prime * result + ((summary == null) ? 0 : summary.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		DeviceAttributeReadError other = (DeviceAttributeReadError) obj;
		if (detail == null) {
			if (other.detail != null)
				return false;
		} else if (!detail.equals(other.detail))
			return false;
		if (summary == null) {
			if (other.summary != null)
				return false;
		} else if (!summary.equals(other.summary))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
