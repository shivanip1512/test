package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;

public class LMGroupPlcExpressCom extends LMGroupExpressCom implements IGroupRoute {

    public LMGroupPlcExpressCom() {
        super(PaoType.LM_GROUP_EXPRESSCOMM);
    }

    @Override
    public void setRouteID(Integer routeId) {
        getLMGroupExpressComm().setRouteID(routeId);
    }

    @Override
    public Integer getRouteID() {
        return getLMGroupExpressComm().getRouteID();
    }
}
