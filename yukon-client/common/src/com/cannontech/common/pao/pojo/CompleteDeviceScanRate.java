package com.cannontech.common.pao.pojo;

import com.cannontech.common.device.DeviceScanType;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.pao.annotation.YukonPaoPart;

@YukonPaoPart(idColumnName = "deviceId")
public class CompleteDeviceScanRate {
    private int intervalRate = 300;
    private int scanGroup = 0;
    private int alternateRate = 300;
    private DeviceScanType scanType = DeviceScanType.GENERAL;

    @YukonPaoField
    public int getIntervalRate() {
        return intervalRate;
    }

    public void setIntervalRate(int intervalRate) {
        this.intervalRate = intervalRate;
    }

    @YukonPaoField
    public int getScanGroup() {
        return scanGroup;
    }

    public void setScanGroup(int scanGroup) {
        this.scanGroup = scanGroup;
    }

    @YukonPaoField
    public int getAlternateRate() {
        return alternateRate;
    }

    public void setAlternateRate(int alternateRate) {
        this.alternateRate = alternateRate;
    }

    @YukonPaoField
    public DeviceScanType getScanType() {
        return scanType;
    }

    public void setScanType(DeviceScanType scanType) {
        this.scanType = scanType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + alternateRate;
        result = prime * result + intervalRate;
        result = prime * result + scanGroup;
        result = prime * result + ((scanType == null) ? 0 : scanType.hashCode());
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
        CompleteDeviceScanRate other = (CompleteDeviceScanRate) obj;
        if (alternateRate != other.alternateRate)
            return false;
        if (intervalRate != other.intervalRate)
            return false;
        if (scanGroup != other.scanGroup)
            return false;
        if (scanType != other.scanType)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CompleteDeviceScanRate [intervalRate=" + intervalRate + ", scanGroup=" + scanGroup
               + ", alternateRate=" + alternateRate + ", scanType=" + scanType + "]";
    }
}
