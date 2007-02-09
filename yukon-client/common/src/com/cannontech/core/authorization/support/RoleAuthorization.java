package com.cannontech.core.authorization.support;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.menu.OptionPropertyChecker;

/**
 * Authorization implementation which ignores the device and permission and just
 * checks a role property for the given user
 */
public class RoleAuthorization implements DeviceAuthorization {

    private OptionPropertyChecker roleCheck = null;

    public void setRoleCheck(OptionPropertyChecker roleCheck) {
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
