package com.cannontech.dr.ecobee.message;

/**
 * Implementation of the ecobee "event" object used for duty cycle dr. Only includes the subset of "event" object fields
 * necessary for this type of control, and uses appropriate default values where possible.
 */
public class DutyCycleEvent {
    private final String name;
    private final String type = "demandResponse";
    private final boolean isOptional = false;
    
    private final String startDate; //format: yyyy-mm-dd
    private final String startTime; //format: hh:mm:ss
    private final String endDate; //format: yyyy-mm-dd
    private final String endTime; //format: hh:mm:ss
    
    //Since this control uses duty cycle, set a relative temperature offset of 0 degrees
    private final boolean isTemperatureRelative = true;
    private final int heatHoldTemp = 0; //value = temp x10
    private final int coolHoldTemp = 0; //vaule = temp x10
    
    private final int dutyCyclePercentage;
    
    public DutyCycleEvent(String name, int dutyCyclePercentage, String startDate, String startTime, String endDate, 
                 String endTime) {
        this.name = name;
        this.dutyCyclePercentage = dutyCyclePercentage;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public boolean getIsTemperatureRelative() {
        return isTemperatureRelative;
    }

    public int getHeatHoldTemp() {
        return heatHoldTemp;
    }

    public int getCoolHoldTemp() {
        return coolHoldTemp;
    }

    public int getDutyCyclePercentage() {
        return dutyCyclePercentage;
    }
    
    public boolean getIsOptional() {
        return isOptional;
    }
}
