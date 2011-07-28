package com.cannontech.thirdparty.model;

import com.cannontech.common.pao.PaoIdentifier;

public class GenericZigbeeDevice implements ZigbeeDevice {

    private String zigbeeMacAddress;
    private PaoIdentifier paoIdentifier;
    private String name; 
    
    public void setZigbeeMacAddress(String zigbeeMacAddress) {
        this.zigbeeMacAddress = zigbeeMacAddress;
    }

    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }

    @Override
    public int getZigbeeDeviceId() {
        return paoIdentifier.getPaoId();
    }

    @Override
    public String getZigbeeMacAddress() {
        return zigbeeMacAddress;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
        result = prime * result + ((zigbeeMacAddress == null) ? 0 : zigbeeMacAddress.hashCode());
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
        GenericZigbeeDevice other = (GenericZigbeeDevice) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (paoIdentifier == null) {
            if (other.paoIdentifier != null)
                return false;
        } else if (!paoIdentifier.equals(other.paoIdentifier))
            return false;
        if (zigbeeMacAddress == null) {
            if (other.zigbeeMacAddress != null)
                return false;
        } else if (!zigbeeMacAddress.equals(other.zigbeeMacAddress))
            return false;
        return true;
    }

}
