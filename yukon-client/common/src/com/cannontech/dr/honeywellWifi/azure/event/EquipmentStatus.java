package com.cannontech.dr.honeywellWifi.azure.event;

public enum EquipmentStatus {
    HEATING("Heating", 0.0),
    COOLING("Cooling", 1.0),
    OFF("Off", 2.0);
    
    private final String jsonString;
    private final Double stateValue;
    
    private EquipmentStatus(String jsonString, double stateValue) {
        this.jsonString = jsonString;
        this.stateValue = stateValue;
    }
    
    public String getJsonString() {
        return jsonString;
    }
    
    public Double getStateValue() {
        return stateValue;
    }
    
    public static EquipmentStatus of(String jsonString) {
        for (EquipmentStatus status : values()) {
            if (status.getJsonString().equalsIgnoreCase(jsonString)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No EquipmentStatus for \"" + jsonString + "\"");
    }
}
