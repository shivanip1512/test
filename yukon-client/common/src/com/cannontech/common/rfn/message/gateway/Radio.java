package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

public class Radio implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private RadioType type;
    private String macAddress;
    private long timestamp; // The moment NM discovered radio on gateway
    
    public RadioType getType() {
        return type;
    }
    
    public void setType(RadioType type) {
        this.type = type;
    }
    
    public String getMacAddress() {
        return macAddress;
    }
    
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((macAddress == null) ? 0 : macAddress.hashCode());
        result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
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
        Radio other = (Radio) obj;
        if (macAddress == null) {
            if (other.macAddress != null)
                return false;
        } else if (!macAddress.equals(other.macAddress))
            return false;
        if (timestamp != other.timestamp)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("Radio [type=%s, macAddress=%s, timestamp=%s]", type, macAddress, timestamp);
    }
    
}