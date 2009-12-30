package com.cannontech.database.db.capcontrol;

public enum TargetSettingType {
    UPPER_VOLT_LIMIT("Upper Volt Limit", "130.0", "Volts"),
    LOWER_VOLT_LIMIT("Lower Volt Limit", "110.0", "Volts"),
    KVAR_LEADING("KVAR Leading", "-600.0", "KVARs"),
    KVAR_LAGGING("KVAR Lagging", "600.0", "KVARs"),
    TARGET_PF("Target PF *", "100.0", "%"),
    MIN_BANK_OPEN("Min. of Bank Open", "80.0", "%"),
    MIN_BANK_CLOSE("Min. of Bank Close", "80.0", "%"),
    KVOLT_WEIGHT("KVOLT Weight", "1.0", ""),
    PF_WEIGHT("PF Weight", "1.0", ""),
    DECISION_WEIGHT("Decision Weight", "1.0", "");
    
    private String name;
    private String defaultValue;
    private String units;
    
    private TargetSettingType(String name, String defaultValue, String units){
        this.name = name;
        this.defaultValue = defaultValue;
        this.units = units;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public PeakTargetSetting getPeakTargetSetting(){
        PeakTargetSetting setting = new PeakTargetSetting(name, defaultValue, defaultValue, units);
        return setting;
    }
}