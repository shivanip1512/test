package com.cannontech.core.authorization.support;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.checker.UserChecker;

/**
 * Authorization implementation which ignores the device and permission and just
 * checks a role property for the given user
 */
public class RoleAuthorization implements DeviceAuthorization {

    private UserChecker roleCheck = null;

    public void setRoleCheck(UserChecker roleCheck) {
        this.roleCheck = roleCheck;
    }

    public AuthorizationResponse isAuthorized(LiteYukonUser user, PaoPermission permission,
            LiteYukonPAObject device) {
        if (this.roleCheck.check(user)) {
            return AuthorizationResponse.AUTHORIZED;
        } else {
            return AuthorizationResponse.UNAUTHORIZED;
        }
    }

}
