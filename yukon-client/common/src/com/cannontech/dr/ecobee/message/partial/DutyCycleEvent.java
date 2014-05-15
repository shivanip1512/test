package com.cannontech.dr.ecobee.message.partial;

import org.joda.time.Instant;

import com.cannontech.common.util.JsonSerializers;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Implementation of the ecobee "event" object used for duty cycle dr. Only includes the subset of "event" object fields
 * necessary for this type of control, and uses appropriate default values where possible.
 */
public class DutyCycleEvent {
    private final String type = "demandResponse";
    private final String name;
    private final boolean isOptional = false;

    private final Instant startDate;
    private final Instant endDate;
    
    //Since this control uses duty cycle, set a relative temperature offset of 0 degrees
    private final boolean isTemperatureRelative = true;
    private final int heatHoldTemp = 0; //value = temp x10
    private final int coolHoldTemp = 0; //vaule = temp x10
    
    private final int dutyCyclePercentage;
    
    public DutyCycleEvent(String name, int dutyCyclePercentage, Instant startDate, Instant endDate) {
        this.name = name;
        this.dutyCyclePercentage = dutyCyclePercentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @JsonSerialize(using=JsonSerializers.EcobeeDate.class)
    public Instant getStartDate() {
        return startDate;
    }

    @JsonSerialize(using=JsonSerializers.EcobeeTime.class)
    public Instant getStartTime() {
        return startDate;
    }

    @JsonSerialize(using=JsonSerializers.EcobeeDate.class)
    public Instant getEndDate() {
        return endDate;
    }

    @JsonSerialize(using=JsonSerializers.EcobeeTime.class)
    public Instant getEndTime() {
        return endDate;
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
