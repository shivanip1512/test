package com.cannontech.web.updater.capcontrol;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.fasterxml.jackson.core.JsonProcessingException;

public class VoltageDeltaBackingService implements UpdateBackingService {
    private static final Logger log = YukonLogManager.getLogger(VoltageDeltaBackingService.class);

    @Autowired private ZoneDao zoneDao;
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

        String[] idParts = StringUtils.split(identifier, "/");
        int capBankId = Integer.parseInt(idParts[0]);
        int pointId = Integer.parseInt(idParts[1]);
        String updaterTypeStr = idParts[2];
        Double returnValue = null;
        
        VoltageDeltaUpdaterTypeEnum updaterType = VoltageDeltaUpdaterTypeEnum.valueOf(updaterTypeStr);
        
        switch(updaterType) {
            case PRE_OP : 
                returnValue = zoneDao.getPreOpForPoint(capBankId, pointId);
                break;
            case VOLTAGE_DELTA : 
                returnValue = zoneDao.getDeltaForPoint(capBankId, pointId);
                break;
        }
        
        if(returnValue != null) {
            try {
                return JsonUtils.toJson(returnValue);
            } catch (JsonProcessingException e) {
                log.error("A JSON exception was caught: " + e);
                return "";
            }
        } else {
            log.error("No Pre-Op or Delta data could be found for cap bank ID: " + capBankId + ", point ID: " + pointId);
            return "";
        }
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
}