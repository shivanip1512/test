package com.cannontech.amr.archivedValueExporter.model.dataRange;

import org.joda.time.LocalDate;

public class DataRange {
    private DataRangeType dataRangeType;
    private LocalDateRange localDateRange;
    private LocalDate endDate = LocalDate.now();
    private int daysPrevious;
    private ChangeIdRange changeIdRange;
    
    public DataRangeType getDataRangeType() {
        return dataRangeType;
    }
    public void setDataRangeType(DataRangeType dataRangeType) {
        this.dataRangeType = dataRangeType;
    }

    public LocalDateRange getLocalDateRange() {
        return localDateRange;
    }
    public void setLocalDateRange(LocalDateRange localDateRange) {
        this.localDateRange = localDateRange;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getDaysPrevious() {
        return daysPrevious;
    }
    public void setDaysPrevious(int daysPrevious) {
        this.daysPrevious = daysPrevious;
    }
    
    public ChangeIdRange getChangeIdRange() {
        return changeIdRange;
    }
    public void setChangeIdRange(ChangeIdRange changeIdRange) {
        this.changeIdRange = changeIdRange;
    }
}