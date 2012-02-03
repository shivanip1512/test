package com.cannontech.common.pao.pojo;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;

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
        result = prime * result + (controlFlag ? 1231 : 1237);
        result = prime * result + currentVarLoadPointId;
        result = prime * result + currentVoltLoadPointId;
        result = prime * result + currentWattLoadPointId;
        result = prime * result + ((mapLocationId == null) ? 0 : mapLocationId.hashCode());
        result = prime * result + (multiMonitorControl ? 1231 : 1237);
        result = prime * result + phaseB;
        result = prime * result + phaseC;
        result = prime * result + (usePhaseData ? 1231 : 1237);
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
