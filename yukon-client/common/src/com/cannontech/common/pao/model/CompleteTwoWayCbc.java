package com.cannontech.common.pao.model;

import com.cannontech.common.device.DeviceScanType;
import com.cannontech.common.device.DeviceWindowType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

@YukonPao(tableBacked = false, paoTypes = { PaoType.CBC_7020, PaoType.CBC_7022, PaoType.CBC_7023, PaoType.CBC_7024,
    PaoType.CBC_8020, PaoType.CBC_8024, PaoType.CBC_DNP, PaoType.CBC_LOGICAL})
public class CompleteTwoWayCbc extends CompleteCbcBase {
    private CompleteDeviceScanRate completeDeviceScanRate = null;
    private final CompleteDeviceWindow completeDeviceWindow = new CompleteDeviceWindow();
    private final CompleteDeviceDirectCommSettings completeDeviceDirectCommSettings =
        new CompleteDeviceDirectCommSettings();
    private final CompleteDeviceAddress completeDeviceAddress = new CompleteDeviceAddress();

    public boolean isScanEnabled() {
        return completeDeviceScanRate != null;
    }

    public void setScanEnabled(boolean scanEnabled) {
        if (scanEnabled) {
            // Check for already existing data to ensure we aren't stomping over previous data.
            if (completeDeviceScanRate == null) {
                completeDeviceScanRate = new CompleteDeviceScanRate();
            }
        } else {
            completeDeviceScanRate = null;
        }
    }

    @YukonPaoField
    public CompleteDeviceScanRate getCompleteDeviceScanRate() {
        return completeDeviceScanRate;
    }

    public void setCompleteDeviceScanRate(CompleteDeviceScanRate completeDeviceScanRate) {
        this.completeDeviceScanRate = completeDeviceScanRate;
    }

    @YukonPaoField
    public CompleteDeviceWindow getCompleteDeviceWindow() {
        return completeDeviceWindow;
    }

    @YukonPaoField
    public CompleteDeviceDirectCommSettings getCompleteDeviceDirectCommSettings() {
        return completeDeviceDirectCommSettings;
    }

    @YukonPaoField
    public CompleteDeviceAddress getCompleteDeviceAddress() {
        return completeDeviceAddress;
    }

    public int getIntervalRate() {
        return completeDeviceScanRate.getIntervalRate();
    }

    public void setIntervalRate(int intervalRate) {
        completeDeviceScanRate.setIntervalRate(intervalRate);
    }

    public int getScanGroup() {
        return completeDeviceScanRate.getScanGroup();
    }

    public void setScanGroup(int scanGroup) {
        completeDeviceScanRate.setScanGroup(scanGroup);
    }

    public int getAlternateRate() {
        return completeDeviceScanRate.getAlternateRate();
    }

    public void setAlternateRate(int alternateRate) {
        completeDeviceScanRate.setAlternateRate(alternateRate);
    }

    public DeviceScanType getScanType() {
        return completeDeviceScanRate.getScanType();
    }

    public void setScanType(DeviceScanType scanType) {
        completeDeviceScanRate.setScanType(scanType);
    }

    public DeviceWindowType getType() {
        return completeDeviceWindow.getType();
    }

    public void setType(DeviceWindowType type) {
        completeDeviceWindow.setType(type);
    }

    public int getWindowOpen() {
        return completeDeviceWindow.getWindowOpen();
    }

    public void setWindowOpen(int windowOpen) {
        completeDeviceWindow.setWindowOpen(windowOpen);
    }

    public int getWindowClose() {
        return completeDeviceWindow.getWindowClose();
    }

    public void setWindowClose(int windowClose) {
        completeDeviceWindow.setWindowClose(windowClose);
    }

    public int getAlternateOpen() {
        return completeDeviceWindow.getAlternateOpen();
    }

    public void setAlternateOpen(int alternateOpen) {
        completeDeviceWindow.setAlternateOpen(alternateOpen);
    }

    public int getAlternateClose() {
        return completeDeviceWindow.getAlternateClose();
    }

    public void setAlternateClose(int alternateClose) {
        completeDeviceWindow.setAlternateClose(alternateClose);
    }

    public int getPortId() {
        return completeDeviceDirectCommSettings.getPortId();
    }

    public void setPortId(int portId) {
        completeDeviceDirectCommSettings.setPortId(portId);
    }

    public int getMasterAddress() {
        return completeDeviceAddress.getMasterAddress();
    }

    public void setMasterAddress(int masterAddress) {
        completeDeviceAddress.setMasterAddress(masterAddress);
    }

    public int getSlaveAddress() {
        return completeDeviceAddress.getSlaveAddress();
    }

    public void setSlaveAddress(int slaveAddress) {
        completeDeviceAddress.setSlaveAddress(slaveAddress);
    }

    public int getPostCommWait() {
        return completeDeviceAddress.getPostCommWait();
    }

    public void setPostCommWait(int postCommWait) {
        completeDeviceAddress.setPostCommWait(postCommWait);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), completeDeviceScanRate, completeDeviceWindow,
            completeDeviceDirectCommSettings, completeDeviceAddress);
    }

    @Override
    protected boolean localEquals(Object object) {
        if (object instanceof CompleteTwoWayCbc) {
            if (!super.localEquals(object)) {
                return false;
            }
            CompleteTwoWayCbc that = (CompleteTwoWayCbc) object;
            return Objects.equal(completeDeviceScanRate, that.completeDeviceScanRate)
                && Objects.equal(completeDeviceWindow, that.completeDeviceWindow)
                && Objects.equal(completeDeviceDirectCommSettings, that.completeDeviceDirectCommSettings)
                && Objects.equal(completeDeviceAddress, that.completeDeviceAddress);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteTwoWayCbc [completeDeviceScanRate=" + completeDeviceScanRate
            + ", completeDeviceWindow=" + completeDeviceWindow + ", completeDeviceDirectCommSettings="
            + completeDeviceDirectCommSettings + ", completeDeviceAddress=" + completeDeviceAddress + "]";
    }
}
