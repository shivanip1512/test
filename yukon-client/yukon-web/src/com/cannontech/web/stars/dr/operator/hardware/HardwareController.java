package com.cannontech.web.stars.dr.operator.hardware;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.deviceread.dao.CollectingDeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReplyType;
import com.cannontech.dr.rfn.service.RfnExpressComMessageService;
import com.cannontech.dr.rfn.service.RfnUnicastCallback;
import com.cannontech.dr.rfn.service.WaitableRfnUnicastCallback;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.AttributeReadingHelper;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/operator/hardware/*")
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES)
public class HardwareController {

    private static final Logger log = YukonLogManager.getLogger(HardwareController.class);
    private static final String keyBase = "yukon.web.modules.operator.hardware.";

    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnExpressComMessageService rfnExpressComMessageService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private AttributeReadingHelper attributeReadingHelper;
    @Autowired private DeviceDao deviceDao;
    @Autowired private AttributeService attributeService;

    @RequestMapping("ecobee/readNow")
    @ResponseBody
    public Map<String, Object> ecobeeReadNow(int deviceId, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        Set<BuiltInAttribute> allAttributes = Sets.newHashSet(BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG,
                BuiltInAttribute.CONTROL_STATUS, BuiltInAttribute.INDOOR_TEMPERATURE,
                BuiltInAttribute.OUTDOOR_TEMPERATURE, BuiltInAttribute.COOL_SET_TEMPERATURE,
                BuiltInAttribute.HEAT_SET_TEMPERATURE);

        CollectingDeviceAttributeReadCallback result = 
                attributeReadingHelper.initiateReadAndWait(device, allAttributes,
                                             DeviceRequestType.LM_DEVICE_DETAILS_ATTRIBUTE_READ,
                                             userContext.getYukonUser());
        
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", result.isSuccess());
        if (result.isSuccess()) {
            resultMap.put("message", accessor.getMessage(keyBase + "readNowSuccess"));
        } else {
            Set<DeviceAttributeReadErrorType> errors = new HashSet<>();
            for (DeviceAttributeReadError error : result.getMessages()) {
                errors.add(error.getType());
            }
            log.info("Read failed for " + device.getPaoIdentifier() + " with errors: " +Joiner.on(',').join(errors));
            resultMap.put("message", accessor.getMessage(keyBase + "error.readNowFailed", Joiner.on(',').join(errors)));
        }
        return resultMap;
    }
    
    @RequestMapping("plc/readNow")
    @ResponseBody
    public Map<String, Object> plcReadNow(int deviceId, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        Set<BuiltInAttribute> allAttributes = Sets.newHashSet(BuiltInAttribute.RELAY_1_REMAINING_CONTROL,
                                                              BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG,
                                                              BuiltInAttribute.RELAY_1_SHED_TIME_DATA_LOG);

        CollectingDeviceAttributeReadCallback result = 
                attributeReadingHelper.initiateReadAndWait(device, allAttributes,
                                             DeviceRequestType.LM_DEVICE_DETAILS_ATTRIBUTE_READ,
                                             userContext.getYukonUser());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", result.isSuccess());

        if (result.isSuccess()) {
            resultMap.put("message", accessor.getMessage(keyBase + "readNowSuccess"));
        } else {
            Set<DeviceAttributeReadErrorType> errors = new HashSet<>();
            for (DeviceAttributeReadError error : result.getMessages()) {
                errors.add(error.getType());
            }
            log.info("Read failed for " + device.getPaoIdentifier() + " with errors: " +Joiner.on(',').join(errors));
            resultMap.put("message", accessor.getMessage(keyBase + "error.readNowFailed", Joiner.on(',').join(errors)));
        }

        return resultMap;
    }
    
    @RequestMapping("rf/readNow")
    @ResponseBody
    public Map<String, Object> rfReadNow(int deviceId, YukonUserContext context) {
        final MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
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
    
}