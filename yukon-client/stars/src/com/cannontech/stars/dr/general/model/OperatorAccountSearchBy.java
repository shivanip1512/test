package com.cannontech.stars.dr.general.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum OperatorAccountSearchBy implements DisplayableEnum {

	ACCOUNT_NUMBER,
    PHONE_NUMBER, 
    LAST_NAME,
    SERIAL_NUMBER,
    MAP_NUMBER,
    ADDRESS,
    ALT_TRACKING_NUMBER,
    COMPANY;

    @Override
    public String getFormatKey() {
    	return "yukon.web.modules.operator.accountSearchByEnum." + this.toString();
    }
}
