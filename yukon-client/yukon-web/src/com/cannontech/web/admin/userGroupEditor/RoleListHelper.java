package com.cannontech.web.admin.userGroupEditor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.ui.ModelMap;

import com.cannontech.common.util.Pair;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleComparator;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMultimap.Builder;

public class RoleListHelper {

    /**
     * Sorts roles by role category, then role name into an immutable multimap of category to roles.
     * Do not include the roles from the System role category.
     */
    public static ImmutableMultimap<YukonRoleCategory, YukonRole> sortRolesByCategory(Set<YukonRole> roles) {
        List<YukonRole> rolesList = Lists.newArrayList(roles);
        Collections.sort(rolesList, new YukonRoleComparator());
        Builder<YukonRoleCategory, YukonRole> builder = ImmutableMultimap.builder();
        for (YukonRole role : rolesList) {
            /* Skip roles from the system category */
            if (role.getCategory() == YukonRoleCategory.System) continue;
            
            builder.put(role.getCategory(), role);
        }
        ImmutableMultimap<YukonRoleCategory, YukonRole> categoryRoleMap = builder.build();
        
        return categoryRoleMap;
    }
    
    /**
     * Sorts roles and groups by role category, then role name into an immutable multimap of category to roles.
     * Do not include the roles from the System role category
     */
    public static ImmutableMultimap<YukonRoleCategory, Pair<YukonRole, LiteYukonGroup>> sortRolesByCategory(Map<YukonRole, LiteYukonGroup> rolesAndGroups) {
        List<YukonRole> rolesList = Lists.newArrayList(rolesAndGroups.keySet());
        Collections.sort(rolesList, new YukonRoleComparator());
        Builder<YukonRoleCategory, Pair<YukonRole, LiteYukonGroup>> builder = ImmutableMultimap.builder();
        for (YukonRole role : rolesList) {
            /* Skip roles from the system category */
            if (role.getCategory() == YukonRoleCategory.System) continue;
            
            Pair<YukonRole, LiteYukonGroup>roleGroupPair = new Pair<YukonRole, LiteYukonGroup>(role, rolesAndGroups.get(role));
            builder.put(role.getCategory(), roleGroupPair);
        }
        ImmutableMultimap<YukonRoleCategory, Pair<YukonRole, LiteYukonGroup>> categoryRoleMap = builder.build();
        
        return categoryRoleMap;
    }
    
    /**
     * Adds an ImmutableMultimap of sorted roles provided to the database and adds ImmutableMultimap
     * of sorted all role not contained in roles provided.
     * @param roles
     * @param model
     */
    public static void addRolesToModel(Set<YukonRole> roles, ModelMap model) {
        /* Add roles owned */
        ImmutableMultimap<YukonRoleCategory, YukonRole> categoryRoleMap = sortRolesByCategory(roles);
        model.addAttribute("categoryRoleMap", categoryRoleMap.asMap());
        
        /* Add available roles */
        Set<YukonRole> availableRolesSet = Sets.difference(Sets.newHashSet(YukonRole.values()), roles);
        ImmutableMultimap<YukonRoleCategory, YukonRole> availableRolesMap = sortRolesByCategory(availableRolesSet);
        model.addAttribute("availableRolesMap", availableRolesMap.asMap());
    }
    
}