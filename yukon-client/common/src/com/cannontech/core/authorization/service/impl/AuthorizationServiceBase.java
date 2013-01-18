package com.cannontech.core.authorization.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.authorization.support.Authorization;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.AuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Base implementation of AuthorizationService. Generic for easy reuse.
 * @param <T> - Type of object to use in authorization checking
 * @param <Y> - Some class with an equals/hashCode defined
 */
public abstract class AuthorizationServiceBase<T,Y> implements AuthorizationService<T> {

    private List<Authorization<Y>> authorizationList = null;

    public void setAuthorizationList(List<Authorization<Y>> authorizationList) {
        this.authorizationList = authorizationList;
    }
    
    protected abstract Y convert(T input);

    public boolean isAuthorized(LiteYukonUser user, Permission permission, T object) {

        // Iterate through each of the Authorizations in the list. If the
        // Authorization doesn't have an authorization rule for the given user,
        // permission, and object, the Authorization is skipped. The first
        // Authorization in the list to return an 'Authorized' or 'UnAuthorized'
        // is used to determine authorization. More specific Authorizations
        // should be at the front of the authorizationList.
        for (Authorization<Y> authorization : authorizationList) {

            AuthorizationResponse response = authorization.isAuthorized(user, permission, convert(object));
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
    
    public void verifyAllPermissions(LiteYukonUser user, T object, Permission... permissions) throws NotAuthorizedException {
        
        for(Permission permission : permissions) {
            if (!this.isAuthorized(user, permission, object)) {
                throw new NotAuthorizedException("The user is not autorized for object " + object.toString());
            }
        }
    }

    @Override
    public <K extends T> List<K> filterAuthorized(LiteYukonUser user,  Iterable<K> objectsToFilter,  Permission permission) {
        Set<Y> authorizedObjects = Sets.newHashSet();
        List<Y> inputList = Lists.newArrayList();
        
        Iterable<Y> transform = Iterables.transform(objectsToFilter, new Function<K,Y>() {
            public Y apply(K from) {
                return convert(from);
            };
        });
        Iterables.addAll(inputList, transform);
        
        for (Authorization<Y> authorization : authorizationList) {
            List<Y> unknownList = new ArrayList<Y>();
            authorization.process(inputList, unknownList, authorizedObjects, user, permission);
            inputList = unknownList;
        }
        
        // Maintain original order of objects
        List<K> result = Lists.newArrayList();
        for (K k : objectsToFilter) {
            if (authorizedObjects.contains(convert(k))) {
                result.add(k);
            }
        }
        return result;
    }

}