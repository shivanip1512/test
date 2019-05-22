package com.cannontech.common.rfn.message.metadatamulti.route;

import java.io.Serializable;

public class Route implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Short hopCount;

    // private String nextHopAddress;
    
    // private String destinationAddress;
    
    // private long dataTimestamp;
    
    // private Short totalCost;
    
    // private Short routeColor;
    
    // private Long routeTimeout; // Note: All values are nulls in the lab server.
    
    public Short getHopCount() {
        return hopCount;
    }

    public void setHopCount(Short hopCount) {
        this.hopCount = hopCount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hopCount == null) ? 0 : hopCount.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Route other = (Route) obj;
        if (hopCount == null) {
            if (other.hopCount != null)
                return false;
        } else if (!hopCount.equals(other.hopCount))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RouteData [hopCount=%s]", hopCount);
    }
    
}
