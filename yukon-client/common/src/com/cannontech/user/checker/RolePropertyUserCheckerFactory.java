package com.cannontech.user.checker;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * This is both a base class and a factory for OptionPropertyCheckers.
 * An UserChecker is an object that has a single check() method
 * that takes a LiteYukonUser as its argument. The provided factory methods
 * create UserChecker for the most common cases (to check a role
 * and to check the value of a boolean role property).
 */
public class RolePropertyUserCheckerFactory {
    private RolePropertyDao rolePropertyDao;
    
    public UserChecker createPropertyChecker(final YukonRoleProperty property) {
        Validate.isTrue(rolePropertyDao.isCheckPropertyCompatible(property), "Property must return a Boolean: " + property);

        UserCheckerBase checker = new UserCheckerBase() {
            @Override
            public boolean check(LiteYukonUser user) {
                return rolePropertyDao.checkProperty(property, user);
            };
            
            @Override
            public String toString() {
                return property + " checker";
            }
        };
        return checker;
    }
    
    public UserChecker createFalsePropertyChecker(final YukonRoleProperty property) {
        Validate.isTrue(rolePropertyDao.isCheckPropertyCompatible(property), "Property must return a Boolean: " + property);
        
        UserCheckerBase checker = new UserCheckerBase() {
            @Override
            public boolean check(LiteYukonUser user) {
                return rolePropertyDao.checkFalseProperty(property, user);
            };
            
            @Override
            public String toString() {
                return property + " false checker";
            }
        };
        return checker;
    }
    
    public UserChecker createRoleChecker(final YukonRole role) {
        
        UserCheckerBase checker = new UserCheckerBase() {
            @Override
            public boolean check(LiteYukonUser user) {
                return rolePropertyDao.checkRole(role, user);
            };
            
            @Override
            public String toString() {
                return role + " checker";
            }
        };
        return checker;
    }
    
    public UserChecker createCategoryChecker(final YukonRoleCategory category) {
        
        UserCheckerBase checker = new UserCheckerBase() {
            @Override
            public boolean check(LiteYukonUser user) {
                return rolePropertyDao.checkCategory(category, user);
            };
            
            @Override
            public String toString() {
                return category + " checker";
            }
        };
        return checker;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

}
