package com.cannontech.common.pao.model;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

@YukonPao(idColumnName = "deviceId", paoTypes = PaoType.CAPBANK)
public class CompleteCapBank extends CompleteDevice {
    private BankOpState operationalState = BankOpState.SWITCHED;
    private int controlDeviceId = 0;
    private int controlPointId = 0;
    private int bankSize = 600;
    private int recloseDelay = 0;
    private int maxDailyOps = 0;
    private String typeOfSwitch = StringUtils.EMPTY;
    private String controllerType = StringUtils.EMPTY;
    private String switchManufacturer = StringUtils.EMPTY;
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

    @YukonPaoField(columnName = "SwitchManufacture")
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
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), operationalState, controlDeviceId, controlPointId, bankSize,
            recloseDelay, maxDailyOps, typeOfSwitch, controllerType, switchManufacturer, mapLocationId, maxOpDisable,
            capBankAdditional);
    }

    @Override
    protected boolean localEquals(Object object) {
        if (object instanceof CompleteCapBank) {
            if (!super.localEquals(object)) {
                return false;
            }
            CompleteCapBank that = (CompleteCapBank) object;
            return operationalState == that.operationalState && controlDeviceId == that.controlDeviceId
                && controlPointId == that.controlPointId && bankSize == that.bankSize
                && recloseDelay == that.recloseDelay && maxDailyOps == that.maxDailyOps
                && Objects.equal(typeOfSwitch, that.typeOfSwitch) && Objects.equal(controllerType, that.controllerType)
                && Objects.equal(switchManufacturer, that.switchManufacturer)
                && Objects.equal(mapLocationId, that.mapLocationId) && maxOpDisable == that.maxOpDisable
                && Objects.equal(capBankAdditional, that.capBankAdditional);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteCapBank [operationalState=" + operationalState + ", controlDeviceId="
            + controlDeviceId + ", controlPointId=" + controlPointId + ", bankSize=" + bankSize + ", recloseDelay="
            + recloseDelay + ", maxDailyOps=" + maxDailyOps + ", typeOfSwitch=" + typeOfSwitch + ", controllerType="
            + controllerType + ", switchManufacturer=" + switchManufacturer + ", mapLocationId=" + mapLocationId
            + ", maxOpDisable=" + maxOpDisable + ", capBankAdditional=" + capBankAdditional + "]";
    }
}
