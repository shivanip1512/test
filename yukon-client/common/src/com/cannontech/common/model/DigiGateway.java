package com.cannontech.common.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;

public class DigiGateway implements YukonDevice{

    private PaoIdentifier paoIdentifier;
    private String firmwareVersion;
    private String macAddress;
    private int digiId;
    
    public DigiGateway() {
        this.digiId = -1;
        this.firmwareVersion = "1.0";
    }

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
    public String getMacAddress() {
        return macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    public int getDigiId() {
        return digiId;
    }
    public void setDigiId(int digiId) {
        this.digiId = digiId;
    }
}
