package com.cannontech.common.bulk.collection.device.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * This enum defines any Collection Action Inputs.
 */
public enum CollectionActionInput implements DisplayableEnum {

    SELECTED_COMMAND,               //Used for Send Command, Locate Route
    COMMAND,                        //Used for Send Command, Locate Route
    ATTRIBUTES,                     //Used for Read Attribute, Configure Data Streaming
    SELECTED_ROUTES,                //Used for Locate Route
    AUTOMATICALLY_UPDATE_ROUTE,     //Used for Locate Route
    CONFIGURATION,                  //Used for Device Configs/Assign
    METER_PROGRAM,                  //Used for Meter Programming
    INTERVAL,                       //Used for Configure Data Streaming
    CHANGE,                         //Used for Mass Change
    DEVICE_TYPE,                    //Used for Change Device Type
    UPDATE_FIELD;                   //Used for Update Points


    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.collectionActions.collectionActionInput." + name();
    }
}
