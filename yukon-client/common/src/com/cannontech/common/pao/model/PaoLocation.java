package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;

public class PaoLocation implements YukonPao {

    private PaoIdentifier paoIdentifier;
    private double latitude;
    private double longitude;
    
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
    
    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
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
    
    public static PaoLocation of(double latitude, double longitude) {
        
        PaoLocation location = new PaoLocation();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        
        return location;
    }
    
    /** Converts decimal degrees to radians */
    public static double deg2rad(double deg) {
        return deg * Math.PI / 180.0;
    }
      
    /** Converts radians to decimal degrees */
    public static double rad2deg(double rad) {
        return rad * 180.0 / Math.PI;
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
        return String.format("PaoLocation [paoIdentifier=%s, latitude=%s, longitude=%s]", paoIdentifier, latitude, longitude);
    }

}