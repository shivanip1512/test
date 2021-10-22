package com.cannontech.simulators.message.request;

import java.util.Map;

import com.cannontech.dr.eatonCloud.model.EatonCloudRetrievalUrl;
import com.cannontech.simulators.SimulatorType;

public class EatonCloudSimulatorSettingsUpdateRequest implements SimulatorRequest{
    private static final long serialVersionUID = 1L;
    private Map<EatonCloudRetrievalUrl, Integer> statuses;
    private Map<EatonCloudRetrievalUrl, Integer> successPercentages;
        
    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.EATON_CLOUD;
    }

    public Map<EatonCloudRetrievalUrl, Integer> getStatuses() {
        return statuses;
    }

    public void setStatuses(Map<EatonCloudRetrievalUrl, Integer> statuses) {
        this.statuses = statuses;
    }

    public Map<EatonCloudRetrievalUrl, Integer> getSuccessPercentages() {
        return successPercentages;
    }

    public void setSuccessPercentages(Map<EatonCloudRetrievalUrl, Integer> successPercentages) {
        this.successPercentages = successPercentages;
    }
}
