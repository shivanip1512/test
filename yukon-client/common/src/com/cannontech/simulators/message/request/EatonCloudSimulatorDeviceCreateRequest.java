package com.cannontech.simulators.message.request;

import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.eatonCloud.model.EatonCloudVersion;
import com.cannontech.simulators.SimulatorType;

public class EatonCloudSimulatorDeviceCreateRequest implements SimulatorRequest{
    private static final long serialVersionUID = 1L;
    private EatonCloudVersion version;
    private PaoType paoType;
    private int devices;
    private boolean isComplete;

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.EATON_CLOUD;
    }

    public EatonCloudSimulatorDeviceCreateRequest(EatonCloudVersion version, PaoType paoType, int devices) {
        this.paoType = paoType;
        this.devices = devices;
        this.version = version;
    }
    
    public EatonCloudSimulatorDeviceCreateRequest(EatonCloudVersion version, boolean isComplete) {
        this.version = version;
        this.isComplete = isComplete;
    }
    
    public PaoType getPaoType() {
        return paoType;
    }

    public int getDevices() {
        return devices;
    }

    public EatonCloudVersion getVersion() {
        return version;
    }

    public boolean isComplete() {
        return isComplete;
    }
}
