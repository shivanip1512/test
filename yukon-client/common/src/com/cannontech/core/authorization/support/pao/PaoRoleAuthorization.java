package com.cannontech.core.authorization.support.pao;

import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.checker.UserChecker;

/**
 * PaoAuthorization implementation which ignores the pao and permission and just
 * checks a role property for the given user
 */
public class PaoRoleAuthorization implements PaoAuthorization {

    private UserChecker roleChecker = null;

    public void setRoleChecker(UserChecker roleChecker) {
        this.roleChecker = roleChecker;
    }

    public AuthorizationResponse isAuthorized(LiteYukonUser user, Permission permission,
            LiteYukonPAObject pao) {

        // Only check the role property - ignore permission and pao
        if (this.roleChecker.check(user)) {
            return AuthorizationResponse.AUTHORIZED;
        } else {
            return AuthorizationResponse.UNAUTHORIZED;
        }
    }
    
    @Override
    public String toString() {
        return roleChecker + " authorization";
    }

}
