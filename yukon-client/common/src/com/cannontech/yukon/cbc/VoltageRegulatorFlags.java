package com.cannontech.yukon.cbc;

public class VoltageRegulatorFlags extends StreamableCapObject {
    
    private boolean lowerTap;
    private boolean raiseTap;
    private boolean autoRemote;
    private boolean autoRemoteManual;
    
    public VoltageRegulatorFlags() {
        
    }

    public boolean isLowerTap() {
        return lowerTap;
    }

    public void setLowerTap(boolean lowerTap) {
        this.lowerTap = lowerTap;
    }

    public boolean isRaiseTap() {
        return raiseTap;
    }

    public void setRaiseTap(boolean raiseTap) {
        this.raiseTap = raiseTap;
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
    
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof VoltageRegulatorFlags) {
            VoltageRegulatorFlags regulator = (VoltageRegulatorFlags) obj;
            return regulator.getCcId().equals(getCcId());
        }
        return false;
    }
}
