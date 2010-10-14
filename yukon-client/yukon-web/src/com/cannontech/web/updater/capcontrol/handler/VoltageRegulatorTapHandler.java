package com.cannontech.web.updater.capcontrol.handler;

import org.springframework.beans.factory.annotation.Autowired;

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
            VoltageRegulatorFlags regulator = capControlCache.getVoltageRegulator(id);
            if (regulator.isLowerTap()) {
                return "LowerTap";
            } else if (regulator.isRaiseTap()) {
                return "RaiseTap";
            } else {
                return "none";
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
