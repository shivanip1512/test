package com.cannontech.dr.ecobee.dao;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Represents the different categories of query that are tracked for Ecobee communications.
 */
public enum EcobeeQueryType implements DisplayableEnum {
    DATA_COLLECTION,    //Daily reads, "Read now", data report
    DEMAND_RESPONSE,    //DR events and restores
    SYSTEM,             //Everything else
    ;
    
    private static final String formatKeyBase = "yukon.web.modules.dr.ecobee.queryType";
    
    @Override
    public String getFormatKey() {
        return formatKeyBase + name();
    }
}