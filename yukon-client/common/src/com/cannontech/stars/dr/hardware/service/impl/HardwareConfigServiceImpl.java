package com.cannontech.stars.dr.hardware.service.impl;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.CommandScheduleDao;
import com.cannontech.stars.dr.hardware.dao.InventoryConfigTaskDao;
import com.cannontech.stars.dr.hardware.model.CommandSchedule;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTaskItem;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.HardwareConfigService;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandRequestExecutor;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class HardwareConfigServiceImpl implements HardwareConfigService{
    
   private final static Logger log = YukonLogManager.getLogger(HardwareConfigServiceImpl.class);

   @Autowired private CommandRequestRouteExecutor commandRequestRouteExecutor;
   @Autowired private CommandScheduleDao commandScheduleDao;
   @Autowired private EnergyCompanyDao ecDao;
   @Autowired private EnergyCompanyService ecService;
   @Autowired private InventoryBaseDao inventoryBaseDao;
   @Autowired private InventoryConfigTaskDao inventoryConfigTaskDao;
   @Autowired private LmHardwareCommandRequestExecutor lmHardwareCommandRequestExecutor;
   @Autowired private WaitableCommandCompletionCallbackFactory waitableCommandCompletionCallbackFactory;
   @Autowired private LmHardwareCommandService commandService;
   @Autowired private YukonListDao yukonListDao;
   @Autowired private EnrollmentDao enrollmentService;
   @Autowired private CustomerAccountDao customerAccountDao;
   
   @Autowired private HardwareEventLogService hardwareEventLog;

   private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
   
   
    @Override
    public void disable(int inventoryId, int accountId, int energyCompanyId, YukonUserContext context)
            throws CommandCompletionException {
        LiteLmHardwareBase lmhb = (LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId);

        if (VersionTools.staticLoadGroupMappingExists()) {
            addSwitchCommand(energyCompanyId, accountId, inventoryId, SwitchCommandQueue.SWITCH_COMMAND_DISABLE);
        } else {
            sendOutOfService(lmhb, context.getYukonUser());

            CustomerAccount customerAccount = customerAccountDao.getById(accountId);
            hardwareEventLog.hardwareDisabled(context.getYukonUser(), lmhb.getManufacturerSerialNumber(),
                customerAccount.getAccountNumber());
        }
    }

    @Override
    public void enable(int inventoryId, int accountId, int energyCompanyId, YukonUserContext context)
            throws CommandCompletionException {
        LiteLmHardwareBase liteHw = (LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId);

        if (VersionTools.staticLoadGroupMappingExists()) {
            addSwitchCommand(energyCompanyId, accountId, inventoryId, SwitchCommandQueue.SWITCH_COMMAND_ENABLE);
        } else {
            sendInService(liteHw, context.getYukonUser());
        }

        CustomerAccount customerAccount = customerAccountDao.getById(accountId);
        hardwareEventLog.hardwareDisabled(context.getYukonUser(), liteHw.getManufacturerSerialNumber(),
            customerAccount.getAccountNumber());
    }

   private void addSwitchCommand(int energyCompanyId, int accountId, int inventoryId, String commandType) {
       SwitchCommandQueue.SwitchCommand cmd = new SwitchCommandQueue.SwitchCommand();
       cmd.setEnergyCompanyID(energyCompanyId);
       cmd.setAccountID(accountId);
       cmd.setInventoryID(inventoryId);
       cmd.setCommandType(commandType);
       SwitchCommandQueue.getInstance().addCommand(cmd, true);
   }

   
   @Override
   public Status config(int inventoryId, boolean forceInService, boolean sendOutOfService, LiteYukonUser user)
           throws CommandCompletionException {
       
        if (log.isDebugEnabled()) {
            log.debug("Sending config to inventory id =" + inventoryId + " forceInService=" + forceInService
                + " sendOutOfService=" + sendOutOfService);
        }

       LiteLmHardwareBase lmhb = null;
       HardwareType type = null;

       try {
           lmhb = inventoryBaseDao.getHardwareByInventoryId(inventoryId);
           type = HardwareType.valueOf(yukonListDao.getYukonListEntry(lmhb.getLmHardwareTypeID()).getYukonDefID());
       } catch (NotFoundException e) {
           // handled below
       }

       if (lmhb != null && type != null && type.isConfigurable()) {

           String sn = lmhb.getManufacturerSerialNumber();
           log.debug("Serial Number=" + sn);
           CustomerAccount account = customerAccountDao.getAccountByInventoryId(lmhb.getInventoryID());

           try {
               if (sendOutOfService) {
                   List<ProgramEnrollment> activeEnrollments =
                       enrollmentService.getActiveEnrollmentsByInventory(inventoryId);

                    if (activeEnrollments.isEmpty()) {
                        hardwareEventLog.hardwareDisableAttempted(user, sn, account.getAccountNumber(),
                            EventSource.OPERATOR);
                        sendOutOfService(lmhb, user);
                        hardwareEventLog.hardwareDisabled(user, sn, account.getAccountNumber());
                        return Status.SUCCESS;
                    }
               }

               if (forceInService) {
                   hardwareEventLog.hardwareEnableAttempted(user, sn, account.getAccountNumber(),
                       EventSource.OPERATOR);
               }
               sendConfig(lmhb, forceInService, user);
               hardwareEventLog.hardwareConfigUpdated(user, sn, account.getAccountNumber());
                if (forceInService) {
                    hardwareEventLog.hardwareEnabled(user, sn, account.getAccountNumber());
                }
               return Status.SUCCESS;

           } catch (CommandCompletionException e) {
               log.error("Failed - Unable to send config command inventory id=" + inventoryId, e);
               throw e;
           }
       } else {
           log.debug("Unsupported");
           String sn = lmhb != null ? lmhb.getManufacturerSerialNumber() : "";
           hardwareEventLog.hardwareConfigUnsupported(user, sn);
           return Status.UNSUPPORTED;
       }
   }

   private class EnergyCompanyRunnable implements Runnable {
       int ecId;

       private EnergyCompanyRunnable(int ecId) {
           this.ecId = ecId;
       }

       private void processItem(InventoryConfigTaskItem item){
           log.debug("Configuration task" + item);
           InventoryConfigTask task = item.getInventoryConfigTask();
      
           try {    
               Status status =
                   config(item.getInventoryId(), task.isSendInService(), task.isSendOutOfService(), task.getUser());        
               inventoryConfigTaskDao.markComplete(item, status);  
           } catch (CommandCompletionException e) {
               inventoryConfigTaskDao.markComplete(item, Status.FAIL);
           } 
       }
       
       private boolean processItems() throws InterruptedException {
           log.trace("processing a chunk of work for ecId = " + ecId);
           TimeZone ecTimeZone = ecService.getDefaultTimeZone(ecId);
           DateTimeZone energyCompanyTimeZone = DateTimeZone.forTimeZone(ecTimeZone);

           boolean workProcessed = false;
           List<InventoryConfigTask> tasks = inventoryConfigTaskDao.getUnfinished(ecId);
           log.trace("tasks has " + tasks.size() + " items");
           if (!tasks.isEmpty()) {
               List<CommandSchedule> schedules = commandScheduleDao.getAllEnabled(ecId);
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
                   Iterable<InventoryConfigTaskItem> items = inventoryConfigTaskDao.getItems(numItems, ecId);
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

   /**
    * Called from inventoryContext.xml. Starts config task when Service Manager starts up.
    */
   public void startConfigTask() {
       log.debug("HardwareConfigService - starting up config task");

       Collection<EnergyCompany> allEnergyCompanies = ecDao.getAllEnergyCompanies();
       for (EnergyCompany energyCompany : allEnergyCompanies) {
           log.debug("HardwareConfigService - starting task for ecId = " + energyCompany.getId());
           Runnable task = new EnergyCompanyRunnable(energyCompany.getId());
           executor.scheduleWithFixedDelay(task, 1, 1, TimeUnit.MINUTES);
       }
   }
   
   private void sendConfig(LiteLmHardwareBase lmhb, boolean forceInService, LiteYukonUser user)
           throws CommandCompletionException {
       LmHardwareCommand command = new LmHardwareCommand();
       command.setDevice(lmhb);
       command.setType(LmHardwareCommandType.CONFIG);
       command.setUser(user);
       command.getParams().put(LmHardwareCommandParam.BULK, true);
       
        if (forceInService) {
            command.getParams().put(LmHardwareCommandParam.FORCE_IN_SERVICE, true);
        }
       commandService.sendConfigCommand(command);
       if (forceInService) {
            log.debug("Success - inventory id =" + lmhb.getInventoryID()
                + " 'Config' and 'In Service' Command was sent");
       } else {
            log.debug("Success - inventory id =" + lmhb.getInventoryID() + " 'Config' Command was sent");
       }
   }
   
    private void sendOutOfService(LiteLmHardwareBase lmhb, LiteYukonUser user) throws CommandCompletionException {
        LmHardwareCommand command = new LmHardwareCommand();
        command.setDevice(lmhb);
        command.setType(LmHardwareCommandType.OUT_OF_SERVICE);
        command.setUser(user);

        commandService.sendOutOfServiceCommand(command);
        log.debug("Success - inventory id =" + lmhb.getInventoryID() + " 'Out of Service' Command was sent");
    }
    
    private void sendInService(LiteLmHardwareBase lmhb, LiteYukonUser user) throws CommandCompletionException {
        LmHardwareCommand command = new LmHardwareCommand();
        command.setDevice(lmhb);
        command.setType(LmHardwareCommandType.IN_SERVICE);
        command.setUser(user);

        commandService.sendInServiceCommand(command);
        log.debug("Success - inventory id =" + lmhb.getInventoryID() + " 'In Service' Command was sent");
    }
}
