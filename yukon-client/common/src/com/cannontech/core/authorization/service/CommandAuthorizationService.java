package com.cannontech.core.authorization.service;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Interface for determining authorization for device commands
 */
public interface CommandAuthorizationService {

    public abstract boolean isAuthorized(LiteYukonUser user, String command,
            LiteYukonPAObject device);

}