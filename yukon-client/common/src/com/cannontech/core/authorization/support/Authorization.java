package com.cannontech.core.authorization.support;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface Authorization<T> {

    /**
     * Method to determine if a user is authorized for the given permisssion
     * with the given object
     * @param user - User asking permission
     * @param permission - Permissiong in question
     * @param object - Object the user needs permission on
     * @return True if the user is authorized
     */
    public AuthorizationResponse isAuthorized(LiteYukonUser user, Permission permission, T object);

}
