package com.cannontech.common.bulk.action;

/**
 * Enumeration of actions that can be performed in bulk
 */
public enum BulkActionEnum {

    EDIT_DEVICES("editGrid"), DELETE_DEVICES("delete"), CHANGE_TYPE(
            "changeType"), UPDATE_ROUTE("updateRoute"), ENABLE_DISABLE_DEVICES(
            "enableDisableDevices");

    // this key prefix can be found in the following file: 
    // com.cannontech.yukon.common.device.bulk.bulkAction.xml
    private final static String keyPrefix = "yukon.common.device.bulk.bulkAction.";

    private String action = null;

    private BulkActionEnum(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    /**
     * I18N key for the display text for this action
     * @return Display key
     */
    public String getDisplayKey() {
        return keyPrefix + name();
    }

    /**
     * I18N key for the description text for this action
     * @return Discription key
     */
    public String getDescriptionKey() {
        return this.getDisplayKey() + ".description";
    }

}
