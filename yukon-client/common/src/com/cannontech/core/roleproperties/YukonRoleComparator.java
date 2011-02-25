package com.cannontech.core.roleproperties;

import java.util.Comparator;

public class YukonRoleComparator implements Comparator<YukonRole> {

    @Override
    public int compare(YukonRole role1, YukonRole role2) {
        int compare =  role1.getCategory().compareTo(role2.getCategory());
        if (compare == 0) {
            return role1.name().compareToIgnoreCase(role2.name());
        }
        
        return compare;
    }
}