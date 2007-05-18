package com.cannontech.web.updater.point;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.updater.DataType;
import com.cannontech.web.updater.DataUpdaterService;
import com.cannontech.web.updater.UpdateValue;

public class PointDataRegistrationServiceImpl implements PointDataRegistrationService {
    private DataUpdaterService updaterService;
    
    public UpdateValue getLatestValue(int pointId, String format, LiteYukonUser user) {
        String id = DataType.POINT + "/" + pointId + "/" + format;
        UpdateValue result = updaterService.getFirstValue(id, user);
        return result;
    }

    @Required
    public void setUpdaterService(DataUpdaterService updaterService) {
        this.updaterService = updaterService;
    }
    
}
