package com.cannontech.web.updater.capcontrol.handler;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.VoltageRegulatorDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.capcontrol.VoltageRegulatorUpdaterTypeEnum;

public class VoltageRegulatorVoltChangePerTapUpdaterHandler implements VoltageRegulatorUpdaterHandler {
    private static final Logger log = YukonLogManager.getLogger(VoltageRegulatorVoltChangePerTapUpdaterHandler.class);
    @Autowired private VoltageRegulatorDao voltageRegulatorDao;
    
    @Override
    public String handle(int id, YukonUserContext userContext) {
        try {
            double vChangePerTap = voltageRegulatorDao.getVoltChangePerTapForRegulator(id);
            return String.valueOf(vChangePerTap);
        } catch (NotFoundException nfe) {
            log.info("Voltage Regulator with Id " + id + " not found.");
            // This can happen if we delete an object and return
            // to a page that that used to have that object before
            // the server was able to update the cache.
            return StringUtils.EMPTY;
        }
    }
    
    @Override
    public VoltageRegulatorUpdaterTypeEnum getUpdaterType() {
        return VoltageRegulatorUpdaterTypeEnum.VOLT_CHANGE_PER_TAP;
    }
}