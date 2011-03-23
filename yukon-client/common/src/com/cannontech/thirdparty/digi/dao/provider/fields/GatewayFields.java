package com.cannontech.thirdparty.digi.dao.provider.fields;

import com.cannontech.common.pao.service.PaoTemplatePart;

public class GatewayFields  implements PaoTemplatePart {
    private String firmwareVersion;
    private String macAddress;
    private int digiId;
    
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
