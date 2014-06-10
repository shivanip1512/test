package com.cannontech.dr.ecobee.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum EcobeeDiscrepancyType implements DisplayableEnum {
    MISSING_MANAGEMENT_SET(EcobeeDiscrepancyCategory.GROUP, true),
    MISLOCATED_MANAGEMENT_SET(EcobeeDiscrepancyCategory.GROUP, true),
    EXTRANEOUS_MANAGEMENT_SET(EcobeeDiscrepancyCategory.GROUP, true),
    MISSING_DEVICE(EcobeeDiscrepancyCategory.DEVICE, true),
    MISLOCATED_DEVICE(EcobeeDiscrepancyCategory.DEVICE, true),
    EXTRANEOUS_DEVICE(EcobeeDiscrepancyCategory.DEVICE, false),
    ;
    
    private final EcobeeDiscrepancyCategory category;
    private final boolean isFixable;
    
    private EcobeeDiscrepancyType(EcobeeDiscrepancyCategory category, boolean isFixable) {
        this.category = category;
        this.isFixable = isFixable;
    }
    
    public EcobeeDiscrepancyCategory getCategory() {
        return category;
    }
    
    public boolean isFixable() {
        return isFixable;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.ecobee.discrepancyType." + name();
    }
    
    public String getDetailKey() {
        return getFormatKey() + ".detail";
    }
}
