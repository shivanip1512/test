package com.cannontech.analysis.tablemodel;

import java.util.Date;

public abstract class BareDatedReportModelBase<T> extends BareReportModelBase<T> implements DatedModelAttributes {
    private Date startDate = null;
    private Date stopDate = null;
    
    public Date getStartDate() {
        return startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }


}
