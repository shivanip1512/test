package com.cannontech.message.capcontrol.streamable;

import java.util.Date;

import com.cannontech.capcontrol.TapOperation;

public class VoltageRegulatorFlags extends StreamableCapObject {
    
    private TapOperation lastOperation;
    private boolean autoRemote;
    private boolean autoRemoteManual;
    private boolean recentOperation;
    private Date lastOperationTime;
    
    public VoltageRegulatorFlags() {
        
    }

    public TapOperation getLastOperation() {
        return lastOperation;
    }

    public void setLastOperation(TapOperation lastOperation) {
        this.lastOperation = lastOperation;
    }

    public boolean isAutoRemote() {
        return autoRemote;
    }

    public void setAutoRemote(boolean autoRemote) {
        this.autoRemote = autoRemote;
    }

    public boolean isAutoRemoteManual() {
        return autoRemoteManual;
    }

    public void setAutoRemoteManual(boolean autoRemoteManual) {
        this.autoRemoteManual = autoRemoteManual;
    }
    
	public boolean isRecentOperation() {
        return recentOperation;
    }

    public void setRecentOperation(boolean recentOperation) {
        this.recentOperation = recentOperation;
    }

    public Date getLastOperationTime() {
        return lastOperationTime;
    }

    public void setLastOperationTime(Date lastOperationTime) {
        this.lastOperationTime = lastOperationTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VoltageRegulatorFlags) {
            VoltageRegulatorFlags regulator = (VoltageRegulatorFlags) obj;
            return regulator.getCcId().equals(getCcId());
        }
        return false;
    }
}
