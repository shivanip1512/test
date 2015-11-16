package com.cannontech.web.updater.capcontrol.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.message.capcontrol.streamable.VoltageRegulatorFlags;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.capcontrol.VoltageRegulatorUpdaterTypeEnum;
import com.fasterxml.jackson.core.JsonProcessingException;

public class VoltageRegulatorModeUpdaterHandler implements VoltageRegulatorUpdaterHandler {
    private static final Logger log = YukonLogManager.getLogger(VoltageRegulatorModeUpdaterHandler.class);
    private CapControlCache capControlCache;
    
    @Override
    public String handle(int id, YukonUserContext userContext) {
        try {
            VoltageRegulatorFlags regulatorFlags = capControlCache.getVoltageRegulatorFlags(id);

            Map<String, Object> response = new HashMap<>();
            response.put("paoId", id);

            if (regulatorFlags.isAutoRemote()) {
                if (regulatorFlags.isAutoRemoteManual()) {
                    response.put("local", true);
                } else {
                    response.put("warning", true);
                }
            }
            try {
                return JsonUtils.toJson(response);
            } catch (JsonProcessingException e) {
                //Writing this simple object to JSON should not throw an exception.
                throw new RuntimeException(e);
            }
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