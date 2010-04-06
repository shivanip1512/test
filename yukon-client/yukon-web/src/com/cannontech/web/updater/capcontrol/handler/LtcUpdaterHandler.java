package com.cannontech.web.updater.capcontrol.handler;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.capcontrol.LtcUpdaterTypeEnum;

public interface LtcUpdaterHandler {

    public String handle(int id, YukonUserContext userContext);
    
    public LtcUpdaterTypeEnum getUpdaterType();
}