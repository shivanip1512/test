package com.cannontech.stars.dr.hardware.model;

import com.cannontech.common.constants.YukonListEntryTypes;

/**
 * Enum which represents load management hardware status
 */
public enum HardwareStatus {
    AVAILABLE(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL), 
    TEMP_UNAVAILABLE(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL), 
    UNAVAILABLE(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL), 
    ORDERED(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_ORDERED), 
    SHIPPED(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_SHIPPED), 
    RECEIVED(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_RECEIVED), 
    ISSUED(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_ISSUED), 
    INSTALLED(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_INSTALLED), 
    REMOVED(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_REMOVED);

    private int definitionId;

    private HardwareStatus(int definitionId) {
        this.definitionId = definitionId;
    }

    public int getDefinitionId() {
        return definitionId;
    }

    /**
     * Overloaded method to get the enum value for a definitionId
     * @param definitionId - Definition id to get enum for
     * @return Enum value
     */
    public static HardwareStatus valueOf(int definitionId) {

        HardwareStatus[] values = HardwareStatus.values();
        for (HardwareStatus type : values) {
            int typeDefinitionId = type.getDefinitionId();
            if (definitionId == typeDefinitionId) {
                return type;
            }
        }

        throw new IllegalArgumentException("No HardwareStatus found for definitionId: " + definitionId);

    }
}
