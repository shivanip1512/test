package com.cannontech.dr.ecobee.message.partial;

import org.joda.time.Instant;

import com.cannontech.dr.JsonSerializers;
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
    private final boolean isOptional;
    private final Instant startDate;
    private final Instant endDate;
    private final int dutyCyclePercentage;
    
    public DutyCycleEvent(String name, int dutyCyclePercentage, Instant startDate, Instant endDate, boolean isOptional) {
        this.name = name;
        this.dutyCyclePercentage = dutyCyclePercentage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isOptional = isOptional;
    }

    @JsonCreator
    private DutyCycleEvent(@JsonProperty("name") String name, @JsonProperty("dutyCyclePercentage") int dutyCyclePercentage,
            @JsonProperty("startDate") Instant startDate, @JsonProperty("endDate") Instant endDate,
            @JsonProperty("startTime") Instant startTime, @JsonProperty("endTime") Instant endTime,
            @JsonProperty("isOptional") boolean isOptional) {
        this.name = name;
        this.dutyCyclePercentage = dutyCyclePercentage;
        // Date and Time are separate in ecobee. This combines the date and time to form a single Instant
        this.startDate = startTime.plus(startDate.getMillis());
        this.endDate = endTime.plus(endDate.getMillis());
        this.isOptional = isOptional;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @JsonSerialize(using=JsonSerializers.TO_DATE.class)
    @JsonDeserialize(using=JsonSerializers.FROM_DATE.class)
    public Instant getStartDate() {
        return startDate;
    }

    @JsonSerialize(using=JsonSerializers.TO_TIME.class)
    @JsonDeserialize(using=JsonSerializers.FROM_TIME.class)
    public Instant getStartTime() {
        return startDate;
    }

    @JsonSerialize(using=JsonSerializers.TO_DATE.class)
    @JsonDeserialize(using=JsonSerializers.FROM_DATE.class)
    public Instant getEndDate() {
        return endDate;
    }

    @JsonSerialize(using=JsonSerializers.TO_TIME.class)
    @JsonDeserialize(using=JsonSerializers.FROM_TIME.class)
    public Instant getEndTime() {
        return endDate;
    }

    public int getDutyCyclePercentage() {
        return dutyCyclePercentage;
    }
    
    public boolean getIsOptional() {
        return isOptional;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + dutyCyclePercentage;
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + (isOptional ? 1231 : 1237);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DutyCycleEvent other = (DutyCycleEvent) obj;
        if (dutyCyclePercentage != other.dutyCyclePercentage) {
            return false;
        }
        if (endDate == null) {
            if (other.endDate != null) {
                return false;
            }
        } else if (!endDate.equals(other.endDate)) {
            return false;
        }
        if (isOptional != other.isOptional) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (startDate == null) {
            if (other.startDate != null) {
                return false;
            }
        } else if (!startDate.equals(other.startDate)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }
}
