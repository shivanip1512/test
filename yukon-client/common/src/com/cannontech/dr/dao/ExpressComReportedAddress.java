package com.cannontech.dr.dao;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class ExpressComReportedAddress extends LmReportedAddress implements Serializable, Cloneable {

    private int spid;
    private int geo;
    private int substation;
    private int feeder;
    private int zip;
    private int uda;
    private int required;
    private Set<ExpressComReportedAddressRelay> relays;
    
    public int getSpid() {
        return spid;
    }
    
    public void setSpid(int spid) {
        this.spid = spid;
    }
    
    public int getGeo() {
        return geo;
    }
    
    public void setGeo(int geo) {
        this.geo = geo;
    }
    
    public int getSubstation() {
        return substation;
    }
    
    public void setSubstation(int substation) {
        this.substation = substation;
    }
    
    public int getFeeder() {
        return feeder;
    }
    
    public void setFeeder(int feeder) {
        this.feeder = feeder;
    }
    
    public int getZip() {
        return zip;
    }
    
    public void setZip(int zip) {
        this.zip = zip;
    }
    
    public int getUda() {
        return uda;
    }
    
    public void setUda(int uda) {
        this.uda = uda;
    }
    
    public int getRequired() {
        return required;
    }
    
    public void setRequired(int required) {
        this.required = required;
    }
    
    public Set<ExpressComReportedAddressRelay> getRelays() {
        return relays;
    }
    
    public Set<ExpressComReportedAddressRelay> getRelaysSorted() {
        Set<ExpressComReportedAddressRelay> sortedRelays = new TreeSet<>(ExpressComReportedAddressRelay.BY_RELAY_NUMBER);
        sortedRelays.addAll(relays);
        return sortedRelays;
    }
    
    public void setRelays(Set<ExpressComReportedAddressRelay> relays) {
        this.relays = relays;
    }

    /**
     * Searches for relays by number. If not found null is returned.
     * 
     * @return null if no relay found
     */
    public ExpressComReportedAddressRelay getRelayByNumber(int relayNumber) {
        for (ExpressComReportedAddressRelay relay : relays) {
            if (relay.getRelayNumber() == relayNumber) {
                return relay;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("ExpressComReportedAddress [changeId=%s, deviceId=%s, " 
                + "timestamp=%s, spid=%s, geo=%s, substation=%s, feeder=%s, zip=%s, uda=%s, required=%s, relays=%s]",
                    changeId,
                    deviceId,
                    timestamp,
                    spid,
                    geo,
                    substation,
                    feeder,
                    zip,
                    uda,
                    required,
                    relays);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + changeId;
        result = prime * result + deviceId;
        result = prime * result + feeder;
        result = prime * result + geo;
        result = prime * result + ((relays == null) ? 0 : relays.hashCode());
        result = prime * result + required;
        result = prime * result + spid;
        result = prime * result + substation;
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + uda;
        result = prime * result + zip;
        return result;
    }

    /**
     * This methods performs normal equivalency checks
     * except that the checks for all fields besides {@link #changeId} and {@link #timestamp}
     * have been moved to {@link #isEquivalent(ExpressComReportedAddress)} and this method
     * calls that one.  Changing {@link #isEquivalent(ExpressComReportedAddress)} will change
     * the behavior of this method.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ExpressComReportedAddress other = (ExpressComReportedAddress) obj;
        if (changeId != other.changeId) {
            return false;
        }
        if (timestamp == null) {
            if (other.timestamp != null) {
                return false;
            }
        } else if (!timestamp.equals(other.timestamp)) {
            return false;
        }
        if (!isEquivalent(other)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Used to decide if the currently stored address is the same
     * as this one.  Addresses should only be persisted to the db
     * when they have changed since this will get reported every 
     * interval.  
     * 
     *  The main difference between this and {@link #equals(Object)}
     *  is that it ignores {@link #changeId} and {@link #timestamp}.
     */
    public boolean isEquivalent(ExpressComReportedAddress other) {
        if (deviceId != other.deviceId) {
            return false;
        }
        if (feeder != other.feeder) {
            return false;
        }
        if (geo != other.geo) {
            return false;
        }
        if (relays == null) {
            if (other.relays != null) {
                return false;
            }
        } else if (!relays.equals(other.relays)) {
            return false;
        }
        if (required != other.required) {
            return false;
        }
        if (spid != other.spid) {
            return false;
        }
        if (substation != other.substation) {
            return false;
        }
        if (uda != other.uda) {
            return false;
        }
        if (zip != other.zip) {
            return false;
        }
        
        return true;
    }

    @Override
    public ExpressComReportedAddress clone() {
        ExpressComReportedAddress newAddress = new ExpressComReportedAddress();

        newAddress.deviceId = deviceId;
        newAddress.spid = spid;
        newAddress.geo = geo;
        newAddress.substation = substation;
        newAddress.feeder = feeder;
        newAddress.zip = zip;
        newAddress.uda = uda;
        newAddress.required = required;
        newAddress.timestamp = timestamp;

        Set<ExpressComReportedAddressRelay> newRelayAddress = new HashSet<>();
        for (ExpressComReportedAddressRelay addressRelay : relays) {
            var newAddressRelay = new ExpressComReportedAddressRelay(addressRelay.getRelayNumber());
            newAddressRelay.setProgram(addressRelay.getProgram());
            newAddressRelay.setSplinter(addressRelay.getSplinter());
            newRelayAddress.add(newAddressRelay);
        }
        newAddress.setRelays(newRelayAddress);

        return newAddress;
    }

}