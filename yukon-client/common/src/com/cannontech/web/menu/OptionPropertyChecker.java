package com.cannontech.web.menu;

import java.lang.reflect.Field;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.Validate;

import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * This is both a base class and a factory for OptionPropertyCheckers.
 * An OptionPropertyChecker is an object that has a single check() method
 * that takes a LiteYukonUser as its argument. The provided factory methods
 * create OptionPropertyChecker for the most common cases (to check a role
 * and to check the value of a boolean role property).
 */
public abstract class OptionPropertyChecker {
    private static OptionPropertyChecker nullChecker;
    static {
        nullChecker = new OptionPropertyChecker() {
            public boolean check(LiteYukonUser user) {
                return true;
            };
        };
    }
    
    /**
     * Create a OptionPropertyChecker that will return true if the
     * user has the indicated role.
     * @param role to base the OptionPropertyChecker on
     * @return an OptionPropertyChecker 
     */
    public static OptionPropertyChecker createRoleChecker(String role) {
        final int roleId = getIntForFQN(role);
        OptionPropertyChecker checker = new OptionPropertyChecker() {
            public boolean check(LiteYukonUser user) {
                if (user == null) {
                    return false;
                }
                return AuthFuncs.checkRole(user,roleId) != null;
            };
        };
        return checker;
    }
    
    /**
     * Create an OptionPropertyChecker that will return true if the
     * user has the indicated role property and it is set to true.
     * @param property to base the OptionPropertyChecker on
     * @return an OptionPropertyChecker
     */
    public static OptionPropertyChecker createPropertyChecker(String property) {
        final int propertyId = getIntForFQN(property);
        
        OptionPropertyChecker checker = new OptionPropertyChecker() {
            public boolean check(LiteYukonUser user) {
                return getBooleanForRoleProperty(propertyId, user);
            };
        };
        return checker;
    }
    
    /**
     * Create an OptionPropertyChecker that will return false if the
     * user has the indicated role property and it is set to true.
     * @param property to base the OptionPropertyChecker on
     * @return an OptionPropertyChecker
     */
    public static OptionPropertyChecker createFalsePropertyChecker(String property) {
        final int propertyId = getIntForFQN(property);
        
        OptionPropertyChecker checker = new OptionPropertyChecker() {
            public boolean check(LiteYukonUser user) {
                return !getBooleanForRoleProperty(propertyId, user);
            };
        };
        return checker;
    }
    
    /**
     * Creates an OptionPropertyChecker that will always return true.
     * @return an OptionPropertyChecker
     */
    public static OptionPropertyChecker createNullChecker() {
        return nullChecker;
    }
    
    /**
     * @param user The current user owning the session
     * @return true if they should be able to see the option for which this
     *         was created.
     */
    public abstract boolean check(LiteYukonUser user);
    
    private static boolean getBooleanForRoleProperty(final int propertyId, LiteYukonUser user) {
        if (user == null) {
            return false;
        }
        LiteYukonRoleProperty liteProp =
            AuthFuncs.getRoleProperty(propertyId);
        if (liteProp == null) {
            return false;
        }
        String val = AuthFuncs.getRolePropertyValue(user,
                                                    liteProp.getRolePropertyID() );
        return BooleanUtils.toBoolean(val);
    }

    /**
     * Uses reflection to look up the value of an fully qualified constant. For instance,
     *   getIntForFQN(com.cannontech.whatever.SomeClass.SOMEFIELD)
     * might return
     *   10000.
     * The field should be declared to be "final static" and must have been initialized
     * before this method is called (so, it should be initialized when the class is loaded).
     * @param fqn a package, class name, and integer field name all separated by periods
     * @return the integer value
     * @throws IllegalArgumentException if the fqn isn't valid (see nested cause for
     *   more detail, usually a reflection problem)
     */
    private static int getIntForFQN(String fqn) {
        Validate.notEmpty(fqn, "No value was supplied for the property checker.");
        int lastDot = fqn.lastIndexOf(".");
        String className = fqn.substring(0, lastDot);
        String intName = fqn.substring(lastDot + 1);
        try {
            Class theClass = Class.forName(className);
            Field intField = theClass.getField(intName);
            int result = intField.getInt(null);
            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to find integer value of " 
                                               + intName + " in class " + className
                                               + ": " + e.getMessage());
        }
    }

}
