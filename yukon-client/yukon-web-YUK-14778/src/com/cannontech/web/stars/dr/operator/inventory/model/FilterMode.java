package com.cannontech.web.stars.dr.operator.inventory.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum FilterMode implements DisplayableEnum {
    AND,
    OR;
    
    public String getValue() {
        return name();
    }
    
    public FilterMode getDisplayName() {
        return this;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.operator.inventory.filter.filterMode." + name();
    }
}