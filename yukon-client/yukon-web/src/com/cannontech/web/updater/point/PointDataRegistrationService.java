package com.cannontech.web.updater.point;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateValue;



public interface PointDataRegistrationService {
    public UpdateValue getLatestValue(final int pointId, final String format, YukonUserContext userContext);
    
    public String getRawPointDataUpdaterSpan(int pointId, String format, YukonUserContext userContext);
}