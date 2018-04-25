package com.cannontech.web.stars.dr.operator.hardware.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.DeviceReadResult;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareReadNowStrategy;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareStrategyType;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public class EcobeeHardwareReadNowStrategy implements HardwareReadNowStrategy{
    
    private static final Logger log = YukonLogManager.getLogger(EcobeeHardwareReadNowStrategy.class);
    private static final String keyBase = "yukon.web.modules.operator.hardware.";
    
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private DeviceDao deviceDao;
    

    @Override
    public Map<String, Object> readNow(int deviceId, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        Set<BuiltInAttribute> allAttributes = Sets.newHashSet(BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG,
                BuiltInAttribute.CONTROL_STATUS, BuiltInAttribute.INDOOR_TEMPERATURE,
                BuiltInAttribute.OUTDOOR_TEMPERATURE, BuiltInAttribute.COOL_SET_TEMPERATURE,
                BuiltInAttribute.HEAT_SET_TEMPERATURE);

        DeviceReadResult result = deviceAttributeReadService.initiateReadAndWait(device, allAttributes,
                                             DeviceRequestType.LM_DEVICE_DETAILS_ATTRIBUTE_READ,
                                             userContext.getYukonUser());
        
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", result.isSuccess());
        if (result.isSuccess()) {
            resultMap.put("message", accessor.getMessage(keyBase + "readNowSuccess"));
        } else {
            List<String> errors = new ArrayList<>();
            for (SpecificDeviceErrorDescription error : result.getErrors()) {
                String detail = accessor.getMessage(error.getDetail());
                errors.add(detail);
            }
            String errorsMsg = Joiner.on(',').join(errors);
            log.info("Read failed for " + device.getPaoIdentifier() + " with errors: " + errorsMsg);
            resultMap.put("message", errorsMsg);
        }
        return resultMap;
    }
    
    @Override
    public HardwareStrategyType getType() {
        return HardwareStrategyType.ECOBEE;
    }
    
    @Override
    public boolean canHandle(HardwareType type) {
        return type.isEcobee();
    }

}
