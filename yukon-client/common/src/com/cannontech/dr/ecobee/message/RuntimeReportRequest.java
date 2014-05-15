package com.cannontech.dr.ecobee.message;

import java.util.ArrayList;

import org.joda.time.Instant;

import com.cannontech.common.util.JsonSerializers;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.dr.ecobee.message.partial.Selection;
import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;
import com.fasterxml.jackson.core.JsonProcessingException;
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

    public RuntimeReportRequest(Instant startDate, int startInterval, Instant endDate, int endInterval,
                                Iterable<String> serialNumbers, Iterable<String> columns) {
        this.startDate = startDate;
        this.startInterval = startInterval;
        this.endDate = endDate;
        this.endInterval = endInterval;
        this.selection = new Selection(SelectionType.THERMOSTATS, serialNumbers);
        this.columns = columns;
    }

    public static void main(String...args) throws JsonProcessingException {
        ArrayList<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("sldjflsdfj");
        serialNumbers.add("sdf");
        
        ArrayList<String> columns = new ArrayList<>();
        columns.add("column_A");
        columns.add("column_B");
        
        RuntimeReportRequest d = new RuntimeReportRequest(new Instant(), 123, new Instant(11199146444545L), 1234234234,
                                                          serialNumbers, columns);

        System.out.println(JsonUtils.toJson(d));
    }

    @JsonSerialize(using=JsonSerializers.EcobeeDate.class)
    public Instant getStartDate() {
        return startDate;
    }

    @JsonSerialize(using=ToStringSerializer.class)
    public int getStartInterval() {
        return startInterval;
    }

    @JsonSerialize(using=JsonSerializers.EcobeeDate.class)
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

    @JsonSerialize(using=JsonSerializers.Csv.class)
    public Iterable<String> getColumns() {
        return columns;
    }
}
