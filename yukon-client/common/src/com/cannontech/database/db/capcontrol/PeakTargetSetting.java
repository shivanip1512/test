package com.cannontech.database.db.capcontrol;

public class PeakTargetSetting {
    private TargetSettingType type;
    private String peakValue;
    private String offPeakValue;
    private String units;
    private final static String PEAK_TYPE = "PEAK";
    private final static String OFFPEAK_TYPE = "OFFPEAK";
    
    public PeakTargetSetting(TargetSettingType type, String peakValue, String offPeakValue, String units){
        this.type = type;
        this.peakValue = peakValue;
        this.offPeakValue = offPeakValue;
        this.units = units;
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

    public void setUnits(String units) {
        this.units = units;
    }

    public String getUnits() {
        return units;
    }

    public static String getPeakType() {
        return PEAK_TYPE;
    }

    public static String getOffpeakType() {
        return OFFPEAK_TYPE;
    }
}