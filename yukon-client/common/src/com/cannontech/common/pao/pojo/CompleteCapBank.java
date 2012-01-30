package com.cannontech.common.pao.pojo;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.util.CtiUtilities;

@YukonPao(idColumnName = "deviceId", paoTypes=PaoType.CAPBANK)
public class CompleteCapBank extends CompleteDevice {
    private BankOpState operationalState = BankOpState.SWITCHED;
    private int controlDeviceId = 0;
    private int controlPointId = 0;
    private int bankSize = 600;
    private int recloseDelay = 0;
    private int maxDailyOps = 0;
    private String typeOfSwitch = CtiUtilities.STRING_NONE;
    private String controllerType = CtiUtilities.STRING_NONE;
    private String switchManufacturer = CtiUtilities.STRING_NONE;
    private String mapLocationId = "0";
    private boolean maxOpDisable = false;
    private CompleteCapBankAdditional capBankAdditional = new CompleteCapBankAdditional();
    
    @YukonPaoField
    public CompleteCapBankAdditional getCapBankAdditional() {
        return capBankAdditional;
    }
    
    public void setCapBankAdditional(CompleteCapBankAdditional capBankAdditional) {
        this.capBankAdditional = capBankAdditional;
    }

    @YukonPaoField
    public BankOpState getOperationalState() {
        return operationalState;
    }

    public void setOperationalState(BankOpState operationalState) {
        this.operationalState = operationalState;
    }

    @YukonPaoField
    public int getControlDeviceId() {
        return controlDeviceId;
    }

    public void setControlDeviceId(int controlDeviceId) {
        this.controlDeviceId = controlDeviceId;
    }

    @YukonPaoField
    public int getControlPointId() {
        return controlPointId;
    }

    public void setControlPointId(int controlPointId) {
        this.controlPointId = controlPointId;
    }

    @YukonPaoField
    public int getBankSize() {
        return bankSize;
    }

    public void setBankSize(int bankSize) {
        this.bankSize = bankSize;
    }

    @YukonPaoField
    public int getRecloseDelay() {
        return recloseDelay;
    }

    public void setRecloseDelay(int recloseDelay) {
        this.recloseDelay = recloseDelay;
    }

    @YukonPaoField
    public int getMaxDailyOps() {
        return maxDailyOps;
    }

    public void setMaxDailyOps(int maxDailyOps) {
        this.maxDailyOps = maxDailyOps;
    }

    @YukonPaoField
    public String getTypeOfSwitch() {
        return typeOfSwitch;
    }

    public void setTypeOfSwitch(String typeOfSwitch) {
        this.typeOfSwitch = typeOfSwitch;
    }

    @YukonPaoField
    public String getControllerType() {
        return controllerType;
    }

    public void setControllerType(String controllerType) {
        this.controllerType = controllerType;
    }

    @YukonPaoField(columnName="SwitchManufacture")
    public String getSwitchManufacturer() {
        return switchManufacturer;
    }

    public void setSwitchManufacturer(String switchManufacturer) {
        this.switchManufacturer = switchManufacturer;
    }

    @YukonPaoField
    public String getMapLocationId() {
        return mapLocationId;
    }

    public void setMapLocationId(String mapLocationId) {
        this.mapLocationId = mapLocationId;
    }

    @YukonPaoField
    public boolean getMaxOpDisable() {
        return maxOpDisable;
    }

    public void setMaxOpDisable(boolean maxOpDisable) {
        this.maxOpDisable = maxOpDisable;
    }

    @Override
    public String toString() {
        return "CompleteCapBankBase [operationalState=" + operationalState + ", controlDeviceId="
               + controlDeviceId + ", controlPointId=" + controlPointId + ", bankSize=" + bankSize
               + ", recloseDelay=" + recloseDelay + ", maxDailyOps=" + maxDailyOps
               + ", typeOfSwitch=" + typeOfSwitch + ", controllerType=" + controllerType
               + ", switchManufacturer=" + switchManufacturer + ", mapLocationId=" + mapLocationId
               + ", maxOpDisable=" + maxOpDisable + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + bankSize;
        result = prime * result + controlDeviceId;
        result = prime * result + controlPointId;
        result = prime * result + ((controllerType == null) ? 0 : controllerType.hashCode());
        result = prime * result + ((mapLocationId == null) ? 0 : mapLocationId.hashCode());
        result = prime * result + maxDailyOps;
        result = prime * result + (maxOpDisable ? 1231 : 1237);
        result = prime * result + ((operationalState == null) ? 0 : operationalState.hashCode());
        result = prime * result + recloseDelay;
        result =
            prime * result + ((switchManufacturer == null) ? 0 : switchManufacturer.hashCode());
        result = prime * result + ((typeOfSwitch == null) ? 0 : typeOfSwitch.hashCode());
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
        CompleteCapBank other = (CompleteCapBank) obj;
        if (bankSize != other.bankSize)
            return false;
        if (controlDeviceId != other.controlDeviceId)
            return false;
        if (controlPointId != other.controlPointId)
            return false;
        if (controllerType == null) {
            if (other.controllerType != null)
                return false;
        } else if (!controllerType.equals(other.controllerType))
            return false;
        if (mapLocationId == null) {
            if (other.mapLocationId != null)
                return false;
        } else if (!mapLocationId.equals(other.mapLocationId))
            return false;
        if (maxDailyOps != other.maxDailyOps)
            return false;
        if (maxOpDisable != other.maxOpDisable)
            return false;
        if (operationalState != other.operationalState)
            return false;
        if (recloseDelay != other.recloseDelay)
            return false;
        if (switchManufacturer == null) {
            if (other.switchManufacturer != null)
                return false;
        } else if (!switchManufacturer.equals(other.switchManufacturer))
            return false;
        if (typeOfSwitch == null) {
            if (other.typeOfSwitch != null)
                return false;
        } else if (!typeOfSwitch.equals(other.typeOfSwitch))
            return false;
        return true;
    }
}
