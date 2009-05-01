package com.cannontech.core.authorization.support;

import com.cannontech.database.data.lite.LiteYukonUser;

public class DefaultAuthorization<T> implements Authorization<T> {

    public AuthorizationResponse isAuthorized(LiteYukonUser user,
            Permission permission, T object) {
        if( permission.getDefault() )
            return AuthorizationResponse.AUTHORIZED;
        else
            return AuthorizationResponse.UNAUTHORIZED;
        
    }

    @Override
    public String toString() {
        return "default permission authorization";
    }
}
