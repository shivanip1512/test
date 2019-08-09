package com.cannontech.common.pao.model;

import org.joda.time.Instant;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.location.Origin;

public class PaoLocation implements YukonPao {

    private PaoIdentifier paoIdentifier;
    private double latitude;
    private double longitude;
    private Origin origin;
    private Instant lastChangedDate;
    
    public PaoLocation(PaoIdentifier paoIdentifier, double latitude, double longitude, Origin origin,
            Instant lastChangedDate) {
        this.paoIdentifier = paoIdentifier;
        this.latitude = latitude;
        this.longitude = longitude;
        this.origin = origin;
        this.lastChangedDate = lastChangedDate;
    }

    public PaoLocation(PaoIdentifier paoIdentifier, double latitude, double longitude) {
        this(paoIdentifier, latitude, longitude, Origin.MANUAL, new Instant());
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public double distanceTo(PaoLocation location, DistanceUnit unit) {
        
        double theta = longitude - location.getLongitude();
        double dist = Math.sin(deg2rad(latitude)) 
                * Math.sin(deg2rad(location.getLatitude())) + Math.cos(deg2rad(latitude)) 
                * Math.cos(deg2rad(location.getLatitude())) 
                * Math.cos(deg2rad(theta));
        
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        
        if (unit == DistanceUnit.KILOMETERS) {
            dist = dist * 1.609344;
        } else if (unit == DistanceUnit.NAUTICAL_MILES) {
            dist = dist * 0.8684;
        }
        
        return dist;
    }
    
    /** Converts decimal degrees to radians */
    public static double deg2rad(double deg) {
        return deg * Math.PI / 180.0;
    }
      
    /** Converts radians to decimal degrees */
    public static double rad2deg(double rad) {
        return rad * 180.0 / Math.PI;
    }
    
    public Origin getOrigin() {
        return origin;
    }

    public Instant getLastChangedDate() {
        return lastChangedDate;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lastChangedDate == null) ? 0 : lastChangedDate.hashCode());
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((origin == null) ? 0 : origin.hashCode());
        result = prime * result + ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
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
        PaoLocation other = (PaoLocation) obj;
        if (lastChangedDate == null) {
            if (other.lastChangedDate != null)
                return false;
        } else if (!lastChangedDate.equals(other.lastChangedDate))
            return false;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
            return false;
        if (origin != other.origin)
            return false;
        if (paoIdentifier == null) {
            if (other.paoIdentifier != null)
                return false;
        } else if (!paoIdentifier.equals(other.paoIdentifier))
            return false;
        return true;
    }

    /**
     * Compares latitude, longitude to determine if coordinates only are equal.
     */
    public boolean equalsCoordinates(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PaoLocation other = (PaoLocation) obj;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
            return false;
        if (paoIdentifier == null) {
            if (other.paoIdentifier != null)
                return false;
        } else if (!paoIdentifier.equals(other.paoIdentifier))
            return false;
        return true;
    }


    @Override
    public String toString() {
        return String.format(
            "PaoLocation [paoIdentifier=%s, latitude=%s, longitude=%s, origin=%s, lastChangedDate=%s]", paoIdentifier,
            latitude, longitude, origin, lastChangedDate);
    }
}