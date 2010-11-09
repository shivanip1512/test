package com.cannontech.web.dr;

import java.util.Date;
import java.util.List;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.LazyList;
import com.cannontech.dr.program.model.GearAdjustment;

public abstract class StartProgramBackingBeanBase {
    
    private boolean startNow;
    // This date represents "now" for when "startNow" is checked.
    private Date now;
    private Date startDate;
    private boolean scheduleStop;
    private Date stopDate;
    private boolean autoObserveConstraints;

    // only used for target cycle gears
    private boolean addAdjustments;
    private List<GearAdjustment> gearAdjustments =
        LazyList.ofInstance(GearAdjustment.class);

    public boolean isStartNow() {
        return startNow;
    }

    public void setStartNow(boolean startNow) {
        this.startNow = startNow;
    }

    public Date getNow() {
        return now;
    }

    public void setNow(Date now) {
        this.now = now;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public boolean isScheduleStop() {
        return scheduleStop;
    }

    public void setScheduleStop(boolean scheduleStop) {
        this.scheduleStop = scheduleStop;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public Date getActualStopDate() {
        return scheduleStop
            ? stopDate : CtiUtilities.get2035GregCalendar().getTime();
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    public boolean isAutoObserveConstraints() {
        return autoObserveConstraints;
    }

    public void setAutoObserveConstraints(boolean autoObserveConstraints) {
        this.autoObserveConstraints = autoObserveConstraints;
    }
    
    
    public boolean isAddAdjustments() {
        return addAdjustments;
    }

    public void setAddAdjustments(boolean addAdjustments) {
        this.addAdjustments = addAdjustments;
    }

    public List<GearAdjustment> getGearAdjustments() {
        return gearAdjustments;
    }

    public void setGearAdjustments(List<GearAdjustment> gearAdjustments) {
        this.gearAdjustments = gearAdjustments;
    }

}