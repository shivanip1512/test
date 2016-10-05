package com.cannontech.dr.ecobee.message.partial;

import java.util.Comparator;

import org.joda.time.LocalDateTime;

import com.cannontech.dr.ecobee.message.JsonSerializers;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using=JsonSerializers.FROM_RUNTIME_REPORTS.class)
@JsonSerialize(using=JsonSerializers.TO_RUNTIME_REPORTS.class)
public class RuntimeReportRow {
    private final LocalDateTime thermostatTime;
    private final String eventName;
    private final Float indoorTemp;
    private final Float outdoorTemp;
    private final Float coolSetPoint;
    private final Float heatSetPoint;
    private final int runtime;

    public RuntimeReportRow(LocalDateTime thermostatTime, String eventName, Float indoorTemp, 
            Float outdoorTemp, Float coolSetPoint,
                            Float heatSetPoint, int runtime) {
        this.thermostatTime = thermostatTime;
        this.eventName = eventName;
        this.indoorTemp = indoorTemp;
        this.outdoorTemp = outdoorTemp;
        this.coolSetPoint = coolSetPoint;
        this.heatSetPoint = heatSetPoint;
        this.runtime = runtime;
    }

    public String getEventName() {
        return eventName;
    }

    public Float getIndoorTemp() {
        return indoorTemp;
    }

    public Float getOutdoorTemp() {
        return outdoorTemp;
    }

    public Float getCoolSetPoint() {
        return coolSetPoint;
    }

    public Float getHeatSetPoint() {
        return heatSetPoint;
    }

    public int getRuntime() {
        return runtime;
    }

    public LocalDateTime getThermostatTime() {
        return thermostatTime;
    }

    public static final Comparator<RuntimeReportRow> ON_THERMOSTAT_TIME = new Comparator<RuntimeReportRow>() {
        @Override
        public int compare(RuntimeReportRow rowA, RuntimeReportRow rowB) {
            return rowA.thermostatTime.compareTo(rowB.thermostatTime);
        }
    };
}
