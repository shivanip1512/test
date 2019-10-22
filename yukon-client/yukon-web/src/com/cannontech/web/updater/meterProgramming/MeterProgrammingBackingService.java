package com.cannontech.web.updater.meterProgramming;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.service.PorterDynamicPaoInfoService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class MeterProgrammingBackingService implements UpdateBackingService {
    
    @Autowired private PorterDynamicPaoInfoService porterDynamicPaoInfoService;
    
    private enum RequestType {
        PROGRESS
        ;
    }
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        String[] idParts = StringUtils.split(identifier, "/");
        String deviceId = idParts[0];
        RequestType type = RequestType.valueOf(idParts[1]);
        if (type == RequestType.PROGRESS) {
            var progress = porterDynamicPaoInfoService.getProgrammingProgress(Integer.valueOf(deviceId));
            if (progress != null && progress >= 0 && progress <= 100) {
                return Integer.toString(progress.intValue());
            }
        }
        return null;
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return false;
    }
}