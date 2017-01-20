package com.cannontech.dr.honeywellWifi.azure.event;

public enum EventPhase {
    NOT_STARTED("NotStarted", 0),
    PHASE_1("Phase1", 1),
    PHASE_2("Phase2", 1),
    PHASE_3("Phase3", 1),
    COMPLETED("Completed", 0),
    ;
    
    /** True (1) means the event is active when in this phase. False (0) means the event is inactive. */
    private final double stateValue;
    private final String jsonString;
    
    private EventPhase(String jsonString, double stateValue) {
        this.jsonString = jsonString;
        this.stateValue = stateValue;
    }
    
    public String getJsonString() {
        return jsonString;
    }
    
    public double getStateValue() {
        return stateValue;
    }
    
    public static EventPhase of(String jsonString) {
        for (EventPhase status : values()) {
            if (status.getJsonString().equalsIgnoreCase(jsonString)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No EventPhase for \"" + jsonString + "\"");
    }
}
