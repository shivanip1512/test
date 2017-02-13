package com.cannontech.web.tools.commander.model;

public class RecentTarget {

    private String target;
    private Integer paoId;
    private Integer routeId;
    private String serialNumber;
    
    public String getTarget() {
        return target;
    }
    
    public void setTarget(String target) {
        this.target = target;
    }
    
    public Integer getPaoId() {
        return paoId;
    }
    
    public void setPaoId(Integer paoId) {
        this.paoId = paoId;
    }
    
    public Integer getRouteId() {
        return routeId;
    }
    
    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    @Override
    public String toString() {
        return String.format("RecentTarget [target=%s, paoId=%s, routeId=%s, serialNumber=%s]", target, paoId, routeId, serialNumber);
    }
    
    public RecentTarget() {

    }
    
    public RecentTarget(String target, Integer paoId, Integer routeId, String serialNumber) {
        super();
        this.target = target;
        this.paoId = paoId;
        this.routeId = routeId;
        this.serialNumber = serialNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((paoId == null) ? 0 : paoId.hashCode());
        result = prime * result + ((routeId == null) ? 0 : routeId.hashCode());
        result = prime * result + ((serialNumber == null) ? 0 : serialNumber.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
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
        RecentTarget other = (RecentTarget) obj;
        if (paoId == null) {
            if (other.paoId != null)
                return false;
        } else if (!paoId.equals(other.paoId))
            return false;
        if (routeId == null) {
            if (other.routeId != null)
                return false;
        } else if (!routeId.equals(other.routeId))
            return false;
        if (serialNumber == null) {
            if (other.serialNumber != null)
                return false;
        } else if (!serialNumber.equals(other.serialNumber))
            return false;
        if (target == null) {
            if (other.target != null)
                return false;
        } else if (!target.equals(other.target))
            return false;
        return true;
    }
    
}