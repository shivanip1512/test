package com.cannontech.core.authorization.support;

import java.util.Collection;
import java.util.Queue;

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
    public void process(Queue<T> inputQueue, Queue<T> unknownQueue,
                        Collection<T> authorizedObjects, LiteYukonUser user,
                        Permission permission) {

        if(permission.getDefault()) {
            // authorized - add all inputs to authorized collection
            authorizedObjects.addAll(inputQueue);
        }
        
        // not authorized - do nothing
        
    }

    @Override
    public String toString() {
        return "default permission authorization";
    }
}
