package com.cannontech.web.updater.validationProcessing;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.validationProcessing.handler.ValidationProcessingUpdaterHandler;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ValidationMonitorBackingService implements UpdateBackingService {
    private final Map<ValidationMonitorUpdaterTypeEnum, ValidationProcessingUpdaterHandler> handlersMap;

    @Autowired
    public ValidationMonitorBackingService(List<ValidationProcessingUpdaterHandler> handlers) {
        Builder<ValidationMonitorUpdaterTypeEnum, ValidationProcessingUpdaterHandler> builder = ImmutableMap.builder();
        for (ValidationProcessingUpdaterHandler handler : handlers) {
            builder.put(handler.getUpdaterType(), handler);
        }
        handlersMap = builder.build();
    }

    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        int validationMonitorId = 0;
        String updaterTypeStr = null;
        String[] idParts = StringUtils.split(identifier, "/");
        if (idParts.length == 1) {
            updaterTypeStr = idParts[0];
        } else {
            validationMonitorId = Integer.parseInt(idParts[0]);
            updaterTypeStr = idParts[1];
        }

        ValidationMonitorUpdaterTypeEnum updaterType = ValidationMonitorUpdaterTypeEnum.valueOf(updaterTypeStr);
        ValidationProcessingUpdaterHandler handler = handlersMap.get(updaterType);
        return handler.handle(validationMonitorId, userContext);
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
}
