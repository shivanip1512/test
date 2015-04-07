package com.cannontech.capcontrol.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Representation of a regulator point mapping task result at the point mapping level. The mapping task is a failure if
 * no points are found, or multiple points are found. Success with overwrite indicates that the mapping was updated
 * successfully and replaced a previously existing mapping.
 */
public enum RegulatorPointMappingResult implements DisplayableEnum {
    
    NO_POINTS_FOUND,
    MULTIPLE_POINTS_FOUND,
    SUCCESS_WITH_OVERWRITE,
    SUCCESS,
    ;
    
    private static final String formatKey = "yukon.web.modules.capcontrol.regulatorMappingPointResult.";
    
    @Override
    public String getFormatKey() {
        return formatKey + name();
    }
    
    public boolean isSuccess() {
        return this == SUCCESS || this == SUCCESS_WITH_OVERWRITE;
    }
    
}