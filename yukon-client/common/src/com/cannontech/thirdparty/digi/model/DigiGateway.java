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
}