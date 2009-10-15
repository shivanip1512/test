package com.cannontech.core.authorization.support.pao;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * PaoPermissionRoleAuthorization class which will return either Authorized or Unknown - this class
 * will never return Unauthorized
 */
public class PaoPermissionRoleAcceptor extends PaoPermissionRoleAuthorization {
    
    @Override
    public AuthorizationResponse isAuthorized(LiteYukonUser user, Permission permission, YukonPao object) {

        if (this.objectChecker.check(object) && this.permission.equals(permission)) {
            // Object and permission match - return Authorized if property is true, 
            // Unknown otherwise
            if (this.roleChecker.check(user)) {
                return AuthorizationResponse.AUTHORIZED;
            } else {
                return AuthorizationResponse.UNKNOWN;
            }
        } else {
            // Either object or permission doesn't match - don't know if
            // authorized or not
            return AuthorizationResponse.UNKNOWN;
        }
    }
    
}
