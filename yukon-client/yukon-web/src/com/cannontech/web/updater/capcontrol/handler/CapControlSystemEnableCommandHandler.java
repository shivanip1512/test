package com.cannontech.web.updater.capcontrol.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.capcontrol.CapControlUpdaterTypeEnum;

public class CapControlSystemEnableCommandHandler implements CapControlUpdaterHandler {
    
    private CapControlCache capControlCache;

    @Override
    public CapControlUpdaterTypeEnum getUpdaterType() {
        return CapControlUpdaterTypeEnum.SYSTEM_ENABLE_COMMAND;
    }

    @Override
    public String handle(int id, YukonUserContext userContext) {
        if(capControlCache.getSystemStatusOn()) {
            return "enabled";
        }
        return "disabled";
    }

    @Autowired
    public void setCapControlCache(CapControlCache capControlCache){
        this.capControlCache = capControlCache;
    }
}