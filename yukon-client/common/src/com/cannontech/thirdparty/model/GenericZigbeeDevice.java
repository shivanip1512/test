package com.cannontech.thirdparty.model;

import com.cannontech.common.pao.PaoIdentifier;

public class GenericZigbeeDevice implements ZigbeeDevice {

    private String zigbeeMacAddress;
    private PaoIdentifier paoIdentifier;
    
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

}
