package com.cannontech.web.updater.capcontrol.handler;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.capcontrol.VoltageRegulatorUpdaterTypeEnum;
import com.cannontech.yukon.cbc.VoltageRegulatorFlags;

public class VoltageRegulatorModeUpdaterHandler implements VoltageRegulatorUpdaterHandler {
    private static final Logger log = YukonLogManager.getLogger(VoltageRegulatorModeUpdaterHandler.class);
    private CapControlCache capControlCache;
    
    @Override
    public String handle(int id, YukonUserContext userContext) {
        try {
            VoltageRegulatorFlags regulatorFlags = capControlCache.getVoltageRegulatorFlags(id);
            if (regulatorFlags.isAutoRemote()) {
                if (regulatorFlags.isAutoRemoteManual()) {
                    return "NormalLocal";
                }
                else {
                    return "WarningLocal";
                }
            }
            return "none";                
        } catch (NotFoundException nfe) {
            log.info("Voltage Regulator with Id " + id + " not found in cache.");
            // This can happen if we delete an object and return
            // to a page that that used to have that object before
            // the server was able to update the cache.
            return StringUtils.EMPTY;
        }
    }
    
    @Override
    public VoltageRegulatorUpdaterTypeEnum getUpdaterType() {
        return VoltageRegulatorUpdaterTypeEnum.MODE;
    }

    @Autowired
    public void setCapControlCache(CapControlCache capControlCache){
        this.capControlCache = capControlCache;
    }
}