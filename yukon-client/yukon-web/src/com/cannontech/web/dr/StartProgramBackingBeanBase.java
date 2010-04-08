/**
 * 
 */
package com.cannontech.web.dr;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.joda.time.DateTime;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.dr.program.model.GearAdjustment;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

@SuppressWarnings("unchecked")
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
        LazyList.decorate(Lists.newArrayList(), FactoryUtils.instantiateFactory(GearAdjustment.class));

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

    public void initDefaults(YukonUserContext userContext) {
        // With start and stop, it's important that we get the values
        // squared away up front and don't change them after checking
        // constraints so when we actually schedule the controlArea, we are
        // scheduling exactly what we checked for constraints.
        // We keep the "startNow" and "scheduleStop" booleans around so we
        // always know if the user chose those options.
        startNow = true;
        DateTime jodaNow = new DateTime(userContext.getJodaTimeZone());
        now = jodaNow.toDate();
        startDate = now;
        scheduleStop = true;
        stopDate = jodaNow.plusHours(4).toDate();
    }
}