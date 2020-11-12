package com.cannontech.web.dev;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.cannontech.dr.pxmw.model.PxMWRetrievalUrl;

public class SimulatedPxMWSettings {
    
    Map<PxMWRetrievalUrl, HttpStatus> selectedStatuses = new EnumMap<PxMWRetrievalUrl, HttpStatus>(PxMWRetrievalUrl.class);

    public SimulatedPxMWSettings() {
        for (PxMWRetrievalUrl url : PxMWRetrievalUrl.values()) {
            selectedStatuses.put(url, HttpStatus.OK);
        }
    }

    public Map<PxMWRetrievalUrl, HttpStatus> getSelectedStatuses() {
        return selectedStatuses;
    }

    public void setSelectedStatuses(Map<PxMWRetrievalUrl, HttpStatus> selectedStatuses) {
        this.selectedStatuses = selectedStatuses;
    }

}
