package com.cannontech.core.roleproperties.dao.impl;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.google.common.base.Predicate;

public class IsCheckPropertyCompatiblePredicate implements Predicate<YukonRoleProperty> {

    @Override
    public boolean apply(YukonRoleProperty property) {
        return Boolean.class.isAssignableFrom(property.getType().getTypeClass());
    }

}
