package com.cannontech.web.stars.gateway.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.database.data.lite.LitePoint;

public class SuperMeterInfo {

    private final PaoIdentifier paoIdentifier;
    private final LitePoint commStatusPointId;
    private final LitePoint rssiPointId;

    public SuperMeterInfo(PaoIdentifier paoIdentifier, LitePoint commStatusPointId, LitePoint rssiPointId) {
        this.paoIdentifier = paoIdentifier;
        this.commStatusPointId = commStatusPointId;
        this.rssiPointId = rssiPointId;

    }

    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }

    public LitePoint getCommStatusPointId() {
        return commStatusPointId;
    }

    public LitePoint getRssiPointId() {
        return rssiPointId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((commStatusPointId == null) ? 0 : commStatusPointId.hashCode());
        result = prime * result + ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
        result = prime * result + ((rssiPointId == null) ? 0 : rssiPointId.hashCode());
        return result;
    }

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
        SuperMeterInfo other = (SuperMeterInfo) obj;
        if (commStatusPointId == null) {
            if (other.commStatusPointId != null) {
                return false;
            }
        } else if (!commStatusPointId.equals(other.commStatusPointId)) {
            return false;
        }
        if (paoIdentifier == null) {
            if (other.paoIdentifier != null) {
                return false;
            }
        } else if (!paoIdentifier.equals(other.paoIdentifier)) {
            return false;
        }
        if (rssiPointId == null) {
            if (other.rssiPointId != null) {
                return false;
            }
        } else if (!rssiPointId.equals(other.rssiPointId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SuperMeterInfo [paoIdentifier=" + paoIdentifier + ", commStatusPointId=" + commStatusPointId + ", rssiPointId="
                + rssiPointId + "]";
    }

}
