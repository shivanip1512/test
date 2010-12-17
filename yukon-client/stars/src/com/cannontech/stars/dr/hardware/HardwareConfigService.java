package com.cannontech.stars.dr.hardware;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.CommandRequestRoute;
import com.cannontech.common.device.commands.CommandRequestRouteExecutor;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.events.loggers.InventoryConfigEventLogService;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.CommandScheduleDao;
import com.cannontech.stars.dr.hardware.dao.InventoryConfigTaskDao;
import com.cannontech.stars.dr.hardware.model.CommandSchedule;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTaskItem;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTaskItem.Status;
import com.cannontech.stars.dr.hardware.service.CommandRequestHardwareExecutor;
import com.cannontech.stars.util.WebClientException;
import com.google.common.collect.Lists;

public class HardwareConfigService {
    private static Logger log = YukonLogManager.getLogger(HardwareConfigService.class);

    private ScheduledExecutorService executor;
    private CommandScheduleDao commandScheduleDao;
    private InventoryConfigTaskDao inventoryConfigTaskDao;
    private com.cannontech.stars.dr.hardwareConfig.service.HardwareConfigService hardwareConfigService;
    private EnergyCompanyDao energyCompanyDao;
    private YukonUserDao yukonUserDao;
    private CommandRequestHardwareExecutor commandRequestHardwareExecutor;
    private CommandRequestRouteExecutor commandRequestRouteExecutor;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private InventoryConfigEventLogService inventoryConfigEventLogService;

    public class EnergyCompanyRunnable implements Runnable {
        int energyCompanyId;
        boolean hadErrors;

        EnergyCompanyRunnable(int energyCompanyId) {
            this.energyCompanyId = energyCompanyId;
        }

        void blockingCommandExecute(CommandRequestExecutionTemplate<CommandRequestRoute> template,
                final LiteStarsLMHardware hardware, String command, LiteYukonUser user)
                throws CommandCompletionException {
            CommandCompletionCallback<CommandRequestRoute> callback =
                new CommandCompletionCallbackAdapter<CommandRequestRoute>() {
                    @Override
                    public void receivedLastError(CommandRequestRoute command,
                            DeviceErrorDescription error) {
                        hadErrors = true;
                    }
            };
            hadErrors = false;
            WaitableCommandCompletionCallback<CommandRequestRoute> waitableCallback =
                new WaitableCommandCompletionCallback<CommandRequestRoute>(callback);
            commandRequestHardwareExecutor.executeWithTemplate(template, hardware, command,
                                                               waitableCallback);
            try {
                waitableCallback.waitForCompletion(60, 60);
            } catch (InterruptedException interruptedException) {
                hadErrors = true;
                log.error("interrupted waiting for command completion", interruptedException);
            } catch (TimeoutException timeoutException) {
                hadErrors = true;
                log.error("timed out waiting for command completion", timeoutException);
            }
        }

        public void processItem(InventoryConfigTaskItem item, Duration delayBetweenCommands) {
            log.trace("processing item " + item);
            Status status = Status.FAIL;  // fail if there are no commands
            int inventoryId = item.getInventoryId();
            LiteStarsLMHardware hardware = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(inventoryId);
            LiteYukonUser user = yukonUserDao.getLiteYukonUser(item.getInventoryConfigTask().getUserId());

            CommandRequestExecutionTemplate<CommandRequestRoute> template =
                commandRequestRouteExecutor.getExecutionTemplate(DeviceRequestType.INVENTORY_RECONFIG, user);
            try {
                List<String> commands =
                    hardwareConfigService.getConfigCommands(inventoryId, energyCompanyId,
                                                            item.getInventoryConfigTask().isSendInService());
                log.trace(item.getInventoryId() + " needs " + commands.size() + " commands");
                if (!commands.isEmpty()) {
                    status = Status.SUCCESS;
                    for (String command : commands) {
                        log.trace("processing command [" + command + "]");
                        try {
                            blockingCommandExecute(template, hardware, command, user);
                            if (hadErrors) {
                                log.error("error(s) executing command [" + command + "]");
                                status = Status.FAIL;
                            }
                        } catch (CommandCompletionException cce) {
                            log.error("exception executing command [" + command + "]");
                            status = Status.FAIL;
                        }
                        try {
                            Thread.sleep(delayBetweenCommands.getMillis());
                        } catch (InterruptedException ie) {
                            log.info("sleep interrupted");
                        }
                    }
                } else {
                    log.debug("no commands");
                }
            } catch (WebClientException wce) {
                log.error("error getting commands for inventory id " + inventoryId, wce);
            } catch (Exception exception) {
                log.error("unexpected error configuration device", exception);
            }
            inventoryConfigTaskDao.markComplete(item, status);
            if (status == Status.SUCCESS) {
                inventoryConfigEventLogService.itemConfigSucceeded(user, hardware.getManufacturerSerialNumber(),
                                                                   inventoryId, template.getContextId().getId());
            } else {
                inventoryConfigEventLogService.itemConfigFailed(user, hardware.getManufacturerSerialNumber(),
                                                                inventoryId, template.getContextId().getId());
            }
        }

