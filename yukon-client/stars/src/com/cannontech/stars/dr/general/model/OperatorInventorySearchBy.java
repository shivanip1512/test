package com.cannontech.stars.dr.general.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum OperatorInventorySearchBy implements DisplayableEnum {
	
	serialNumber,
    meterNumber, 
    accountNumber,
    phoneNumber,
	lastName,
	workOrderNumber,
	altTrackingNumber;

    @Override
    public String getFormatKey() {
    	return "yukon.web.modules.operator.inventory." + this.toString();
    }

}
