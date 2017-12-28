package com.cannontech.maintenance;

import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CronExprOption;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.model.GlobalSetting;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Sets;

/**
 * This class contains helper functions for Maintenance Tasks.
 */
public class MaintenanceHelper {
    private final static Logger log = YukonLogManager.getLogger(MaintenanceHelper.class);
    // This will help to terminate searching of run time window when no run window is available for 
    // maintenance task.
    private static int maxSearchCount = 7;

    @Autowired private GlobalSettingDao globalSettingDao;

    /**
     * This method search next run time window for maintenance task and returns 
     * start time of run window.
     * @throws Exception , if no time window found for maintenance task.
     */
    public DateTime getNextRunTime () throws Exception {
        return getNextScheduledRunTime(new Date(), 1);
    }

    /**
     * Return next scheduled start time for Business/Maintenance hour based on globalSettingType.
     */
    public DateTime getNextStartTime(GlobalSettingType globalSettingType, Date refDate) throws Exception {
        return getNextScheduledTime(globalSettingType, true, refDate);
    }

    /**
     * Return next scheduled stop time for Business/Maintenance hour based on globalSettingType.
     */
    public DateTime getNextStopTime(GlobalSettingType globalSettingType, Date refDate) throws Exception {
        return getNextScheduledTime(globalSettingType, false, refDate);
    }

    /**
     * This method actually search next run window for maintenance task and return start time of run window.
     * @param refDate , reference Date to find run window
     * @param searchCount , count of searching run window. This will help to terminate searching if no run window found 
     * @throws Exception , if no time window found for maintenance task
     */
    private DateTime getNextScheduledRunTime(Date refDate, int searchCount) throws Exception {
        DateTime nextScheduledRunTime = null;
        // Get Start-Stop time for business hour
        DateTime businessHourStart = getNextStartTime(GlobalSettingType.BUSINESS_DAYS, refDate);
        DateTime businessHourStop = getNextStopTime(GlobalSettingType.BUSINESS_DAYS, refDate);
        // Get Start-Stop time for external maintenance hour
        DateTime extMaintenanceHourStart = getNextStartTime(GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS, refDate);
        DateTime extMaintenanceHourStop = getNextStopTime(GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS, refDate);

        DateTime currentTime = new DateTime();
        // Check if search count exceeds.
        if (searchCount > maxSearchCount) {
            // No time window is available for maintenance task
            log.info("No run window found for maintenance task");
            throw new Exception("No run window found for maintenance task");
        }
        // Check if no business and external maintenance hour selected
        if ((businessHourStart == null) && (extMaintenanceHourStart == null)) {
            // Best time for maintenance.Return current time
            // TODO This case might be changed while handling YUK-17664
            return currentTime;
        } else if (businessHourStart == null) {// Check if no business hour selected i.e only external maintenance hour selected
            // Check if external maintenance is running
            boolean isExtMaintenanceHourRunning = isRunning(currentTime, extMaintenanceHourStart, extMaintenanceHourStop);
            if (isExtMaintenanceHourRunning) {
                // If external maintenance is running ,extMaintenanceHourStop is next run time.
                //This further need to check with overlapping conditions.
                nextScheduledRunTime = getNextRunTimeForOverlap(extMaintenanceHourStop, businessHourStart,
                    businessHourStop, extMaintenanceHourStart, extMaintenanceHourStop, searchCount);
            } else {
                // External maintenance is not running, return current time
                return currentTime;
            }
        } else if (extMaintenanceHourStart == null) {// Check if no external maintenance hour selected i.e only business hour selected
            // Check if business hour is running
            boolean isBusinessHourRunning = isRunning(currentTime, businessHourStart, businessHourStop);
            if (isBusinessHourRunning) {
                // If business hour is running ,businessHourStop is next run time.
                //This need to further check with overlapping conditions.
                nextScheduledRunTime = getNextRunTimeForOverlap(businessHourStop, businessHourStart, businessHourStop,
                    extMaintenanceHourStart, extMaintenanceHourStop, searchCount);
            } else {
                // Business hour is not running, return current time.
                return currentTime;
            }
        } else { // Come here when both,business hour and external maintenance hour are selected
            // Check if both, business hour and external maintenance hour are in future
            if (searchCount == 1) { // No need to go here every time. This condition is for first time.
                boolean isBusinessHourRunning = isRunning(currentTime, businessHourStart, businessHourStop);
                boolean isExtMaintenanceHourRunning =
                    isRunning(currentTime, extMaintenanceHourStart, extMaintenanceHourStop);
                if (!isBusinessHourRunning && !isExtMaintenanceHourRunning) {
                    // Both are not running currently, valid time for maintenance task. Return current time.
                    return currentTime;
                }
            }
            // If you are here, this means both business hour and external maintenance are selected and either business hour
            // or external maintenance hour is running. Time to select next run time, based on selected hours
            
            // Non-overlapping condition of business and external maintenance hour
            if ((extMaintenanceHourStart.isAfter(businessHourStop) && (extMaintenanceHourStop.isAfter(extMaintenanceHourStart)))
                    || (businessHourStart.isAfter(extMaintenanceHourStop) && businessHourStop.isAfter(businessHourStart))) {
                // Select whichever ends first
                nextScheduledRunTime = businessHourStop.isAfter(extMaintenanceHourStop) ? extMaintenanceHourStop : businessHourStop;
            } else {
                // Business and external maintenance hour overlapped. Select whichever ends last
                nextScheduledRunTime = businessHourStop.isAfter(extMaintenanceHourStop) ? businessHourStop : extMaintenanceHourStop;
            }
            // If above calculated nextScheduledRunTime is overlapped with coming business and external maintenance hour
            // Further need to calculate nextRunTime.
            nextScheduledRunTime = getNextRunTimeForOverlap(nextScheduledRunTime, businessHourStart, businessHourStop,
                                                                      extMaintenanceHourStart, extMaintenanceHourStop, searchCount);
        }
        return nextScheduledRunTime;
    }

