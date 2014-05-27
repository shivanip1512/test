package com.cannontech.dr.ecobee.message;

import org.joda.time.DateTimeFieldType;
import org.joda.time.Instant;

import com.cannontech.dr.ecobee.message.partial.Selection;
import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * Request serialized for https://www.ecobee.com/home/developer/api/documentation/v1/operations/get-runtime-report.shtml
 */
public class RuntimeReportRequest {
    private final Selection selection;
    private final Instant startDate;
    private final int startInterval;
    private final Instant endDate;
    private final int endInterval;
    private final Iterable<String> columns;

    public RuntimeReportRequest(Instant startDate, Instant endDate, Iterable<String> serialNumbers,
                                Iterable<String> columns) {
        this.startDate = startDate;
        this.startInterval = startDate.get(DateTimeFieldType.minuteOfDay()) / 5;
        this.endDate = endDate;
        this.endInterval = endDate.get(DateTimeFieldType.minuteOfDay()) / 5;
        this.selection = new Selection(SelectionType.THERMOSTATS, serialNumbers);
        this.columns = columns;
    }

    @JsonSerialize(using=EcobeeJsonSerializers.Date.class)
    public Instant getStartDate() {
        return startDate;
    }

    @JsonSerialize(using=ToStringSerializer.class)
    public int getStartInterval() {
        return startInterval;
    }

    @JsonSerialize(using=EcobeeJsonSerializers.Date.class)
    public Instant getEndDate() {
        return endDate;
    }

    @JsonSerialize(using=ToStringSerializer.class)
    public int getEndInterval() {
        return endInterval;
    }

    public Selection getSelection() {
        return selection;
    }

    @JsonSerialize(using=EcobeeJsonSerializers.Csv.class)
    public Iterable<String> getColumns() {
        return columns;
    }
}
