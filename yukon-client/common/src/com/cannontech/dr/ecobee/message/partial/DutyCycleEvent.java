package com.cannontech.dr.ecobee.message.partial;

import org.joda.time.Instant;

import com.cannontech.dr.ecobee.message.EcobeeJsonSerializers;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Implementation of the ecobee "event" object used for duty cycle dr. Only includes the subset of "event" object fields
 * necessary for this type of control, and uses appropriate default values where possible.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
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

    @JsonCreator
    private DutyCycleEvent(@JsonProperty("name") String name, @JsonProperty("dutyCyclePercentage") int dutyCyclePercentage,
            @JsonProperty("startDate") Instant startDate, @JsonProperty("endDate") Instant endDate,
            @JsonProperty("startTime") Instant startTime, @JsonProperty("endTime") Instant endTime) {
        this.name = name;
        this.dutyCyclePercentage = dutyCyclePercentage;
        // Date and Time are separate in ecobee. This combines the date and time to form a single Instant
        this.startDate = startTime.plus(startDate.getMillis());
        this.endDate = endTime.plus(endDate.getMillis());
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @JsonSerialize(using=EcobeeJsonSerializers.TO_DATE.class)
    @JsonDeserialize(using=EcobeeJsonSerializers.FROM_DATE.class)
    public Instant getStartDate() {
        return startDate;
    }

    @JsonSerialize(using=EcobeeJsonSerializers.TO_TIME.class)
    @JsonDeserialize(using=EcobeeJsonSerializers.FROM_TIME.class)
    public Instant getStartTime() {
        return startDate;
    }

    @JsonSerialize(using=EcobeeJsonSerializers.TO_DATE.class)
    @JsonDeserialize(using=EcobeeJsonSerializers.FROM_DATE.class)
    public Instant getEndDate() {
        return endDate;
    }

    @JsonSerialize(using=EcobeeJsonSerializers.TO_TIME.class)
    @JsonDeserialize(using=EcobeeJsonSerializers.FROM_TIME.class)
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
