package com.cannontech.core.authorization.support.string;

import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.PermissionRoleAuthorizationBase;

/**
 * Place holder class which extends the generic PermissionRoleAuthorizationBase
 * class with a type. This class makes it easier to instantiate a
 * StringAuthorization for String using spring.
 */
public class StringPermissionRoleAuthorization extends PermissionRoleAuthorizationBase<String>
        implements StringAuthorization {

    @Override
    protected AuthorizationResponse isRoleAuthorized(boolean roleValue){
        if (roleValue) {
            return AuthorizationResponse.AUTHORIZED;
        } else {
            return AuthorizationResponse.UNAUTHORIZED;
        }
    }
}
