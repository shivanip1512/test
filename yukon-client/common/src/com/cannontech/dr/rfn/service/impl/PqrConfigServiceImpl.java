package com.cannontech.dr.rfn.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.PqrEventLogService;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.rfn.model.PqrConfig;
import com.cannontech.dr.rfn.model.PqrConfigCommandStatus;
import com.cannontech.dr.rfn.model.PqrConfigResult;
import com.cannontech.dr.rfn.service.PqrConfigService;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.impl.RfCommandStrategy;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;

public class PqrConfigServiceImpl implements PqrConfigService {
    private static final Logger log = YukonLogManager.getLogger(PqrConfigServiceImpl.class);
    private final Cache<String, PqrConfigResult> results = CacheBuilder.newBuilder().expireAfterWrite(7, TimeUnit.DAYS).build();
    
    @Autowired private AttributeService attributeService;
    @Autowired @Qualifier("main") private Executor executor;
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private PqrEventLogService pqrEventLogService;
    @Autowired private RfCommandStrategy rfCommandStrategy;
    
    @Override
    public Optional<PqrConfigResult> getResult(String resultId) {
        return Optional.ofNullable(results.getIfPresent(resultId));
    }
    
    @Override
    public String sendConfigs(List<LiteLmHardwareBase> hardware, PqrConfig config, LiteYukonUser user) {
        pqrEventLogService.sendConfig(user, hardware.size(), config.toString());
        log.info("Initiating Power Quality Response configuration for " + hardware.size() + " inventory. "
                 + config.toString());
        
        List<LiteLmHardwareBase> unsupportedHardware = new ArrayList<>();
        for (LiteLmHardwareBase hw : hardware) {
            // DeviceId 0 means one-way inventory, doesn't support PQR
            if (hw.getDeviceID() == 0) {
                unsupportedHardware.add(hw);
                log.debug("InventoryId=" + hw.getInventoryID() + " is one-way. Marking as unsupported.");
            } else {
                LiteYukonPAObject pao = serverDatabaseCache.getAllPaosMap().get(hw.getDeviceID());
                // It's an actual pao, check if it can support PQR
                if (pao == null || !pao.getPaoType().supportsPqr()) {
                    unsupportedHardware.add(hw);
                    log.debug("InventoryId=" + hw.getInventoryID() + " (DeviceId=" + hw.getDeviceID() + ") has paoType="
                              + pao.getPaoType() + ", which does not support PQR. Marking as unsupported.");
                } else {
                    // The device can support PQR, check if it is generating PQR point data
                    // (In the future, we could store the firmware version from the TLV and check that instead.
                    boolean pqrEnabledPointExists = attributeService.pointExistsForAttribute(pao, BuiltInAttribute.POWER_QUALITY_RESPONSE_ENABLED);
                    if (!pqrEnabledPointExists) {
                        unsupportedHardware.add(hw);
                        log.debug("InventoryId=" + hw.getInventoryID() + " (DeviceId=" + hw.getDeviceID() + ") has not "
                                + "reported PQR configuration. Marking as unsupported.");
                    }
                }
            }
        }
        
        // Remove the unsupported devices from the list of devices to configure
        List<LiteLmHardwareBase> supportedHardware = hardware.stream()
                                                             .filter(hw -> !unsupportedHardware.contains(hw))
                                                             .collect(Collectors.toList());
        
        log.info("PQR configuration task found " + supportedHardware.size() + " supported devices and " 
                 + unsupportedHardware.size() + "unsupported devices.");
        
        // Generate a new results object for this operation
        String id = UUID.randomUUID().toString();
        PqrConfigResult result = new PqrConfigResult(supportedHardware, unsupportedHardware, config);
        results.put(id, result);
        
        // Send the configuration messages in a separate thread
        executor.execute(() -> {
            try {
                for(LiteLmHardwareBase hw : supportedHardware) {
                    sendConfig(hw, config, id, user);
                }
            } catch (Exception e) {
                log.error("Unexpected error while sending PQR configuration messages", e);
                log.info("PQR configuration operation ended unexpectedly. Marking in-progress messages as failed.");
                result.complete();
            }
        });
        
        return id;
    }
    
