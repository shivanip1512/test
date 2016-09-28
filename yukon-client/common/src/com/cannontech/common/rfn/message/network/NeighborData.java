package com.cannontech.common.rfn.message.network;

import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class NeighborData {
    private RfnIdentifier rfnIdentifier;
    private String serialNumber;
    private long neighborDataTimestamp;
    private String neighborAddress;
    private long lastCommTime;
    private long nextCommTime;
    private Set<NeighborFlagType> neighborFlags;
    private float neighborLinkCost;
    private int numSamples;
    private short etxBand;
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

    public long getLastCommTime() {
        return lastCommTime;
    }

    public void setLastCommTime(long lastCommTime) {
        this.lastCommTime = lastCommTime;
    }

    public long getNextCommTime() {
        return nextCommTime;
    }

    public void setNextCommTime(long nextCommTime) {
        this.nextCommTime = nextCommTime;
    }

    public Set<NeighborFlagType> getNeighborFlags() {
        return neighborFlags;
    }

    public void setNeighborFlags(Set<NeighborFlagType> neighborFlags) {
        this.neighborFlags = neighborFlags;
    }

    public float getNeighborLinkCost() {
        return neighborLinkCost;
    }

    public void setNeighborLinkCost(float neighborLinkCost) {
        this.neighborLinkCost = neighborLinkCost;
    }

    public int getNumSamples() {
        return numSamples;
    }

    public void setNumSamples(int numSamples) {
        this.numSamples = numSamples;
    }

    public short getEtxBand() {
        return etxBand;
    }

    public void setEtxBand(short etxBand) {
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
        result = prime * result + etxBand;
        result = prime * result + (int) (lastCommTime ^ (lastCommTime >>> 32));
        result = prime * result + ((linkPower == null) ? 0 : linkPower.hashCode());
        result = prime * result + ((linkRate == null) ? 0 : linkRate.hashCode());
        result = prime * result + ((neighborAddress == null) ? 0 : neighborAddress.hashCode());
        result = prime * result + (int) (neighborDataTimestamp ^ (neighborDataTimestamp >>> 32));
        result = prime * result + ((neighborFlags == null) ? 0 : neighborFlags.hashCode());
        result = prime * result + Float.floatToIntBits(neighborLinkCost);
        result = prime * result + (int) (nextCommTime ^ (nextCommTime >>> 32));
        result = prime * result + numSamples;
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
        if (etxBand != other.etxBand)
            return false;
        if (lastCommTime != other.lastCommTime)
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
        if (Float.floatToIntBits(neighborLinkCost) != Float.floatToIntBits(other.neighborLinkCost))
            return false;
        if (nextCommTime != other.nextCommTime)
            return false;
        if (numSamples != other.numSamples)
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
