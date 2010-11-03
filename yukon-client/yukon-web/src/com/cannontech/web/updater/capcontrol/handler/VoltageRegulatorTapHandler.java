package com.cannontech.web.updater.capcontrol.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.TapOperation;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.capcontrol.VoltageRegulatorUpdaterTypeEnum;
import com.cannontech.yukon.cbc.VoltageRegulatorFlags;

public class VoltageRegulatorTapHandler implements VoltageRegulatorUpdaterHandler {

    private CapControlCache capControlCache;
    
    @Override
    public VoltageRegulatorUpdaterTypeEnum getUpdaterType() {
        return VoltageRegulatorUpdaterTypeEnum.TAP;
    }

    @Override
    public String handle(int id, YukonUserContext userContext) {
        try {
            VoltageRegulatorFlags regulatorFlags = capControlCache.getVoltageRegulatorFlags(id);
            TapOperation tapOp = regulatorFlags.getLastOperation();
            String retStr = tapOp.getDisplayValue(); 
            if (regulatorFlags.isRecentOperation()) {
                return retStr+"Recent";
            } else {
                return retStr;                    
            }
        } catch (NotFoundException nfe) {
            // This can happen if we delete an object and return
            // to a page that that used to have that object before
            // the server was able to update the cache.
            // By returning null the service won't try to update
            // that object.
            return null;
        }
    }
    
    @Autowired
    public void setCapControlCache(CapControlCache capControlCache){
        this.capControlCache = capControlCache;
    }
}