    /**
     * Goes through the PqrConfig and sends a message for each piece of the configuration where data was specified.
     */
    private void sendConfig(LiteLmHardwareBase hardware, PqrConfig config, String resultId, LiteYukonUser user) {
        log.debug("Sending PQR configuration messages for inventoryId=" + hardware.getInventoryID() + " (deviceId=" 
                  + hardware.getDeviceID() + ")");
        
        //PQR Enable/Disable
        config.getPqrEnableOptional()
              .ifPresent(enable -> sendTogglePqrEnable(enable, hardware, resultId, user));
        
        //LOV Parameters
        if (config.hasLovParams()) {
            sendLovParams(config.getLovTrigger(), config.getLovRestore(), config.getLovTriggerTime(), 
                          config.getLovRestoreTime(), hardware, resultId, user);
        }
        
        //LOV Event Duration
        if (config.hasLovEventDurations()) {
            sendLovEventDurations(config.getLovMinEventDuration(), config.getLovMaxEventDuration(), hardware, resultId, user);
        }
        
        //LOV Delay Duration
        if (config.hasLovDelayDurations()) {
            sendLovDelayDurations(config.getLovStartRandomTime(), config.getLovEndRandomTime(), hardware, resultId, user);
        }
        
        //LOF Parameters
        if (config.hasLofParams()) {
            sendLofParams(config.getLofTrigger(), config.getLofRestore(), config.getLofTriggerTime(), 
                          config.getLofRestoreTime(), hardware, resultId, user);
        }
        
        //LOF Event Duration
        if (config.hasLofEventDurations()) {
            sendLofEventDurations(config.getLofMinEventDuration(), config.getLofMaxEventDuration(), hardware, resultId, user);
        }
        
        //LOF Delay Duration
        if (config.hasLofDelayDurations()) {
            sendLofDelayDurations(config.getLofStartRandomTime(), config.getLofEndRandomTime(), hardware, resultId, user);
        }
        
        //Minimum Event Separation
        config.getMinimumEventSeparationOptional()
              .ifPresent(separationSeconds -> sendMinimumEventSeparation(separationSeconds, hardware, resultId, user));
    }
    
    /**
     * Sends a PQR configuration message and updates the results. The message is considered successful if it is accepted
     * by Network Manager. Yukon does not guarantee that the message made it to the actual device.
     */
    private void sendCommand(LiteLmHardwareBase hardware, LmHardwareCommandType commandType, String resultId, 
                             LiteYukonUser user, Map<LmHardwareCommandParam, Object> additionalParameters) {
        
        LmHardwareCommand command = new LmHardwareCommand();
        command.setDevice(hardware);
        command.setType(commandType);
        command.setUser(user);
        ImmutableMap.Builder<LmHardwareCommandParam, Object> builder = new ImmutableMap.Builder<>();
        Map<LmHardwareCommandParam, Object> parameters = builder.put(LmHardwareCommandParam.WAITABLE, true)
                                                                .put(LmHardwareCommandParam.EXPIRATION_DURATION, 60 * 1000)
                                                                .putAll(additionalParameters)
                                                                .build();
        command.setParams(parameters);
        
        log.trace("Sending " + commandType + " configuration message for inventoryId=" + hardware.getInventoryID()
                  + " (deviceId=" + hardware.getDeviceID() + ")");
        
        try {
            rfCommandStrategy.sendCommand(command);
            results.getIfPresent(resultId)
                   .getForInventoryId(hardware.getInventoryID())
                   .setStatus(commandType, PqrConfigCommandStatus.SUCCESS);
        } catch (CommandCompletionException e) {
            log.error("Error sending PQR configuration message", e);
            results.getIfPresent(resultId)
                   .getForInventoryId(hardware.getInventoryID())
                   .setStatus(commandType, PqrConfigCommandStatus.FAILED);
        }
    }
    
    private void sendTogglePqrEnable(boolean enable, LiteLmHardwareBase hardware, String resultId, LiteYukonUser user) {
        LmHardwareCommandType command = LmHardwareCommandType.PQR_ENABLE;
        Map<LmHardwareCommandParam, Object> params = ImmutableMap.of(LmHardwareCommandParam.PQR_ENABLE, true);
        
        sendCommand(hardware, command, resultId, user, params);
    }
    
    private void sendLovParams(double trigger, double restore, short triggerTimeMillis, short restoreTimeMillis,
                               LiteLmHardwareBase hardware, String resultId, LiteYukonUser user) {
        
        LmHardwareCommandType command = LmHardwareCommandType.PQR_LOV_PARAMETERS;
        Map<LmHardwareCommandParam, Object> params = ImmutableMap.of(
            LmHardwareCommandParam.PQR_LOV_TRIGGER, trigger,
            LmHardwareCommandParam.PQR_LOV_RESTORE, restore,
            LmHardwareCommandParam.PQR_LOV_TRIGGER_TIME, triggerTimeMillis,
            LmHardwareCommandParam.PQR_LOV_RESTORE_TIME, restoreTimeMillis
        );
        
        sendCommand(hardware, command, resultId, user, params);
    }
    
