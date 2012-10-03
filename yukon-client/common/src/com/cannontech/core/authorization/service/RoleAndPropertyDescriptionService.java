package com.cannontech.core.authorization.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.YukonSettingsDao;
import com.cannontech.user.checker.AggregateOrUserChecker;
import com.cannontech.user.checker.NotUserChecker;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.user.checker.RolePropertyUserCheckerFactory;
import com.cannontech.user.checker.UserChecker;
import com.google.common.collect.Lists;

public class RoleAndPropertyDescriptionService {
    @Autowired private RolePropertyUserCheckerFactory userCheckerFactory;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private YukonSettingsDao yukonSettingsDao;
    
    /**
     * This will check that the user has the given roles, categories,
     * or has a true value for the given role properties.
     * Categories, roles and properties are specified as a comma-separated
     * list of enum names. 
     * 
     * This works in an OR fashion. If the user passes any
     * of the role or property checks a boolean value of
     * true is returned.
     * 
     * Specific categories or roles can be not'd by prepending a !. Properties
     * can be checked with the False Checks by prepending a !.
     */
    public boolean checkIfAtLeaseOneExists(final String rolePropDescription, 
            final LiteYukonUser user) {
        return compile(rolePropDescription).check(user);
    }
    
    public UserChecker compile(final String rolePropDescription) {
        if (rolePropDescription.equals("*")) return NullUserChecker.getInstance();

        List<UserChecker> checkers = Lists.newArrayListWithExpectedSize(5);
        // split value
        String[] valueArray = rolePropDescription.split("[\\s,\\n]+");
        for (String someEnumName : valueArray) {
            someEnumName = someEnumName.trim();
            if (someEnumName.isEmpty()) continue;

            // check if it is inverted
            boolean inverted = false;
            if (someEnumName.startsWith("!")) {
                someEnumName = someEnumName.substring(1);
                inverted = true;
            }
            // see if it is a category
            try {
                YukonRoleCategory category = YukonRoleCategory.valueOf(someEnumName);
                UserChecker categoryChecker = userCheckerFactory.createCategoryChecker(category);
                if (inverted) {
                    categoryChecker = new NotUserChecker(categoryChecker);
                }
                checkers.add(categoryChecker);
                continue;
                
            } catch (IllegalArgumentException e) {
            }
            
            
            // see if it is a role
            try {
                YukonRole role = YukonRole.valueOf(someEnumName);
                UserChecker roleChecker = userCheckerFactory.createRoleChecker(role);
                if (inverted) {
                    roleChecker = new NotUserChecker(roleChecker);
                }
                checkers.add(roleChecker);
                continue;

            } catch (IllegalArgumentException e) {
            }

            // not a role, check if it is a property
            try {
                YukonRoleProperty property = YukonRoleProperty.valueOf(someEnumName);
                UserChecker propertyChecker;
                if (inverted) {
                    propertyChecker = userCheckerFactory.createFalsePropertyChecker(property);
                } else {
                    propertyChecker = userCheckerFactory.createPropertyChecker(property);
                }
                checkers.add(propertyChecker);
                continue;

            } catch (IllegalArgumentException ignore) { }
            
            // see if it is a supported boolean key in master.cfg
            try {
            	
            	MasterConfigBooleanKeysEnum key = MasterConfigBooleanKeysEnum.valueOf(someEnumName);
            	boolean bool = configurationSource.getBoolean(key);
            	
                UserChecker propertyChecker;
                if (inverted) {
                    propertyChecker = userCheckerFactory.createBooleanChecker(!bool);
                } else {
                    propertyChecker = userCheckerFactory.createBooleanChecker(bool);
                }
                checkers.add(propertyChecker);
                continue;

            } catch (IllegalArgumentException ignore) { }

            // see if it is a supported system setting 
            try {
                
                boolean bool = yukonSettingsDao.checkSetting(YukonSetting.valueOf(someEnumName));
                
                UserChecker propertyChecker;
                if (inverted) {
                    propertyChecker = userCheckerFactory.createBooleanChecker(!bool);
                } else {
                    propertyChecker = userCheckerFactory.createBooleanChecker(bool);
                }
                checkers.add(propertyChecker);
                continue;

            } catch (IllegalArgumentException ignore) { }
            
            // if we get here, we must not have a valid role or property
            throw new IllegalArgumentException("Can't use '" + someEnumName + "', check that it is a valid role, category, or boolean property");
        }
        // if we get here, nothing matched
        AggregateOrUserChecker userChecker = new AggregateOrUserChecker(checkers);
        return userChecker;  
    }
        

}
