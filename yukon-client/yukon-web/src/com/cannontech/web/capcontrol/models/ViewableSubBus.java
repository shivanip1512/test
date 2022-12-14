package com.cannontech.web.capcontrol.models;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.message.capcontrol.streamable.SubBus;

public class ViewableSubBus {

    private int ccId;
    private String ccName;
    private boolean usePhaseData;
    private boolean busControlled;
    private boolean ivvcControlled;
    private boolean multiVoltVarControlled;
    private boolean showTargetTooltip;
    private int alternateAreaId;
    private int alternateStationId;

    public void setSubBusInfo(SubBus bus) {
        ccId = bus.getCcId();
        ccName = bus.getCcName();
        usePhaseData = bus.getUsePhaseData();
        ControlMethod controlMethod = bus.getControlMethod();
        busControlled = controlMethod != null ? controlMethod.isBusControlled() : false;
        ivvcControlled = bus.getAlgorithm() == ControlAlgorithm.INTEGRATED_VOLT_VAR;
        setMultiVoltVarControlled(bus.getAlgorithm() == ControlAlgorithm.MULTI_VOLT_VAR);
    }
    
    public void setCcId(int ccId) {
        this.ccId = ccId;
    }

    public void setCcName(String ccName) {
        this.ccName = ccName;
    }

    public final int getCcId() {
        return ccId;
    }

    public final String getCcName() {
        return ccName;
    }

    public final boolean isUsePhaseData() {
        return usePhaseData;
    }

    public final boolean isBusControlled() {
        return busControlled;
    }

    public final boolean isIvvcControlled() {
        return ivvcControlled;
    }

    public void setIvvcControlled(boolean ivvcControlled) {
        this.ivvcControlled = ivvcControlled;
    }

    public final boolean isShowTargetTooltip() {
        return showTargetTooltip;
    }

    public void setShowTargetTooltip(boolean showTargetTooltip) {
        this.showTargetTooltip = showTargetTooltip;
    }

    public final int getAlternateAreaId() {
        return alternateAreaId;
    }

    public void setAlternateAreaId(int alternateAreaId) {
        this.alternateAreaId = alternateAreaId;
    }

    public final int getAlternateStationId() {
        return alternateStationId;
    }

    public void setAlternateStationId(int alternateStationId) {
        this.alternateStationId = alternateStationId;
    }

    public boolean isMultiVoltVarControlled() {
        return multiVoltVarControlled;
    }

    public void setMultiVoltVarControlled(boolean multiVoltVarControlled) {
        this.multiVoltVarControlled = multiVoltVarControlled;
    }
}