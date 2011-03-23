package com.cannontech.thirdparty.digi.dao.provider.fields;

import com.cannontech.common.pao.service.PaoTemplatePart;

public class UtilityProZigbeeFields implements PaoTemplatePart {
    private String installCode;
    private String macAddress;
    
    public String getInstallCode() {
        return installCode;
    }

    public void setInstallCode(String installCode) {
        this.installCode = installCode;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
