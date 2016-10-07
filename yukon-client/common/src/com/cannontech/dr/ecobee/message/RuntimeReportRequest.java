package com.cannontech.dr.ecobee.message;

import java.util.Collection;

import org.joda.time.DateTimeFieldType;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.dr.JsonSerializers.FROM_BASIC_CSV;
import com.cannontech.dr.JsonSerializers.FROM_DATE;
import com.cannontech.dr.JsonSerializers.TO_BASIC_CSV;
import com.cannontech.dr.JsonSerializers.TO_DATE;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((columns == null) ? 0 : columns.hashCode());
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + endInterval;
        result = prime * result + ((selection == null) ? 0 : selection.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + startInterval;
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
        RuntimeReportRequest other = (RuntimeReportRequest) obj;
        if (columns == null) {
            if (other.columns != null) {
                return false;
            }
        } else if (!columns.equals(other.columns)) {
            return false;
        }
        if (endDate == null) {
            if (other.endDate != null) {
                return false;
            }
        } else if (!endDate.equals(other.endDate)) {
            return false;
        }
        if (endInterval != other.endInterval) {
            return false;
        }
        if (selection == null) {
            if (other.selection != null) {
                return false;
            }
        } else if (!selection.equals(other.selection)) {
            return false;
        }
        if (startDate == null) {
            if (other.startDate != null) {
                return false;
            }
        } else if (!startDate.equals(other.startDate)) {
            return false;
        }
        if (startInterval != other.startInterval) {
            return false;
        }
        return true;
    }
}
