package com.cannontech.common.rfn.message.location;

import java.io.Serializable;

/**
 * JMS Queue name: yukon.qr.obj.amr.rfn.LocationResponseAck
 */
public class LocationResponseAck implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private long locationId;

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }
    
    @Override
    public String toString() {
        return String.format("LocationResponseAck [locationId=%s]", locationId);
    }
}
