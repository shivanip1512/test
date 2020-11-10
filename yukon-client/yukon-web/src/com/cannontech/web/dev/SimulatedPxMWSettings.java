package com.cannontech.web.dev;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

public class SimulatedPxMWSettings {
    
    Map<RetrievalUrl, HttpStatus> selectedStatuses = new EnumMap<RetrievalUrl, HttpStatus>(RetrievalUrl.class);
    
    public SimulatedPxMWSettings() {
        for (RetrievalUrl url : RetrievalUrl.values()) {
            getSelectedStatuses().put(url, url.getStatuses().get(0));
        }
    }

    public Map<RetrievalUrl, HttpStatus> getSelectedStatuses() {
        return selectedStatuses;
    }

    public void setSelectedStatuses(Map<RetrievalUrl, HttpStatus> selectedStatuses) {
        this.selectedStatuses = selectedStatuses;
    }

}
