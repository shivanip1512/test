package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

@YukonPao(idColumnName="FeederId", paoTypes=PaoType.CAP_CONTROL_FEEDER)
public class CompleteCapControlFeeder extends CompleteYukonPao {
    private int currentVarLoadPointId = 0;
    private int currentWattLoadPointId = 0;
    private int currentVoltLoadPointId = 0;
    private int phaseB = 0;
    private int phaseC = 0;
    private String mapLocationId = "0";
    private boolean multiMonitorControl = false;
    private boolean usePhaseData = false;
    private boolean controlFlag = false;

    @YukonPaoField
    public int getCurrentVarLoadPointId() {
        return currentVarLoadPointId;
    }

    public void setCurrentVarLoadPointId(int currentVarLoadPointId) {
        this.currentVarLoadPointId = currentVarLoadPointId;
    }

    @YukonPaoField
    public int getCurrentWattLoadPointId() {
        return currentWattLoadPointId;
    }

    public void setCurrentWattLoadPointId(int currentWattLoadPointId) {
        this.currentWattLoadPointId = currentWattLoadPointId;
    }

    @YukonPaoField
    public int getCurrentVoltLoadPointId() {
        return currentVoltLoadPointId;
    }

    public void setCurrentVoltLoadPointId(int currentVoltLoadPointId) {
        this.currentVoltLoadPointId = currentVoltLoadPointId;
    }

    @YukonPaoField
    public int getPhaseB() {
        return phaseB;
    }

    public void setPhaseB(int phaseB) {
        this.phaseB = phaseB;
    }

    @YukonPaoField
    public int getPhaseC() {
        return phaseC;
    }

    public void setPhaseC(int phaseC) {
        this.phaseC = phaseC;
    }

    @YukonPaoField
    public String getMapLocationId() {
        return mapLocationId;
    }

    public void setMapLocationId(String mapLocationId) {
        this.mapLocationId = mapLocationId;
    }

    @YukonPaoField
    public boolean getMultiMonitorControl() {
        return multiMonitorControl;
    }

    public void setMultiMonitorControl(boolean multiMonitorControl) {
        this.multiMonitorControl = multiMonitorControl;
    }

    @YukonPaoField
    public boolean getUsePhaseData() {
        return usePhaseData;
    }

    public void setUsePhaseData(boolean usePhaseData) {
        this.usePhaseData = usePhaseData;
    }

    @YukonPaoField
    public boolean getControlFlag() {
        return controlFlag;
    }

    public void setControlFlag(boolean controlFlag) {
        this.controlFlag = controlFlag;
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(super.hashCode(), currentVarLoadPointId, currentWattLoadPointId, 
                                currentVoltLoadPointId, phaseB, phaseC, mapLocationId, 
                                multiMonitorControl, usePhaseData, controlFlag);
    }
    
    @Override
    public boolean equals(Object object){
        if (object instanceof CompleteCapControlFeeder) {
            if (!super.equals(object)) 
                return false;
            CompleteCapControlFeeder that = (CompleteCapControlFeeder) object;
            return Objects.equal(this.currentVarLoadPointId, that.currentVarLoadPointId)
                && Objects.equal(this.currentWattLoadPointId, that.currentWattLoadPointId)
                && Objects.equal(this.currentVoltLoadPointId, that.currentVoltLoadPointId)
                && Objects.equal(this.phaseB, that.phaseB)
                && Objects.equal(this.phaseC, that.phaseC)
                && Objects.equal(this.mapLocationId, that.mapLocationId)
                && Objects.equal(this.multiMonitorControl, that.multiMonitorControl)
                && Objects.equal(this.usePhaseData, that.usePhaseData)
                && Objects.equal(this.controlFlag, that.controlFlag);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteCapControlFeeder [currentVarLoadPointId=" + currentVarLoadPointId
               + ", currentWattLoadPointId=" + currentWattLoadPointId + ", currentVoltLoadPointId="
               + currentVoltLoadPointId + ", phaseB=" + phaseB + ", phaseC=" + phaseC
               + ", mapLocationId=" + mapLocationId + ", multiMonitorControl="
               + multiMonitorControl + ", usePhaseData=" + usePhaseData + ", controlFlag="
               + controlFlag + "]";
    }
}