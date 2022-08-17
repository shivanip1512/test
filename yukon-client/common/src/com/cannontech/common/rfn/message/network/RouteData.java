package com.cannontech.common.rfn.message.network;

import java.io.Serializable;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class RouteData implements Serializable {

    private static final long serialVersionUID = 1L;

    private RfnIdentifier rfnIdentifier;
    private String serialNumber;
    private Long routeDataTimestamp;
    private String destinationAddress;
    private String nextHopAddress;
    private Short totalCost;
    private Short hopCount;
    private Long routeTimeout;
    private Set<RouteFlagType> routeFlags;
    private Short routeColor;

    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Long getRouteDataTimestamp() {
        return routeDataTimestamp;
    }

    public void setRouteDataTimestamp(Long routeDataTimestamp) {
        this.routeDataTimestamp = routeDataTimestamp;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getNextHopAddress() {
        return nextHopAddress;
    }

    public void setNextHopAddress(String nextHopAddress) {
        this.nextHopAddress = nextHopAddress;
    }

    public Short getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Short totalCost) {
        this.totalCost = totalCost;
    }

    public Short getHopCount() {
        return hopCount;
    }

    public void setHopCount(Short hopCount) {
        this.hopCount = hopCount;
    }

    public Long getRouteTimeout() {
        return routeTimeout;
    }

    public void setRouteTimeout(Long routeTimeout) {
        this.routeTimeout = routeTimeout;
    }

    public Set<RouteFlagType> getRouteFlags() {
        return routeFlags;
    }

    public void setRouteFlags(Set<RouteFlagType> routeFlags) {
        this.routeFlags = routeFlags;
    }

    public Short getRouteColor() {
        return routeColor;
    }

    public void setRouteColor(Short routeColor) {
        this.routeColor = routeColor;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((destinationAddress == null) ? 0 : destinationAddress.hashCode());
        result = prime * result + ((hopCount == null) ? 0 : hopCount.hashCode());
        result = prime * result + ((nextHopAddress == null) ? 0 : nextHopAddress.hashCode());
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
        result = prime * result + ((routeColor == null) ? 0 : routeColor.hashCode());
        result = prime * result + ((routeDataTimestamp == null) ? 0 : routeDataTimestamp.hashCode());
        result = prime * result + ((routeFlags == null) ? 0 : routeFlags.hashCode());
        result = prime * result + ((routeTimeout == null) ? 0 : routeTimeout.hashCode());
        result = prime * result + ((serialNumber == null) ? 0 : serialNumber.hashCode());
        result = prime * result + ((totalCost == null) ? 0 : totalCost.hashCode());
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
        RouteData other = (RouteData) obj;
        if (destinationAddress == null) {
            if (other.destinationAddress != null)
                return false;
        } else if (!destinationAddress.equals(other.destinationAddress))
            return false;
        if (hopCount == null) {
            if (other.hopCount != null)
                return false;
        } else if (!hopCount.equals(other.hopCount))
            return false;
        if (nextHopAddress == null) {
            if (other.nextHopAddress != null)
                return false;
        } else if (!nextHopAddress.equals(other.nextHopAddress))
            return false;
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        if (routeColor == null) {
            if (other.routeColor != null)
                return false;
        } else if (!routeColor.equals(other.routeColor))
            return false;
        if (routeDataTimestamp == null) {
            if (other.routeDataTimestamp != null)
                return false;
        } else if (!routeDataTimestamp.equals(other.routeDataTimestamp))
            return false;
        if (routeFlags == null) {
            if (other.routeFlags != null)
                return false;
        } else if (!routeFlags.equals(other.routeFlags))
            return false;
        if (routeTimeout == null) {
            if (other.routeTimeout != null)
                return false;
        } else if (!routeTimeout.equals(other.routeTimeout))
            return false;
        if (serialNumber == null) {
            if (other.serialNumber != null)
                return false;
        } else if (!serialNumber.equals(other.serialNumber))
            return false;
        if (totalCost == null) {
            if (other.totalCost != null)
                return false;
        } else if (!totalCost.equals(other.totalCost))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("RouteData [rfnIdentifier=%s, serialNumber=%s, routeDataTimestamp=%s, destinationAddress=%s, nextHopAddress=%s, totalCost=%s, hopCount=%s, routeTimeout=%s, routeFlags=%s, routeColor=%s]",
                    rfnIdentifier,
                    serialNumber,
                    routeDataTimestamp,
                    destinationAddress,
                    nextHopAddress,
                    totalCost,
                    hopCount,
                    routeTimeout,
                    routeFlags,
                    routeColor);
    }
}