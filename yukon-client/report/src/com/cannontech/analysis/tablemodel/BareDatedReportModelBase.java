package com.cannontech.analysis.tablemodel;

import java.util.Date;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

public abstract class BareDatedReportModelBase<T> extends BareReportModelBase<T> implements DatedModelAttributes {
    private Date startDate = null;
    private Date stopDate = null;
    
    public Date getStartDate() {
        return startDate;
    }

    public ReadableInstant getStartDateAsInstant() {
        return startDate == null ? null : new Instant(startDate);
    }

    public Date getStopDate() {
        return stopDate;
    }

    public ReadableInstant getStopDateAsInstant() {
        return stopDate == null ? null : new Instant(stopDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }
    
    @Override
    public boolean useStartDate(){
        return true;
    }

    @Override
    public boolean useStopDate(){
        return true;
    }
}