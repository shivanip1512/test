package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

@YukonPao(idColumnName="SubstationBusId", paoTypes=PaoType.CAP_CONTROL_SUBBUS)
public class CompleteCapControlSubstationBus extends CompleteYukonPao {
    private int currentVarLoadPointId = 0;
    private int currentWattLoadPointId = 0;
    private String mapLocationId = "0";
    private int currentVoltLoadPointId = 0;
    private int altSubId = 0;
    private int switchPointId = 0;
    private int phaseB = 0;
    private int phaseC = 0;
    private int voltReductionPointId = 0;
    private boolean controlFlag = false;
    private boolean dualBusEnabled = false;
    private boolean multiMonitorControl = false;
    private boolean usePhaseData = false;
    private int disableBusPointId = 0;

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
    public String getMapLocationId() {
        return mapLocationId;
    }

    public void setMapLocationId(String mapLocationId) {
        this.mapLocationId = mapLocationId;
    }

    @YukonPaoField
    public int getCurrentVoltLoadPointId() {
        return currentVoltLoadPointId;
    }

    public void setCurrentVoltLoadPointId(int currentVoltLoadPointId) {
        this.currentVoltLoadPointId = currentVoltLoadPointId;
    }

    @YukonPaoField
    public int getAltSubId() {
        return altSubId;
    }

    public void setAltSubId(int altSubId) {
        this.altSubId = altSubId;
    }

    @YukonPaoField
    public int getSwitchPointId() {
        return switchPointId;
    }

    public void setSwitchPointId(int switchPointId) {
        this.switchPointId = switchPointId;
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
    public int getVoltReductionPointId() {
        return voltReductionPointId;
    }

    public void setVoltReductionPointId(int voltReductionPointId) {
        this.voltReductionPointId = voltReductionPointId;
    }

    @YukonPaoField
    public boolean getControlFlag() {
        return controlFlag;
    }

    public void setControlFlag(boolean controlFlag) {
        this.controlFlag = controlFlag;
    }

    @YukonPaoField
    public boolean getDualBusEnabled() {
        return dualBusEnabled;
    }

    public void setDualBusEnabled(boolean dualBusEnabled) {
        this.dualBusEnabled = dualBusEnabled;
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
    public int getDisableBusPointId() {
        return disableBusPointId;
    }

    public void setDisableBusPointId(int disableBusPointId) {
        this.disableBusPointId = disableBusPointId;
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(super.hashCode(), currentVarLoadPointId, currentWattLoadPointId, 
                                mapLocationId, currentVoltLoadPointId, altSubId, switchPointId, 
                                phaseB, phaseC, voltReductionPointId, controlFlag, dualBusEnabled, 
                                multiMonitorControl, usePhaseData, disableBusPointId);
    }
    
    @Override
    public boolean equals(Object object){
        if (object instanceof CompleteCapControlSubstationBus) {
            if (!super.equals(object)) 
                return false;
            CompleteCapControlSubstationBus that = (CompleteCapControlSubstationBus) object;
            return Objects.equal(this.currentVarLoadPointId, that.currentVarLoadPointId)
                && Objects.equal(this.currentWattLoadPointId, that.currentWattLoadPointId)
                && Objects.equal(this.mapLocationId, that.mapLocationId)
                && Objects.equal(this.currentVoltLoadPointId, that.currentVoltLoadPointId)
                && Objects.equal(this.altSubId, that.altSubId)
                && Objects.equal(this.switchPointId, that.switchPointId)
                && Objects.equal(this.phaseB, that.phaseB)
                && Objects.equal(this.phaseC, that.phaseC)
                && Objects.equal(this.voltReductionPointId, that.voltReductionPointId)
                && Objects.equal(this.controlFlag, that.controlFlag)
                && Objects.equal(this.dualBusEnabled, that.dualBusEnabled)
                && Objects.equal(this.multiMonitorControl, that.multiMonitorControl)
                && Objects.equal(this.usePhaseData, that.usePhaseData)
                && Objects.equal(this.disableBusPointId, that.disableBusPointId);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteCapControlSubstationBus [currentVarLoadPointId=" + currentVarLoadPointId
               + ", currentWattLoadPointId=" + currentWattLoadPointId + ", mapLocationId="
               + mapLocationId + ", currentVoltLoadPointId=" + currentVoltLoadPointId
               + ", altSubId=" + altSubId + ", switchPointId=" + switchPointId + ", phaseB="
               + phaseB + ", phaseC=" + phaseC + ", voltReductionPointId=" + voltReductionPointId
               + ", controlFlag=" + controlFlag + ", dualBusEnabled=" + dualBusEnabled
               + ", multiMonitorControl=" + multiMonitorControl + ", usePhaseData=" + usePhaseData
               + ", disableBusPointId=" + disableBusPointId + "]";
    }
}