        public boolean processItems() {
            log.debug("beginning of loop, ecId = " + energyCompanyId);
            LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompanyId);
            DateTimeZone timeZone = energyCompany.getDefaultDateTimeZone();

            boolean workProcessed = false;
            List<InventoryConfigTask> tasks = inventoryConfigTaskDao.getUnfinished(energyCompanyId);
            if (!tasks.isEmpty()) {
                log.debug("tasks not empty");
                List<CommandSchedule> schedules = commandScheduleDao.getAllEnabled(energyCompanyId);
                List<CommandSchedule> activeSchedules = Lists.newArrayList();
                for (CommandSchedule schedule : schedules) {
                    try {
                        CronExpression startTimeCron = new CronExpression(schedule.getStartTimeCronString());
                        ReadablePeriod runPeriod = schedule.getRunPeriod();
                        DateTime now = new DateTime(timeZone);
                        DateTime scheduleStartTime = new DateTime(startTimeCron.getNextValidTimeAfter(now.minus(runPeriod).toDate()),
                                                                  timeZone);
                        if (scheduleStartTime.isBefore(now)) {
                            activeSchedules.add(schedule);
                        }
                    } catch (ParseException parseException) {
                        log.error("error parsing cron string [" + schedule.getStartTimeCronString() +
                                  "]", parseException);
                    }
                }
                log.debug(activeSchedules.size() + " out of " + schedules.size() + " schedules active");
                if (!activeSchedules.isEmpty()) {
                    // Grab approximately a minute's worth of work and process it.
                    Duration delayBetweenCommands = null;
                    for (CommandSchedule activeSchedule : activeSchedules) {
                        ReadablePeriod thisDelayPeriod = activeSchedule.getDelayPeriod();
                        Duration thisDelay = new Period(thisDelayPeriod).toStandardDuration();
                        if (delayBetweenCommands == null || thisDelay.isShorterThan(delayBetweenCommands)) {
                            delayBetweenCommands = thisDelay;
                        }
                    }
                    log.debug("using delay of " + delayBetweenCommands);
                    // (We're estimating 2 commands per configuration.)
                    int numItems = delayBetweenCommands.isLongerThan(Duration.ZERO)
                        ? (int) Math.round(60.0 * 1000.0 / delayBetweenCommands.getMillis() / 2.0) : 100;
                    numItems = Math.max(Math.min(numItems, 100), 1);
                    log.debug("getting no more than " + numItems + " items");
                    Iterable<InventoryConfigTaskItem> items =
                        inventoryConfigTaskDao.getItems(numItems, energyCompanyId);
                    for (InventoryConfigTaskItem item : items) {
                        processItem(item, delayBetweenCommands);
                        workProcessed = true;
                    }
                }
            } else {
                log.debug("no unfinished tasks");
            }
            return workProcessed;
        }

        @Override
        public void run() {
            while (processItems()) {
                // Stuff in processItems; it returns false if there is nothing to do at the moment.
            }
        }
    }

    @PostConstruct
    public void init() throws InterruptedException {
        log.debug("HardwareConfigService - starting up");

        List<LiteEnergyCompany> allEnergyCompanies = energyCompanyDao.getAllEnergyCompanies();
        for (LiteEnergyCompany energyCompany : allEnergyCompanies) {
            log.debug("HardwareConfigService - starting task for ecId = " + energyCompany.getEnergyCompanyID());
            Runnable task = new EnergyCompanyRunnable(energyCompany.getEnergyCompanyID());
            executor.scheduleWithFixedDelay(task, 1, 1, TimeUnit.MINUTES);
        }
    }

    @Autowired
    public void setExecutor(@Qualifier("hardwareConfig") ScheduledExecutorService executor) {
        this.executor = executor;
    }

    @Autowired
    public void setCommandScheduleDao(CommandScheduleDao commandScheduleDao) {
        this.commandScheduleDao = commandScheduleDao;
    }

    @Autowired
    public void setInventoryConfigTaskDao(
            InventoryConfigTaskDao inventoryConfigTaskDao) {
        this.inventoryConfigTaskDao = inventoryConfigTaskDao;
    }

    @Autowired
    public void setHardwareConfigService(
            com.cannontech.stars.dr.hardwareConfig.service.HardwareConfigService hardwareConfigService) {
        this.hardwareConfigService = hardwareConfigService;
    }

    @Autowired
    public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
        this.energyCompanyDao = energyCompanyDao;
    }

    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    @Autowired
    public void setCommandRequestHardwareExecutor(
            CommandRequestHardwareExecutor commandRequestHardwareExecutor) {
        this.commandRequestHardwareExecutor = commandRequestHardwareExecutor;
    }

    @Autowired
    public void setCommandRequestRouteExecutor(CommandRequestRouteExecutor commandRequestRouteExecutor) {
        this.commandRequestRouteExecutor = commandRequestRouteExecutor;
    }

    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }

    @Autowired
    public void setInventoryConfigEventLogService(
            InventoryConfigEventLogService inventoryConfigEventLogService) {
        this.inventoryConfigEventLogService = inventoryConfigEventLogService;
    }
}
