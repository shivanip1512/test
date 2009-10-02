package com.cannontech.core.authorization.support;

import com.cannontech.common.util.Checker;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.user.checker.UserChecker;

/**
 * Base implementation of Authorization. Generic for easy reuse.
 * @param <T> - Type of object to use in authorization checking
 */
public class PermissionRoleAuthorizationBase<T> implements Authorization<T> {

    private boolean fallthroughIfNotAuthorized = false;
    
    Checker<T> objectChecker = new Checker<T>() {
        public boolean check(T object) {
            return true;
        }
        @Override
        public String toString() {
            return "any object";
        }
        
    };
    UserChecker roleChecker = NullUserChecker.getInstance();
    Permission permission = null;

    public PermissionRoleAuthorizationBase() {
        super();
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public void setObjectChecker(Checker<T> objectChecker) {
        this.objectChecker = objectChecker;
    }

    public void setRoleChecker(UserChecker roleChecker) {
        this.roleChecker = roleChecker;
    }

    public AuthorizationResponse isAuthorized(LiteYukonUser user, Permission permission, T object) {

        if (this.objectChecker.check(object) && this.permission.equals(permission)) {
            // Object and permission match - return value of role
            if (this.roleChecker.check(user)) {
                return AuthorizationResponse.AUTHORIZED;
            } else if(!this.fallthroughIfNotAuthorized) {
                return AuthorizationResponse.UNAUTHORIZED;
            } else {
                return AuthorizationResponse.UNKNOWN;
            }
        } else {
            // Either object or permission doesn't match - don't know if
            // authorized or not
            return AuthorizationResponse.UNKNOWN;
        }
    }
    
    @Override
    public String toString() {
        return permission + " and " + roleChecker + " and " + objectChecker + " authorization";
    }
    
    public void setFallthroughIfNotAuthorized(boolean fallthroughIfNotAuthorized) {
        this.fallthroughIfNotAuthorized = fallthroughIfNotAuthorized;
    }

}