package com.cannontech.core.authorization.support;

import java.util.Collection;
import java.util.Set;

import com.cannontech.database.data.lite.LiteYukonUser;

public class DefaultAuthorization<T> implements Authorization<T> {

    public AuthorizationResponse isAuthorized(LiteYukonUser user,
            Permission permission, T object) {
        if(permission.getDefault())
            return AuthorizationResponse.AUTHORIZED;
        else
            return AuthorizationResponse.UNAUTHORIZED;
        
    }
    
    @Override
    public void process(Collection<T> inputObjects,
                        Collection<T> unknownObjects,
                        Set<T> authorizedObjects,
                        LiteYukonUser user, 
                        Permission permission) {

        if(permission.getDefault()) {
            // authorized - add all inputs to authorized collection
            authorizedObjects.addAll(inputObjects);
        }
        
        // not authorized - these objects are now filtered OUT of our list of objects
        
    }

    @Override
    public String toString() {
        return "default permission authorization";
    }
}
