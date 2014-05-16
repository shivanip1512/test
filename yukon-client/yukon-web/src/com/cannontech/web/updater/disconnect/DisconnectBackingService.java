package com.cannontech.web.updater.disconnect;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.RecentResultUpdateBackingService;

public class DisconnectBackingService  extends RecentResultUpdateBackingService{
    @Autowired private DisconnectService disconnectService; 
    
    @Override
    public Object getResultValue(String key, String resultTypeStr) {

        DisconnectResult disconnectResult = disconnectService.getResult(key);
       
       if (disconnectResult  == null) {
           return "";
       }
       DisconnectResultFields connectDisconnectResultFields = DisconnectResultFields.valueOf(resultTypeStr);
       return connectDisconnectResultFields.getValue(disconnectResult);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
}
