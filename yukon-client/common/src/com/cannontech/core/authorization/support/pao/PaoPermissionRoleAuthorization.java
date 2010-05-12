package com.cannontech.core.authorization.support.pao;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.PermissionRoleAuthorizationBase;

/**
 * PermissionRoleAuthorization class which can return Authorized or Unauthorized
 */
public class PaoPermissionRoleAuthorization extends
        PermissionRoleAuthorizationBase<PaoIdentifier> implements PaoAuthorization {
    
    @Override
    protected AuthorizationResponse isRoleAuthorized(boolean roleValue){
        if (roleValue) {
            return AuthorizationResponse.AUTHORIZED;
        } else {
            return AuthorizationResponse.UNAUTHORIZED;
        }
    }
}
