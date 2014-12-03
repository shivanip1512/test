package com.cannontech.amr.archivedValueExporter.model.dataRange;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class DataRange {
    private DataRangeType dataRangeType;
    private LocalDateRange localDateRange;
    private LocalDate endDate = LocalDate.now();
    private LocalTime time = new LocalTime(0, 0);
    private int daysPrevious = 1;
    private ChangeIdRange changeIdRange;
    private int daysOffset = 0;
    boolean timeSelected;
    
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

    public int getDaysOffset() {
        return daysOffset;
    }
    
    public void setDaysOffset(int daysOffset) {
        this.daysOffset = daysOffset;
    }
    
    public LocalTime getTime() {
        return time;
    }
    
    public void setTime(LocalTime time) {
        this.time = time;
    }
    
    public boolean isTimeSelected() {
        return timeSelected;
    }
    
    public void setTimeSelected(boolean timeSelected) {
        this.timeSelected = timeSelected;
    }
}