package com.cannontech.web.updater.capcontrol.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.capcontrol.LtcUpdaterTypeEnum;
import com.cannontech.yukon.cbc.Ltc;

public class LtcModeUpdaterHandler implements LtcUpdaterHandler {

    private CapControlCache capControlCache;
    
    @Override
    public String handle(int id, YukonUserContext userContext) {
        try {
            Ltc ltc = capControlCache.getLtc(id);
            if (ltc.isAutoRemote()) {
                if (ltc.isAutoRemoteManual()) {
                    return "NormalLocal";
                }
                else {
                    return "WarningLocal";
                }
            }
            return "none";                
        } catch (NotFoundException nfe) {
            // This can happen if we delete an object and return
            // to a page that that used to have that object before
            // the server was able to update the cache.
            // By returning null the service won't try to update
            // that object.
            return null;
        }
    }
    
    @Override
    public LtcUpdaterTypeEnum getUpdaterType() {
        return LtcUpdaterTypeEnum.MODE;
    }

    @Autowired
    public void setCapControlCache(CapControlCache capControlCache){
        this.capControlCache = capControlCache;
    }
}