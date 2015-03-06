package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

@YukonPao(idColumnName = "RegulatorId", paoTypes = { PaoType.LOAD_TAP_CHANGER, PaoType.GANG_OPERATED,
    PaoType.PHASE_OPERATED })
public class CompleteRegulator extends CompleteDevice {
    private int keepAliveTimer;
    private int keepAliveConfig;
    private double voltChangePerTap = 0.75;

    @YukonPaoField
    public int getKeepAliveTimer() {
        return keepAliveTimer;
    }

    public void setKeepAliveTimer(int keepAliveTimer) {
        this.keepAliveTimer = keepAliveTimer;
    }

    @YukonPaoField
    public int getKeepAliveConfig() {
        return keepAliveConfig;
    }

    public void setKeepAliveConfig(int keepAliveConfig) {
        this.keepAliveConfig = keepAliveConfig;
    }

    @YukonPaoField
    public double getVoltChangePerTap() {
        return voltChangePerTap;
    }

    public void setVoltChangePerTap(double voltChangePerTap) {
        this.voltChangePerTap = voltChangePerTap;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), keepAliveTimer, keepAliveConfig, voltChangePerTap);
    }

    @Override
    protected boolean localEquals(Object object) {
        if (object instanceof CompleteRegulator) {
            if (!super.localEquals(object)) {
                return false;
            }
            CompleteRegulator that = (CompleteRegulator) object;
            return keepAliveTimer == that.keepAliveTimer && keepAliveConfig == that.keepAliveConfig
                && voltChangePerTap == that.voltChangePerTap;
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteRegulator [keepAliveTimer=" + keepAliveTimer + ", keepAliveConfig="
            + keepAliveConfig + ", voltChangePerTap=" + voltChangePerTap + "]";
    }

}
