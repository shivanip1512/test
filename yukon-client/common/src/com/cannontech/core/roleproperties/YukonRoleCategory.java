package com.cannontech.core.roleproperties;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;


public enum YukonRoleCategory implements DisplayableEnum {
    Application(-100,-10000),
    CapControl(-700,-70000), 
    Cbc_Oneline(-1000,-100000),
    CiCustomer(), //TODO appears unused? No roles in the category.
    Consumer(-400,-40000), 
    LoadControl(-900,-90000), 
    Notifications(-800,-80000), 
    Operator(-200,-20000), 
    ;

    public final int baseRoleId;
    public final int basePropertyId;
    
    private YukonRoleCategory(int baseRoleId, int basePropertyId) {
        this.baseRoleId = baseRoleId;
        this.basePropertyId = basePropertyId;
    }
    
    private YukonRoleCategory() {
        this.baseRoleId = 0;
        this.basePropertyId = 0;
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

    public boolean isSystem() {
        return false;
    }
}
