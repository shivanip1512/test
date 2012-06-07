package com.cannontech.stars.dr.hardware.model;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;

/**
 * Enum which represents customer actions
 */
public enum CustomerAction implements ListEntryEnum {
    SIGNUP(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_SIGNUP), 
    ACTIVATION_PENDING(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_PENDING), 
    ACTIVATION_COMPLETED(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED), 
    TERMINATION(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION), 
    TEMP_OPT_OUT(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION), 
    FUTURE_ACTIVATION(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION), 
    INSTALL(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL), 
    CONFIGURE(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG),
    PROGRAMMING(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_PROGRAMMING),
    MANUAL_OPTION(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_MANUAL_OPTION),
    UNINSTALL(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_UNINSTALL);

    private int definitionId;

    private CustomerAction(int definitionId) {
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
    public static CustomerAction valueOf(int definitionId) {

        CustomerAction[] values = CustomerAction.values();
        for (CustomerAction mode : values) {
            int modeDefinitionId = mode.getDefinitionId();
            if (definitionId == modeDefinitionId) {
                return mode;
            }
        }

        throw new IllegalArgumentException("No CustomerAction found for definitionId: " + definitionId);

    }
    
    public String getValue(){
        return toString();
    }

    @Override
    public String getListName() {
        return YukonSelectionListDefs.YUK_LIST_NAME_LM_CUSTOMER_ACTION;
    }
}
