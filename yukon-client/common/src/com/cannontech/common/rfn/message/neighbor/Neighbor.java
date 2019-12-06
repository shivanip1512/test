package com.cannontech.common.rfn.message.neighbor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class Neighbor implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // Map neighborRfnIdentifier to its NeighborData
    private Map<RfnIdentifier, NeighborData> neighborDataMap = new HashMap<>();

    public Map<RfnIdentifier, NeighborData> getNeighborDataMap() {
        return neighborDataMap;
    }

    public void setNeighborDataMap(Map<RfnIdentifier, NeighborData> neighborDataMap) {
        this.neighborDataMap = neighborDataMap;
    }
}
