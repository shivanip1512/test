package com.cannontech.web.stars.dr.operator.hardware.service.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareReadNowStrategy;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareStrategyType;
import com.google.common.collect.Maps;

public class ZigBeeHardwareReadNowStrategy implements HardwareReadNowStrategy{
    
    private static final Logger log = YukonLogManager.getLogger(ZigBeeHardwareReadNowStrategy.class);
    private static final String keyBase = "yukon.web.modules.operator.hardware.";
    
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ZigbeeWebService zigbeeWebService;
    @Autowired private ZigbeeDeviceDao zigbeeDeviceDao;
    
    @Override
    public Map<String, Object> readNow(int deviceId,YukonUserContext context) {
        final MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        final ZigbeeDevice device = zigbeeDeviceDao.getZigbeeDevice(deviceId);

        Map<String, Object> json = Maps.newHashMapWithExpectedSize(2);
        try {
            zigbeeWebService.readLoadGroupAddressing(device); // TODO this should take a callback similar to rf and support timeout
            
            log.debug("Read now initiated for " + device);
            json.put("success", true);
            json.put("message", accessor.getMessage(keyBase + "readNowSuccess"));
        } catch (DigiWebServiceException e) {
            log.debug("Read now failed for " + device);
            json.put("success", false);
            json.put("message", accessor.getMessage(keyBase + "error.readNowFailed", e.getMessage()));
        }
        
        return json;
    }
    
    @Override
    public HardwareStrategyType getType() {
        return HardwareStrategyType.ZIGBEE;
    }
    
    @Override
    public boolean canHandle(HardwareType type) {
        return type.isZigbee();
    }

}
