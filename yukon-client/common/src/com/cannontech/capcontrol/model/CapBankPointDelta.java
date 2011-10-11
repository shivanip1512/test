package com.cannontech.capcontrol.model;

public class CapBankPointDelta {
    
    private int pointId;
    private int bankId;
    private String bankName;
    private String cbcName;
    private String affectedDeviceName;
    private String affectedPointName;
    private double preOpValue;
    private double delta;
    private double deltaRounded; // For display purposes, since delta can get huge
    private boolean staticDelta;
    
    public CapBankPointDelta() {
    	
    }

	public int getPointId() {
		return pointId;
	}

	public void setPointId(int pointId) {
		this.pointId = pointId;
	}

	public int getBankId() {
		return bankId;
	}

	public void setBankId(int bankId) {
		this.bankId = bankId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCbcName() {
		return cbcName;
	}

	public void setCbcName(String cbcName) {
		this.cbcName = cbcName;
	}

	public String getAffectedDeviceName() {
		return affectedDeviceName;
	}

	public void setAffectedDeviceName(String affectedDeviceName) {
		this.affectedDeviceName = affectedDeviceName;
	}

	public String getAffectedPointName() {
		return affectedPointName;
	}

	public void setAffectedPointName(String affectedPointName) {
		this.affectedPointName = affectedPointName;
	}

	public double getPreOpValue() {
		return preOpValue;
	}

	public void setPreOpValue(double preOpValue) {
		this.preOpValue = preOpValue;
	}

	public double getDelta() {
		return delta;
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}

    public double getDeltaRounded() {
        return deltaRounded;
    }

    public void setDeltaRounded(double deltaRounded) {
        this.deltaRounded = deltaRounded;
    }

    public boolean isStaticDelta() {
        return staticDelta;
    }

    public void setStaticDelta(boolean staticDelta) {
        this.staticDelta = staticDelta;
    }
}
