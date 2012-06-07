package com.cannontech.stars.dr.hardware.model;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;

/**
 * Enum which represents customer event types
 */
public enum CustomerEventType implements ListEntryEnum {
    PROGRAM(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMPROGRAM), 
    HARDWARE(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE), 
    THERMOSTAT_MANUAL(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMTHERMOSTAT_MANUAL);

    private int definitionId;

    private CustomerEventType(int definitionId) {
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
    public static CustomerEventType valueOf(int definitionId) {

        CustomerEventType[] values = CustomerEventType.values();
        for (CustomerEventType mode : values) {
            int modeDefinitionId = mode.getDefinitionId();
            if (definitionId == modeDefinitionId) {
                return mode;
            }
        }

        throw new IllegalArgumentException("No CustomerEvent found for definitionId: " + definitionId);

    }

    public String getValue() {
        return toString();
    }

    @Override
    public String getListName() {
        return YukonSelectionListDefs.YUK_LIST_NAME_LM_CUSTOMER_EVENT;
    }
}
