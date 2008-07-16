package com.cannontech.stars.dr.hardware.model;

import com.cannontech.common.constants.YukonListEntryTypes;

/**
 * Enum which represents load management hardware types
 */
public enum HardwareType {
    EXPRESSSTAT(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT), 
    MCT(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT), 
    COMMERCIAL_EXPRESSSTAT(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT), 
    LCR_5000_EXPRESSCOM(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_XCOM), 
    LCR_5000_VERSACOM(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_VCOM), 
    LCR_4000(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_4000), 
    LCR_3000(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3000), 
    LCR_2000(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_2000), 
    LCR_1000(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_1000), 
    SA_205(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA205), 
    SA_305(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA305), 
    SA_SIMPLE(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA_SIMPLE), 
    EXPRESSSTAT_HEAT_PUMP(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP), 
    EXPRESSSTAT_UTILITY_PRO(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_UTILITYPRO), 
    ENERGYPRO(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO);

    // this key prefix can be found in the following file:
    // com.cannontech.yukon.common.device.bulk.bulkAction.xml
    private final static String keyPrefix = "yukon.dr.consumer.hardware.type.";
    
    private int definitionId;

    private HardwareType(int definitionId) {
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
    public static HardwareType valueOf(int definitionId) {

        HardwareType[] values = HardwareType.values();
        for (HardwareType type : values) {
            int typeDefinitionId = type.getDefinitionId();
            if (definitionId == typeDefinitionId) {
                return type;
            }
        }

        throw new IllegalArgumentException("No HardwareType found for definitionId: " + definitionId);

    }
    
    /**
     * I18N key for the display text for this action
     * @return Display key
     */
    public String getDisplayKey() {
        return keyPrefix + name();
    }
}
