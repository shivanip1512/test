package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public final class PaoDistance implements YukonPao {
    
    private LiteYukonPAObject pao;
    private double distance;
    private PaoLocation location;
    private DistanceUnit unit;
    
    private PaoDistance(LiteYukonPAObject pao, double distance, DistanceUnit unit, PaoLocation location) {
        this.pao = pao;
        this.location = location;
        this.distance = distance;
        this.unit = unit;
    }

    public LiteYukonPAObject getPao() {
        return pao;
    }

    public double getDistance() {
        return distance;
    }

    public PaoLocation getLocation() {
        return location;
    }

    public DistanceUnit getUnit() {
        return unit;
    }
    
    public static PaoDistance of(LiteYukonPAObject pao, double distance, DistanceUnit unit, PaoLocation location) {
        return new PaoDistance(pao, distance, unit, location);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(distance);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((pao == null) ? 0 : pao.hashCode());
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
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
        PaoDistance other = (PaoDistance) obj;
        if (Double.doubleToLongBits(distance) != Double.doubleToLongBits(other.distance))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (pao == null) {
            if (other.pao != null)
                return false;
        } else if (!pao.equals(other.pao))
            return false;
        if (unit != other.unit)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("PaoDistance [pao=%s, distance=%s, location=%s, unit=%s]", pao, distance, location, unit);
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return pao.getPaoIdentifier();
    }
    
}