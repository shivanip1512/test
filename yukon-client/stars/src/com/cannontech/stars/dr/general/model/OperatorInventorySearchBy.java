package com.cannontech.stars.dr.general.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum OperatorInventorySearchBy implements DisplayableEnum {
	
	SERIAL_NUMBER,
    METER_NUMBER, 
    ACCOUNT_NUMBER,
    PHONE_NUMBER,
	LAST_NAME,
	WORK_ORDER_NUMBER,
	ALT_TRACKING_NUMBER;

    @Override
    public String getFormatKey() {
    	return "yukon.web.modules.operator.inventory.operatorInventorySearchByEnum." + this.toString();
    }

}
