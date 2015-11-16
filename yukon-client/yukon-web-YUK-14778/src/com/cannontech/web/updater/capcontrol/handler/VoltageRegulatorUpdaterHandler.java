package com.cannontech.web.updater.capcontrol.handler;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.capcontrol.VoltageRegulatorUpdaterTypeEnum;

public interface VoltageRegulatorUpdaterHandler {

    public String handle(int id, YukonUserContext userContext);
    
    public VoltageRegulatorUpdaterTypeEnum getUpdaterType();
}