package com.cannontech.core.authorization.support;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.menu.OptionPropertyChecker;

/**
 * Class which determines if a permission is allowed.
 */
public class DevicePermissionRoleAuthorization implements DeviceAuthorization {

    private DeviceCheck deviceCheck = null;
    private OptionPropertyChecker roleCheck = null;
    private PaoPermission permission = null;

    public void setPermission(PaoPermission permission) {
        this.permission = permission;
    }

    public void setDeviceCheck(DeviceCheck deviceCheck) {
        this.deviceCheck = deviceCheck;
    }

    public void setRoleCheck(OptionPropertyChecker roleCheck) {
        this.roleCheck = roleCheck;
    }

    public AuthorizationResponse isAuthorized(LiteYukonUser user, PaoPermission permission,
            LiteYukonPAObject device) {

        if (this.deviceCheck.checkDevice(device) && this.permission.equals(permission)) {
            // Device and permission match - return value of role
            if (this.roleCheck.check(user)) {
                return AuthorizationResponse.AUTHORIZED;
            } else {
                return AuthorizationResponse.UNAUTHORIZED;
            }
        } else {
            // Either device or permission doesn't match - don't know if
            // authorized or not
            return AuthorizationResponse.UNKNOWN;
        }
    }

}
