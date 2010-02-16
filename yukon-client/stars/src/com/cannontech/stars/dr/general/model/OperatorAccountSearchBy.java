package com.cannontech.stars.dr.general.model;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.i18n.DisplayableEnum;

public enum OperatorAccountSearchBy implements DisplayableEnum {

	ACCOUNT_NUMBER(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_ACCT_NO), 
    PHONE_NUMBER(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_PHONE_NO), 
    LAST_NAME(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_LAST_NAME),
    SERIAL_NUMBER(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_SERIAL_NO),
    MAP_NUMBER(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_MAP_NO),
    ADDRESS(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_ADDRESS),
    ALT_TRACING_NUMBER(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_ALT_TRACK_NO),
    COMPANY(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_COMPANY_NAME);

    private int definitionId;

    private OperatorAccountSearchBy(int definitionId) {
        this.definitionId = definitionId;
    }
    
    public int getDefinitionId() {
        return definitionId;
    }

    
    @Override
    public String getFormatKey() {
    	return "yukon.web.modules.operator.accountSearchByEnum." + this.toString();
    }
}
