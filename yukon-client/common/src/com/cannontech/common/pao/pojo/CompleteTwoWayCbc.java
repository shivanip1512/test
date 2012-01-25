package com.cannontech.common.pao.pojo;

import com.cannontech.common.device.DeviceScanType;
import com.cannontech.common.device.DeviceWindowType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;

@YukonPao(tableBacked=false,
          paoTypes={PaoType.CBC_7020, PaoType.CBC_7022, PaoType.CBC_7023, PaoType.CBC_7024, 
                    PaoType.CBC_8020, PaoType.CBC_8024, PaoType.CBC_DNP})
public class CompleteTwoWayCbc extends CompleteCbcBase {
    private CompleteDeviceScanRate completeDeviceScanRate = new CompleteDeviceScanRate();
    private CompleteDeviceWindow completeDeviceWindow = new CompleteDeviceWindow();
    private CompleteDeviceDirectCommSettings completeDeviceDirectCommSettings = new CompleteDeviceDirectCommSettings();
    private CompleteDeviceAddress completeDeviceAddress = new CompleteDeviceAddress();

    @YukonPaoField
    public CompleteDeviceScanRate getCompleteDeviceScanRate() {
        return completeDeviceScanRate;
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
}
