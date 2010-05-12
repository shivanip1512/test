package com.cannontech.core.authorization.support.pao;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.PermissionRoleAuthorizationBase;

/**
 * PaoPermissionRoleAuthorization class which will return either Authorized or Unknown - this 
 * class will never return Unauthorized
 */
public class PaoPermissionRoleAcceptor extends PermissionRoleAuthorizationBase<YukonPao> {
    
    @Override
    protected AuthorizationResponse isRoleAuthorized(boolean roleValue) {
        if (roleValue) {
            return AuthorizationResponse.AUTHORIZED;
        } else {
            return AuthorizationResponse.UNKNOWN;
        }
    }
    
}
