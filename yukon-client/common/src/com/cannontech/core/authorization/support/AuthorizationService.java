package com.cannontech.core.authorization.support;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface AuthorizationService<T> {

    /**
     * Method to determine if a user is authorized for the given permission with
     * the given object
     * @param permission - Permission in question
     * @param user - User asking permission
     * @param object - Object the user needs permission on
     * @return True if user is authorized
     */
    public boolean isAuthorized(LiteYukonUser user, Permission permission, T object);

}