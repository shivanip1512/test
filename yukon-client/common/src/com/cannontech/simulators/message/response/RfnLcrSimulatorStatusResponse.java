package com.cannontech.simulators.message.response;

import com.cannontech.dr.rfn.model.RfnLcrDataSimulatorStatus;
import com.cannontech.dr.rfn.model.SimulatorSettings;

public class RfnLcrSimulatorStatusResponse extends SimulatorResponseBase {

    private RfnLcrDataSimulatorStatus allDevicesStatus;
    private RfnLcrDataSimulatorStatus statusByRange;
    private SimulatorSettings settings;

    public RfnLcrSimulatorStatusResponse(RfnLcrDataSimulatorStatus allDevicesStatus,
            RfnLcrDataSimulatorStatus statusByRange, SimulatorSettings settings) {
        this.statusByRange = statusByRange;
        this.allDevicesStatus = allDevicesStatus;
        this.settings = settings;
    }

    public SimulatorSettings getSettings() {
        return settings;
    }

    public RfnLcrDataSimulatorStatus getStatusByRange() {
        return statusByRange;
    }

    public RfnLcrDataSimulatorStatus getAllDevicesStatus() {
        return allDevicesStatus;
    }

    public void setAllDevicesStatus(RfnLcrDataSimulatorStatus allDevicesStatus) {
        this.allDevicesStatus = allDevicesStatus;
    }
}
