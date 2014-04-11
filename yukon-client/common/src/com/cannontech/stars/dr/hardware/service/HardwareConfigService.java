package com.cannontech.stars.dr.hardware.service;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.CommandRequestRoute;
import com.cannontech.common.device.commands.CommandRequestRouteExecutor;
import com.cannontech.common.device.commands.WaitableCommandCompletionCallbackFactory;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.events.loggers.InventoryConfigEventLogService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.dao.CommandScheduleDao;
import com.cannontech.stars.dr.hardware.dao.InventoryConfigTaskDao;
import com.cannontech.stars.dr.hardware.model.CommandSchedule;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTaskItem;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTaskItem.Status;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.google.common.collect.Lists;

public class HardwareConfigService {
    private static Logger log = YukonLogManager.getLogger(HardwareConfigService.class);

    @Autowired private CommandScheduleDao commandScheduleDao;
    @Autowired private CommandRequestRouteExecutor commandRequestRouteExecutor;
    @Autowired private EnergyCompanyService ecService;
    @Autowired private com.cannontech.stars.dr.hardwareConfig.HardwareConfigService hardwareConfigService;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private InventoryConfigEventLogService inventoryConfigEventLogService;
    @Autowired private InventoryConfigTaskDao inventoryConfigTaskDao;
    @Autowired private LmHardwareCommandRequestExecutor lmHardwareCommandRequestExecutor;
    @Autowired private WaitableCommandCompletionCallbackFactory waitableCommandCompletionCallbackFactory;
    @Autowired private YukonEnergyCompanyService yEcService;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    private class EnergyCompanyRunnable implements Runnable {
        int energyCompanyId;
        boolean hadErrors;

        private EnergyCompanyRunnable(int energyCompanyId) {
            this.energyCompanyId = energyCompanyId;
        }

        private void blockingCommandExecute(CommandRequestExecutionTemplate<CommandRequestRoute> template,
                final LiteLmHardwareBase hardware, String command)
                throws CommandCompletionException {
            CommandCompletionCallback<CommandRequestRoute> callback =
                new CommandCompletionCallbackAdapter<CommandRequestRoute>() {
                    @Override
                    public void receivedLastError(CommandRequestRoute command,
                            SpecificDeviceErrorDescription error) {
                        hadErrors = true;
                    }
            };
            hadErrors = false;
            WaitableCommandCompletionCallback<CommandRequestRoute> waitableCallback =
                waitableCommandCompletionCallbackFactory.createWaitable(callback);
            lmHardwareCommandRequestExecutor.executeWithTemplate(template, hardware, command,
                                                               waitableCallback);
            try {
                waitableCallback.waitForCompletion();
            } catch (InterruptedException interruptedException) {
                hadErrors = true;
                log.error("interrupted waiting for command completion", interruptedException);
            } catch (TimeoutException timeoutException) {
                hadErrors = true;
                log.error("timed out waiting for command completion", timeoutException);
            }
        }

        private void processItem(InventoryConfigTaskItem item, Duration delayBetweenCommands)
                throws InterruptedException {
            log.trace("processing item " + item);
            Status status = Status.FAIL;
            int inventoryId = item.getInventoryId();
            LiteLmHardwareBase hardware = inventoryBaseDao.getHardwareByInventoryId(inventoryId);
            LiteYukonUser user = item.getInventoryConfigTask().getUser();

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
                        blockingCommandExecute(template, hardware, command);
                        if (hadErrors) {
                            log.error("error(s) executing command [" + command +
                                      "]; inventory id=" + inventoryId +
                                      "; CMRE id=" + template.getContextId().getId());
                            status = Status.FAIL;
                            break;
                        }
                        Thread.sleep(delayBetweenCommands.getMillis());
                    }
                } else {
                    log.debug("no commands");
                }
            } catch (CommandCompletionException cce) {
                log.error("exception executing command; inventory id=" + inventoryId +
                          "; CMRE id=" + template.getContextId().getId() +
                          "; msg=[" + cce.getMessage() + "]");
                status = Status.FAIL;
            } catch (InterruptedException ie) {
                throw ie;
            } catch (Exception exception) {
                log.error("unexpected error configuring device; inventory id=" + inventoryId +
                          "; CMRE id=" + template.getContextId().getId() +
                          "; msg=[" + exception.getMessage() + "]");
                status = Status.FAIL;
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

        private boolean processItems() throws InterruptedException {
            log.trace("processing a chunk of work for ecId = " + energyCompanyId);
            TimeZone ecTimeZone = ecService.getDefaultTimeZone(energyCompanyId);
            DateTimeZone energyCompanyTimeZone = DateTimeZone.forTimeZone(ecTimeZone);

            boolean workProcessed = false;
            List<InventoryConfigTask> tasks = inventoryConfigTaskDao.getUnfinished(energyCompanyId);
            log.trace("tasks has " + tasks.size() + " items");
            if (!tasks.isEmpty()) {
                List<CommandSchedule> schedules = commandScheduleDao.getAllEnabled(energyCompanyId);
                List<CommandSchedule> activeSchedules = Lists.newArrayList();
                DateTime now = new DateTime(energyCompanyTimeZone);
                for (CommandSchedule schedule : schedules) {
                    try {
                        CronExpression startTimeCron = new CronExpression(schedule.getStartTimeCronString());
                        ReadablePeriod runPeriod = schedule.getRunPeriod();
                        Date earliestPossibleStartTime = now.minus(runPeriod).toDate();
                        Date nextValidTime = startTimeCron.getNextValidTimeAfter(earliestPossibleStartTime);
                        if (nextValidTime != null) {
                            Instant scheduleStart = new Instant((nextValidTime).getTime());
                            if (scheduleStart.isBefore(now)) {
                                activeSchedules.add(schedule);
                            }
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
                    log.trace("using delay of " + delayBetweenCommands);
                    // (We're estimating 2 commands per configuration.)
                    int numItems = delayBetweenCommands.isLongerThan(Duration.ZERO)
                        ? (int) Math.round(60.0 * 1000.0 / delayBetweenCommands.getMillis() / 2.0) : 100;
                    numItems = Math.max(Math.min(numItems, 100), 1);
                    log.trace("getting no more than " + numItems + " items");
                    Iterable<InventoryConfigTaskItem> items =
                        inventoryConfigTaskDao.getItems(numItems, energyCompanyId);
                    int actualNumItems = 0;
                    for (InventoryConfigTaskItem item : items) {
                        actualNumItems++;
                        processItem(item, delayBetweenCommands);
                        workProcessed = true;
                    }
                    log.debug("proccessed " + actualNumItems + " items");
                }
            }
            return workProcessed;
        }

        @Override
        public void run() {
            try {
                while (processItems()) {
                    // Stuff in processItems; it returns false if there is nothing to do at the moment.
                }
            } catch (InterruptedException ie) {
                log.info("interrupted");
            }
        }
    }

    @PostConstruct
    public void init() {
        log.debug("HardwareConfigService - starting up");

        Collection<EnergyCompany> allEnergyCompanies = yEcService.getAllEnergyCompanies();
        for (EnergyCompany energyCompany : allEnergyCompanies) {
            log.debug("HardwareConfigService - starting task for ecId = " + energyCompany.getId());
            Runnable task = new EnergyCompanyRunnable(energyCompany.getId());
            executor.scheduleWithFixedDelay(task, 1, 1, TimeUnit.MINUTES);
        }
    }
}
