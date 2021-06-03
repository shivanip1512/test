package com.cannontech.dr.ecobee.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum EcobeeZeusDiscrepancyType implements DisplayableEnum {
    MISSING_GROUP(EcobeeZeusDiscrepancyCategory.GROUP, true),
    // TODO: We should not have a mislocated group as all group have parents as root group and do not have hierarchy
    MISLOCATED_GROUP(EcobeeZeusDiscrepancyCategory.GROUP, true),
    
    EXTRANEOUS_GROUP(EcobeeZeusDiscrepancyCategory.GROUP, true),
    MISSING_DEVICE(EcobeeZeusDiscrepancyCategory.DEVICE, true),
    MISLOCATED_DEVICE(EcobeeZeusDiscrepancyCategory.DEVICE, true),
    EXTRANEOUS_DEVICE(EcobeeZeusDiscrepancyCategory.DEVICE, false),
    ;
    
    private final EcobeeZeusDiscrepancyCategory category;
    private final boolean isFixable;
    
    private EcobeeZeusDiscrepancyType(EcobeeZeusDiscrepancyCategory category, boolean isFixable) {
        this.category = category;
        this.isFixable = isFixable;
    }
    
    public EcobeeZeusDiscrepancyCategory getCategory() {
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
