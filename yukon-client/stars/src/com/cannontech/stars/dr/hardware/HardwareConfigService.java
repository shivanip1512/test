package com.cannontech.stars.dr.hardware;

import java.text.ParseException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
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

public class HardwareConfigService implements Runnable {
    private static Logger log = YukonLogManager.getLogger(HardwareConfigService.class);

    private CommandScheduleDao commandScheduleDao;
    private InventoryConfigTaskDao inventoryConfigTaskDao;
    private com.cannontech.stars.dr.hardwareConfig.service.HardwareConfigService hardwareConfigService;
    private EnergyCompanyDao energyCompanyDao;
    private CommandRequestHardwareExecutor commandRequestHardwareExecutor;
    private StarsInventoryBaseDao starsInventoryBaseDao;

    @PostConstruct
    public void init() throws InterruptedException {
        log.debug("HardwareConfigService - starting up");
        Thread runner = new Thread(this, "HardwareConfigurationService");
        runner.start();
    }

    @Override
    public void run() {
        // TODO:  Do we need a separate loop for each energy company?
        while (true) {
            log.debug("beginning of loop");

            boolean workProcessed = false;
            List<InventoryConfigTask> tasks = inventoryConfigTaskDao.getUnfinished();
            if (!tasks.isEmpty()) {
                log.debug("tasks not empty");
                List<CommandSchedule> schedules = commandScheduleDao.getAllEnabled();
                List<CommandSchedule> activeSchedules = Lists.newArrayList();
                for (CommandSchedule schedule : schedules) {
                    try {
                        // TODO:  time zones
                        CronExpression startTimeCron = new CronExpression(schedule.getStartTimeCronString());
                        ReadablePeriod runPeriod = schedule.getRunPeriod();
                        DateTime now = new DateTime();
                        DateTime scheduleStartTime = new DateTime(startTimeCron.getNextValidTimeAfter(now.minus(runPeriod).toDate()));
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
                    long shortestDelayInMillis = Long.MAX_VALUE;
                    for (CommandSchedule activeSchedule : activeSchedules) {
                        ReadablePeriod thisDelayPeriod = activeSchedule.getDelayPeriod();
                        long thisDelayInMillis = new Period(thisDelayPeriod).toStandardDuration().getMillis();
                        if (thisDelayInMillis < shortestDelayInMillis) {
                            shortestDelayInMillis = thisDelayInMillis;
                        }
                    }
                    if (shortestDelayInMillis < 0) shortestDelayInMillis = 0;
                    log.debug("using delay of " + shortestDelayInMillis + " ms");
                    // (We're estimating 2 commands per config.)
                    int numItems = shortestDelayInMillis > 0 ? (int) Math.round(60.0 * 1000.0 / shortestDelayInMillis / 2.0) : 100;
                    numItems = Math.max(Math.min(numItems, 100), 1);
                    log.debug("getting no more than " + numItems + " items");
                    List<InventoryConfigTaskItem> items = inventoryConfigTaskDao.getItems(numItems);
                    log.debug("got " + items.size() + " items");
                    for (InventoryConfigTaskItem item : items) {
                        log.debug("processing item " + item);
                        workProcessed = true;
                        int inventoryId = item.getInventoryId();
                        int energyCompanyId = item.getEnergyCompanyId();
                        LiteYukonUser user = energyCompanyDao.getEnergyCompanyUser(energyCompanyId);
                        List<String> commands = null;
                        try {
                            commands = hardwareConfigService.getConfigCommands(inventoryId, energyCompanyId, user);
                        } catch (WebClientException e) {
                            log.error("error getting commands for inventory id " + inventoryId, e);
                            continue;
                        }
                        log.debug(item.getInventoryId() + " needs " + commands.size() + " commands");
                        LiteStarsLMHardware hardware = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(inventoryId);
                        Status status = Status.FAIL;  // fail if there are no commands
                        if (!commands.isEmpty()) {
                            status = Status.SUCCESS;
                            for (String command : commands) {
                                log.debug("processing command [" + command + "]");
                                try {
                                    commandRequestHardwareExecutor.execute(hardware, command, user);
                                } catch (CommandCompletionException cce) {
                                    log.error("error executing command [" + command + "]");
                                    status = Status.FAIL;
                                }
                                try {
                                    Thread.sleep(shortestDelayInMillis);
                                } catch (InterruptedException ie) {
                                    log.info("sleep interrupted");
                                }
                            }
                        } else {
                            log.debug("no commands");
                        }
                        inventoryConfigTaskDao.markComplete(item, status);
                    }
                }
            } else {
                log.debug("no unfinished tasks");
            }

            if (!workProcessed) {
                // There wasn't anything to do a nanosecond ago; there probably isn't anything to
                // do right now.

                // This makes the effective maximum "delay period" a minute.  In the future, we
                // can keep track of how long we've slept to allow for longer delay periods if
                // that becomes necessary.
                try {
                    Thread.sleep(1 * 60 * 1000);
                } catch (InterruptedException ie) {
                    log.info("sleep interrupted");
                }
            }
        }
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
    public void setCommandRequestHardwareExecutor(
            CommandRequestHardwareExecutor commandRequestHardwareExecutor) {
        this.commandRequestHardwareExecutor = commandRequestHardwareExecutor;
    }

    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }
}
