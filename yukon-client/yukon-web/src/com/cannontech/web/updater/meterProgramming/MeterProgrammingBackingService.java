package com.cannontech.web.updater.meterProgramming;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class MeterProgrammingBackingService implements UpdateBackingService {
    
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
            //TODO: get progress percent
            Random r = new Random();
            return String.valueOf(r.nextInt((100 - 1) + 1) + 1);
        }
        return null;
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
}