package com.cannontech.web.updater.demandreset;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.demandreset.model.DemandResetResult;
import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.RecentResultUpdateBackingService;

public class DemandResetBackingService  extends RecentResultUpdateBackingService{
    @Autowired private DemandResetService demandResetService; 
    
    @Override
    public Object getResultValue(String key, String resultTypeStr) {

        DemandResetResult demandResetResult = demandResetService.getResult(key);
       
       if (demandResetResult  == null) {
           return "";
       }
       DemandResetResultField fields = DemandResetResultField.valueOf(resultTypeStr);
       return fields.getValue(demandResetResult);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
}
