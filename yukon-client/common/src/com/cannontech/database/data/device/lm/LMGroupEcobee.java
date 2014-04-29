package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;


public class LMGroupEcobee extends LMGroup {
    private final static long serialVersionUID = 1L;

    public LMGroupEcobee() {
        getYukonPAObject().setType(PaoType.LM_GROUP_ECOBEE.getDbString());
    }

}
