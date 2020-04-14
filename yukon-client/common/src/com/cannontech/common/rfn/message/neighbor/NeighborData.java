package com.cannontech.common.rfn.message.neighbor;

import java.io.Serializable;
import java.util.Set;

public class NeighborData implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private long neighborDataTimestamp;

    private String neighborMacAddress; 
    
    private Float neighborLinkCost;
       
    private Long lastCommTime;
    
    private Long nextCommTime;
    
    private Set<NeighborFlag> neighborFlags;

    private Integer numSamples;
    
    private Short etxBand;

    private LinkRate currentLinkRate; // Use Enum instead of ID (tinyint) or String
    
    private LinkPower currentLinkPower; // Use Enum instead of ID (tinyint) or String

    public long getNeighborDataTimestamp() {
        return neighborDataTimestamp;
    }

    public void setNeighborDataTimestamp(long neighborDataTimestamp) {
        this.neighborDataTimestamp = neighborDataTimestamp;
    }

    public String getNeighborMacAddress() {
        return neighborMacAddress;
    }

    public void setNeighborMacAddress(String neighborMacAddress) {
        this.neighborMacAddress = neighborMacAddress;
    }

    public Float getNeighborLinkCost() {
        return neighborLinkCost;
    }

    public void setNeighborLinkCost(Float neighborLinkCost) {
        this.neighborLinkCost = neighborLinkCost;
    }

    public Long getLastCommTime() {
        return lastCommTime;
    }

    public void setLastCommTime(Long lastCommTime) {
        this.lastCommTime = lastCommTime;
    }

    public Long getNextCommTime() {
        return nextCommTime;
    }

    public void setNextCommTime(Long nextCommTime) {
        this.nextCommTime = nextCommTime;
    }

    public Set<NeighborFlag> getNeighborFlags() {
        return neighborFlags;
    }

    public void setNeighborFlags(Set<NeighborFlag> neighborFlags) {
        this.neighborFlags = neighborFlags;
    }

    public Integer getNumSamples() {
        return numSamples;
    }

    public void setNumSamples(Integer numSamples) {
        this.numSamples = numSamples;
    }

    public Short getEtxBand() {
        return etxBand;
    }

    public void setEtxBand(Short etxBand) {
        this.etxBand = etxBand;
    }

    public LinkRate getCurrentLinkRate() {
        return currentLinkRate;
    }

    public void setCurrentLinkRate(LinkRate currentLinkRate) {
        this.currentLinkRate = currentLinkRate;
    }

    public LinkPower getCurrentLinkPower() {
        return currentLinkPower;
    }

    public void setCurrentLinkPower(LinkPower currentLinkPower) {
        this.currentLinkPower = currentLinkPower;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((currentLinkPower == null) ? 0 : currentLinkPower.hashCode());
        result = prime * result + ((currentLinkRate == null) ? 0 : currentLinkRate.hashCode());
        result = prime * result + ((etxBand == null) ? 0 : etxBand.hashCode());
        result = prime * result + ((lastCommTime == null) ? 0 : lastCommTime.hashCode());
        result = prime * result + (int) (neighborDataTimestamp ^ (neighborDataTimestamp >>> 32));
        result = prime * result + ((neighborFlags == null) ? 0 : neighborFlags.hashCode());
        result = prime * result + ((neighborLinkCost == null) ? 0 : neighborLinkCost.hashCode());
        result =
            prime * result + ((neighborMacAddress == null) ? 0 : neighborMacAddress.hashCode());
        result = prime * result + ((nextCommTime == null) ? 0 : nextCommTime.hashCode());
        result = prime * result + ((numSamples == null) ? 0 : numSamples.hashCode());
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
        NeighborData other = (NeighborData) obj;
        if (currentLinkPower != other.currentLinkPower)
            return false;
        if (currentLinkRate != other.currentLinkRate)
            return false;
        if (etxBand == null) {
            if (other.etxBand != null)
                return false;
        } else if (!etxBand.equals(other.etxBand))
            return false;
        if (lastCommTime == null) {
            if (other.lastCommTime != null)
                return false;
        } else if (!lastCommTime.equals(other.lastCommTime))
            return false;
        if (neighborDataTimestamp != other.neighborDataTimestamp)
            return false;
        if (neighborFlags == null) {
            if (other.neighborFlags != null)
                return false;
        } else if (!neighborFlags.equals(other.neighborFlags))
            return false;
        if (neighborLinkCost == null) {
            if (other.neighborLinkCost != null)
                return false;
        } else if (!neighborLinkCost.equals(other.neighborLinkCost))
            return false;
        if (neighborMacAddress == null) {
            if (other.neighborMacAddress != null)
                return false;
        } else if (!neighborMacAddress.equals(other.neighborMacAddress))
            return false;
        if (nextCommTime == null) {
            if (other.nextCommTime != null)
                return false;
        } else if (!nextCommTime.equals(other.nextCommTime))
            return false;
        if (numSamples == null) {
            if (other.numSamples != null)
                return false;
        } else if (!numSamples.equals(other.numSamples))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("NeighborData [neighborDataTimestamp=%s, neighborMacAddress=%s, neighborLinkCost=%s, lastCommTime=%s, nextCommTime=%s, neighborFlags=%s, numSamples=%s, etxBand=%s, currentLinkRate=%s, currentLinkPower=%s]",
                    neighborDataTimestamp,
                    neighborMacAddress,
                    neighborLinkCost,
                    lastCommTime,
                    nextCommTime,
                    neighborFlags,
                    numSamples,
                    etxBand,
                    currentLinkRate,
                    currentLinkPower);
    }
}
