package com.cannontech.core.authorization.support;

import java.util.Collection;
import java.util.Queue;

import com.cannontech.common.util.Checker;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.user.checker.UserChecker;

/**
 * Base implementation of Authorization. Generic for easy reuse.
 * @param <T> - Type of object to use in authorization checking
 */
public class PermissionRoleAuthorizationBase<T> implements Authorization<T> {

    protected Checker<T> objectChecker = new Checker<T>() {
        public boolean check(T object) {
            return true;
        }
        @Override
        public String toString() {
            return "any object";
        }
        
    };
    protected UserChecker roleChecker = NullUserChecker.getInstance();
    protected Permission permission = null;

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
            } else {
                return AuthorizationResponse.UNAUTHORIZED;
            }
        } else {
            // Either object or permission doesn't match - don't know if
            // authorized or not
            return AuthorizationResponse.UNKNOWN;
        }
    }

    @Override
    public void process(Queue<T> inputQueue, Queue<T> unknownQueue,
                        Collection<T> authorizedObjects, LiteYukonUser user,
                        Permission permission) {
        
        if (this.permission.equals(permission)) {
            boolean roleValue = this.roleChecker.check(user);
            
            for(T object : inputQueue) {
                if(this.objectChecker.check(object)) {
                    if(roleValue) {
                        // authorized
                        authorizedObjects.add(object);
                    }
                    // unauthorized objects are ignored
                } else {
                    // Object doesn't match checker - authorization unknown
                    unknownQueue.add(object);
                }
            }
        } else {
            // Permission doesn't match - all objects are authorization unknown
            unknownQueue.addAll(inputQueue);
        }
        
    }
    
    @Override
    public String toString() {
        return permission + " and " + roleChecker + " and " + objectChecker + " authorization";
    }
    
}