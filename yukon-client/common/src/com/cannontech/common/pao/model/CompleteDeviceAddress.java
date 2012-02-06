package com.cannontech.common.pao.model;

import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.pao.annotation.YukonPaoPart;

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
        final int prime = 31;
        int result = 1;
        result = prime * result + masterAddress;
        result = prime * result + postCommWait;
        result = prime * result + slaveAddress;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CompleteDeviceAddress other = (CompleteDeviceAddress) obj;
        if (masterAddress != other.masterAddress)
            return false;
        if (postCommWait != other.postCommWait)
            return false;
        if (slaveAddress != other.slaveAddress)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CompleteDeviceAddress [masterAddress=" + masterAddress + ", slaveAddress="
               + slaveAddress + ", postCommWait=" + postCommWait + "]";
    }

}
