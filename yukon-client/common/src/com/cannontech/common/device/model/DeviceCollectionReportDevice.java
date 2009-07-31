package com.cannontech.common.device.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;

/**
 * This class is more meter-specific than it should be, but 
 * I want to preserve existing functionality.
 */
public class DeviceCollectionReportDevice implements YukonDevice {
    private PaoIdentifier paoIdentifier;
    private String name = "";
    private String meterNumber = "";
    private String address = "";
    private String route = "";
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getType() {
        return paoIdentifier.getPaoType().getPaoTypeName();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }

    public DeviceCollectionReportDevice(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }
    
    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }

}
