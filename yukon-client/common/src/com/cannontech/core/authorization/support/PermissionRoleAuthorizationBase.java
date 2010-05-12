package com.cannontech.core.authorization.support;

import java.util.Collection;
import java.util.Set;

import com.cannontech.common.util.Checker;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.user.checker.UserChecker;

/**
 * Base implementation of Authorization. Generic for easy reuse.
 * @param <T> - Type of object to use in authorization checking
 */
public abstract class PermissionRoleAuthorizationBase<T> implements Authorization<T> {

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
            return isRoleAuthorized(this.roleChecker.check(user));
        } else {
            // Either object or permission doesn't match - don't know if
            // authorized or not
            return AuthorizationResponse.UNKNOWN;
        }
    }

    @Override
    public void process(Collection<T> inputObjects,
                        Collection<T> unknownObjects,
                        Set<T> authorizedObjects,
                        LiteYukonUser user, 
                        Permission permission) {
        
        if (this.permission.equals(permission)) {
            boolean roleValue = this.roleChecker.check(user);
            AuthorizationResponse roleAuthorization = isRoleAuthorized(roleValue);
            
            for(T object : inputObjects) {
                if(this.objectChecker.check(object)) {
                    if(AuthorizationResponse.AUTHORIZED == roleAuthorization) {
                        // authorized - add to collection
                        authorizedObjects.add(object);
                    } else if (AuthorizationResponse.UNKNOWN == roleAuthorization) {
                        // unknown - add to queue to be processed by a future authorization
                        unknownObjects.add(object);
                    } else {
                        // unauthorized objects are 'ignored' - these objects are now
                        // filtered OUT of our list of objects
                    }
                } else {
                    // Object doesn't match checker - authorization unknown
                    unknownObjects.add(object);
                }
            }
        } else {
            // Permission doesn't match - all objects are authorization unknown
            unknownObjects.addAll(inputObjects);
        }
        
    }

    /**
     * Helper method to determine authorization based on role value
     * @param roleValue - Value of role
     * @return authorization
     */
    protected abstract AuthorizationResponse isRoleAuthorized(boolean roleValue);
    
    @Override
    public String toString() {
        return permission + " and " + roleChecker + " and " + objectChecker + " authorization";
    }
    
}