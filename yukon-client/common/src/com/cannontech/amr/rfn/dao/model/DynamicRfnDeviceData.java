package com.cannontech.amr.rfn.dao.model;

import org.joda.time.Instant;

import com.cannontech.common.rfn.model.RfnDevice;

public class DynamicRfnDeviceData {
    private RfnDevice device;
    private RfnDevice gateway;
    private int descendantCount;
    private Instant lastTransferTime;
    
    public DynamicRfnDeviceData(RfnDevice device, RfnDevice gateway, int descendantCount, Instant lastTransferTime) {
        this.device = device;
        this.gateway = gateway;
        this.descendantCount = descendantCount;
        this.lastTransferTime = lastTransferTime;
    }

    
    public int getDescendantCount() {
        return descendantCount;
    }

    public void setDescendantCount(int descendantCount) {
        this.descendantCount = descendantCount;
    }

    public RfnDevice getDevice() {
        return device;
    }

    public void setDevice(RfnDevice device) {
        this.device = device;
    }

    public RfnDevice getGateway() {
        return gateway;
    }

    public void setGateway(RfnDevice gateway) {
        this.gateway = gateway;
    }

    public Instant getLastTransferTime() {
        return lastTransferTime;
    }

    public void setLastTransferTime(Instant lastTransferTime) {
        this.lastTransferTime = lastTransferTime;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + descendantCount;
        result = prime * result + ((device == null) ? 0 : device.hashCode());
        result = prime * result + ((gateway == null) ? 0 : gateway.hashCode());
        result = prime * result + ((lastTransferTime == null) ? 0 : lastTransferTime.hashCode());
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
        DynamicRfnDeviceData other = (DynamicRfnDeviceData) obj;
        if (descendantCount != other.descendantCount)
            return false;
        if (device == null) {
            if (other.device != null)
                return false;
        } else if (!device.equals(other.device))
            return false;
        if (gateway == null) {
            if (other.gateway != null)
                return false;
        } else if (!gateway.equals(other.gateway))
            return false;
        if (lastTransferTime == null) {
            if (other.lastTransferTime != null)
                return false;
        } else if (!lastTransferTime.equals(other.lastTransferTime))
            return false;
        return true;
    }
}
