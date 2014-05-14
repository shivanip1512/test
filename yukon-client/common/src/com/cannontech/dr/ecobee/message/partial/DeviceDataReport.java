package com.cannontech.dr.ecobee.message.partial;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DeviceDataReport {
    private final String thermostatIdentifier;
    private final int rowCount;
    private final List<String> rowList;
    
    @JsonCreator
    public DeviceDataReport(@JsonProperty("thermostatIdentifier") String thermostatIdentifier,
                            @JsonProperty("rowCount") int rowCount,
                            @JsonProperty("rowList") List<String> rowList) {
        this.thermostatIdentifier = thermostatIdentifier;
        this.rowCount = rowCount;
        this.rowList = rowList;
    }

    public String getThermostatIdentifier() {
        return thermostatIdentifier;
    }

    public int getRowCount() {
        return rowCount;
    }

    public List<String> getRowList() {
        return rowList;
    }
}
