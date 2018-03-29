package com.cannontech.maintenance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
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

    /**
     * Return next available interval > minInterval occurring after startTime
     */
    public Interval getNextAvailableRunTime(Instant startTime, Duration minInterval) throws Exception {
        List<Interval> exclusionIntervals = new ArrayList<>();
        // Get all selected Business Days start-stop intervals
        exclusionIntervals.addAll(getAllSelectedInterval(GlobalSettingType.BUSINESS_DAYS, startTime.toDate()));
        // Get all selected External Maintenance Days start-stop intervals
        exclusionIntervals.addAll(getAllSelectedInterval(GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS, startTime.toDate()));
        Interval availableRunTime = null;
        if (!exclusionIntervals.isEmpty()) {
            // Arrange all time interval in ascending order to find first available Run-window
            exclusionIntervals.sort((i1, i2) -> i1.getStart().compareTo(i2.getStart()));
            // Special case when startTime is not overlapping with next Business Day or External Maintenance Day
            // Return interval with current time and start of coming Business Day or External Maintenance Day
            if (TimeUtil.isDateEqualOrAfter(exclusionIntervals.get(0).getStart(), startTime.toDateTime())) {
                Interval timeInterval = getValidInterval(startTime.toDateTime(), exclusionIntervals.get(0).getStart(), minInterval);
                if (timeInterval != null) {
                     return timeInterval;
                }
            }
            // Search valid interval within all available Business Days and External Maintenance Days Intervals
            for (int i = 0; i < exclusionIntervals.size() - 1; i++) {
                if (exclusionIntervals.get(i).overlaps(exclusionIntervals.get(i + 1)) 
                        || exclusionIntervals.get(i).abuts(exclusionIntervals.get(i + 1))) {
                    continue;
                } else {
                    Interval timeInterval = getValidInterval(exclusionIntervals.get(i).getEnd(),
                                                             exclusionIntervals.get(i + 1)
                                                                               .getStart(),
                                                             minInterval);
                    if (timeInterval != null) {
                        availableRunTime = timeInterval;
                        break;
                    }
                }
            }
        } else {
            // No day is selected.Set default interval.
            DateTime currentTime = new DateTime(startTime);
            availableRunTime = new Interval(currentTime, currentTime.plusDays(1));
        }
        return availableRunTime;
    }

    /**
     * Return time interval which is greater than minInterval else null
     */
    private Interval getValidInterval (DateTime startTime, DateTime stopTime, Duration minInterval) {
        Interval timeInterval = new Interval(startTime, stopTime);
        Duration timeDuration = new Duration(timeInterval.getEndMillis() - timeInterval.getStartMillis());
        if (timeDuration.isLongerThan(minInterval) || timeDuration.equals(minInterval)) {
            return timeInterval;
        } else {
           return null;
        }
    }

    /**
     * Return list of intervals for Business or External Maintenance Days which are coming after refDate
     */
    private List<Interval> getAllSelectedInterval(GlobalSettingType globalSettingType, Date refDate) throws Exception {
        TimeZone timeZone = YukonUserContext.system.getTimeZone();
        GlobalSetting daysSetting;
        GlobalSetting hourStartStopSetting;
        if (globalSettingType == GlobalSettingType.BUSINESS_DAYS) {
            daysSetting = globalSettingDao.getSetting(GlobalSettingType.BUSINESS_DAYS);
            hourStartStopSetting = globalSettingDao.getSetting(GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME);
        } else {
            daysSetting = globalSettingDao.getSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS);
            hourStartStopSetting = globalSettingDao.getSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME);
        }

        String timeSettings[] = ((String) hourStartStopSetting.getValue()).split(",");
        int startTimeInMinute = Integer.parseInt(timeSettings[0]);
        int stopTimeInMinute = Integer.parseInt(timeSettings[1]);
        
        String daysSettingValue = (String) daysSetting.getValue();
        int daysCount = (int) daysSettingValue.chars().filter(c -> c == 'Y').count();

        List<Interval> exclusionIntervalList = new ArrayList<>();
        Interval runTimeWindow = null;
        Date nextRefDate = refDate;
        if (daysSettingValue.contains("Y")) {
            for (int i = 0; i <= daysCount; i++) {
                String cronFornextScheduledStartDay = TimeUtil.buildCronExpression(CronExprOption.WEEKDAYS,
                                                                                   startTimeInMinute,
                                                                                   daysSettingValue,
                                                                                   'Y');
                String cronFornextScheduledStopDay = TimeUtil.buildCronExpression(CronExprOption.WEEKDAYS,
                                                                                  stopTimeInMinute,
                                                                                  daysSettingValue,
                                                                                  'Y');
                DateTime nextStartDateTime = new DateTime(TimeUtil.getNextRuntime(nextRefDate, cronFornextScheduledStartDay, timeZone));
                DateTime nextStopDateTime = new DateTime(TimeUtil.getNextRuntime(nextRefDate, cronFornextScheduledStopDay, timeZone));
                // If start time is after stop i.e either Business Day or
                // External Maintenance Day is overlapping with current time.
                if (TimeUtil.isDateEqualOrAfter(nextStartDateTime, nextStopDateTime)) {
                    nextStartDateTime = new DateTime(nextRefDate);
                }
                runTimeWindow = new Interval(nextStartDateTime.toInstant(), nextStopDateTime.toInstant());
                exclusionIntervalList.add(runTimeWindow);
                nextRefDate = nextStopDateTime.toDate();
            }
        }
        return exclusionIntervalList;
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
}
