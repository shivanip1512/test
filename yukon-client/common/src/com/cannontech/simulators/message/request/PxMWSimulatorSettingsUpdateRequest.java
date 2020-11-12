package com.cannontech.simulators.message.request;

import java.util.Map;

import com.cannontech.dr.pxmw.model.PxMWRetrievalUrl;
import com.cannontech.simulators.SimulatorType;

public class PxMWSimulatorSettingsUpdateRequest implements SimulatorRequest{
    private Map<PxMWRetrievalUrl, Integer> statuses;
    
    public PxMWSimulatorSettingsUpdateRequest(Map<PxMWRetrievalUrl, Integer> statuses) {
        this.statuses = statuses;
    }
    
    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.PxMW;
    }

    public Map<PxMWRetrievalUrl, Integer> getStatuses() {
        return statuses;
    }
}
