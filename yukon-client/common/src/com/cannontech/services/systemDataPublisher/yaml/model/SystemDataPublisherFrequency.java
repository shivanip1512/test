package com.cannontech.services.systemDataPublisher.yaml.model;

import com.fasterxml.jackson.annotation.JsonValue;
/**
 * List of Frequencies to publish the system data.
 */
public enum SystemDataPublisherFrequency {

    ON_STARTUP_ONLY("OnStartupOnly", 0),
    EVERY_SIX_HOURS("EverySixHours", 6),
    DAILY("Daily", 24),
    ONCHANGE("OnChange")
    ;

    @JsonValue private final String name;

    private final Integer hours;

    private SystemDataPublisherFrequency(String name, Integer hours) {
        this.name = name;
        this.hours = hours;
    }

    private SystemDataPublisherFrequency(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    public Integer getHours() {
        return hours;
    }
}
