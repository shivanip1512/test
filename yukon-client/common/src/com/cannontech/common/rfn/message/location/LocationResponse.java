package com.cannontech.common.rfn.message.location;

import java.io.Serializable;

import org.joda.time.Instant;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/**
 * JMS Queue name: yukon.qr.obj.amr.rfn.LocationResponse
 */
public class LocationResponse implements RfnIdentifyingMessage, Serializable {

    private static final long serialVersionUID = 1L;

    private RfnIdentifier rfnIdentifier;
    private Origin origin;
    private double latitude;
    private double longitude;
    private Instant lastChangedDate;
    private long locationId;

    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Instant getLastChangedDate() {
        return lastChangedDate;
    }

    public void setLastChangedDate(Instant lastChangedDate) {
        this.lastChangedDate = lastChangedDate;
    }

    public Origin getOrigin() {
        return origin;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }
    
    @Override
    public String toString() {
        return String.format(
            "LocationResponse [rfnIdentifier=%s, origin=%s, latitude=%s, longitude=%s, locationId=%s, lastChangedDate=%s]",
            rfnIdentifier, origin, latitude, longitude, locationId, lastChangedDate);
    }
}