    /**
     * Gets all the GlobalSettings for Maintenance.
     */
    public Set<GlobalSettingType> getGlobalSettingsForMaintenance(){
        return Sets.newHashSet(
            GlobalSettingType.BUSINESS_DAYS,
            GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS,
            GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME,
            GlobalSettingType.EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME);
    }

    /**
     * Return next scheduled start time if isStartTime is true else return next scheduled stop time.
     */
    private DateTime getNextScheduledTime(GlobalSettingType globalSettingType, boolean isStartTime, Date refDate) throws Exception {
        TimeZone timeZone = YukonUserContext.system.getTimeZone();
        GlobalSetting daysSetting ;
        GlobalSetting hourStartStopSetting ;
        if (globalSettingType == GlobalSettingType.BUSINESS_DAYS) {
            daysSetting = globalSettingDao.getSetting(GlobalSettingType.BUSINESS_DAYS);
            hourStartStopSetting = globalSettingDao.getSetting(GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME);
        } else {
            daysSetting = globalSettingDao.getSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS);
            hourStartStopSetting = globalSettingDao.getSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME);
        }
        String timeSettings[] = ((String) hourStartStopSetting.getValue()).split(",");
        Date nextScheduledTime = null;
        String daysSettingValue = (String) daysSetting.getValue();
        try {
            int timeInMinute;
            if (isStartTime) {
                timeInMinute = Integer.parseInt(timeSettings[0]);
            } else {
                // Use stop time
                timeInMinute = Integer.parseInt(timeSettings[1]);
            }
            if (daysSettingValue.contains("Y")) {
                String cronFornextScheduledDay = TimeUtil.buildCronExpression(CronExprOption.WEEKDAYS,
                                                                              timeInMinute,
                                                                              daysSettingValue,
                                                                              'Y');
                nextScheduledTime = TimeUtil.getNextRuntime(refDate, cronFornextScheduledDay, timeZone);
            } else {
                // No day is selected. Return null.
                return null;
            }
            
        } catch (Exception e) {
            throw e;
        }
        return new DateTime(nextScheduledTime);
    }

    /**
     * This method will check if nextScheduledRunTime is overlapping with coming business or external maintenance days.
     * If true, it recalculate nextScheduledRunTime.
     */
    private DateTime getNextRunTimeForOverlap(DateTime nextScheduledRunTime, DateTime businessHourStart, DateTime businessHourStop, 
                         DateTime extMaintenanceHourStart, DateTime extMaintenanceHourStop, int searchCount) throws Exception {
        boolean isNextBHOverlapped = false;
        boolean isNextMHOverlapped = false;
        // Check, if calculated nextScheduledRunTime is overlapping with coming business hour
        if (businessHourStart != null) {
            isNextBHOverlapped = isNextDayOverlapped(nextScheduledRunTime, businessHourStart, businessHourStop);
        }
        // Check, if calculated nextScheduledRunTime is overlapping with coming external maintenance hour
        if (extMaintenanceHourStart != null) {
            if (TimeUtil.isDateEqualOrAfter(extMaintenanceHourStart, nextScheduledRunTime)) {
                isNextMHOverlapped = isNextDayOverlapped(nextScheduledRunTime, extMaintenanceHourStart, extMaintenanceHourStop);
            } else { // When current day settings are not sufficient for calculating overlap condition.
                DateTime nextExtMaintenanceHourStart = getNextStartTime(GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS,
                    new Date(extMaintenanceHourStart.getMillis()));
                isNextMHOverlapped = TimeUtil.isDateEqualOrAfter(nextScheduledRunTime, nextExtMaintenanceHourStart);
            }
        }
        // If calculated nextScheduledRunTime is overlapped. Calculate nextScheduledRunTime again
        if (isNextBHOverlapped || isNextMHOverlapped) {
            searchCount++;
            nextScheduledRunTime = getNextScheduledRunTime(new Date(nextScheduledRunTime.getMillis()), searchCount);
        }
       return nextScheduledRunTime;
    }
    
    /**
     * This method checks if nextScheduledRunTime is overlapping with coming business or external maintenance days.
     */
    private boolean isNextDayOverlapped(DateTime nextScheduledRunTime, DateTime hourStartTime, DateTime hourStopTime) {
        return TimeUtil.isDateEqualOrAfter(nextScheduledRunTime, hourStartTime)
                                          && TimeUtil.isDateEqualOrAfter(hourStartTime, hourStopTime);
    }
    
    /**
     * This method checks whether business or external maintenance hour are running currently.
     */
    private boolean isRunning(DateTime currentTime, DateTime hourStartTime, DateTime hourStopTime) {
        
        return TimeUtil.isDateEqualOrAfter(hourStartTime, currentTime) && TimeUtil.isDateEqualOrAfter(hourStartTime, hourStopTime);
    }
}
