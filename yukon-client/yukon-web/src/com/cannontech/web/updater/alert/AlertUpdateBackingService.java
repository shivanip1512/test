package com.cannontech.web.updater.alert;

import com.cannontech.common.alert.service.AlertService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class AlertUpdateBackingService implements UpdateBackingService {
    private AlertService alertService;

    @Override
    public String getLatestValue(final String identifier, final long afterDate, final YukonUserContext userContext) {
        String latestValue = Integer.toString(alertService.getCountForUser(userContext.getYukonUser()));
        return latestValue;
    }

    public void setAlertService(AlertService alertService) {
        this.alertService = alertService;
    }
    
}
