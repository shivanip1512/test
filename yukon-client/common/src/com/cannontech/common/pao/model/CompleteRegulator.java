package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;

@YukonPao(idColumnName="RegulatorId", 
          paoTypes={PaoType.LOAD_TAP_CHANGER, PaoType.GANG_OPERATED, PaoType.PHASE_OPERATED})
public class CompleteRegulator extends CompleteYukonPao {
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
    public String toString() {
        return "CompleteRegulator [keepAliveTimer=" + keepAliveTimer + ", keepAliveConfig="
               + keepAliveConfig + ", voltChangePerTap=" + voltChangePerTap + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + keepAliveConfig;
        result = prime * result + keepAliveTimer;
        long temp;
        temp = Double.doubleToLongBits(voltChangePerTap);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        CompleteRegulator other = (CompleteRegulator) obj;
        if (keepAliveConfig != other.keepAliveConfig)
            return false;
        if (keepAliveTimer != other.keepAliveTimer)
            return false;
        if (Double.doubleToLongBits(voltChangePerTap) != Double
            .doubleToLongBits(other.voltChangePerTap))
            return false;
        return true;
    }
}
