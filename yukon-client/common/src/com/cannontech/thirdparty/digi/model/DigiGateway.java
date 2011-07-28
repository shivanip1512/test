package com.cannontech.thirdparty.digi.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.thirdparty.model.ZigbeeDevice;

public class DigiGateway implements ZigbeeDevice {

    private PaoIdentifier paoIdentifier;
    private String firmwareVersion;
    private String macAddress;
    private int digiId = -1;
    private String name;
    
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
    public int getDigiId() {
        return digiId;
    }
    public void setDigiId(int digiId) {
        this.digiId = digiId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    public String getMacAddress() {
        return macAddress;
    }    

    @Override
    public String getZigbeeMacAddress() {
        return macAddress;
    }
    @Override
    public int getZigbeeDeviceId() {
        return paoIdentifier.getPaoId();
    }
    
    public int getPaoId() {
        return paoIdentifier.getPaoId();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + digiId;
        result = prime * result + ((firmwareVersion == null) ? 0 : firmwareVersion.hashCode());
        result = prime * result + ((macAddress == null) ? 0 : macAddress.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
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
        DigiGateway other = (DigiGateway) obj;
        if (digiId != other.digiId)
            return false;
        if (firmwareVersion == null) {
            if (other.firmwareVersion != null)
                return false;
        } else if (!firmwareVersion.equals(other.firmwareVersion))
            return false;
        if (macAddress == null) {
            if (other.macAddress != null)
                return false;
        } else if (!macAddress.equals(other.macAddress))
            return false;
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
        return true;
    }
    
}