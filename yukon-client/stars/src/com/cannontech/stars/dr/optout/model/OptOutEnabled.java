package com.cannontech.stars.dr.optout.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum OptOutEnabled implements DisplayableEnum {

    //Column varChar(18)
    //in order of restriction - refer to com.cannontech.stars.dr.optout.service.impl.OptOutStatusServiceImpl.getOptOutEnabled(LiteYukonUser)
	ENABLED (true, true), 
	DISABLED_WITH_COMM (false, true),
	DISABLED_WITHOUT_COMM (false, false);
	
	private boolean optOutEnabled;
	private boolean communicationEnabled;
	
	private OptOutEnabled(boolean optOutEnabled, boolean communicationEnabled) {
	    this.optOutEnabled = optOutEnabled;
	    this.communicationEnabled = communicationEnabled;
    }
	
	public boolean isOptOutEnabled(){
	    return this.optOutEnabled;
	}
	
	public boolean isCommunicationEnabled(){
	    return this.communicationEnabled;
	}
	
	private final static String keyPrefix = "yukon.web.modules.dr.optOutAdmin.optOutEnabledEnum.";

	@Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
