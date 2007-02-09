package com.cannontech.core.authorization.service;

import com.cannontech.core.authorization.support.PaoPermission;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Interface for determining authorization for devices
 */
public interface PaoAuthorizationService {

    /**
     * Method to determine if a user is authorized for the given permission with
     * the given device
     * @param permission - Permission in question
     * @param user - User to perform action
     * @param device - Device to use with action
     * @return True is user is authorized
     */
    public boolean isAuthorized(LiteYukonUser user, PaoPermission permission, LiteYukonPAObject device);

}
