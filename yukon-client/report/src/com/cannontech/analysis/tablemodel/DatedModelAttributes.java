package com.cannontech.analysis.tablemodel;

import java.util.Date;

public interface DatedModelAttributes  {
    public void setStartDate(Date startDate);
    public void setStopDate(Date stopDate);

    public Date getStartDate();
    public Date getStopDate();
    
}
