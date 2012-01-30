package com.cannontech.common.pao.pojo;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;

@YukonPao(idColumnName="RegulatorId", 
          paoTypes={PaoType.LOAD_TAP_CHANGER, PaoType.GANG_OPERATED, PaoType.PHASE_OPERATED})
public class CompleteRegulator extends CompleteYukonPaObject {
    private int keepAliveTimer;
    private int keepAliveConfig;

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

    @Override
    public String toString() {
        return "CompleteRegulator [keepAliveTimer=" + keepAliveTimer + ", keepAliveConfig="
               + keepAliveConfig + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + keepAliveConfig;
        result = prime * result + keepAliveTimer;
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
        return true;
    }
}
