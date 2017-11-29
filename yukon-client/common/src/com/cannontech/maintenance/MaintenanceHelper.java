package com.cannontech.maintenance;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.Days;
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
    public Date getNextScheduledRunTime(TimeZone timeZone) throws Exception {
        CronExprOption cronOption = CronExprOption.EVERYDAY;
        GlobalSetting businessDaysSetting = globalSettingDao.getSetting(GlobalSettingType.BUSINESS_HOURS_DAYS);
        GlobalSetting businessHourSetting = globalSettingDao.getSetting(GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME);
        String businessTimeSetting[] = ((String) businessHourSetting.getValue()).split(",");
        int businessHrsStartTime = Integer.parseInt(businessTimeSetting[0]);
        int businessHrsEndTime = Integer.parseInt(businessTimeSetting[1]);
        if ((businessHrsEndTime - businessHrsStartTime) / 60 == 24) {
            cronOption = CronExprOption.WEEKDAYS;
        }
        GlobalSetting maintenanceDaysSetting = globalSettingDao.getSetting(GlobalSettingType.MAINTENANCE_DAYS);
        GlobalSetting maintenanceHourSetting = globalSettingDao.getSetting(GlobalSettingType.MAINTENANCE_HOURS_START_STOP_TIME);
        String maintenanceTimeSetting[] = ((String) maintenanceHourSetting.getValue()).split(",");
        Date nextRunForDataPruning = null;
        try {
            String cronForDataPruning = TimeUtil.buildCronExpression(cronOption, Integer.parseInt(businessTimeSetting[1]),
                                                                  (String) businessDaysSetting.getValue(), 'N');
            nextRunForDataPruning = TimeUtil.getNextRuntime(new Date(), cronForDataPruning, timeZone);
            if (((String) maintenanceDaysSetting.getValue()).contains("Y")) {
                String cronForMaintenance = TimeUtil.buildCronExpression(CronExprOption.WEEKDAYS, Integer.parseInt(maintenanceTimeSetting[1]),
                                                                   (String) maintenanceDaysSetting.getValue(), 'Y');
                Date nextRunForMaintenance = TimeUtil.getNextRuntime(new Date(), cronForMaintenance, timeZone);
                DateTime startDate = new DateTime(nextRunForDataPruning);
                DateTime endDate = new DateTime(nextRunForMaintenance); // current date
                Days diff = Days.daysBetween(startDate, endDate);
                if (diff.getDays() == 0) {
                    SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
                    String time = null;
                    if (nextRunForMaintenance.after(nextRunForDataPruning)) {
                        time = localDateFormat.format(nextRunForMaintenance);
                    } else {
                        time = localDateFormat.format(nextRunForDataPruning);
                    }
                    time = localDateFormat.format(nextRunForMaintenance);
                    LocalTime localTime = LocalTime.parse(time);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(nextRunForDataPruning);
                    cal.set(Calendar.HOUR_OF_DAY, localTime.getHour());
                    cal.set(Calendar.MINUTE, localTime.getMinute());
                    nextRunForDataPruning = cal.getTime();
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return nextRunForDataPruning;
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
}
