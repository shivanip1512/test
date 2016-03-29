package com.cannontech.simulators.message.response;

import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.SimulatorSettings;

public class RfnLcrSimulatorStatusResponse extends SimulatorResponseBase {

    private RfnDataSimulatorStatus allDevicesStatus;
    private RfnDataSimulatorStatus statusByRange;
    private SimulatorSettings settings;

    public RfnLcrSimulatorStatusResponse(RfnDataSimulatorStatus allDevicesStatus,
            RfnDataSimulatorStatus statusByRange, SimulatorSettings settings) {
        this.statusByRange = statusByRange;
        this.allDevicesStatus = allDevicesStatus;
        this.settings = settings;
    }

    public SimulatorSettings getSettings() {
        return settings;
    }

    public RfnDataSimulatorStatus getStatusByRange() {
        return statusByRange;
    }

    public RfnDataSimulatorStatus getAllDevicesStatus() {
        return allDevicesStatus;
    }

    public void setAllDevicesStatus(RfnDataSimulatorStatus allDevicesStatus) {
        this.allDevicesStatus = allDevicesStatus;
    }
}
