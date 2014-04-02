package com.cannontech.web.updater.alert;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.cannontech.common.alert.service.AlertService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.SingleUpdateService;
import com.cannontech.web.updater.UpdateBackingService;

public class AlertUpdateBackingService implements UpdateBackingService {
    private AlertService alertService;
    
    private SingleUpdateService countUpdater = new SingleUpdateService() {
        public String getLatestValue(long afterDate, YukonUserContext userContext) {
            String latestValue = Integer.toString(alertService.getCountForUser(userContext.getYukonUser()));
            return latestValue;
        }  
    };

    private SingleUpdateService lastIdUpdater = new SingleUpdateService() {
        public String getLatestValue(long afterDate, YukonUserContext userContext) {
            String latestValue = Long.toString(alertService.getLatestAlertTime(userContext.getYukonUser()));
            return latestValue;
        }  
    };
    
    private Map<String, SingleUpdateService> serviceMap = new HashMap<String, SingleUpdateService>();
    
    {
        serviceMap.put("COUNT", countUpdater);
        serviceMap.put("LASTID", lastIdUpdater);
    }
    
    @Override
    public String getLatestValue(final String identifier, final long afterDate, final YukonUserContext userContext) {
        SingleUpdateService singleUpdateService = serviceMap.get(identifier);
        Validate.notNull(singleUpdateService, "Unknown identifier: " + identifier);
        String latestValue = singleUpdateService.getLatestValue(afterDate, userContext);
        return latestValue;
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier,
    		long afterDate, YukonUserContext userContext) {
    	return true;
    }

    public void setAlertService(AlertService alertService) {
        this.alertService = alertService;
    }
    
}
