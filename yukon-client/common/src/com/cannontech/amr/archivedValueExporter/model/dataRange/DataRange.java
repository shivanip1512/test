package com.cannontech.amr.archivedValueExporter.model.dataRange;

import org.joda.time.LocalDate;

public class DataRange {
    private DataRangeType dataRangeType;
    private DateRange dateRange;
    private LocalDate endDate = LocalDate.now();
    private int daysPrevious;
    private SinceLastChangeId sinceLastChangeId;
    
    public DataRangeType getDataRangeType() {
        return dataRangeType;
    }
    public void setDataRangeType(DataRangeType dataRangeType) {
        this.dataRangeType = dataRangeType;
    }
    
    public DateRange getDateRange() {
        return dateRange;
    }
    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
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
    
    public SinceLastChangeId getSinceLastChangeId() {
        return sinceLastChangeId;
    }
    public void setSinceLastChangeId(SinceLastChangeId sinceLastChangeId) {
        this.sinceLastChangeId = sinceLastChangeId;
    }
}