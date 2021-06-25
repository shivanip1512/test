package com.cannontech.web.dev;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.cannontech.dr.eatonCloud.model.EatonCloudRetrievalUrl;

public class SimulatedEatonCloudSettings {
    
    Map<EatonCloudRetrievalUrl, HttpStatus> selectedStatuses = new EnumMap<EatonCloudRetrievalUrl, HttpStatus>(EatonCloudRetrievalUrl.class);

    public SimulatedEatonCloudSettings() {
        for (EatonCloudRetrievalUrl url : EatonCloudRetrievalUrl.values()) {
            selectedStatuses.put(url, HttpStatus.OK);
        }
    }

    public Map<EatonCloudRetrievalUrl, HttpStatus> getSelectedStatuses() {
        return selectedStatuses;
    }

    public void setSelectedStatuses(Map<EatonCloudRetrievalUrl, HttpStatus> selectedStatuses) {
        this.selectedStatuses = selectedStatuses;
    }

}
