package com.cannontech.web.capcontrol.models;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.message.capcontrol.streamable.Feeder;

public class ViewableFeeder {

    private int ccId;
    private String ccName;
    private int parentId;
    private boolean ivvcControlled = false;
    private boolean showTargetTooltip = true;
    private boolean individualFeederControlled;
    private boolean usePhaseData;

    public void setFeederInfo(Feeder feeder) {
        ccId = feeder.getCcId();
        ccName  = feeder.getCcName();
        parentId = feeder.getParentID();
        ivvcControlled = feeder.getAlgorithm() == ControlAlgorithm.INTEGRATED_VOLT_VAR;
        individualFeederControlled = feeder.getControlmethod() == ControlMethod.INDIVIDUAL_FEEDER;
        usePhaseData = feeder.getUsePhaseData();
    }

    public final boolean isUsePhaseData() {
        return usePhaseData;
    }

    public final int getCcId() {
        return ccId;
    }

    public final String getCcName() {
        return ccName;
    }

    public final int getParentId() {
        return parentId;
    }

    public boolean isIvvcControlled() {
        return ivvcControlled;
    }

    public boolean isIndividualFeederControlled() {
        return individualFeederControlled;
    }

    public boolean isShowTargetTooltip() {
        return showTargetTooltip;
    }

    public void setShowTargetTooltip(boolean showTargetTooltip) {
        this.showTargetTooltip = showTargetTooltip;
    }
}