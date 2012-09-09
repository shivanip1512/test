package com.cannontech.web.admin.userGroupEditor;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.ui.ModelMap;

import com.cannontech.common.util.Pair;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.database.data.lite.LiteYukonGroup;
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
    public static Multimap<YukonRoleCategory, Pair<YukonRole, LiteYukonGroup>> sortRolesByCategory(Multimap<YukonRole, LiteYukonGroup> rolesAndGroups) {
        List<YukonRole> rolesList = Lists.newArrayList(rolesAndGroups.keySet());
        Ordering<Pair<YukonRole, LiteYukonGroup>> orderingByYukonRole = Ordering.natural().nullsFirst().onResultOf(new Function<Pair<YukonRole, LiteYukonGroup>, String>() {
            @Override
            public String apply(Pair<YukonRole, LiteYukonGroup> input) {
                if (input.getFirst() == null) return null;
                return input.getFirst().name();
            }
        });
        
        Multimap<YukonRoleCategory, Pair<YukonRole, LiteYukonGroup>> roleCategoryToRoleRoleGroupPairMap = 
                TreeMultimap.create(YukonRoleCategory.ORDERING_BY_ROLE_CATEGORY_NAME, orderingByYukonRole);
        for (YukonRole role : rolesList) {
            for (LiteYukonGroup roleGroup : rolesAndGroups.get(role)) {
                Pair<YukonRole, LiteYukonGroup>roleGroupPair = new Pair<YukonRole, LiteYukonGroup>(role, roleGroup);

                if (role == null) {
                    roleCategoryToRoleRoleGroupPairMap.put(null, roleGroupPair);
                    continue;
                }

                roleCategoryToRoleRoleGroupPairMap.put(role.getCategory(), roleGroupPair);
            }
        }
        
        return roleCategoryToRoleRoleGroupPairMap;
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
        
        /* Filter out System roles */
        Set<YukonRole> filteredRoles = Sets.filter(availableRolesSet, new Predicate<YukonRole>() {
            @Override
            public boolean apply(YukonRole role) {
                if (role.getCategory().isSystem()) {
                    return false;
                } else {
                    return true;
                }
            }
        });
        
        Multimap<YukonRoleCategory, YukonRole> availableRolesMap = sortRolesByCategory(filteredRoles);
        model.addAttribute("availableRolesMap", availableRolesMap.asMap());
    }
}