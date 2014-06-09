package com.cannontech.dr.ecobee.message;

import java.util.Collection;

import org.joda.time.DateTimeFieldType;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.dr.ecobee.message.EcobeeJsonSerializers.FROM_BASIC_CSV;
import com.cannontech.dr.ecobee.message.EcobeeJsonSerializers.FROM_DATE;
import com.cannontech.dr.ecobee.message.EcobeeJsonSerializers.TO_BASIC_CSV;
import com.cannontech.dr.ecobee.message.EcobeeJsonSerializers.TO_DATE;
import com.cannontech.dr.ecobee.message.partial.Selection;
import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * Request serialized for https://www.ecobee.com/home/developer/api/documentation/v1/operations/get-runtime-report.shtml
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class RuntimeReportRequest {
    private final Selection selection;
    private final Instant startDate;
    private final int startInterval;
    private final Instant endDate;
    private final int endInterval;
    
    private final Collection<String> columns;

    public RuntimeReportRequest(Instant startDate, Instant endDate, Collection<String> serialNumbers,
            Collection<String> columns) {
        selection = new Selection(SelectionType.THERMOSTATS, serialNumbers);
        startInterval = startDate.get(DateTimeFieldType.minuteOfDay()) / 5;
        this.startDate = startDate.minus(Duration.standardMinutes(startInterval * 5));
        endInterval = endDate.get(DateTimeFieldType.minuteOfDay()) / 5;
        this.endDate = endDate.minus(Duration.standardMinutes(endInterval * 5));
        this.columns = columns;
    }

  @JsonCreator
  public RuntimeReportRequest(@JsonDeserialize(using=FROM_DATE.class) @JsonProperty("startDate") Instant startDate,
          @JsonDeserialize(using=FROM_DATE.class) @JsonProperty("endDate") Instant endDate,
          @JsonProperty("selection") Selection selection, 
          @JsonDeserialize(using=FROM_BASIC_CSV.class) @JsonProperty("columns") Collection<String> columns,
          @JsonProperty("startInterval") int startInterval, 
          @JsonProperty("endInterval") int endInterval) {
    this.selection = selection;
    this.startDate = startDate;
    this.startInterval = startInterval;
    this.endDate = endDate;
    this.endInterval = endInterval;
    this.columns = columns;
  }

    @JsonSerialize(using=TO_DATE.class)
    public Instant getStartDate() {
        return startDate;
    }

    @JsonSerialize(using=ToStringSerializer.class)
    public int getStartInterval() {
        return startInterval;
    }

    @JsonSerialize(using=TO_DATE.class)
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

    @JsonSerialize(using=TO_BASIC_CSV.class)
    public Collection<String> getColumns() {
        return columns;
    }
}
