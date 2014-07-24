package com.cannontech.web.stars.dr.operator.hardware.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.CollectingDeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareReadNowStrategy;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareStrategyType;
import com.cannontech.web.widget.AttributeReadingHelper;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public class PlcHardwareReadNowStrategy implements HardwareReadNowStrategy {
    
    private static final Logger log = YukonLogManager.getLogger(PlcHardwareReadNowStrategy.class);
    private static final String keyBase = "yukon.web.modules.operator.hardware.";
    
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private AttributeReadingHelper attributeReadingHelper;
    @Autowired private DeviceDao deviceDao;
    @Autowired private AttributeService attributeService;
    
    @Override
    public Map<String, Object> readNow(int deviceId, YukonUserContext userContext) {
       

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
    
    @Override
    public HardwareStrategyType getType() {
        return HardwareStrategyType.PLC;
    }
    
    @Override
    public boolean canHandle(HardwareType type) {
        return type.isTwoWayPlcLcr();
    }
   
}
