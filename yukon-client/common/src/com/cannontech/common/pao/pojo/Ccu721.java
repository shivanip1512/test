package com.cannontech.common.pao.pojo;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;

@YukonPao(paoTypes = PaoType.CCU721, tableBacked=false)
public final class Ccu721 extends CompleteDevice {
    private CompleteDeviceAddress completeDeviceAddress = new CompleteDeviceAddress();
    private CompleteDeviceDirectCommSettings completeDeviceDirectCommSettings = new CompleteDeviceDirectCommSettings();

    @YukonPaoField
    public CompleteDeviceAddress getCompleteDeviceAddress() {
        return completeDeviceAddress;
    }

    @YukonPaoField
    public CompleteDeviceDirectCommSettings getCompleteDeviceDirectCommSettings() {
        return completeDeviceDirectCommSettings;
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

    public int getPortId() {
        return completeDeviceDirectCommSettings.getPortId();
    }

    public void setPortId(int portId) {
        completeDeviceDirectCommSettings.setPortId(portId);
    }

    @Override
    public String toString() {
        return "Ccu721 [completeDeviceAddress=" + completeDeviceAddress
               + ", completeDeviceDirectCommSettings=" + completeDeviceDirectCommSettings + "]";
    }
}
