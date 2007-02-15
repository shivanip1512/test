package com.cannontech.core.authorization.support.pao;

import com.cannontech.common.util.Checker;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * PaoAuthorization implementation which ignores the pao and permission and just
 * checks a role property for the given user
 */
public class PaoRoleAuthorization implements PaoAuthorization {

    private Checker<LiteYukonUser> roleChecker = null;

    public void setRoleChecker(Checker<LiteYukonUser> roleChecker) {
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

}
