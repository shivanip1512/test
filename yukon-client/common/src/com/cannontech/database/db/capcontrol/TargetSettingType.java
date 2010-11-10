package com.cannontech.database.db.capcontrol;


public enum TargetSettingType {
    UPPER_VOLT_LIMIT("Upper Volt Limit", "130.0", "Volts"),
    LOWER_VOLT_LIMIT("Lower Volt Limit", "110.0", "Volts"),
    KVAR_LEADING("KVAR Leading", "-600.0", "KVARs"),
    KVAR_LAGGING("KVAR Lagging", "600.0", "KVARs"),
    TARGET_PF("Target PF", "100.0", "%"),
    MIN_BANK_OPEN("Min. of Bank Open", "80.0", "%"),
    MIN_BANK_CLOSE("Min. of Bank Close", "80.0", "%"),
    VOLT_WEIGHT("Volt Weight", "1.0", ""),
    PF_WEIGHT("PF Weight", "1.0", ""),
    DECISION_WEIGHT("Decision Weight", "1.0", ""),
    VOLTAGE_REGULATION_MARGIN("Voltage Regulation Margin","1.0","Volts"),
    MAX_CONSECUTIVE_BANK_OPERATIONS("Max Consecutive CapBank Ops.","2.0",""),
    HOUR_ZERO("00:00", "0", "%"),
    HOUR_ONE("01:00", "0", "%"),
    HOUR_TWO("02:00", "0", "%"),
    HOUR_THREE("03:00", "0", "%"),
    HOUR_FOUR("04:00", "0", "%"),
    HOUR_FIVE("05:00", "0", "%"),
    HOUR_SIX("06:00", "0", "%"),
    HOUR_SEVEN("07:00", "0", "%"),
    HOUR_EIGHT("08:00", "0", "%"),
    HOUR_NINE("09:00", "0", "%"),
    HOUR_TEN("10:00", "0", "%"),
    HOUR_ELEVEN("11:00", "0", "%"),
    HOUR_TWELVE("12:00", "0", "%"),
    HOUR_THIRTEEN("13:00", "0", "%"),
    HOUR_FOURTEEN("14:00", "0", "%"),
    HOUR_FIFTEEN("15:00", "0", "%"),
    HOUR_SIXTEEN("16:00", "0", "%"),
    HOUR_SEVENTEEN("17:00", "0", "%"),
    HOUR_EIGHTEEN("18:00", "0", "%"),
    HOUR_NINETEEN("19:00", "0", "%"),
    HOUR_TWENTY("20:00", "0", "%"),
    HOUR_TWENTYONE("21:00", "0", "%"),
    HOUR_TWENTYTWO("22:00", "0", "%"),
    HOUR_TWENTYTHREE("23:00", "0", "%");
    
    private String displayName;
    private String defaultValue;
    private String units;
    
    private TargetSettingType(String displayName, String defaultValue, String units){
        this.displayName = displayName;
        this.defaultValue = defaultValue;
        this.units = units;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public PeakTargetSetting getPeakTargetSetting(){
		PeakTargetSetting setting = new PeakTargetSetting(this, defaultValue, defaultValue, units);
        return setting;
    }
    
    public String getUnits() {
        return units;
    }
}