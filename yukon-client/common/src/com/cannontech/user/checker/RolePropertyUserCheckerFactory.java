package com.cannontech.user.checker;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.AccessLevel;
import com.cannontech.core.roleproperties.CapControlCommandsAccessLevel;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;

/**
 * This is both a base class and a factory for OptionPropertyCheckers.
 * An UserChecker is an object that has a single check() method
 * that takes a LiteYukonUser as its argument. The provided factory methods
 * create UserChecker for the most common cases (to check a role
 * and to check the value of a boolean role property).
 */
public class RolePropertyUserCheckerFactory {
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private EnergyCompanySettingDao energyCompanSettingDao;
    @Autowired private EnergyCompanyDao ecDao;
    
    public UserChecker createHeirarchicalLevelChecker(YukonRoleProperty property, String level) {
        HierarchyPermissionLevel permissionLevel = HierarchyPermissionLevel.valueOf(level);
        UserCheckerBase checker = new UserCheckerBase() {
            @Override
            public boolean check(LiteYukonUser user) {
                return rolePropertyDao.checkLevel(property, permissionLevel, user);
            };
            
            @Override
            public String toString() {
                return YukonRoleProperty.ENDPOINT_PERMISSION + " checker";
            }
        };
        return checker;
    }
    
    public UserChecker createAccessLevelChecker(YukonRoleProperty property, String level) {
        AccessLevel accessLevel = AccessLevel.valueOf(level);
        UserCheckerBase checker = new UserCheckerBase() {
            @Override
            public boolean check(LiteYukonUser user) {
                return rolePropertyDao.checkLevel(property, accessLevel, user);
            };
            
            @Override
            public String toString() {
                return YukonRoleProperty.ENDPOINT_PERMISSION + " checker";
            }
        };
        return checker;
    }
    
    public UserChecker createCapControlCommandsAccessChecker(YukonRoleProperty property, String level) {
        UserCheckerBase checker = new UserCheckerBase() {
            @Override
            public boolean check(LiteYukonUser user) {
                boolean result = false;
                if (!level.isEmpty()) {
                    String[] possibleLevels = level.split(",");
                    for (String possibleLevel : possibleLevels) {
                        CapControlCommandsAccessLevel accessLevel = CapControlCommandsAccessLevel.valueOf(possibleLevel.trim());
                        result = rolePropertyDao.checkLevel(property, accessLevel, user);
                        if (result) {
                            return result;
                        }
                    }
                }
                return result;
            };
            
            @Override
            public String toString() {
                return YukonRoleProperty.ENDPOINT_PERMISSION + " checker";
            }
        };
        return checker;
    }
    
    public UserChecker createBooleanPropertyChecker(final YukonRoleProperty property) {
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
    
    public UserChecker createECSettingChecker(final EnergyCompanySettingType setting) {

        UserCheckerBase checker = new UserCheckerBase() {
            @Override
            public boolean check(LiteYukonUser user) {
                return energyCompanSettingDao.getBoolean(setting, ecDao.getEnergyCompanyByOperator(user).getEnergyCompanyId());
            };
            
            @Override
            public String toString() {
                return setting + " EC checker";
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

    public UserChecker createECFalseSettingChecker(final EnergyCompanySettingType setting) {

        UserCheckerBase checker = new UserCheckerBase() {
            @Override
            public boolean check(LiteYukonUser user) {
                return energyCompanSettingDao.getFalseBoolean(setting, ecDao.getEnergyCompanyByOperator(user).getEnergyCompanyId());
            };
            
            @Override
            public String toString() {
                return setting + " false EC checker";
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
