package com.cannontech.web.dev;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.cannontech.dr.eatonCloud.model.EatonCloudRetrievalUrl;

public class SimulatedEatonCloudSettings {
   
    Map<EatonCloudRetrievalUrl, HttpStatus> selectedStatuses = new EnumMap<EatonCloudRetrievalUrl, HttpStatus>(EatonCloudRetrievalUrl.class);
    private Map<EatonCloudRetrievalUrl, Integer> successPercentages = new HashMap<>();
    
    public SimulatedEatonCloudSettings() {
        for (EatonCloudRetrievalUrl url : EatonCloudRetrievalUrl.values()) {
            selectedStatuses.put(url, HttpStatus.OK);
        }
        for (EatonCloudRetrievalUrl url : EatonCloudRetrievalUrl.values()) {
        	if (url.displaySuccessPercentage()) {
        		successPercentages.put(url, 100);
        	}
        }
    }

    public Map<EatonCloudRetrievalUrl, HttpStatus> getSelectedStatuses() {
        return selectedStatuses;
    }

    public void setSelectedStatuses(Map<EatonCloudRetrievalUrl, HttpStatus> selectedStatuses) {
        this.selectedStatuses = selectedStatuses;
    }

    public Map<EatonCloudRetrievalUrl, Integer> getSuccessPercentages() {
        return successPercentages;
    }

    public void setSuccessPercentages(Map<EatonCloudRetrievalUrl, Integer> successPercentages) {
        this.successPercentages = successPercentages;
    }
    
   
}
