package com.cannontech.web.stars.gateway.model;

import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.data.lite.LitePoint;

public class WiFiMeterCommData {

    private final RfnDevice device;
    private final LitePoint commStatusPoint;
    private final LitePoint rssiPoint;

    public WiFiMeterCommData(RfnDevice device, LitePoint commStatusPoint, LitePoint rssiPoint) {
        this.device = device;
        this.commStatusPoint = commStatusPoint;
        this.rssiPoint = rssiPoint;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((commStatusPoint == null) ? 0 : commStatusPoint.hashCode());
        result = prime * result + ((device == null) ? 0 : device.hashCode());
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
        return true;
    }

    @Override
    public String toString() {
        return "WiFiMeterCommData [device=" + device + ", commStatusPoint=" + commStatusPoint + ", rssiPoint="
                + rssiPoint + "]";
    }

}
