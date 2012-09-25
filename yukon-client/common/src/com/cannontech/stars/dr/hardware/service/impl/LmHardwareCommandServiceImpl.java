package com.cannontech.stars.dr.hardware.service.impl;

import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.event.LMHardwareEvent;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.event.LMCustomerEventBase;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.HardwareStrategyType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandStrategy;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class LmHardwareCommandServiceImpl implements LmHardwareCommandService {
    
    private static final Logger log = YukonLogManager.getLogger(LmHardwareCommandServiceImpl.class);
    private static final LogHelper logHelper = LogHelper.getInstance(log);
    
    @Autowired private YukonListDao yukonListDao;
    @Autowired private StarsDatabaseCache cache;
    @Autowired private EnergyCompanyRolePropertyDao ecRolePropertyDao;
    @Autowired private YukonEnergyCompanyService yecService;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private InventoryDao inventoryDao;
    
    //@Autowired by setter
    private JmsTemplate jmsTemplate;

    private ImmutableMap<HardwareStrategyType, LmHardwareCommandStrategy> strategies = ImmutableMap.of();
    @Autowired
    public void setStrategies(List<LmHardwareCommandStrategy> strategyList) {
        Builder<HardwareStrategyType, LmHardwareCommandStrategy> builder = ImmutableMap.builder();
        for (LmHardwareCommandStrategy strategy : strategyList) {
            builder.put(strategy.getType(), strategy);
        }
        strategies = builder.build();
        logHelper.debug("supported strategies: %s", strategies.keySet());
    }
    
    @Override
    public void doManualAdjustment(ThermostatManualEvent event, Thermostat stat, LiteYukonUser user) 
    throws CommandCompletionException {
        
        HardwareType type = stat.getType();
        HardwareStrategyType foundStrategy = getStrategy(type);
        LmHardwareCommandStrategy impl = strategies.get(foundStrategy);
        
        impl.doManualAdjustment(event, stat, user);
    }

    @Override
    public ThermostatScheduleUpdateResult doScheduleUpdate(CustomerAccount account,
                                                           AccountThermostatSchedule ats,
                                                           ThermostatScheduleMode mode,
                                                           Thermostat stat, LiteYukonUser user) throws CommandCompletionException {
        HardwareType type = stat.getType();
        HardwareStrategyType foundStrategy = getStrategy(type);
        LmHardwareCommandStrategy impl = strategies.get(foundStrategy);
        
        return impl.doScheduleUpdate(account, ats, mode, stat, user);
    }
    
    @Override
    public void sendConfigCommand(final LmHardwareCommand command) 
    throws CommandCompletionException {
        
        LiteLmHardwareBase device = command.getDevice();
        HardwareType type = HardwareType.valueOf(yukonListDao.getYukonListEntry(device.getLmHardwareTypeID()).getYukonDefID());
        HardwareStrategyType strategy = getStrategy(type);
        final LmHardwareCommandStrategy impl = strategies.get(strategy);
        
        YukonEnergyCompany yec = yecService.getEnergyCompanyByInventoryId(device.getInventoryID());
        int ecId = yec.getEnergyCompanyId();
        LiteStarsEnergyCompany lsec = cache.getEnergyCompany(ecId);
        
        boolean autoConfig = ecRolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.AUTOMATIC_CONFIGURATION, yec);
        
        int inventoryId = device.getInventoryID();
        int unavailable = YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL;
        
        boolean supportsServiceInOut = type.getHardwareConfigType().isSupportsServiceInOut();
        boolean forceInService = false;
        Boolean param = command.findParam(LmHardwareCommandParam.FORCE_IN_SERVICE, Boolean.class);
        if (param != null) {
            forceInService = param;
        }
        boolean unavailableOrForceInService = inventoryBaseDao.getDeviceStatus(inventoryId) == unavailable || forceInService;
        if (unavailableOrForceInService && supportsServiceInOut) {
            
            // Send an in service command first
            LmHardwareCommand.Builder b = new LmHardwareCommand.Builder(device, LmHardwareCommandType.IN_SERVICE, command.getUser());
            ImmutableMap<LmHardwareCommandParam, Object> optionsCopy = command.optionsCopy();
            for (LmHardwareCommandParam option : optionsCopy.keySet()) {
                b.withParam(option, optionsCopy.get(option));
            }
            LmHardwareCommand inservice = b.build();
            sendInServiceCommand(inservice);
            log.debug("Automated in-service LM command sent: " + inservice);
            if (autoConfig) {
                // Send the config command a while later
                TimerTask sendConfigLater = new TimerTask() {
                    public void run() {
                        try {
                            impl.sendCommand(command);
                            log.debug("Delayed config command sent: " + command);
                        } catch (Exception e) {
                            log.error("Unable to send config command", e);
                        }
                    }
                };
                logHelper.debug("Scheduling config command for: %s %s in 5 minutes from now.", type, device.getManufacturerSerialNumber());
                YukonSpringHook.getGlobalTimer().schedule(sendConfigLater, 300 * 1000);
            }
        } else {
            // Only send the config command
            try {
                impl.sendCommand(command);
                log.debug("Config command sent: " + command);
            } catch (Exception e) {
                log.error("Unable to send config command", e);
                throw new CommandCompletionException("Unable to send config command", e);
            }
        }
    
        // Add "Config" to hardware events
        int event = lsec.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID();
        int config = lsec.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG).getEntryID();
        addHardwareEvents(ecId, inventoryId, event, config);
        
        int available = lsec.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL).getEntryID();
        inventoryBaseDao.updateCurrentState(inventoryId, available);
    }
    
    @Override
    public void sendInServiceCommand(LmHardwareCommand command) throws CommandCompletionException {
        
        LiteLmHardwareBase device = command.getDevice();
        HardwareType type = HardwareType.valueOf(yukonListDao.getYukonListEntry(device.getLmHardwareTypeID()).getYukonDefID());
        HardwareStrategyType strategy = getStrategy(type);
        final LmHardwareCommandStrategy impl = strategies.get(strategy);
        
        int inventoryId = device.getInventoryID();
        YukonEnergyCompany yec = yecService.getEnergyCompanyByInventoryId(inventoryId);
        
        impl.sendCommand(command);
        log.debug("In-Service command sent: " + command);

        LiteStarsEnergyCompany lsec = cache.getEnergyCompany(yec.getEnergyCompanyId());
        
        // Add "Activation Completed" to hardware events
        int event = lsec.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID();
        int complete = lsec.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).getEntryID();
        addHardwareEvents(yec.getEnergyCompanyId(), inventoryId, event, complete);

        int available = lsec.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL).getEntryID();
        inventoryBaseDao.updateCurrentState(inventoryId, available);
    }
    
    @Override
    public void sendOutOfServiceCommand(LmHardwareCommand command) throws CommandCompletionException {
        
        LiteLmHardwareBase device = command.getDevice();
        HardwareType type = HardwareType.valueOf(yukonListDao.getYukonListEntry(device.getLmHardwareTypeID()).getYukonDefID());
        HardwareStrategyType strategy = getStrategy(type);
        final LmHardwareCommandStrategy impl = strategies.get(strategy);
        
        int inventoryId = device.getInventoryID();
        YukonEnergyCompany yec = yecService.getEnergyCompanyByInventoryId(inventoryId);
        int ecId = yec.getEnergyCompanyId();
        LiteStarsEnergyCompany lsec = cache.getEnergyCompany(ecId);

        impl.sendCommand(command);
        log.debug("Out-of-Service command sent: " + command);
        
        // Add "Termination" to hardware events
        int event = lsec.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID();
        int termination = lsec.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION).getEntryID();
        addHardwareEvents(ecId, inventoryId, event, termination);
            
        int unavailable = lsec.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL).getEntryID();
        inventoryBaseDao.updateCurrentState(inventoryId, unavailable);
    }
    
    @Override
    public void sendOptOutCommand(LmHardwareCommand command) throws CommandCompletionException {
        
        LiteLmHardwareBase device = command.getDevice();
        HardwareType type = HardwareType.valueOf(yukonListDao.getYukonListEntry(device.getLmHardwareTypeID()).getYukonDefID());
        HardwareStrategyType strategy = getStrategy(type);
        final LmHardwareCommandStrategy impl = strategies.get(strategy);

        impl.sendCommand(command);
        log.debug("Opt out command sent: " + command);
        
        //TODO probably should add event like other commands, maybe opt out service is already doing this?
    }

    private HardwareStrategyType getStrategy(HardwareType type) throws CommandCompletionException {
        for (HardwareStrategyType strategyType : strategies.keySet()) {
            LmHardwareCommandStrategy impl = strategies.get(strategyType);
            if (impl.canHandle(type)) {
                logHelper.debug("Strategy found for device of type %s", type);
                return strategyType;
            }
        }
        
        logHelper.debug("No strategy found for device of type %s", type);
        throw new CommandCompletionException("No command strategy found for type: " + type);
    }
    
    private void addHardwareEvents(int ecId, int inventoryId, int eventType, int configType) {
        
        LMHardwareEvent event = new LMHardwareEvent();
        LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
   
        event.getLMHardwareEvent().setInventoryID(inventoryId);
        eventBase.setEventTypeID(eventType);
        eventBase.setActionID(configType);
        eventBase.setEventDateTime(new Date());
        event.setEnergyCompanyID(ecId);
   
        dbPersistentDao.performDBChange(event, TransactionType.INSERT);
    }
    
    @Override
    public void sendTextMessage(YukonTextMessage message) {
        Map<Integer, HardwareSummary> hardwareSummary =
            inventoryDao.findHardwareSummariesById(message.getInventoryIds());
        Map<HardwareType, Set<Integer>> hardwareTypeToInventoryIds =
            getHardwareTypeToInventoryIdsMap(message.getInventoryIds(), hardwareSummary);

        for (HardwareType hardwareType : hardwareTypeToInventoryIds.keySet()) {
            boolean isSupportsTextMessages = false;
            if (hardwareType.isSupportsTextMessages()) {
                message.setInventoryIds(hardwareTypeToInventoryIds.get(hardwareType));
                HardwareStrategyType foundStrategy;
                try {
                    foundStrategy = getStrategy(hardwareType);
                    LmHardwareCommandStrategy impl = strategies.get(foundStrategy);
                    impl.sendTextMessage(message);
                    isSupportsTextMessages = true;
                } catch (CommandCompletionException e) {
                    //No strategy found for this device
                }
            }
            if (!isSupportsTextMessages) {
                log.error("Send Text Message is not supported by hardware config type "
                          + hardwareType.getHardwareConfigType() + " for " + message.getInventoryIds().size()
                          + " devices.");
            }
        }
    }
    
    @Override
    public void cancelTextMessage(YukonCancelTextMessage message) {
        Map<Integer, HardwareSummary> hardwareSummary =
            inventoryDao.findHardwareSummariesById(message.getInventoryIds());
        Map<HardwareType, Set<Integer>> hardwareTypeToInventoryIds =
            getHardwareTypeToInventoryIdsMap(message.getInventoryIds(), hardwareSummary);

        for (HardwareType hardwareType : hardwareTypeToInventoryIds.keySet()) {
            boolean isSupportsCancelTextMessages = false;
            message.setInventoryIds(hardwareTypeToInventoryIds.get(hardwareType));
            HardwareStrategyType foundStrategy;
            try {
                foundStrategy = getStrategy(hardwareType);
                LmHardwareCommandStrategy impl = strategies.get(foundStrategy);
                impl.cancelTextMessage(message);
                isSupportsCancelTextMessages = true;
            } catch (CommandCompletionException e) {
                // No strategy found for this device
            }

            if (!isSupportsCancelTextMessages) {
                log.error("Cancel Text Message is not supported by hardware config type "
                          + hardwareType.getHardwareConfigType() + " for " + message.getInventoryIds().size()
                          + " devices.");
            }
        }
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(false);
    }
    
    private Map<HardwareType, Set<Integer>> getHardwareTypeToInventoryIdsMap( Set<Integer> inventoryIds,  Map<Integer, HardwareSummary> hardwareSummary){
        Map<HardwareType, Set<Integer>> hardwareTypeToInventoryIds = new EnumMap<HardwareType, Set<Integer>>(HardwareType.class);
        for (int inventoryId : inventoryIds) {
            HardwareType type = hardwareSummary.get(inventoryId).getHardwareType();
            
            if(!hardwareTypeToInventoryIds.containsKey(type)){
                hardwareTypeToInventoryIds.put(type, new HashSet<Integer>());
            }
            hardwareTypeToInventoryIds.get(type).add(inventoryId);
        }
        return hardwareTypeToInventoryIds;
    }
    
    
}