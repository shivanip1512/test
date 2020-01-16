package com.cannontech.web.stars.gateway.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.database.data.lite.LitePoint;

public class WiFiMeterCommData {

    private final PaoIdentifier paoIdentifier;
    private final LitePoint commStatusPoint;
    private final LitePoint rssiPoint;

    public WiFiMeterCommData(PaoIdentifier paoIdentifier, LitePoint commStatusPoint, LitePoint rssiPoint) {
        this.paoIdentifier = paoIdentifier;
        this.commStatusPoint = commStatusPoint;
        this.rssiPoint = rssiPoint;

    }

    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }

    public LitePoint getCommStatusPoint() {
        return commStatusPoint;
    }

    public LitePoint getRssiPoint() {
        return rssiPoint;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((commStatusPoint == null) ? 0 : commStatusPoint.hashCode());
        result = prime * result + ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
        result = prime * result + ((rssiPoint == null) ? 0 : rssiPoint.hashCode());
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
        WiFiMeterCommData other = (WiFiMeterCommData) obj;
        if (commStatusPoint == null) {
            if (other.commStatusPoint != null) {
                return false;
            }
        } else if (!commStatusPoint.equals(other.commStatusPoint)) {
            return false;
        }
        if (paoIdentifier == null) {
            if (other.paoIdentifier != null) {
                return false;
            }
        } else if (!paoIdentifier.equals(other.paoIdentifier)) {
            return false;
        }
        if (rssiPoint == null) {
            if (other.rssiPoint != null) {
                return false;
            }
        } else if (!rssiPoint.equals(other.rssiPoint)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WiFiMeterCommData [paoIdentifier=" + paoIdentifier + ", commStatusPoint=" + commStatusPoint + ", rssiPoint="
                + rssiPoint + "]";
    }
}
