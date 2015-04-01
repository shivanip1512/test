package com.cannontech.capcontrol.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Representation of a regulator point mapping task result at the device level. Partially successful devices have all
 * point mappings completely processed, and have at least one successful and at least one failed point mapping.
 */
public enum RegulatorMappingResultType implements DisplayableEnum {
    SUCCESSFUL,
    PARTIALLY_SUCCESSFUL,
    FAILED,
    INCOMPLETE,
    ;
    
    private static final String formatKey = "yukon.web.modules.capcontrol.regulatorMappingResultType.";
    
    @Override
    public String getFormatKey() {
        return formatKey + name();
    }
}
