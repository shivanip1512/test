package com.cannontech.web.updater.tdc;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.tdc.service.TdcService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class TdcBackingService implements UpdateBackingService{
    
    @Autowired private TdcService tdcService;
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext){
        String[] idParts = StringUtils.split(identifier, "/");
        String updaterTypeStr =idParts[0];
  
        if(updaterTypeStr.equals("STATE")){
            int pointId = Integer.parseInt(idParts[1]);
            return tdcService.getPointState(pointId);
        }
        else if(updaterTypeStr.equals("ALARM")){
            int count = tdcService.getUnackAlarmCount();
            if(count > 0){
                return "MULT_ALARMS";
            }
            else{
                return "NO_ALARMS";
            }
        }
        else if(updaterTypeStr.equals("ALARM_DISPLAY")){
            int displayId = Integer.parseInt(idParts[1]);
            int count = tdcService.getUnackAlarmCountForDisplay(displayId);
            if(count > 0){
                return "MULT_ALARMS";
            }
            else{
                return "NO_ALARMS";
            }
        }
        else if(updaterTypeStr.equals("BG_COLOR_POINT")){
            int pointId = Integer.parseInt(idParts[1]);
            int condition = Integer.parseInt(idParts[2]);
            if(condition == 0){
                return tdcService.getUnackOrActiveAlarmColor(pointId);
            }else{
                return tdcService.getUnackOrActiveAlarmColor(pointId, condition);
            }
        }
        else if(updaterTypeStr.equals("ALARM_POINT")){
            int pointId = Integer.parseInt(idParts[1]);
            int count = tdcService.getUnackAlarmCountForPoint(pointId);
            if(count > 0){
                return "MULT_ALARMS";
            }
            else{
                return "NO_ALARMS";
            }
        }
        else if(updaterTypeStr.equals("ALARM_POINT_CONDITION")){
            int pointId = Integer.parseInt(idParts[1]);
            int condition = Integer.parseInt(idParts[2]);
            int count = tdcService.getUnackAlarmCountForPoint(pointId, condition);
            if(count > 0){
                return "ONE_ALARM";
            }
            else{
                return "NO_ALARMS";
            }
        }
        else if(updaterTypeStr.equals("ALARM_COUNT_POINT")){
            int pointId = Integer.parseInt(idParts[1]);
            int count = tdcService.getUnackAlarmCountForPoint(pointId);
            if(count == 0){
                return "NO_ALARMS";
            }
            if(count == 1){
                return "ONE_ALARM";
            }
            if(count > 1){
                return "MULT_ALARMS";
            }
        }else if(updaterTypeStr.equals("MAN_CONTROL")){
            int pointId = Integer.parseInt(idParts[1]);
            if(tdcService.isManualControlEnabled(pointId)){
                return "TRUE";
            }else{
                return "FALSE";
            }
        }else if(updaterTypeStr.equals("MAN_ENTRY")){
            int pointId = Integer.parseInt(idParts[1]);
            int pointTypeId = Integer.parseInt(idParts[2]);
            boolean hasPointValueColumn = Boolean.parseBoolean(idParts[3]);
            if(tdcService.isManualEntryEnabled(pointId, pointTypeId, hasPointValueColumn)){
                return "TRUE";
            }else{
                return "FALSE";
            }
        }
        throw new RuntimeException("Identifier string isn't well formed: " + identifier);
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier,
            long afterDate, YukonUserContext userContext){
        return true;
    }
}
