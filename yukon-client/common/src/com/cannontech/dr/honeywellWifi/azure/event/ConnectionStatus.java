package com.cannontech.dr.honeywellWifi.azure.event;

public enum ConnectionStatus {
    CONNECTED("Connected", 0.0),
    CONNECTION_LOST("ConnectionLost", 1.0);
    
    private String jsonString;
    private double commStatus;
    
    private ConnectionStatus(String jsonString, double commStatus) {
        this.jsonString = jsonString;
        this.commStatus = commStatus;
    }
    
    public String getJsonString() {
        return jsonString;
    }
    
    public double getCommStatus() {
        return commStatus;
    }
    
    public static ConnectionStatus of(String jsonString) {
        for (ConnectionStatus status : values()) {
            if (status.getJsonString().equalsIgnoreCase(jsonString)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No ConnectionStatus for \"" + jsonString + "\"");
    }
    
}
