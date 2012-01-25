package com.cannontech.common.pao.pojo;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.database.YNBoolean;

@YukonPao(tableName="CapControlFeeder", idColumnName="FeederId", paoTypes=PaoType.CAP_CONTROL_FEEDER)
public class CompleteCapControlFeeder extends CompleteYukonPaObject {
    private int currentVarLoadPointId = 0;
    private int currentWattLoadPointId = 0;
    private int currentVoltLoadPointId = 0;
    private int phaseB = 0;
    private int phaseC = 0;
    private String mapLocationId = "0";
    private YNBoolean multiMonitorControl = YNBoolean.NO;
    private YNBoolean usePhaseData = YNBoolean.NO;
    private YNBoolean controlFlag = YNBoolean.NO;

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
    public YNBoolean getControlFlag() {
        return controlFlag;
    }

    public void setControlFlag(YNBoolean controlFlag) {
        this.controlFlag = controlFlag;
    }

    @Override
    public String toString() {
        return "CompleteCapControlFeeder [currentVarLoadPointId=" + currentVarLoadPointId
               + ", currentWattLoadPointId=" + currentWattLoadPointId + ", currentVoltLoadPointId="
               + currentVoltLoadPointId + ", phaseB=" + phaseB + ", phaseC=" + phaseC
               + ", mapLocationId=" + mapLocationId + ", multiMonitorControl="
               + multiMonitorControl + ", usePhaseData=" + usePhaseData + ", controlFlag="
               + controlFlag + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((controlFlag == null) ? 0 : controlFlag.hashCode());
        result = prime * result + currentVarLoadPointId;
        result = prime * result + currentVoltLoadPointId;
        result = prime * result + currentWattLoadPointId;
        result = prime * result + ((mapLocationId == null) ? 0 : mapLocationId.hashCode());
        result =
            prime * result + ((multiMonitorControl == null) ? 0 : multiMonitorControl.hashCode());
        result = prime * result + phaseB;
        result = prime * result + phaseC;
        result = prime * result + ((usePhaseData == null) ? 0 : usePhaseData.hashCode());
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
        CompleteCapControlFeeder other = (CompleteCapControlFeeder) obj;
        if (controlFlag != other.controlFlag)
            return false;
        if (currentVarLoadPointId != other.currentVarLoadPointId)
            return false;
        if (currentVoltLoadPointId != other.currentVoltLoadPointId)
            return false;
        if (currentWattLoadPointId != other.currentWattLoadPointId)
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
        if (usePhaseData != other.usePhaseData)
            return false;
        return true;
    }
}
