package com.cannontech.common.pao.pojo;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.database.YNBoolean;

@YukonPao(tableName="CapControlSubstationBus", idColumnName="SubstationBusId", paoTypes=PaoType.CAP_CONTROL_SUBBUS)
public class CompleteCapControlSubstationBus extends CompleteYukonPaObject {
    private int currentVarLoadPointId = 0;
    private int currentWattLoadPointId = 0;
    private String mapLocationId = "0";
    private int currentVoltLoadPointId = 0;
    private int altSubId = 0;
    private int switchPointId = 0;
    private int phaseB = 0;
    private int phaseC = 0;
    private int voltReductionPointId = 0;
    private YNBoolean controlFlag = YNBoolean.NO;
    private YNBoolean dualBusEnabled = YNBoolean.NO;
    private YNBoolean multiMonitorControl = YNBoolean.NO;
    private YNBoolean usePhaseData = YNBoolean.NO;
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
    public YNBoolean getControlFlag() {
        return controlFlag;
    }

    public void setControlFlag(YNBoolean controlFlag) {
        this.controlFlag = controlFlag;
    }

    @YukonPaoField
    public YNBoolean getDualBusEnabled() {
        return dualBusEnabled;
    }

    public void setDualBusEnabled(YNBoolean dualBusEnabled) {
        this.dualBusEnabled = dualBusEnabled;
    }

    @YukonPaoField
    public YNBoolean getMultiMonitorControl() {
        return multiMonitorControl;
    }

    public void setMultiMonitorControl(YNBoolean multiMonitorControl) {
        this.multiMonitorControl = multiMonitorControl;
    }

    @YukonPaoField
    public YNBoolean getUsePhaseData() {
        return usePhaseData;
    }

    public void setUsePhaseData(YNBoolean usePhaseData) {
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
    public String toString() {
        return "CompleteCapControlSubstationBus [currentVarLoadPointId=" + currentVarLoadPointId
               + ", currentWattLoadPointId=" + currentWattLoadPointId + ", mapLocationId="
               + mapLocationId + ", currentVoltLoadPointId=" + currentVoltLoadPointId
               + ", altSubId=" + altSubId + ", switchPointId=" + switchPointId + ", phaseB="
               + phaseB + ", phaseC=" + phaseC + ", voltReductionPointId=" + voltReductionPointId
               + ", controlFlag=" + controlFlag + ", dualBusEnabled=" + dualBusEnabled
               + ", multiMonitorControl=" + multiMonitorControl + ", usePhaseData=" + usePhaseData
               + ", disableBusPointId=" + disableBusPointId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + altSubId;
        result = prime * result + ((controlFlag == null) ? 0 : controlFlag.hashCode());
        result = prime * result + currentVarLoadPointId;
        result = prime * result + currentVoltLoadPointId;
        result = prime * result + currentWattLoadPointId;
        result = prime * result + disableBusPointId;
        result = prime * result + ((dualBusEnabled == null) ? 0 : dualBusEnabled.hashCode());
        result = prime * result + ((mapLocationId == null) ? 0 : mapLocationId.hashCode());
        result =
            prime * result + ((multiMonitorControl == null) ? 0 : multiMonitorControl.hashCode());
        result = prime * result + phaseB;
        result = prime * result + phaseC;
        result = prime * result + switchPointId;
        result = prime * result + ((usePhaseData == null) ? 0 : usePhaseData.hashCode());
        result = prime * result + voltReductionPointId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        CompleteCapControlSubstationBus other = (CompleteCapControlSubstationBus) obj;
        if (altSubId != other.altSubId)
            return false;
        if (controlFlag != other.controlFlag)
            return false;
        if (currentVarLoadPointId != other.currentVarLoadPointId)
            return false;
        if (currentVoltLoadPointId != other.currentVoltLoadPointId)
            return false;
        if (currentWattLoadPointId != other.currentWattLoadPointId)
            return false;
        if (disableBusPointId != other.disableBusPointId)
            return false;
        if (dualBusEnabled != other.dualBusEnabled)
            return false;
        if (mapLocationId == null) {
            if (other.mapLocationId != null)
                return false;
        } else if (!mapLocationId.equals(other.mapLocationId))
            return false;
        if (multiMonitorControl != other.multiMonitorControl)
            return false;
        if (phaseB != other.phaseB)
            return false;
        if (phaseC != other.phaseC)
            return false;
        if (switchPointId != other.switchPointId)
            return false;
        if (usePhaseData != other.usePhaseData)
            return false;
        if (voltReductionPointId != other.voltReductionPointId)
            return false;
        return true;
    }
}
