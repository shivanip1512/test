package com.cannontech.core.roleproperties;


import java.util.HashSet;

import org.junit.Test;

import com.google.common.collect.Sets;

public class YukonRolePropertyCollisionTest {

    
    @Test
    public void checkForCollisions() {
        HashSet<String> enumText = Sets.newHashSet();
        
        addAll(enumText, YukonRoleProperty.class);
        addAll(enumText, YukonRole.class);
        addAll(enumText, YukonRoleCategory.class);
    }

    private <T extends Enum<T>> void addAll(HashSet<String> enumText,
            Class<T> class1) {
        T[] enumConstants = class1.getEnumConstants();
        for (T t : enumConstants) {
            boolean add = enumText.add(t.name());
            if (!add) throw new RuntimeException(t + " is a duplicate");
        }
    }

}
