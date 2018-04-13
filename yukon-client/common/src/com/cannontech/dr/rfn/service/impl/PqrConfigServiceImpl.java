package com.cannontech.dr.rfn.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
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
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;

//TODO logging
//TODO event logging
//TODO timeout?
//TODO spin off new task for sending each set of configs
//TODO javadoc everywhere
public class PqrConfigServiceImpl implements PqrConfigService {
    private static final Logger log = YukonLogManager.getLogger(PqrConfigServiceImpl.class);
    private final Cache<String, PqrConfigResult> results = CacheBuilder.newBuilder().expireAfterWrite(7, TimeUnit.DAYS).build();
    
    @Autowired private RfCommandStrategy rfCommandStrategy;
    
    @Override
    public String sendConfigs(List<LiteLmHardwareBase> hardware, PqrConfig config, LiteYukonUser user) {
        //TODO handle unsupported
        
        String id = UUID.randomUUID().toString();
        PqrConfigResult result = new PqrConfigResult(hardware, config);
        results.put(id, result);
        
        for(LiteLmHardwareBase hw : hardware) {
            sendConfig(hw, config, id, user);
        }
        
        return id;
    }
    
    private void sendConfig(LiteLmHardwareBase hardware, PqrConfig config, String resultId, LiteYukonUser user) {
        //PQR Enable/Disable
        config.getPqrEnable()
              .ifPresent(enable -> sendTogglePqrEnable(enable, hardware, resultId, user));
        
        //TODO LOV Parameters
        //TODO LOV Event Duration
        //TODO LOV Delay Duration
        //TODO LOF Parameters
        //TODO LOF Event Duration
        //TODO LOF Delay Duration
        //TODO General Event Separation
    }
    
    private void sendTogglePqrEnable(boolean enable, LiteLmHardwareBase hardware, String resultId, LiteYukonUser user) {
        LmHardwareCommandType command = LmHardwareCommandType.PQR_ENABLE;
        Map<LmHardwareCommandParam, Object> params = ImmutableMap.of(LmHardwareCommandParam.PQR_ENABLE, true);
        
        sendCommand(hardware, command, resultId, user, params);
    }
    
    private void sendCommand(LiteLmHardwareBase hardware, LmHardwareCommandType commandType, String resultId, 
                             LiteYukonUser user, Map<LmHardwareCommandParam, Object> additionalParameters) {
        
        LmHardwareCommand command = new LmHardwareCommand();
        command.setDevice(hardware);
        command.setType(commandType);
        command.setUser(user);
        ImmutableMap.Builder<LmHardwareCommandParam, Object> builder = new ImmutableMap.Builder<>();
        Map<LmHardwareCommandParam, Object> parameters = builder.put(LmHardwareCommandParam.WAITABLE, true)
                                                                 //TODO do we need EXPECT_RESPONSE?
                                                                .putAll(additionalParameters)
                                                                .build();
        command.setParams(parameters);
        
        try {
            rfCommandStrategy.sendCommand(command);
            results.getIfPresent(resultId)
                   .getForInventoryId(hardware.getInventoryID())
                   .setStatus(commandType, PqrConfigCommandStatus.SUCCESS);;
        } catch (CommandCompletionException e) {
            results.getIfPresent(resultId)
                   .getForInventoryId(hardware.getInventoryID())
                   .setStatus(commandType, PqrConfigCommandStatus.FAILED);
        }
    }
    
}
