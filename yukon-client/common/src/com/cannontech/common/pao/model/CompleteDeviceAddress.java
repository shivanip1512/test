package com.cannontech.common.pao.model;

import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.pao.annotation.YukonPaoPart;
import com.google.common.base.Objects;

@YukonPaoPart(idColumnName = "deviceId")
public class CompleteDeviceAddress {
    private int masterAddress;
    private int slaveAddress;
    private int postCommWait;

    @YukonPaoField
    public int getMasterAddress() {
        return masterAddress;
    }

    public void setMasterAddress(int masterAddress) {
        this.masterAddress = masterAddress;
    }

    @YukonPaoField
    public int getSlaveAddress() {
        return slaveAddress;
    }

    public void setSlaveAddress(int slaveAddress) {
        this.slaveAddress = slaveAddress;
    }

    @YukonPaoField
    public int getPostCommWait() {
        return postCommWait;
    }

    public void setPostCommWait(int postCommWait) {
        this.postCommWait = postCommWait;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(masterAddress, slaveAddress, postCommWait);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != CompleteDeviceAddress.class) {
            return false;
        }
        CompleteDeviceAddress that = (CompleteDeviceAddress) other;
        return masterAddress == that.masterAddress && slaveAddress == that.slaveAddress
            && postCommWait == that.postCommWait;
    }

    @Override
    public String toString() {
        return "CompleteDeviceAddress [masterAddress=" + masterAddress + ", slaveAddress=" + slaveAddress
            + ", postCommWait=" + postCommWait + "]";
    }
}
