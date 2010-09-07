package com.cannontech.capcontrol.model;

public class PointDelta {
    
    private int pointId;
    private int bankId;
    private String bankName;
    private String cbcName;
    private String affectedDeviceName;
    private String affectedPointName;
    private double preOpValue;
    private double delta;
    
    public PointDelta(int pointId, int bankId, String bankName, String cbcName, String affectedDeviceName, String affectedPointName, double preOpValue, double delta)
    {
        this.pointId = pointId;
        this.bankId = bankId;
        this.bankName = bankName;
        this.affectedDeviceName = affectedDeviceName;
        this.affectedPointName = affectedPointName;
        this.cbcName = cbcName;
        this.preOpValue = preOpValue;
        this.delta = delta;
    }

    public int getPointId() {
        return pointId;
    }
    
    public int getBankId() {
        return bankId;
    }

    public double getPreOpValue() {
        return preOpValue;
    }

    public double getDelta() {
        return delta;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAffectedDeviceName() {
        return affectedDeviceName;
    }

    public String getCbcName() {
        return cbcName;
    }

    public String getAffectedPointName() {
        return affectedPointName;
    }
}
