package com.cannontech.core.authorization.service.impl;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.authorization.support.Authorization;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.AuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Base implementation of AuthorizationService. Generic for easy reuse.
 * @param <T> - Type of object to use in authorization checking
 */
public class AuthorizationServiceBase<T> implements AuthorizationService<T> {

    private List<Authorization<T>> authorizationList = null;

    public void setAuthorizationList(List<Authorization<T>> authorizationList) {
        this.authorizationList = authorizationList;
    }

    public boolean isAuthorized(LiteYukonUser user, Permission permission, T object) {

        // Iterate through each of the Authorizations in the list. If the
        // Authorization doesn't have an authorization rule for the given user,
        // permission, and object, the Authorization is skipped. The first
        // Authorization in the list to return an 'Authorized' or 'UnAuthorized'
        // is used to determine authorization. More specific Authorizations
        // should be at the front of the authorizationList.
        for (Authorization<T> authorization : authorizationList) {

            AuthorizationResponse response = authorization.isAuthorized(user, permission, object);
            if (AuthorizationResponse.UNKNOWN.equals(response)) {
                continue;
            } else if (AuthorizationResponse.AUTHORIZED.equals(response)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    public void verifyAllPermissions(LiteYukonUser user, T object, Permission... permissions) 
            throws NotAuthorizedException {
        
        for(Permission permission : permissions) {
            if (!this.isAuthorized(user,
                                   permission,
                                   object)) {
                throw new NotAuthorizedException("The user is not autorized for object " + 
                                                 object.toString());
            }
        }
    }

    @Override
    public List<T> filterAuthorized(LiteYukonUser user, 
                                    Iterable<? extends T> objectsToFilter, 
                                    Permission permission) {
        
        List<T> authorizedObjects = Lists.newArrayList();
        Queue<T> inputQueue = new ArrayDeque<T>();
        Iterables.addAll(inputQueue, objectsToFilter);
        
        for (Authorization<T> authorization : authorizationList) {
            Queue<T> unknownQueue = new ArrayDeque<T>();
            authorization.process(inputQueue, unknownQueue, authorizedObjects, user, permission);
            inputQueue = unknownQueue;
        }
        
        return authorizedObjects;
    }

}