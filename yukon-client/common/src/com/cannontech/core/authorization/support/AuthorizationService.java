package com.cannontech.core.authorization.support;

import java.util.List;

import com.cannontech.common.exception.NotAuthorizedException;
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

    /**
     * Method to determine if a user is authorized for ALL of the given permissions for
     * the given object
     * @param user - User to verify permission for
     * @param object - Object to verify permission for
     * @param permissions - Permissions to verify
     * @return Throws NotAuthorizedException if the user is not authorized for ALL of the permissions
     */
    public void verifyAllPermissions(LiteYukonUser user, T object, Permission... permissions)
            throws NotAuthorizedException;
    
    /**
     * Takes an Iterable<? extends T>, LiteYukonUser, a permission and returns the subset list 
     * that the user is authorized to use. 
     * @return - List of authorized objects
     */
    public <K extends T> List<K> filterAuthorized(LiteYukonUser user, Iterable<K> objectsToFilter, Permission permission);
}