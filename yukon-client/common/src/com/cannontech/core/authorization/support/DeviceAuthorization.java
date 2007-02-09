package com.cannontech.core.authorization.support;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DeviceAuthorization {

    public AuthorizationResponse isAuthorized(LiteYukonUser user, PaoPermission permission,
            LiteYukonPAObject device);

}