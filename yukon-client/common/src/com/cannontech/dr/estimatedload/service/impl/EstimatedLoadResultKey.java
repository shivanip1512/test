package com.cannontech.dr.estimatedload.service.impl;

public final class EstimatedLoadResultKey {
    private final int programId;
    private final int gearId;

    public EstimatedLoadResultKey(int programId, int gearId) {
        this.programId = programId;
        this.gearId = gearId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + gearId;
        result = prime * result + programId;
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
        EstimatedLoadResultKey other = (EstimatedLoadResultKey) obj;
        if (gearId != other.gearId)
            return false;
        if (programId != other.programId)
            return false;
        return true;
    }
    
}
