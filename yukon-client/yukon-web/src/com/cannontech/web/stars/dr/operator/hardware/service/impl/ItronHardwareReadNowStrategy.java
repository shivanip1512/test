package com.cannontech.web.stars.dr.operator.hardware.service.impl;

import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.itron.service.ItronCommunicationException;
import com.cannontech.dr.itron.service.ItronDataReadService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareReadNowStrategy;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareStrategyType;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Maps;

public class ItronHardwareReadNowStrategy implements HardwareReadNowStrategy{
    
    private static final Logger log = YukonLogManager.getLogger(ItronHardwareReadNowStrategy.class);
    private static final String keyBase = "yukon.web.modules.operator.hardware.";
  
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ItronDataReadService itronDataReadService; 
    @Autowired private IDatabaseCache cache;   
    
    @Override
    public Map<String, Object> readNow(int deviceId, YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        LiteYukonPAObject device = cache.getAllPaosMap().get(deviceId);
        Map<String, Object> json = Maps.newHashMapWithExpectedSize(2);
        try {
            itronDataReadService.collectDataForRead(deviceId);
            log.debug("Read completed for " + device);
            json.put("success", true);
            json.put("message", accessor.getMessage(keyBase + "readNowSuccess"));
        } catch (ItronCommunicationException e) {
            log.debug("Read now failed for " + device);
            json.put("success", false);
            json.put("message", accessor.getMessage(keyBase + "error.readNowFailed", e.getMessage()));
        }
        
        return json;
    }
    
    @Override
    public HardwareStrategyType getType() {
        return HardwareStrategyType.ITRON;
    }
    
    @Override
    public boolean canHandle(HardwareType type) {
        return type.isItron();
    }

}
