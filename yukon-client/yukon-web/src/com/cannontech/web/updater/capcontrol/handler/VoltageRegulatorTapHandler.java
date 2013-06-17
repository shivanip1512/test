package com.cannontech.web.updater.capcontrol.handler;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.TapOperation;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.message.capcontrol.streamable.VoltageRegulatorFlags;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.capcontrol.VoltageRegulatorUpdaterTypeEnum;

public class VoltageRegulatorTapHandler implements VoltageRegulatorUpdaterHandler {
    private static final Logger log = YukonLogManager.getLogger(VoltageRegulatorTapHandler.class);
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
            String retStr = tapOp.name(); 
            if (regulatorFlags.isRecentOperation()) {
                return retStr+"_RECENT";
            } else {
                return retStr;                    
            }
        } catch (NotFoundException nfe) {
            log.info("Voltage Regulator with Id " + id + " not found in cache.");
            // This can happen if we delete an object and return
            // to a page that that used to have that object before
            // the server was able to update the cache.
            return StringUtils.EMPTY;
        }
    }
    
    @Autowired
    public void setCapControlCache(CapControlCache capControlCache){
        this.capControlCache = capControlCache;
    }
}
