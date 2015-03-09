package com.cannontech.stars.dr.hardware.service;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.CommandRequestRouteExecutor;
import com.cannontech.common.device.commands.WaitableCommandCompletionCallbackFactory;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.InventoryConfigEventLogService;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.dao.CommandScheduleDao;
import com.cannontech.stars.dr.hardware.dao.InventoryConfigTaskDao;
import com.cannontech.stars.dr.hardware.model.CommandSchedule;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTaskItem;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTaskItem.Status;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.google.common.collect.Lists;

public class HardwareConfigService {
    private final static Logger log = YukonLogManager.getLogger(HardwareConfigService.class);

    @Autowired private com.cannontech.stars.dr.hardwareConfig.HardwareConfigService hardwareConfigService;
    @Autowired private CommandRequestRouteExecutor commandRequestRouteExecutor;
    @Autowired private CommandScheduleDao commandScheduleDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private EnergyCompanyService ecService;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private InventoryConfigEventLogService inventoryConfigEventLogService;
    @Autowired private InventoryConfigTaskDao inventoryConfigTaskDao;
    @Autowired private LmHardwareCommandRequestExecutor lmHardwareCommandRequestExecutor;
    @Autowired private WaitableCommandCompletionCallbackFactory waitableCommandCompletionCallbackFactory;
    @Autowired private LmHardwareCommandService commandService;
    @Autowired private YukonListDao yukonListDao;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    private class EnergyCompanyRunnable implements Runnable {
        int energyCompanyId;

        private EnergyCompanyRunnable(int energyCompanyId) {
            this.energyCompanyId = energyCompanyId;
        }

        private void processItem(InventoryConfigTaskItem item)
                throws InterruptedException {
            log.debug("Configuration task" + item);
            LiteLmHardwareBase hardware = inventoryBaseDao.getHardwareByInventoryId(item.getInventoryId());
            HardwareType type = HardwareType.valueOf(yukonListDao.getYukonListEntry(hardware.getLmHardwareTypeID()).getYukonDefID());
            LiteYukonUser user = item.getInventoryConfigTask().getUser();

            try {
                if (type.isConfigurable()) {
                    YukonEnergyCompany yec = ecDao.getEnergyCompanyByInventoryId(hardware.getInventoryID());

                    LmHardwareCommand command = new LmHardwareCommand();
                    command.setDevice(hardware);
                    command.setType(LmHardwareCommandType.CONFIG);
                    command.setUser(yec.getEnergyCompanyUser());
                    command.getParams().put(LmHardwareCommandParam.BULK, true);
                    boolean sendInService = item.getInventoryConfigTask().isSendInService();
                    if (sendInService) {
                        command.getParams().put(LmHardwareCommandParam.FORCE_IN_SERVICE, true);
                    }
                    commandService.sendConfigCommand(command, true);
                    inventoryConfigTaskDao.markComplete(item, Status.SUCCESS);
                    inventoryConfigEventLogService.itemConfigSucceeded(user, hardware.getManufacturerSerialNumber(),
                        hardware.getInventoryID());
                } else {
                    inventoryConfigTaskDao.markComplete(item, Status.UNSUPPORTED);
                    inventoryConfigEventLogService.itemConfigUnsupported(user, hardware.getManufacturerSerialNumber(),
                        hardware.getInventoryID());
                }
            } catch (CommandCompletionException e) {
                log.error("Unable to send config command inventory id=" + item.getInventoryId(), e);
                inventoryConfigTaskDao.markComplete(item, Status.FAIL);
                inventoryConfigEventLogService.itemConfigFailed(user, hardware.getManufacturerSerialNumber(),
                    hardware.getInventoryID());
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
                        log.error("error parsing cron string [" + schedule.getStartTimeCronString() + "]",
                            parseException);
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
                    int numItems =
                        delayBetweenCommands.isLongerThan(Duration.ZERO)
                            ? (int) Math.round(60.0 * 1000.0 / delayBetweenCommands.getMillis() / 2.0) : 100;
                    numItems = Math.max(Math.min(numItems, 100), 1);
                    log.trace("getting no more than " + numItems + " items");
                    Iterable<InventoryConfigTaskItem> items =
                        inventoryConfigTaskDao.getItems(numItems, energyCompanyId);
                    int actualNumItems = 0;
                    for (InventoryConfigTaskItem item : items) {
                        actualNumItems++;
                        processItem(item);
                        workProcessed = true;
                        if (delayBetweenCommands != null) {
                            log.debug("Waiting " + delayBetweenCommands.getStandardSeconds() + " seconds.");
                            try {
                                Thread.sleep(delayBetweenCommands.getMillis());
                            } catch (InterruptedException e) {
                                // ignore
                            }
                            log.debug("Done");
                        }
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

        Collection<EnergyCompany> allEnergyCompanies = ecDao.getAllEnergyCompanies();
        for (EnergyCompany energyCompany : allEnergyCompanies) {
            log.debug("HardwareConfigService - starting task for ecId = " + energyCompany.getId());
            Runnable task = new EnergyCompanyRunnable(energyCompany.getId());
            executor.scheduleWithFixedDelay(task, 1, 1, TimeUnit.MINUTES);
        }
    }
}
