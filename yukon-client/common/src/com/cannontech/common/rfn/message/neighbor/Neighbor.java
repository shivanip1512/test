package com.cannontech.common.rfn.message.neighbor;

import java.io.Serializable;
import java.util.Map;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class Neighbor implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private RfnIdentifier myRfnIdentifier;

    // use Map to avoid duplicate neighbors
    // Map neighborRfnIdentifier to its NeighborData
    private Map<RfnIdentifier, NeighborData> neighborData;

    public RfnIdentifier getMyRfnIdentifier() {
        return myRfnIdentifier;
    }

    public void setMyRfnIdentifier(RfnIdentifier myRfnIdentifier) {
        this.myRfnIdentifier = myRfnIdentifier;
    }

    public Map<RfnIdentifier, NeighborData> getNeighborData() {
        return neighborData;
    }

    public void setNeighborData(Map<RfnIdentifier, NeighborData> neighborData) {
        this.neighborData = neighborData;
    }
}
