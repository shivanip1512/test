package com.cannontech.dr.ecobee.message.partial;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RuntimeReport {
    private final String thermostatIdentifier;
    private final int rowCount;
    private final List<RuntimeReportRow> runtimeReports;
    
    @JsonCreator
    public RuntimeReport(@JsonProperty("thermostatIdentifier") String thermostatIdentifier,
                            @JsonProperty("rowCount") int rowCount,
                            @JsonProperty("rowList") List<RuntimeReportRow> runtimeReports) {
        this.thermostatIdentifier = thermostatIdentifier;
        this.rowCount = rowCount;
        this.runtimeReports = runtimeReports;
    }

    public String getThermostatIdentifier() {
        return thermostatIdentifier;
    }

    public int getRowCount() {
        return rowCount;
    }

    public List<RuntimeReportRow> getRuntimeReports() {
        return runtimeReports;
    }
    
    public static final Function<RuntimeReport, String> TO_SERIAL_NUMBER = new Function<RuntimeReport, String>() {
        @Override
        public String apply(RuntimeReport report) {
            return report.getThermostatIdentifier();
        }
    };
}
