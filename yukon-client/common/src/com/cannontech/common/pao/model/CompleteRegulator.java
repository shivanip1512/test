package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;

@YukonPao(idColumnName = "RegulatorId", paoTypes = { PaoType.LOAD_TAP_CHANGER, PaoType.GANG_OPERATED,
    PaoType.PHASE_OPERATED })
public class CompleteRegulator extends CompleteDevice {

    @YukonPaoField
    public int getKeepAliveTimer() {
        return 0;
    }
    
    @YukonPaoField
    public int getKeepAliveConfig() {
        return 0;
    }

    @YukonPaoField
    public double getVoltChangePerTap() {
        return 0;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteRegulator";
    }

}
