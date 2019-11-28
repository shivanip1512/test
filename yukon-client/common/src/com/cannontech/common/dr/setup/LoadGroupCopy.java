package com.cannontech.common.dr.setup;

import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.LMGroupEmetcon;
import com.cannontech.database.data.device.lm.LMGroupExpressCom;
import com.cannontech.database.data.device.lm.LMGroupRipple;
import com.cannontech.database.data.device.lm.LMGroupVersacom;

/**
 * Class for setting the additional fields for coping the load group
 */
public class LoadGroupCopy extends LMCopy {
    private Integer routeId;

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    @Override
    public void buildModel(LMGroup group) {
        // Set parent fields
        super.buildModel(group);
        if (getRouteId() != null) {
            switch (group.getPaoType()) {
            case LM_GROUP_EXPRESSCOMM:
                com.cannontech.database.db.device.lm.LMGroupExpressCom expresscom =
                    ((LMGroupExpressCom) group).getLMGroupExpressComm();
                expresscom.setRouteID(getRouteId());
                break;
            case LM_GROUP_EMETCON:
                ((LMGroupEmetcon) group).setRouteID(getRouteId());
                break;
            case LM_GROUP_VERSACOM:
                com.cannontech.database.db.device.lm.LMGroupVersacom lmGroupVersacom =
                    ((LMGroupVersacom) group).getLmGroupVersacom();
                lmGroupVersacom.setRouteID(getRouteId());
                break;
            case LM_GROUP_SADIGITAL:
                // TODO Implement Copy for SADigital
                break;
            case LM_GROUP_SA305:
                // TODO Implement Copy for SA305
                break;
            case LM_GROUP_SA205:
                // TODO Implement Copy for SA205
                break;
            case LM_GROUP_RIPPLE:
                com.cannontech.database.db.device.lm.LMGroupRipple lmGroupRipple = ((LMGroupRipple) group).getLmGroupRipple();
                lmGroupRipple.setRouteID(getRouteId());
                break;
            case LM_GROUP_MCT:
                // TODO Implement Copy for MCT
                break;
            case LM_GROUP_GOLAY:
                // TODO Implement Copy for Golay
                break;
            }
        }
    }
}
