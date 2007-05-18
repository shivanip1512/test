package com.cannontech.web.updater.point;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.updater.UpdateValue;



public interface PointDataRegistrationService {
    public UpdateValue getLatestValue(final int pointId, final String format, LiteYukonUser user);
}