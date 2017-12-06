package com.cannontech.maintenance;

import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.CronExprOption;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.model.GlobalSetting;
import com.google.common.collect.Sets;

/**
 * This class contains helper functions for Maintenance Tasks.
 */
public class MaintenanceHelper {

    @Autowired private GlobalSettingDao globalSettingDao;
    public DateTime getNextScheduledRunTime(TimeZone timeZone) throws Exception {
        DateTime nextScheduledRunTime = null;
        try {
            // Get Start-Stop time for business hour
            DateTime businessHourStart = getNextStartTime(timeZone, GlobalSettingType.BUSINESS_HOURS_DAYS);
            DateTime businessHourStop = getNextStopTime(timeZone, GlobalSettingType.BUSINESS_HOURS_DAYS);
            // Get Start-Stop time for maintenance hour
            DateTime maintenanceHourStart = getNextStartTime(timeZone, GlobalSettingType.MAINTENANCE_DAYS);
            DateTime maintenanceHourStop = getNextStopTime(timeZone, GlobalSettingType.MAINTENANCE_DAYS);
            // Non-overlapping condition of business and maintenance hour
            if ((maintenanceHourStart.isAfter(businessHourStop) && (maintenanceHourStop.isAfter(maintenanceHourStart))) 
                    || (businessHourStart.isAfter(maintenanceHourStop) && businessHourStop.isAfter(businessHourStart))) {
                // Select whichever ends first
                nextScheduledRunTime = businessHourStop.isAfter(maintenanceHourStop) ? maintenanceHourStop : businessHourStop;
            } else {
                // Business and maintenance hour overlapped. Select whichever ends last 
                nextScheduledRunTime = businessHourStop.isAfter(maintenanceHourStop) ? businessHourStop : maintenanceHourStop;
            }
        } catch (Exception e) {
            throw e;
        }
        return nextScheduledRunTime;
    }

    /**
     * Gets all the GlobalSettings for Maintenance.
     */
    public Set<GlobalSettingType> getGlobalSettingsForMaintenance(){
        return Sets.newHashSet(
            GlobalSettingType.BUSINESS_HOURS_DAYS,
            GlobalSettingType.MAINTENANCE_DAYS,
            GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME,
            GlobalSettingType.MAINTENANCE_HOURS_START_STOP_TIME);
    }

    /**
     * Return next scheduled start time for Business/Maintenance hour based on globalSettingType.
     */
    public DateTime getNextStartTime(TimeZone timeZone, GlobalSettingType globalSettingType) throws Exception {
        return getNextScheduledTime(timeZone, globalSettingType, true);
    }

    /**
     * Return next scheduled stop time for Business/Maintenance hour based on globalSettingType.
     */
    public DateTime getNextStopTime(TimeZone timeZone, GlobalSettingType globalSettingType) throws Exception {
        return getNextScheduledTime(timeZone, globalSettingType, false);
    }

    /**
     * Return next scheduled start time if isStartTime is true else return next scheduled stop time.
     */
    private DateTime getNextScheduledTime(TimeZone timeZone, GlobalSettingType globalSettingType, boolean isStartTime) throws Exception {
        GlobalSetting daysSetting ;
        GlobalSetting hourStartStopSetting ;
        if (globalSettingType == GlobalSettingType.BUSINESS_HOURS_DAYS) {
            daysSetting = globalSettingDao.getSetting(GlobalSettingType.BUSINESS_HOURS_DAYS);
            hourStartStopSetting = globalSettingDao.getSetting(GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME);
        } else {
            daysSetting = globalSettingDao.getSetting(GlobalSettingType.MAINTENANCE_DAYS);
            hourStartStopSetting = globalSettingDao.getSetting(GlobalSettingType.MAINTENANCE_HOURS_START_STOP_TIME);
        }
        String timeSettings[] = ((String) hourStartStopSetting.getValue()).split(",");
        Date nextScheduledTime = null;
        try {
            int timeInMinute;
            if (isStartTime) {
                timeInMinute = Integer.parseInt(timeSettings[0]);
            } else {
                // stopTime = startTime + duration
                // We are storing duration in seconds hence dividing with 60.
                timeInMinute = Integer.parseInt(timeSettings[0]) + Integer.parseInt(timeSettings[1]) / 60;
            }
            String cronFornextScheduledDay = TimeUtil.buildCronExpression(CronExprOption.WEEKDAYS,
                                                                          timeInMinute,
                                                                          (String) daysSetting.getValue(),
                                                                          'Y');
            nextScheduledTime = TimeUtil.getNextRuntime(new Date(), cronFornextScheduledDay, timeZone);
        } catch (Exception e) {
            throw e;
        }
        return new DateTime(nextScheduledTime);
    }
}
