package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifier;

public class LocationData {

    private RfnIdentifier rfnIdentifier;
    private double latitude;
    private double longitude;
    private PaoIdentifier paoIdentifier;

    public LocationData(RfnIdentifier rfnIdentifier, double latitude, double longitude, PaoIdentifier paoIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
        this.latitude = latitude;
        this.longitude = longitude;
        this.paoIdentifier = paoIdentifier;
    }
    
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

    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }

    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
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
        LocationData other = (LocationData) obj;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
            return false;
        if (paoIdentifier == null) {
            if (other.paoIdentifier != null)
                return false;
        } else if (!paoIdentifier.equals(other.paoIdentifier))
            return false;
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "LocationData [rfnIdentifier=" + rfnIdentifier + ", latitude=" + latitude + ", longitude=" + longitude
                + ", paoIdentifier=" + paoIdentifier + "]";
    }
}
