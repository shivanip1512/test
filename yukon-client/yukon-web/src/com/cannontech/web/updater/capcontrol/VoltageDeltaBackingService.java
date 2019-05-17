package com.cannontech.web.updater.capcontrol;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.fasterxml.jackson.core.JsonProcessingException;

public class VoltageDeltaBackingService implements UpdateBackingService {
    private static final Logger log = YukonLogManager.getLogger(VoltageDeltaBackingService.class);

    @Autowired private ZoneDao zoneDao;
    @Autowired private PointDao pointDao;
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

        String[] idParts = StringUtils.split(identifier, "/");
        int capBankId = Integer.parseInt(idParts[0]);
        int pointId = Integer.parseInt(idParts[1]);
        String updaterTypeStr = idParts[2];
        Double returnValue = null;
        
        VoltageDeltaUpdaterTypeEnum updaterType = VoltageDeltaUpdaterTypeEnum.valueOf(updaterTypeStr);
        LitePointUnit pointUnit = pointDao.getPointUnit(pointId);
        
        switch(updaterType) {
            case PRE_OP : 
                Double preOp = zoneDao.getPreOpForPoint(capBankId, pointId);
                BigDecimal preOpValue = new BigDecimal(preOp).setScale(pointUnit.getDecimalPlaces(), RoundingMode.HALF_DOWN);
                returnValue = preOpValue.doubleValue();
                break;
            case VOLTAGE_DELTA :
                Double delta = zoneDao.getDeltaForPoint(capBankId, pointId);
                BigDecimal bdDelta = new BigDecimal(delta).setScale(2, RoundingMode.HALF_DOWN);
                returnValue = bdDelta.doubleValue();
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