    private void sendLovEventDurations(short minEventDurationSeconds, short maxEventDurationSeconds, 
                                       LiteLmHardwareBase hardware, String resultId, LiteYukonUser user) {
        
        LmHardwareCommandType command = LmHardwareCommandType.PQR_LOV_EVENT_DURATION;
        Map<LmHardwareCommandParam, Object> params = ImmutableMap.of(
            LmHardwareCommandParam.PQR_LOV_MIN_EVENT_DURATION, minEventDurationSeconds,
            LmHardwareCommandParam.PQR_LOV_MAX_EVENT_DURATION, maxEventDurationSeconds
        );
        
        sendCommand(hardware, command, resultId, user, params);
    }
    
    private void sendLovDelayDurations(short startRandomTimeMillis, short endRandomTimeMillis, 
                                       LiteLmHardwareBase hardware, String resultId, LiteYukonUser user) {
        
        LmHardwareCommandType command = LmHardwareCommandType.PQR_LOV_DELAY_DURATION;
        Map<LmHardwareCommandParam, Object> params = ImmutableMap.of(
            LmHardwareCommandParam.PQR_LOV_START_RANDOM_TIME, startRandomTimeMillis,
            LmHardwareCommandParam.PQR_LOV_END_RANDOM_TIME, endRandomTimeMillis
        );
        
        sendCommand(hardware, command, resultId, user, params);
    }
    
    private void sendLofParams(short trigger, short restore, short triggerTimeMillis, short restoreTimeMillis,
                               LiteLmHardwareBase hardware, String resultId, LiteYukonUser user) {
        
        LmHardwareCommandType command = LmHardwareCommandType.PQR_LOF_PARAMETERS;
        Map<LmHardwareCommandParam, Object> params = ImmutableMap.of(
            LmHardwareCommandParam.PQR_LOF_TRIGGER, trigger,
            LmHardwareCommandParam.PQR_LOF_RESTORE, restore,
            LmHardwareCommandParam.PQR_LOF_TRIGGER_TIME, triggerTimeMillis,
            LmHardwareCommandParam.PQR_LOF_RESTORE_TIME, restoreTimeMillis
        );
        
        sendCommand(hardware, command, resultId, user, params);
    }
    
    private void sendLofEventDurations(short minEventDurationSeconds, short maxEventDurationSeconds, 
                                       LiteLmHardwareBase hardware, String resultId, LiteYukonUser user) {
        
        LmHardwareCommandType command = LmHardwareCommandType.PQR_LOF_EVENT_DURATION;
        Map<LmHardwareCommandParam, Object> params = ImmutableMap.of(
            LmHardwareCommandParam.PQR_LOF_MIN_EVENT_DURATION, minEventDurationSeconds,
            LmHardwareCommandParam.PQR_LOF_MAX_EVENT_DURATION, maxEventDurationSeconds
        );
        
        sendCommand(hardware, command, resultId, user, params);
    }
    
    private void sendLofDelayDurations(short startRandomTimeMillis, short endRandomTimeMillis, 
                                       LiteLmHardwareBase hardware, String resultId, LiteYukonUser user) {
        
        LmHardwareCommandType command = LmHardwareCommandType.PQR_LOF_DELAY_DURATION;
        Map<LmHardwareCommandParam, Object> params = ImmutableMap.of(
            LmHardwareCommandParam.PQR_LOF_START_RANDOM_TIME, startRandomTimeMillis,
            LmHardwareCommandParam.PQR_LOF_END_RANDOM_TIME, endRandomTimeMillis
        );
        
        sendCommand(hardware, command, resultId, user, params);
    }
    
    private void sendMinimumEventSeparation(short separationSeconds, LiteLmHardwareBase hardware, String resultId, 
                                            LiteYukonUser user) {
        
        LmHardwareCommandType command = LmHardwareCommandType.PQR_EVENT_SEPARATION;
        Map<LmHardwareCommandParam, Object> params = ImmutableMap.of(LmHardwareCommandParam.PQR_EVENT_SEPARATION,
                                                                     separationSeconds);
        
        sendCommand(hardware, command, resultId, user, params);
    }
}
