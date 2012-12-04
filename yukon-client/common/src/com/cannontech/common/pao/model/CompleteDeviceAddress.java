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
    public int hashCode(){
        return Objects.hashCode(masterAddress, slaveAddress, postCommWait);
    }
    
    @Override
    public boolean equals(Object object){
        if (object instanceof CompleteDeviceAddress) {
            CompleteDeviceAddress that = (CompleteDeviceAddress) object;
            return Objects.equal(this.masterAddress, that.masterAddress)
                && Objects.equal(this.slaveAddress, that.slaveAddress)
                && Objects.equal(this.postCommWait, that.postCommWait);
        }
        return false;
    }

    @Override
    public String toString() {
        return "CompleteDeviceAddress [masterAddress=" + masterAddress + ", slaveAddress="
               + slaveAddress + ", postCommWait=" + postCommWait + "]";
    }
}
