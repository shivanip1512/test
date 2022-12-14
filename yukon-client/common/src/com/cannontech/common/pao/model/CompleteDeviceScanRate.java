package com.cannontech.common.pao.model;

import com.cannontech.common.device.DeviceScanType;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.pao.annotation.YukonPaoPart;
import com.google.common.base.Objects;

@YukonPaoPart(idColumnName = "deviceId")
public class CompleteDeviceScanRate {
    private int intervalRate = 300;
    private int scanGroup = 0;
    private int alternateRate = 300;
    private DeviceScanType scanType = DeviceScanType.INTEGRITY;

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
        return Objects.hashCode(intervalRate, scanGroup, alternateRate, scanType);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != CompleteDeviceScanRate.class) {
            return false;
        }
        CompleteDeviceScanRate that = (CompleteDeviceScanRate) other;
        return intervalRate == that.intervalRate && scanGroup == that.scanGroup && alternateRate == that.alternateRate
            && scanType == that.scanType;
    }

    @Override
    public String toString() {
        return "CompleteDeviceScanRate [intervalRate=" + intervalRate + ", scanGroup=" + scanGroup + ", alternateRate="
            + alternateRate + ", scanType=" + scanType + "]";
    }
}
