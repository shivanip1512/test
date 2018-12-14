package com.cannontech.core.authorization.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.core.roleproperties.AccessLevel;
import com.cannontech.core.roleproperties.CapControlCommandsAccessLevel;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.OnOff;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.checker.AggregateOrUserChecker;
import com.cannontech.user.checker.NotUserChecker;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.user.checker.RolePropertyUserCheckerFactory;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.user.checker.UserCheckerBase;
import com.cannontech.web.input.type.InputType;
import com.google.common.collect.Lists;

public class RoleAndPropertyDescriptionService {
    @Autowired private RolePropertyUserCheckerFactory userCheckerFactory;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private GlobalSettingDao globalSettingDao;

    /**
     * This will check that the user has the given roles, categories, has a
     * true value for the given role properties, or a boolean GlobalSetting value,
     * or a boolean EnergyCompanySetting, a boolean MasterConfig parameter.
     * 
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
    public boolean checkIfAtLeastOneExists(String rolePropDescription, LiteYukonUser user) {
        return checkIfAtLeastOneExists(rolePropDescription, null, user);
    }

    public boolean checkIfAtLeastOneExists(String rolePropDescription, String level, LiteYukonUser user) {
        return compile(rolePropDescription, level).check(user);
    }

    public UserChecker compile(String rolePropDescription) {
        return compile(rolePropDescription, null);
    }
    
    public UserChecker compile(String rolePropDescription, String level) {
        if (rolePropDescription.equals("*")) {
            return NullUserChecker.getInstance();
        }

        List<UserChecker> checkers = Lists.newArrayListWithExpectedSize(5);
        // Split value.
        String[] valueArray = rolePropDescription.split("[\\s,\\n]+");
        for (String someEnumName : valueArray) {
            someEnumName = someEnumName.trim();
            if (someEnumName.isEmpty()) {
                continue;
            }

            // Check if it is inverted.
            boolean inverted = false;
            if (someEnumName.startsWith("!")) {
                someEnumName = someEnumName.substring(1);
                inverted = true;
            }

            try {
                YukonRoleCategory category = YukonRoleCategory.valueOf(someEnumName);
                UserChecker categoryChecker = userCheckerFactory.createCategoryChecker(category);
                if (inverted) {
                    categoryChecker = new NotUserChecker(categoryChecker);
                }
                checkers.add(categoryChecker);
                continue;
            } catch (IllegalArgumentException e) {
                // It's not a category.
            }

            try {
                YukonRole role = YukonRole.valueOf(someEnumName);
                UserChecker roleChecker = userCheckerFactory.createRoleChecker(role);
                if (inverted) {
                    roleChecker = new NotUserChecker(roleChecker);
                }
                checkers.add(roleChecker);
                continue;
            } catch (IllegalArgumentException e) {
                // It's not a role.
            }

            try {
                YukonRoleProperty property = YukonRoleProperty.valueOf(someEnumName);
                UserChecker propertyChecker;
                if (property.getType().getTypeClass().isEnum() && property.getType().getTypeClass() == AccessLevel.class) {
                    propertyChecker = userCheckerFactory.createAccessLevelChecker(property, level);
                } else if (property.getType().getTypeClass().isEnum() && property.getType().getTypeClass() == CapControlCommandsAccessLevel.class) {
                    propertyChecker = userCheckerFactory.createCapControlCommandsAccessChecker(property, level);
                } else if(!StringUtils.isEmpty(level)) {
                    propertyChecker = userCheckerFactory.createHeirarchicalLevelChecker(property, level);
                } else if (inverted) {
                    propertyChecker = userCheckerFactory.createFalsePropertyChecker(property);
                } else {
                    propertyChecker = userCheckerFactory.createBooleanPropertyChecker(property);
                }
                checkers.add(propertyChecker);
                continue;
            } catch (IllegalArgumentException ignore) {
                // It's not a role property.
            }

            try {
                GlobalSettingType type = GlobalSettingType.valueOf(someEnumName);
                InputType<?> dataType = type.getType();
                Boolean globalSettingValue = null;
                if (dataType.getTypeClass() == Boolean.class) {
                    globalSettingValue = globalSettingDao.getBoolean(type);
                } else if (dataType.getTypeClass() == OnOff.class) {
                    globalSettingValue = globalSettingDao.getEnum(type, OnOff.class).isOn();
                }
                if (globalSettingValue != null) {
                    UserChecker propertyChecker = globalSettingValue ? UserCheckerBase.TRUE : UserCheckerBase.FALSE;
                    if (inverted) {
                        propertyChecker = globalSettingValue ? UserCheckerBase.FALSE : UserCheckerBase.TRUE;
                    }
                    checkers.add(propertyChecker);
                    continue;
                }
            } catch (IllegalArgumentException ignore) {
                // It's not a system setting.
            }

            try {
                EnergyCompanySettingType ecSettingType = EnergyCompanySettingType.valueOf(someEnumName);
                UserChecker settingChecker;
                if (inverted) {
                    settingChecker = userCheckerFactory.createECFalseSettingChecker(ecSettingType);
                } else {
                    settingChecker = userCheckerFactory.createECSettingChecker(ecSettingType);
                }
                checkers.add(settingChecker);
                continue;
            } catch (IllegalArgumentException ignore) {
                // It's not an energy company setting.
            }

            try {
                MasterConfigBoolean key = MasterConfigBoolean.valueOf(someEnumName);
                boolean configValue = configurationSource.getBoolean(key);

                UserChecker propertyChecker = configValue ? UserCheckerBase.TRUE : UserCheckerBase.FALSE;
                if (inverted) {
                    propertyChecker = configValue ? UserCheckerBase.FALSE : UserCheckerBase.TRUE;
                }
                checkers.add(propertyChecker);
                continue;
            } catch (IllegalArgumentException ignore) {
                // It's not a master.cfg boolean.
            }

           // If we get here, we must not have a valid setting of any time.
            throw new IllegalArgumentException("Can't use '" + someEnumName
                + "', check that it is a valid role, category, boolean property, or boolean global setting.");
        }

        // If we get here, nothing matched.
        AggregateOrUserChecker userChecker = new AggregateOrUserChecker(checkers);
        return userChecker;
    }
}
