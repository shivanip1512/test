package com.cannontech.core.authorization.support.pao;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.PermissionRoleAuthorizationBase;

/**
 * PaoPermissionRoleAuthorization class which will return either Unauthorized or Unknown - this 
 * class will never return Authorized
 */
public class PaoPermissionRoleRejector extends PermissionRoleAuthorizationBase<YukonPao> {
    
    @Override
    protected AuthorizationResponse isRoleAuthorized(boolean roleValue) {
        if (roleValue) {
            return AuthorizationResponse.UNKNOWN;
        } else {
            return AuthorizationResponse.UNAUTHORIZED;
        }
    }
    
}
