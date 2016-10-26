package com.cannontech.common.rfn.message.network;

import java.io.Serializable;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class NeighborData implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private RfnIdentifier rfnIdentifier;
    private String serialNumber;
    private long neighborDataTimestamp;
    private String neighborAddress;
    private Long lastCommTime;
    private Long nextCommTime;
    private Set<NeighborFlagType> neighborFlags;
    private Float neighborLinkCost;
    private Integer numSamples;
    private Short etxBand;
    private String linkRate;
    private String linkPower;

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

    public long getNeighborDataTimestamp() {
        return neighborDataTimestamp;
    }

    public void setNeighborDataTimestamp(long neighborDataTimestamp) {
        this.neighborDataTimestamp = neighborDataTimestamp;
    }

    public String getNeighborAddress() {
        return neighborAddress;
    }

    public void setNeighborAddress(String neighborAddress) {
        this.neighborAddress = neighborAddress;
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

    public Set<NeighborFlagType> getNeighborFlags() {
        return neighborFlags;
    }

    public void setNeighborFlags(Set<NeighborFlagType> neighborFlags) {
        this.neighborFlags = neighborFlags;
    }

    public Float getNeighborLinkCost() {
        return neighborLinkCost;
    }

    public void setNeighborLinkCost(Float neighborLinkCost) {
        this.neighborLinkCost = neighborLinkCost;
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

    public String getLinkRate() {
        return linkRate;
    }

    public void setLinkRate(String linkRate) {
        this.linkRate = linkRate;
    }

    public String getLinkPower() {
        return linkPower;
    }

    public void setLinkPower(String linkPower) {
        this.linkPower = linkPower;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((etxBand == null) ? 0 : etxBand.hashCode());
        result = prime * result + ((lastCommTime == null) ? 0 : lastCommTime.hashCode());
        result = prime * result + ((linkPower == null) ? 0 : linkPower.hashCode());
        result = prime * result + ((linkRate == null) ? 0 : linkRate.hashCode());
        result = prime * result + ((neighborAddress == null) ? 0 : neighborAddress.hashCode());
        result = prime * result + (int) (neighborDataTimestamp ^ (neighborDataTimestamp >>> 32));
        result = prime * result + ((neighborFlags == null) ? 0 : neighborFlags.hashCode());
        result = prime * result + ((neighborLinkCost == null) ? 0 : neighborLinkCost.hashCode());
        result = prime * result + ((nextCommTime == null) ? 0 : nextCommTime.hashCode());
        result = prime * result + ((numSamples == null) ? 0 : numSamples.hashCode());
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
        result = prime * result + ((serialNumber == null) ? 0 : serialNumber.hashCode());
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
        if (linkPower == null) {
            if (other.linkPower != null)
                return false;
        } else if (!linkPower.equals(other.linkPower))
            return false;
        if (linkRate == null) {
            if (other.linkRate != null)
                return false;
        } else if (!linkRate.equals(other.linkRate))
            return false;
        if (neighborAddress == null) {
            if (other.neighborAddress != null)
                return false;
        } else if (!neighborAddress.equals(other.neighborAddress))
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
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        if (serialNumber == null) {
            if (other.serialNumber != null)
                return false;
        } else if (!serialNumber.equals(other.serialNumber))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("NeighborData [rfnIdentifier=%s, serialNumber=%s, neighborDataTimestamp=%s, neighborAddress=%s, lastCommTime=%s, nextCommTime=%s, neighborFlags=%s, neighborLinkCost=%s, numSamples=%s, etxBand=%s, linkRate=%s, linkPower=%s]",
                    rfnIdentifier,
                    serialNumber,
                    neighborDataTimestamp,
                    neighborAddress,
                    lastCommTime,
                    nextCommTime,
                    neighborFlags,
                    neighborLinkCost,
                    numSamples,
                    etxBand,
                    linkRate,
                    linkPower);
    }
}
