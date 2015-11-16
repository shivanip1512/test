package com.cannontech.web.updater.phaseDetect.handler;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.phaseDetect.PhaseDetectUpdaterTypeEnum;

public interface PhaseDetectUpdaterHandler {

    public String handle(int id, YukonUserContext userContext);
    
    public PhaseDetectUpdaterTypeEnum getUpdaterType();
}

