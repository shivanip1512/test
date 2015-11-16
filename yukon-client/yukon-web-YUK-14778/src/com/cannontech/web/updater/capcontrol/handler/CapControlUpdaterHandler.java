package com.cannontech.web.updater.capcontrol.handler;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.capcontrol.CapControlUpdaterTypeEnum;

public interface CapControlUpdaterHandler {

    public String handle(int id, YukonUserContext userContext);
    
    public CapControlUpdaterTypeEnum getUpdaterType();
}