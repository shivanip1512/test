package com.cannontech.web.admin.userGroupEditor;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.ui.ModelMap;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.web.admin.userGroupEditor.model.RoleAndGroup;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

public class RoleListHelper {

    /**
     * Sorts roles by role category, then role name into an immutable multimap of category to roles.
     * Do not include the roles from the System role category.
     */
    public static Multimap<YukonRoleCategory, YukonRole> sortRolesByCategory(Set<YukonRole> roles) {
        List<YukonRole> rolesList = Lists.newArrayList(roles);
        Collections.sort(rolesList, YukonRole.CATEGORY_AND_NAME_COMPARATOR);
        Builder<YukonRoleCategory, YukonRole> builder = ImmutableMultimap.builder();
        for (YukonRole role : rolesList) {
            builder.put(role.getCategory(), role);
        }
        ImmutableMultimap<YukonRoleCategory, YukonRole> categoryRoleMap = builder.build();
        
        return categoryRoleMap;
    }
    
    /**
     * Sorts roles and groups by role category, then role name into an multimap of category to roles.
     * Do not include the roles from the System role category
     */
    public static Multimap<YukonRoleCategory, RoleAndGroup> 
        sortRolesByCategory(Multimap<YukonRole, LiteYukonGroup> rolesAndGroups) {
        
        List<YukonRole> roles = Lists.newArrayList(rolesAndGroups.keySet());
        Ordering<RoleAndGroup> orderingByRole = 
                Ordering.natural().nullsFirst().onResultOf(new Function<RoleAndGroup, String>() {
            @Override
            public String apply(RoleAndGroup input) {
                if (input.getRole() == null) return null;
                return input.getRole().name();
            }
        });
        
        Multimap<YukonRoleCategory, RoleAndGroup> categoryToGroups = 
                TreeMultimap.create(YukonRoleCategory.ORDERING_BY_ROLE_CATEGORY_NAME, orderingByRole);
        for (YukonRole role : roles) {
            for (LiteYukonGroup roleGroup : rolesAndGroups.get(role)) {
                RoleAndGroup roleAndGroup = RoleAndGroup.of(role, roleGroup);
                if (role == null) {
                    categoryToGroups.put(null, roleAndGroup);
                    continue;
                }
                categoryToGroups.put(role.getCategory(), roleAndGroup);
            }
        }
        
        return categoryToGroups;
    }
    
    /**
     * Adds an ImmutableMultimap of sorted roles provided to the database and adds ImmutableMultimap
     * of sorted all role not contained in roles provided.
     * @param roles
     * @param model
     */
    public static void addRolesToModel(Set<YukonRole> roles, ModelMap model) {
        /* Add roles owned */
        Multimap<YukonRoleCategory, YukonRole> categoryRoleMap = sortRolesByCategory(roles);
        model.addAttribute("categoryRoleMap", categoryRoleMap.asMap());
        
        /* Add available roles */
        Set<YukonRole> availableRolesSet = Sets.difference(Sets.newHashSet(YukonRole.values()), roles);
        
        /* Filter out RESIDENTIAL_CUSTOMER role */
        Set<YukonRole> filteredRoles = Sets.filter(availableRolesSet, 
                role -> role.getRoleId() != YukonRoleCategory.Consumer.baseRoleId);
        
        Multimap<YukonRoleCategory, YukonRole> availableRolesMap = sortRolesByCategory(filteredRoles);
        model.addAttribute("availableRolesMap", availableRolesMap.asMap());
    }
}