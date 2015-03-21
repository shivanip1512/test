package com.cannontech.core.roleproperties;


import java.util.HashSet;

import org.junit.Test;

import com.cannontech.common.config.MasterConfigBoolean;
import com.google.common.collect.Sets;

public class YukonRolePropertyCollisionTest {

    
    @Test
    public void checkNameForCollisions() {
        HashSet<String> enumText = Sets.newHashSet();
        
        addAll(enumText, YukonRoleProperty.class);
        addAll(enumText, YukonRole.class);
        addAll(enumText, YukonRoleCategory.class);
        addAll(enumText, MasterConfigBoolean.class);
    }

    private <T extends Enum<T>> void addAll(HashSet<String> enumText,
            Class<T> class1) {
        T[] enumConstants = class1.getEnumConstants();
        for (T t : enumConstants) {
            boolean add = enumText.add(t.name());
            if (!add) throw new RuntimeException(t + " is a duplicate");
        }
    }
    
    @Test
    public void checkPropertyIdForCollisions() {
        // the current implementation of YukonRoleProperty prevents this, but just in case
        HashSet<Integer> propertyIds = Sets.newHashSet();
        
        YukonRoleProperty[] values = YukonRoleProperty.values();
        for (YukonRoleProperty yukonRoleProperty : values) {
            boolean add = propertyIds.add(yukonRoleProperty.getPropertyId());
            if (!add) throw new RuntimeException(yukonRoleProperty + " has a duplicate id");
        }
        
    }
    
    @Test
    public void checkRoleIdForCollisions() {
        // the current implementation of YukonRole prevents this, but just in case
        HashSet<Integer> roleIds = Sets.newHashSet();
        
        YukonRole[] values = YukonRole.values();
        for (YukonRole yukonRole : values) {
            boolean add = roleIds.add(yukonRole.getRoleId());
            if (!add) throw new RuntimeException(yukonRole + " has a duplicate id");
        }
        
    }
    
}
