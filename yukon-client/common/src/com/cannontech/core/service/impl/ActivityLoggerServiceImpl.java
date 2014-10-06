package com.cannontech.core.service.impl;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.core.service.ActivityLoggerService;

public class ActivityLoggerServiceImpl implements ActivityLoggerService {
    @Override
    public void logEvent(int userID, String action, String description) {
        ActivityLogger.logEvent(userID, action, description);
    }
}