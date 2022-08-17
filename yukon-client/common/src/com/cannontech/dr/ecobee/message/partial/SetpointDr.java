package com.cannontech.dr.ecobee.message.partial;

import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SetpointDr {
    private final String name;
    private final String message;
    private final SetpointEvent event;
    private final boolean sendEmail;

    public SetpointDr(String name, String message, Instant startDate,
            Instant endDate, boolean isOptional, boolean isTemperatureRelative, int coolRelativeTemp, int heatRelativeTemp,
            boolean sendEmail) {
        this.name = name;
        this.message = message;
        event = new SetpointEvent(name, startDate, endDate, isOptional, isTemperatureRelative, coolRelativeTemp,
                heatRelativeTemp);
        this.sendEmail = sendEmail;
    }

    @JsonCreator
    public SetpointDr(@JsonProperty("name") String name, @JsonProperty("message") String message,
            @JsonProperty("event") SetpointEvent event,
            @JsonProperty("sendEmail") boolean sendEmail) {
        this.name = name;
        this.message = message;
        this.event = event;
        this.sendEmail = sendEmail;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public SetpointEvent getEvent() {
        return event;
    }

    public boolean getSendEmail() {
        return sendEmail;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((event == null) ? 0 : event.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (sendEmail ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SetpointDr other = (SetpointDr) obj;
        if (event == null) {
            if (other.event != null)
                return false;
        } else if (!event.equals(other.event))
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (sendEmail != other.sendEmail)
            return false;
        return true;
    }

}
