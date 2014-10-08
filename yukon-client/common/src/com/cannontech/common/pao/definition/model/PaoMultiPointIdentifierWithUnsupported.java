package com.cannontech.common.pao.definition.model;

import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.YukonPao;


public class PaoMultiPointIdentifierWithUnsupported {

    private final List<PaoMultiPointIdentifier> supportedDevicesAndPoints;
    private final Set<YukonPao> unsupportedDevices;
    
    public PaoMultiPointIdentifierWithUnsupported(List<PaoMultiPointIdentifier> supportedDevicesAndPoints,
            Set<YukonPao> unsupportedDevices) {
        this.supportedDevicesAndPoints = supportedDevicesAndPoints;
        this.unsupportedDevices = unsupportedDevices;
    }

    public Set<YukonPao> getUnsupportedDevices() {
        return unsupportedDevices;
    }

    public List<PaoMultiPointIdentifier> getSupportedDevicesAndPoints() {
        return supportedDevicesAndPoints;
    }
}
