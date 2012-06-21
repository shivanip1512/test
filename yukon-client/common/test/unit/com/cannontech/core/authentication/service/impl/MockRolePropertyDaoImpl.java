package com.cannontech.core.authentication.service.impl;

import java.util.Map;

import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.impl.RolePropertyDaoImpl;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

public class MockRolePropertyDaoImpl extends RolePropertyDaoImpl {
    
    private LoadingCache<LiteYukonUser, RolePropertyHolder> userRolePropertyHolders = CacheBuilder.newBuilder().build(new CacheLoader<LiteYukonUser, RolePropertyHolder>() {
        @Override
        public RolePropertyHolder load(LiteYukonUser key) throws Exception {
            throw new IllegalArgumentException(" The user does not have any roles or role properties setup.");
        }
    });
    
    public RolePropertyHolder setupRolesFor(LiteYukonUser user) {
        // The user already exists.  Lets just add to it.
        RolePropertyHolder rolePropertyHolder = userRolePropertyHolders.getIfPresent(user);
        if (rolePropertyHolder != null) {
            return rolePropertyHolder;
        }
        
        // This user doesn't have roles/role properties yet.  Add the user and create a role property holder for it.
        rolePropertyHolder = new RolePropertyHolder();
        userRolePropertyHolders.put(user, rolePropertyHolder);
        return rolePropertyHolder;
    }

    @Override
    public boolean checkRole(YukonRole role, LiteYukonUser user) {
        RolePropertyHolder rolePropertyHolder = userRolePropertyHolders.getUnchecked(user);
        Boolean roleValue = rolePropertyHolder.getRoleValue(role);
        return roleValue;
    }

    
    @Override
    public int getPropertyIntegerValue(YukonRoleProperty roleProperty, LiteYukonUser user)
    throws UserNotInRoleException {
        RolePropertyHolder rolePropertyHolder = userRolePropertyHolders.getUnchecked(user);
        Object rolePropertyValue = rolePropertyHolder.getRolePropertyValue(roleProperty);
        return (Integer) rolePropertyValue;
    }

    @Override
    public boolean getPropertyBooleanValue(YukonRoleProperty roleProperty, LiteYukonUser user)
    throws UserNotInRoleException {
        RolePropertyHolder rolePropertyHolder = userRolePropertyHolders.getUnchecked(user);
        Object rolePropertyValue = rolePropertyHolder.getRolePropertyValue(roleProperty);
        return (Boolean) rolePropertyValue;
    }
    
    public class RolePropertyHolder {
        private Map<YukonRole, Boolean> roleValueMap = Maps.newHashMap();
        private Map<YukonRoleProperty, Object> rolePropertyValueMap = Maps.newHashMap();
        
        public RolePropertyHolder withRole(YukonRole yukonRole, Boolean value) {
            roleValueMap.put(yukonRole, value);
            return this;
        }
        
        public RolePropertyHolder withRoleProperty(YukonRoleProperty yukonRoleProperty, Object value) {
            // Checking the role property value coming in matches what it should be.
            if (!yukonRoleProperty.getType().getTypeClass().isInstance(value)) {
                throw new IllegalArgumentException("The value supplied does not match the role property value type.");
            }
            
            // Add the user to the role and then add the role property and value to the map.
            roleValueMap.put(yukonRoleProperty.getRole(), true);
            rolePropertyValueMap.put(yukonRoleProperty, value);
            return this;
        }
        
        public Boolean getRoleValue(YukonRole role) {
            Boolean roleValue = roleValueMap.get(role);
            if (roleValue == null) {
                throw new IllegalArgumentException("The role you are trying to access does not exist in the mock.");
            }

            return roleValue;
        }
        
        public Object getRolePropertyValue(YukonRoleProperty roleProperty) {
            Object rolePropertyValue = rolePropertyValueMap.get(roleProperty);
            if (rolePropertyValue == null) {
                throw new IllegalArgumentException("The role property you are trying access does not exist in the mock.");
            }

            return rolePropertyValue;
        }
    }
}