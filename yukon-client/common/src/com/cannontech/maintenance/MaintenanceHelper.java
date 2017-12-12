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
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Sets;

/**
 * This class contains helper functions for Maintenance Tasks.
 */
public class MaintenanceHelper {

    @Autowired private GlobalSettingDao globalSettingDao;
    public DateTime getNextScheduledRunTime() throws Exception {
        DateTime nextScheduledRunTime = null;
        try {
            // Get Start-Stop time for business hour
            DateTime businessHourStart = getNextStartTime(GlobalSettingType.BUSINESS_HOURS_DAYS);
            DateTime businessHourStop = getNextStopTime(GlobalSettingType.BUSINESS_HOURS_DAYS);
            // Get Start-Stop time for external maintenance hour
            DateTime extMaintenanceHourStart = getNextStartTime(GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS);
            DateTime extMaintenanceHourStop = getNextStopTime( GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS);
            // Non-overlapping condition of business and external maintenance hour
            if ((extMaintenanceHourStart.isAfter(businessHourStop) && (extMaintenanceHourStop.isAfter(extMaintenanceHourStart))) 
                    || (businessHourStart.isAfter(extMaintenanceHourStop) && businessHourStop.isAfter(businessHourStart))) {
                // Select whichever ends first
                nextScheduledRunTime = businessHourStop.isAfter(extMaintenanceHourStop) ? extMaintenanceHourStop : businessHourStop;
            } else {
                // Business and external maintenance hour overlapped. Select whichever ends last 
                nextScheduledRunTime = businessHourStop.isAfter(extMaintenanceHourStop) ? businessHourStop : extMaintenanceHourStop;
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
            GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS,
            GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME,
            GlobalSettingType.EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME);
    }

    /**
     * Return next scheduled start time for Business/Maintenance hour based on globalSettingType.
     */
    public DateTime getNextStartTime(GlobalSettingType globalSettingType) throws Exception {
        return getNextScheduledTime(globalSettingType, true);
    }

    /**
     * Return next scheduled stop time for Business/Maintenance hour based on globalSettingType.
     */
    public DateTime getNextStopTime(GlobalSettingType globalSettingType) throws Exception {
        return getNextScheduledTime(globalSettingType, false);
    }

    /**
     * Return next scheduled start time if isStartTime is true else return next scheduled stop time.
     */
    private DateTime getNextScheduledTime(GlobalSettingType globalSettingType, boolean isStartTime) throws Exception {
        TimeZone timeZone = YukonUserContext.system.getTimeZone();
        GlobalSetting daysSetting ;
        GlobalSetting hourStartStopSetting ;
        if (globalSettingType == GlobalSettingType.BUSINESS_HOURS_DAYS) {
            daysSetting = globalSettingDao.getSetting(GlobalSettingType.BUSINESS_HOURS_DAYS);
            hourStartStopSetting = globalSettingDao.getSetting(GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME);
        } else {
            daysSetting = globalSettingDao.getSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS);
            hourStartStopSetting = globalSettingDao.getSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME);
        }
        String timeSettings[] = ((String) hourStartStopSetting.getValue()).split(",");
        Date nextScheduledTime = null;
        try {
            int timeInMinute;
            if (isStartTime) {
                timeInMinute = Integer.parseInt(timeSettings[0]);
            } else {
                // Use stop time
                timeInMinute = Integer.parseInt(timeSettings[1]);
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
