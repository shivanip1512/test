/**
 * 
 */
package com.cannontech.web.dr;

import java.util.Date;

public class StopProgramBackingBeanBase {
    private boolean stopNow;
    private Date stopDate;
    private boolean autoObserveConstraints;
   
    public boolean isAutoObserveConstraints() {
        return autoObserveConstraints;
    }

    public boolean isStopNow() {
        return stopNow;
    }

    public void setStopNow(boolean stopNow) {
        this.stopNow = stopNow;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    public void setAutoObserveConstraints(boolean autoObserveConstraints) {
        this.autoObserveConstraints = autoObserveConstraints;
    }

}