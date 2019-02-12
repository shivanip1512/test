package com.cannontech.web.updater.porterResponseMonitoring;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.porterResponseMonitoring.handler.PorterResponseUpdaterHandler;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class PorterResponseMonitorBackingService implements UpdateBackingService {
    private final Map<PorterResponseMonitorUpdaterTypeEnum, PorterResponseUpdaterHandler> handlersMap;

    @Autowired
    public PorterResponseMonitorBackingService(List<PorterResponseUpdaterHandler> handlers) {
        Builder<PorterResponseMonitorUpdaterTypeEnum, PorterResponseUpdaterHandler> builder = ImmutableMap.builder();
        for (PorterResponseUpdaterHandler handler : handlers) {
            builder.put(handler.getUpdaterType(), handler);
        }
        handlersMap = builder.build();
    }

    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        int porterResponseMonitorId = 0;
        String updaterTypeStr = null;
        String[] idParts = StringUtils.split(identifier, "/");
        if (idParts.length == 1) {
            updaterTypeStr = idParts[0];
        } else {
            porterResponseMonitorId = Integer.parseInt(idParts[0]);
            updaterTypeStr = idParts[1];
        }

        PorterResponseMonitorUpdaterTypeEnum updaterType = PorterResponseMonitorUpdaterTypeEnum.valueOf(updaterTypeStr);
        PorterResponseUpdaterHandler handler = handlersMap.get(updaterType);
        return handler.handle(porterResponseMonitorId);
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
}
