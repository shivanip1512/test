package com.cannontech.dr.ecobee.message.partial;

import com.cannontech.common.util.JsonSerializers;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using=JsonSerializers.EcobeeRuntimeReportRow.class)
public class RuntimeReportRow {
    private final String dateStr;
    private final String eventName;
    private final Float indoorTemp;
    private final Float outdoorTemp;
    private final Float coolSetPoint;
    private final Float heatSetPoint;
    private final int runtime;

    public RuntimeReportRow(String dateStr, String eventName, Float indoorTemp, Float outdoorTemp, Float coolSetPoint,
                            Float heatSetPoint, int runtime) {
        this.dateStr = dateStr;
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

    public String getDateStr() {
        return dateStr;
    }
}
