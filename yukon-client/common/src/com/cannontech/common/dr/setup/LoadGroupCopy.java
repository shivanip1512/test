package com.cannontech.common.dr.setup;

public class LoadGroupCopy extends LMCopy {
    private Integer routeId;

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    @Override
    public void buildModel(LoadGroupBase group) {
        // Set parent fields
        super.buildModel(group);
        if (getRouteId() != null) {
            switch (group.getType()) {
            case LM_GROUP_EXPRESSCOMM:
                ((LoadGroupExpresscom) group).setRouteID(getRouteId());
                break;
            case LM_GROUP_EMETCON:
                ((LoadGroupEmetcon) group).setRouteID(getRouteId());
                break;
            case LM_GROUP_VERSACOM:
                ((LoadGroupVersacom) group).setRouteId(getRouteId());
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
                // TODO Implement Copy for Ripple
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
