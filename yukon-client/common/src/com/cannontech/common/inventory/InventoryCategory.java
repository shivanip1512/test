package com.cannontech.common.inventory;

import com.cannontech.common.constants.YukonListEntryTypes;

/**
 * Enum which represents load management inventory categories
 */
public enum InventoryCategory {
    ONE_WAY_RECEIVER(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC), 
    TWO_WAY_RECEIVER(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC), 
    YUKON_METER(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_YUKON_METER), 
    NON_YUKON_METER(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_NON_YUKON_METER);

    private int definitionId;

    private InventoryCategory(int definitionId) {
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
    public static InventoryCategory valueOf(int definitionId) {

        InventoryCategory[] values = InventoryCategory.values();
        for (InventoryCategory type : values) {
            int typeDefinitionId = type.getDefinitionId();
            if (definitionId == typeDefinitionId) {
                return type;
            }
        }

        throw new IllegalArgumentException("No InventoryCategory found for definitionId: " + definitionId);

    }
    
    public boolean isLmHardware() {
        return this == ONE_WAY_RECEIVER || this == TWO_WAY_RECEIVER;
    }
}