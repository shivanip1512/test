package com.cannontech.dr.honeywellWifi;

public enum EquipmentStatus {
    HEATING("Heating"),
    COOLING("Cooling"),
    OFF("Off");
    
    private String jsonString;
    
    private EquipmentStatus(String jsonString) {
        this.jsonString = jsonString;
    }
    
    public String getJsonString() {
        return jsonString;
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
