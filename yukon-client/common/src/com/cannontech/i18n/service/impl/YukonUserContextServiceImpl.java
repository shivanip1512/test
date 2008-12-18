package com.cannontech.i18n.service.impl;

import java.util.Locale;
import java.util.TimeZone;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.service.YukonUserContextService;
import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.user.YukonUserContext;

public class YukonUserContextServiceImpl implements YukonUserContextService {

	
	@Override
	public YukonUserContext getEnergyCompanyDefaultUserContext(LiteYukonUser user) {

		TimeZone timeZone = TimeZone.getDefault();
		
		Locale locale = Locale.US; 
		
		YukonUserContext userContext = 
			new SimpleYukonUserContext(user, locale, timeZone, "");
		
		return userContext;
	}
	
}
