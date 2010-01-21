package com.cannontech.database.db.capcontrol;

public class PeakTargetSetting {
    private String name;
    private String peakValue;
    private String offPeakValue;
    private String units;
    private final static String PEAK_TYPE = "PEAK";
    private final static String OFFPEAK_TYPE = "OFFPEAK";
    
    public PeakTargetSetting(String name, String peakValue, String offPeakValue, String units){
        this.name = name;
        this.peakValue = peakValue;
        this.offPeakValue = offPeakValue;
        this.units = units;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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