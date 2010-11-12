package com.cannontech.database.db.capcontrol;

public class PeakTargetSetting {
    private TargetSettingType type;
    private String peakValue;
    private String offPeakValue;
    private final static String PEAK_TYPE = "PEAK";
    private final static String OFFPEAK_TYPE = "OFFPEAK";
    
    public PeakTargetSetting(TargetSettingType type, String peakValue, String offPeakValue){
        this.type = type;
        this.peakValue = peakValue;
        this.offPeakValue = offPeakValue;
    }

    public TargetSettingType getType() {
        return type;
    }

    public void setType(TargetSettingType name) {
        this.type = name;
    }

    public String getPeakValue() {
        return peakValue;
    }

    public void setPeakValue(String peakValue) {
        this.peakValue = peakValue;
    }
    
    public String getOffPeakValue() {
        return offPeakValue;
    }

    public void setOffPeakValue(String offPeakValue) {
        this.offPeakValue = offPeakValue;
    }

    public static String getPeakType() {
        return PEAK_TYPE;
    }

    public static String getOffpeakType() {
        return OFFPEAK_TYPE;
    }
}