package com.cannontech.web.menu;

import java.util.Collection;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ReflectivePropertySearcher;

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
        if (StringUtils.isBlank(role)) {
            return nullChecker;
        }
        final int roleId = ReflectivePropertySearcher.getRoleProperty().getIntForName(role);
        OptionPropertyChecker checker = new OptionPropertyChecker() {
            public boolean check(LiteYukonUser user) {
                if (user == null) {
                    return false;
                }
                return DaoFactory.getAuthDao().checkRole(user,roleId) != null;
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
        final int propertyId = ReflectivePropertySearcher.getRoleProperty().getIntForName(property);
        
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
        final int propertyId = ReflectivePropertySearcher.getRoleProperty().getIntForName(property);
        
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
    
    public static OptionPropertyChecker createAggregateChecker(final Collection<OptionPropertyChecker> list) {
        OptionPropertyChecker checker = new OptionPropertyChecker() {
            public boolean check(LiteYukonUser user) {
                for (OptionPropertyChecker checker2 : list) {
                    if (!checker2.check(user)) {
                        return false;
                    }
                }
                return true;
            }
        };
        return checker;
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
        
        String val = DaoFactory.getAuthDao().getRolePropertyValue(user,
                                                    propertyId );
        return BooleanUtils.toBoolean(val);
    }


}
