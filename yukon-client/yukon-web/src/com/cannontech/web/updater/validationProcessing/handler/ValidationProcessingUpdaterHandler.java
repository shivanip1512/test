package com.cannontech.web.updater.validationProcessing.handler;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.validationProcessing.ValidationMonitorUpdaterTypeEnum;

public interface ValidationProcessingUpdaterHandler {
    
    public String handle(int validationMonitorId, YukonUserContext userContext);
    
    public ValidationMonitorUpdaterTypeEnum getUpdaterType();
}
