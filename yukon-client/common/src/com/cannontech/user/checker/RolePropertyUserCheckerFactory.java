package com.cannontech.user.checker;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

/**
 * This is both a base class and a factory for OptionPropertyCheckers.
 * An UserChecker is an object that has a single check() method
 * that takes a LiteYukonUser as its argument. The provided factory methods
 * create UserChecker for the most common cases (to check a role
 * and to check the value of a boolean role property).
 */
public class RolePropertyUserCheckerFactory {
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private EnergyCompanyRolePropertyDao ecRolePropertyDao;
    @Autowired private YukonEnergyCompanyService yecService;
    
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
    
    public UserChecker createECPropertyChecker(final YukonRoleProperty property) {
        Validate.isTrue(ecRolePropertyDao.isCheckPropertyCompatible(property), "Property must return a Boolean: " + property);

        UserCheckerBase checker = new UserCheckerBase() {
            @Override
            public boolean check(LiteYukonUser user) {
                
                return ecRolePropertyDao.checkProperty(property, yecService.getEnergyCompanyByOperator(user));
            };
            
            @Override
            public String toString() {
                return property + " EC checker";
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
    
    public UserChecker createECFalsePropertyChecker(final YukonRoleProperty property) {
        Validate.isTrue(rolePropertyDao.isCheckPropertyCompatible(property), "Property must return a Boolean: " + property);
        
        UserCheckerBase checker = new UserCheckerBase() {
            @Override
            public boolean check(LiteYukonUser user) {
                return ecRolePropertyDao.checkFalseProperty(property, yecService.getEnergyCompanyByOperator(user));
            };
            
            @Override
            public String toString() {
                return property + " false EC checker";
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
    
    public UserChecker createBooleanChecker(final boolean isTrue) {
        
        UserCheckerBase checker = new UserCheckerBase() {
            @Override
            public boolean check(LiteYukonUser user) {
                return isTrue;
            };
            
            @Override
            public String toString() {
                return "boolean checker (" + isTrue + ")";
            }
        };
        return checker;
    }
}
