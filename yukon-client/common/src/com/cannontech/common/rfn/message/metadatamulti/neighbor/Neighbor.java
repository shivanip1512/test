package com.cannontech.common.rfn.message.metadatamulti.neighbor;

import java.io.Serializable;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class Neighbor implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private RfnIdentifier myRfnIdentifier;

    private RfnIdentifier neighborRfnIdentifier;
    
    private String neighborMacAddress; 
        // neighborMacAddress is already part of NeighborData
        // neighborMacAddress can also be queried by RfnMetadataMulti.NodeData.
    
    private String neighborSerialNumber; // Node S/N or Gateway S/N
        //    Node neighborSerialNumber can also be queried by RfnMetadataMulti.NodeData.
        // Gateway neighborSerialNumber can be retrieved from neighborRfnIdentifier

    private long neighborDataTimestamp;
    
    private Float neighborLinkCost;
       
    private Long lastCommTime;
    
    private Long nextCommTime;
    
    private Set<NeighborFlag> neighborFlags;

    private Integer numSamples;
    
    private Short etxBand;

    private LinkRate currentLinkRate; // Use Enum instead of ID (tinyint) or String
    
    private LinkPower currentLinkPower; // Use Enum instead of ID (tinyint) or String

    public RfnIdentifier getMyRfnIdentifier() {
        return myRfnIdentifier;
    }

    public void setMyRfnIdentifier(RfnIdentifier myRfnIdentifier) {
        this.myRfnIdentifier = myRfnIdentifier;
    }

    public RfnIdentifier getNeighborRfnIdentifier() {
        return neighborRfnIdentifier;
    }

    public void setNeighborRfnIdentifier(RfnIdentifier neighborRfnIdentifier) {
        this.neighborRfnIdentifier = neighborRfnIdentifier;
    }

    public String getNeighborMacAddress() {
        return neighborMacAddress;
    }

    public void setNeighborMacAddress(String neighborMacAddress) {
        this.neighborMacAddress = neighborMacAddress;
    }

    public String getNeighborSerialNumber() {
        return neighborSerialNumber;
    }

    public void setNeighborSerialNumber(String neighborSerialNumber) {
        this.neighborSerialNumber = neighborSerialNumber;
    }

    public long getNeighborDataTimestamp() {
        return neighborDataTimestamp;
    }

    public void setNeighborDataTimestamp(long neighborDataTimestamp) {
        this.neighborDataTimestamp = neighborDataTimestamp;
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
        result = prime * result + ((myRfnIdentifier == null) ? 0 : myRfnIdentifier.hashCode());
        result = prime * result + (int) (neighborDataTimestamp ^ (neighborDataTimestamp >>> 32));
        result = prime * result + ((neighborFlags == null) ? 0 : neighborFlags.hashCode());
        result = prime * result + ((neighborLinkCost == null) ? 0 : neighborLinkCost.hashCode());
        result =
            prime * result + ((neighborMacAddress == null) ? 0 : neighborMacAddress.hashCode());
        result = prime * result
            + ((neighborRfnIdentifier == null) ? 0 : neighborRfnIdentifier.hashCode());
        result =
            prime * result + ((neighborSerialNumber == null) ? 0 : neighborSerialNumber.hashCode());
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
        Neighbor other = (Neighbor) obj;
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
        if (myRfnIdentifier == null) {
            if (other.myRfnIdentifier != null)
                return false;
        } else if (!myRfnIdentifier.equals(other.myRfnIdentifier))
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
        if (neighborRfnIdentifier == null) {
            if (other.neighborRfnIdentifier != null)
                return false;
        } else if (!neighborRfnIdentifier.equals(other.neighborRfnIdentifier))
            return false;
        if (neighborSerialNumber == null) {
            if (other.neighborSerialNumber != null)
                return false;
        } else if (!neighborSerialNumber.equals(other.neighborSerialNumber))
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
            .format("Neighbor [myRfnIdentifier=%s, neighborRfnIdentifier=%s, neighborMacAddress=%s, neighborSerialNumber=%s, neighborDataTimestamp=%s, neighborLinkCost=%s, lastCommTime=%s, nextCommTime=%s, neighborFlags=%s, numSamples=%s, etxBand=%s, currentLinkRate=%s, currentLinkPower=%s]",
                    myRfnIdentifier,
                    neighborRfnIdentifier,
                    neighborMacAddress,
                    neighborSerialNumber,
                    neighborDataTimestamp,
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
