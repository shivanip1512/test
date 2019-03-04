package com.cannontech.stars.dr.hardware.service.impl;

import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.event.LMHardwareEvent;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.db.event.LMCustomerEventBase;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.LmCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.HardwareStrategyType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandStrategy;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class LmHardwareCommandServiceImpl implements LmHardwareCommandService {
    
    private static final Logger log = YukonLogManager.getLogger(LmHardwareCommandServiceImpl.class);
    
    @Autowired private YukonListDao yukonListDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private SelectionListService selectionListService;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    
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
        log.debug(String.format("Supported Strategies: %s", strategies.keySet()));
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
    public void sendConfigCommand(final LmHardwareCommand command) throws CommandCompletionException {
        
        // Throw a CommandCompletionException if it is not possible to configure this device.
        verifyCanSendConfig(command);
        
        LiteLmHardwareBase device = command.getDevice();
        EnergyCompany ec = ecDao.getEnergyCompany(device.getEnergyCompanyId());
        
        int inventoryId = device.getInventoryID();
        int unavailableStatus = YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL;
        
        int hardwareDefId = yukonListDao.getYukonListEntry(device.getLmHardwareTypeID()).getYukonDefID();
        HardwareType type = HardwareType.valueOf(hardwareDefId);
        Boolean forceInServiceParam = command.findParam(LmHardwareCommandParam.FORCE_IN_SERVICE, Boolean.class);
        
        boolean supportsInService = type.getHardwareConfigType().isSupportsServiceInOut();
        boolean forceInService = forceInServiceParam != null && forceInServiceParam == true;
        boolean unavailable = inventoryBaseDao.getDeviceStatus(inventoryId) == unavailableStatus;
        
        boolean suppressMessages = ecSettingDao.getBoolean(EnergyCompanySettingType.SUPPRESS_IN_OUT_SERVICE_MESSAGES, ec.getId());
        boolean sendInService = supportsInService && (forceInService || (unavailable && !suppressMessages));
        
        if (sendInService) {
            // Send an in service command first, schedule config command for 5 min later.
            LmHardwareCommand inservice = new LmHardwareCommand();
            inservice.setDevice(device);
            inservice.setType(LmHardwareCommandType.IN_SERVICE);
            inservice.setUser(command.getUser());
            inservice.setParams(command.optionsCopy());
            
            sendInServiceCommand(inservice);
            
            Runnable runner = new Runnable() {
                @Override
                public void run() {
                    try {
                        sendCommand(command);
                    } catch (CommandCompletionException e) {
                        log.error("Unable to send config command", e);
                    }
                }
            };
           
            log.debug("Sending config command in 5 minutes.");
            scheduledExecutor.schedule(runner, 5, TimeUnit.MINUTES);
        } else {
            // Only send the config command
            sendCommand(command);
        }
        
        // Add "Config" to hardware events
        int event = selectionListService.getListEntry(ec, YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID();
        int config = selectionListService.getListEntry(ec, YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG).getEntryID();
        addHardwareEvents(ec.getId(), inventoryId, event, config);
        
        int available = selectionListService.getListEntry(ec, YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL).getEntryID();
        inventoryBaseDao.updateCurrentState(inventoryId, available);
    }
    
    private void sendCommand(LmHardwareCommand command) throws CommandCompletionException{
        
        LiteLmHardwareBase device = command.getDevice();
        int hardwareDefId = yukonListDao.getYukonListEntry(device.getLmHardwareTypeID()).getYukonDefID();
        HardwareType type = HardwareType.valueOf(hardwareDefId);
        HardwareStrategyType strategy = getStrategy(type);
        final LmHardwareCommandStrategy impl = strategies.get(strategy);
        
        try {
            log.debug("Send " + command.getType() 
                    + " device id:" + command.getDevice().getDeviceID() 
                    + " command:" + command);
            impl.sendCommand(command);
            log.debug(command.getType() + " was sent");
        } catch (CommandCompletionException e) {
            log.error("Unable to send " + command.getType() + " command", e);
            throw e;
        } catch (Exception e2) {
            log.error("Unable to send " + command.getType() + " command", e2);
            throw new CommandCompletionException("Unable to send "+command.getType()+" command", e2);
        }
    }
    
    /*
     * Throws CommandCompletionException if verification failed
     * Error example: Device is not Enrolled in any program.
     */
    private void verifyCanSendConfig(LmHardwareCommand command) throws CommandCompletionException {
        
        LiteLmHardwareBase device = command.getDevice();
        int hardwareDefId = yukonListDao.getYukonListEntry(device.getLmHardwareTypeID()).getYukonDefID();
        HardwareType type = HardwareType.valueOf(hardwareDefId);
        HardwareStrategyType strategy = getStrategy(type);
        final LmHardwareCommandStrategy impl = strategies.get(strategy);
        
        try {
            impl.verifyCanSendConfig(command);
        } catch (BadConfigurationException e) {
            throw new CommandCompletionException("Unable to send " + command.getType() + " command. " + e.getMessage(), e);
        }
    }
    
    @Override
    public void sendInServiceCommand(LmHardwareCommand command) throws CommandCompletionException {
        
        sendCommand(command);
        
        // Add "Activation Completed" to hardware events
        LiteLmHardwareBase device = command.getDevice();
        EnergyCompany ec = ecDao.getEnergyCompany(device.getEnergyCompanyId());
        int event = selectionListService.getListEntry(ec, YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID();
        int complete = selectionListService.getListEntry(ec, YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).getEntryID();
        addHardwareEvents(ec.getId(), device.getInventoryID(), event, complete);

        int available = selectionListService.getListEntry(ec, YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL).getEntryID();
        inventoryBaseDao.updateCurrentState(device.getInventoryID() , available);
    }
    
    @Override
    public void sendOutOfServiceCommand(LmHardwareCommand command) throws CommandCompletionException {
        
        sendCommand(command);
        
        // Add "Termination" to hardware events
        LiteLmHardwareBase device = command.getDevice();
        EnergyCompany ec = ecDao.getEnergyCompany(device.getEnergyCompanyId());
        int event = selectionListService.getListEntry(ec, YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID();
        int termination = selectionListService.getListEntry(ec, YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION).getEntryID();
        addHardwareEvents(ec.getId(), device.getInventoryID(), event, termination);
            
        int unavailable = selectionListService.getListEntry(ec, YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL).getEntryID();
        inventoryBaseDao.updateCurrentState(device.getInventoryID(), unavailable);
    }
    
    @Override
    public void sendOptOutCommand(LmHardwareCommand command) throws CommandCompletionException {
        
        sendCommand(command);
    }

    @Override
    public void sendShedLoadCommand(LmHardwareCommand command) throws CommandCompletionException {
        
        verifyCanSendShed(command);
        sendCommand(command);
    }
    
    @Override
    public void sendRestoreCommand(LmHardwareCommand command) throws CommandCompletionException {

        verifyCanSendShed(command);
        sendCommand(command);
    }
    
    /*
     * Throws CommandCompletionException if shed command is not allowed for a device type.
     */
    private void verifyCanSendShed(LmHardwareCommand command) throws CommandCompletionException {
        LiteLmHardwareBase lhb = command.getDevice();
        InventoryIdentifier inventory = inventoryDao.getYukonInventory(lhb.getInventoryID());
        if (!inventory.getHardwareType().isSupportsIndividualDeviceShed()) {
            throw new CommandCompletionException("Cannot send shed for " + inventory.getHardwareType());
        }
    }
    
    private HardwareStrategyType getStrategy(HardwareType type) throws CommandCompletionException {
        for (HardwareStrategyType strategyType : strategies.keySet()) {
            LmHardwareCommandStrategy impl = strategies.get(strategyType);
            if (impl.canHandle(type)) {
                return strategyType;
            }
        }
        
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
            if (hardwareType.isZigbee() && hardwareType.isSupportsTextMessages()) {
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
            }

            if (!isSupportsCancelTextMessages) {
                log.error("Cancel Text Message is not supported by hardware config type "
                          + hardwareType.getHardwareConfigType() + " for " + message.getInventoryIds().size()
                          + " devices.");
            }
        }
    }
    
    @Override
    public void sendBroadcastCommand(LmCommand command) {
        for (LmHardwareCommandStrategy strategy : strategies.values() ) {
            try {
                if(strategy.canBroadcast(command)) {
                    strategy.sendBroadcastCommand(command);
                }
            } catch (CommandCompletionException e) {
                log.error("Error executing broadcast command: ", e);
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