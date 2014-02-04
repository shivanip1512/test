package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;

public class LMGroupRfnExpressCom extends LMGroupExpressCom {
    public LMGroupRfnExpressCom() {
        super(PaoType.LM_GROUP_RFN_EXPRESSCOMM);
        // There is no routeId for RFN groups...but LmGroupExpresscom db table requires it.
        getLMGroupExpressComm().setRouteID(0);
    }
}