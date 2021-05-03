package com.cannontech.simulators.message.request;

import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.pxmw.model.PxMWVersion;
import com.cannontech.simulators.SimulatorType;

public class PxMWSimulatorDeviceCreateRequest implements SimulatorRequest{
    private static final long serialVersionUID = 1L;
    private PxMWVersion version;
    private PaoType paoType;
    private int devices;
    private boolean isComplete;

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.PX_MIDDLEWARE;
    }

    public PxMWSimulatorDeviceCreateRequest(PxMWVersion version, PaoType paoType, int devices) {
        this.paoType = paoType;
        this.devices = devices;
        this.version = version;
    }
    
    public PxMWSimulatorDeviceCreateRequest(PxMWVersion version, boolean isComplete) {
        this.version = version;
        this.isComplete = isComplete;
    }
    
    public PaoType getPaoType() {
        return paoType;
    }

    public int getDevices() {
        return devices;
    }

    public PxMWVersion getVersion() {
        return version;
    }

    public boolean isComplete() {
        return isComplete;
    }
}
