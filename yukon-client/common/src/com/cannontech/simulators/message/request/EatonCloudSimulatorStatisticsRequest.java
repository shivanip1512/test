package com.cannontech.simulators.message.request;

import java.io.Serializable;

import com.cannontech.dr.eatonCloud.model.EatonCloudVersion;
import com.cannontech.simulators.SimulatorType;

/**
 * Request to simulator to get statistics
 */
public class EatonCloudSimulatorStatisticsRequest implements SimulatorRequest {
    private static final long serialVersionUID = 1L;
    private EatonCloudVersion version;
    public EatonCloudSimulatorStatisticsRequest(EatonCloudVersion version) {
        this.version = version;
    }
    public EatonCloudVersion getVersion() {
        return version;
    }
    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.EATON_CLOUD;
    }
}
