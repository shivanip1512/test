package com.cannontech.web.stars.gateway.model;

import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.data.lite.LitePoint;

public class CellularDeviceCommData {

    private final RfnDevice device;
    private final LitePoint commStatusPoint;
    private final LitePoint rssiPoint;
    private final LitePoint rsrpPoint;
    private final LitePoint rsrqPoint;
    private final LitePoint sinrPoint;

    public CellularDeviceCommData(RfnDevice device, LitePoint commStatusPoint, LitePoint rssiPoint, 
                                  LitePoint rsrpPoint, LitePoint rsrqPoint, LitePoint sinrPoint) {
        this.device = device;
        this.commStatusPoint = commStatusPoint;
        this.rssiPoint = rssiPoint;
        this.rsrpPoint = rsrpPoint;
        this.rsrqPoint = rsrqPoint;
        this.sinrPoint = sinrPoint;
    }

    public RfnDevice getDevice() {
        return device;
    }

    public LitePoint getCommStatusPoint() {
        return commStatusPoint;
    }

    public LitePoint getRssiPoint() {
        return rssiPoint;
    }
    
    public LitePoint getRsrpPoint() {
        return rsrpPoint;
    }
    
    public LitePoint getRsrqPoint() {
        return rsrqPoint;
    }
    
    public LitePoint getSinrPoint() {
        return sinrPoint;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((commStatusPoint == null) ? 0 : commStatusPoint.hashCode());
        result = prime * result + ((device == null) ? 0 : device.hashCode());
        result = prime * result + ((rssiPoint == null) ? 0 : rssiPoint.hashCode());
        result = prime * result + ((rsrpPoint == null) ? 0 : rsrpPoint.hashCode());
        result = prime * result + ((rsrqPoint == null) ? 0 : rsrqPoint.hashCode());
        result = prime * result + ((sinrPoint == null) ? 0 : sinrPoint.hashCode());
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
        CellularDeviceCommData other = (CellularDeviceCommData) obj;
        if (commStatusPoint == null) {
            if (other.commStatusPoint != null) {
                return false;
            }
        } else if (!commStatusPoint.equals(other.commStatusPoint)) {
            return false;
        }
        if (device == null) {
            if (other.device != null) {
                return false;
            }
        } else if (!device.equals(other.device)) {
            return false;
        }
        if (rssiPoint == null) {
            if (other.rssiPoint != null) {
                return false;
            }
        } else if (!rssiPoint.equals(other.rssiPoint)) {
            return false;
        }
        if (rsrpPoint == null) {
            if (other.rsrpPoint != null) {
                return false;
            }
        } else if (!rsrpPoint.equals(other.rsrpPoint)) {
            return false;
        }
        if (rsrqPoint == null) {
            if (other.rsrqPoint != null) {
                return false;
            }
        } else if (!rsrqPoint.equals(other.rsrqPoint)) {
            return false;
        }
        if (sinrPoint == null) {
            if (other.sinrPoint != null) {
                return false;
            }
        } else if (!sinrPoint.equals(other.sinrPoint)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CellularDeviceCommData [device=" + device + ", commStatusPoint=" + commStatusPoint + ", rssiPoint="
                + rssiPoint + ", rsrpPoint=" + rsrpPoint + ", rsrqPoint=" + rsrqPoint + ", sinrPoint=" + sinrPoint + "]";
    }

}
