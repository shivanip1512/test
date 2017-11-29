package com.cannontech.maintenance.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CronExprOption;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.maintenance.MaintenanceHelper;
import com.cannontech.maintenance.MaintenanceSettingType;
import com.cannontech.maintenance.MaintenanceTaskType;
import com.cannontech.maintenance.dao.MaintenanceTaskDao;
import com.cannontech.maintenance.service.MaintenanceTaskService;
import com.cannontech.maintenance.task.MaintenanceTask;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.model.GlobalSetting;
import com.cannontech.system.model.MaintenanceSetting;
import com.cannontech.user.YukonUserContext;

public class MaintenanceTaskServiceImpl implements MaintenanceTaskService {
    private final static Logger log = YukonLogManager.getLogger(MaintenanceTaskServiceImpl.class);
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private MaintenanceTaskDao maintenanceTaskDao;
    @Autowired private MaintenanceHelper maintenanceHelper;
    private Map<MaintenanceTaskType, MaintenanceTask> maintenanceTaskMap = new HashMap<>();

    @Override
    public List<MaintenanceTask> getMaintenanceTasks() {
        List<MaintenanceTaskType> maintenanceTaskTypes = maintenanceTaskDao.getMaintenanceTaskTypes(true);
        List<MaintenanceTask> tasks = maintenanceTaskMap.entrySet().stream()
                                                                   .filter(e -> maintenanceTaskTypes.contains(e.getKey()))
                                                                   .map(e -> e.getValue())
                                                                   .collect(Collectors.toList());
        return tasks;
    }

    @Override
    public Instant getEndOfRunWindow() {
        TimeZone timeZone = YukonUserContext.system.getTimeZone();
        GlobalSetting businessDaysSetting = globalSettingDao.getSetting(GlobalSettingType.BUSINESS_HOURS_DAYS);
        GlobalSetting businessHourSetting = globalSettingDao.getSetting(GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME);
        String businessTimeSetting[] = ((String) businessHourSetting.getValue()).split(",");

        GlobalSetting maintenanceDaysSetting = globalSettingDao.getSetting(GlobalSettingType.MAINTENANCE_DAYS);
        GlobalSetting maintenanceHourSetting = globalSettingDao.getSetting(GlobalSettingType.MAINTENANCE_HOURS_START_STOP_TIME);
        String maintenanceTimeSetting[] = ((String) maintenanceHourSetting.getValue()).split(",");
        Instant instant = null;
        try {
            String cronForBusineesDay = TimeUtil.buildCronExpression(CronExprOption.WEEKDAYS,
                                                           Integer.parseInt(businessTimeSetting[0]),
                                                           (String) businessDaysSetting.getValue(),
                                                           'Y');
            Date nextBusineesDayStartTime = TimeUtil.getNextRuntime(new Date(), cronForBusineesDay, timeZone);

            String cronForMaintenanceDay = TimeUtil.buildCronExpression(CronExprOption.WEEKDAYS,
                                                        Integer.parseInt(maintenanceTimeSetting[0]),
                                                        (String) maintenanceDaysSetting.getValue(),
                                                        'Y');
            Date nextMaintenanceStartTime = TimeUtil.getNextRuntime(new Date(), cronForMaintenanceDay, timeZone);
            DateTime businessHourStartDate = new DateTime(nextBusineesDayStartTime);
            DateTime maintenanceHourStartDate = new DateTime(nextMaintenanceStartTime);
            boolean isMaintenanceFirst = businessHourStartDate.isAfter(maintenanceHourStartDate);
            DateTime endOfRunWindow = isMaintenanceFirst ? maintenanceHourStartDate : businessHourStartDate;
            instant = endOfRunWindow.toInstant();
        } catch (Exception e) {
            log.error("Parsing exception for end time", e);
        }
        return instant;
    }

    @Override
    public long getSecondsUntilRun() {
        TimeZone timeZone = YukonUserContext.system.getTimeZone();
        Date nextRunDataPruning = null;
        try {
            nextRunDataPruning = maintenanceHelper.getNextScheduledRunTime(timeZone);
        } catch (Exception e) {
            log.error("Parsing exception for next run time", e);
        }
        long curentTimeInSec = new DateTime().getMillis()/1000;
     return nextRunDataPruning.getTime() - curentTimeInSec;
    }

    @Override
    public Object getMaintenanceSettings(MaintenanceTaskType taskName, MaintenanceSettingType type) {
        List<MaintenanceSetting> allSettings = maintenanceTaskDao.getSettingsForMaintenanceTaskType(taskName);
        MaintenanceSetting settings = allSettings.stream().filter(setting -> setting.getAttribute() == type).findAny().get();
        return settings.getAttributeValue();
    }

    @Autowired
    public void setMaintenanceTasks(List<MaintenanceTask> listOfMaintenanceTask) {
        for (MaintenanceTask maintenanceTask : listOfMaintenanceTask) {
            maintenanceTaskMap.put(maintenanceTask.getMaintenanceTaskType(), maintenanceTask);
        }
    }

}
