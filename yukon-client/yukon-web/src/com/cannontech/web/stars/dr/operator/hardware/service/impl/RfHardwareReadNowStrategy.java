package com.cannontech.web.stars.dr.operator.hardware.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReplyType;
import com.cannontech.dr.rfn.service.RfnExpressComMessageService;
import com.cannontech.dr.rfn.service.RfnUnicastCallback;
import com.cannontech.dr.rfn.service.WaitableRfnUnicastCallback;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareReadNowStrategy;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareStrategyType;

public class RfHardwareReadNowStrategy implements HardwareReadNowStrategy {
    private static final Logger log = YukonLogManager.getLogger(RfHardwareReadNowStrategy.class);
    private static final String keyBase = "yukon.web.modules.operator.hardware.";

    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnExpressComMessageService rfnExpressComMessageService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DeviceDao deviceDao;
    @Autowired private AttributeService attributeService;

    @Override
    public Map<String, Object> readNow(int deviceId, YukonUserContext userContext) {
        final MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        final RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        final Map<String, Object> resultMap = new HashMap<>();
        
        /* Using a waitable, this will block for the initial response to the read request or until the intial response timeout expires. */
        WaitableRfnUnicastCallback waitableCallback = new WaitableRfnUnicastCallback(new RfnUnicastCallback() {

            @Override
            public void processingExceptionOccured(MessageSourceResolvable message) {
                log.error(message);
                resultMap.put("success", false);
                resultMap.put("message", accessor.getMessage(message));
            }
            
            @Override
            public void receivedStatus(RfnExpressComUnicastReplyType status) {
                boolean success = status == RfnExpressComUnicastReplyType.OK ? true : false;
                resultMap.put("success", success);
                if (success) {
                    log.debug("Read now initiated for " + device);
                    resultMap.put("message", accessor.getMessage(keyBase + "readNowSuccess"));
                } else {
                    log.debug("Read now failed for " + device);
                    resultMap.put("message", accessor.getMessage(keyBase + "error.readNowFailed", status));
                }
            }

            @Override 
            public void receivedStatusError(RfnExpressComUnicastReplyType replyType) {
                log.error("Error Reading RF Hardware: " + replyType);
                resultMap.put("success", false);
                resultMap.put("message", accessor.getMessage(keyBase + "error.readNowFailed", replyType));
            }
            
            @Override public void complete() {}
        });
            
        rfnExpressComMessageService.readDevice(device, waitableCallback);
        
        try {
            waitableCallback.waitForCompletion();
        } catch (InterruptedException e) {/* ignore */};
        
        return resultMap;
    }
    
    @Override
    public HardwareStrategyType getType() {
        return HardwareStrategyType.RF;
    }
    
    @Override
    public boolean canHandle(HardwareType type) {
        return type.isRf();
    }


}
