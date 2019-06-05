package com.cannontech.dr.ecobee.message;

import java.util.Collection;

import org.joda.time.LocalDate;

import com.cannontech.dr.JsonSerializers.FROM_BASIC_CSV;
import com.cannontech.dr.JsonSerializers.FROM_LOCAL_DATE;
import com.cannontech.dr.JsonSerializers.TO_BASIC_CSV;
import com.cannontech.dr.JsonSerializers.TO_LOCAL_DATE;
import com.cannontech.dr.ecobee.message.partial.Selection;
import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Request serialized for https://www.ecobee.com/home/developer/api/documentation/v1/operations/post-create-runtime-report-job.shtml
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class RuntimeReportJobRequest {
    private final Selection selection;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Collection<String> columns;

    public RuntimeReportJobRequest(LocalDate startDate, LocalDate endDate, Collection<String> selectionMatch, SelectionType selectionType,
            Collection<String> columns) {
        this.selection = new Selection(selectionType, selectionMatch);
        this.startDate = startDate;
        this.endDate = endDate;
        this.columns = columns;
    }

  @JsonCreator
  public RuntimeReportJobRequest(@JsonProperty("selection") Selection selection, @JsonDeserialize(using=FROM_LOCAL_DATE.class) @JsonProperty("startDate") LocalDate startDate,
          @JsonDeserialize(using=FROM_LOCAL_DATE.class) @JsonProperty("endDate") LocalDate endDate,
          @JsonDeserialize(using=FROM_BASIC_CSV.class) @JsonProperty("columns") Collection<String> columns) {
    this.selection = selection;
    this.startDate = startDate;
    this.endDate = endDate;
    this.columns = columns;
  }

    @JsonSerialize(using=TO_LOCAL_DATE.class)
    public LocalDate getStartDate() {
        return startDate;
    }

    @JsonSerialize(using=TO_LOCAL_DATE.class)
    public LocalDate getEndDate() {
        return endDate;
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
        result = prime * result + ((selection == null) ? 0 : selection.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
        RuntimeReportJobRequest other = (RuntimeReportJobRequest) obj;
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
        return true;
    }
}

