package com.cannontech.web.bulk.model;

import java.util.Date;

public interface BulkOperationResults {

    public void setOperationStartTime(Date operationStartTime);
    public void setOperationStopTime(Date operationStopTime);
    
    public Date getOperationStartTime();
    public Date getOperationStopTime();
    
    public boolean isComplete();
}
