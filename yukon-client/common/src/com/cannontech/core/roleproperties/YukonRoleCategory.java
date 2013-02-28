package com.cannontech.core.roleproperties;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;


public enum YukonRoleCategory implements DisplayableEnum {
    Application, CapControl, CiCustomer, Consumer, LoadControl, Notifications, Operator, System;

    public boolean isSystem() {
        return this.equals(System);
    }
    
    public static final Ordering<YukonRoleCategory> ORDERING_BY_ROLE_CATEGORY_NAME = 
        Ordering.natural().nullsFirst().onResultOf(new Function<YukonRoleCategory, String>() {
            @Override
            public String apply(YukonRoleCategory input) {
                if (input == null) return null;
                return input.name();
            }
        });

    @Override
    public String getFormatKey() {
        return "yukon.common.role.category." + name();    }
}
