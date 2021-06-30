package com.cannontech.simulators.message.request;

import java.util.Map;

import com.cannontech.dr.eatonCloud.model.EatonCloudRetrievalUrl;
import com.cannontech.simulators.SimulatorType;

public class EatonCloudSimulatorSettingsUpdateRequest implements SimulatorRequest{
    private Map<EatonCloudRetrievalUrl, Integer> statuses;
    
    public EatonCloudSimulatorSettingsUpdateRequest(Map<EatonCloudRetrievalUrl, Integer> statuses) {
        this.statuses = statuses;
    }
    
    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.EATON_CLOUD;
    }

    public Map<EatonCloudRetrievalUrl, Integer> getStatuses() {
        return statuses;
    }
}
