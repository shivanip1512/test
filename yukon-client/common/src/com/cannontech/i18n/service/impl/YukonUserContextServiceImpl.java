package com.cannontech.i18n.service.impl;

import java.util.Locale;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.service.YukonUserContextService;
import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.user.YukonUserContext;

public class YukonUserContextServiceImpl implements YukonUserContextService {

	private SystemDateFormattingService systemDateFormattingService;
	
	@Override
	public YukonUserContext getEnergyCompanyDefaultUserContext(LiteYukonUser user) {

		TimeZone timeZone = systemDateFormattingService.getSystemTimeZone();
		
		Locale locale = Locale.US; 
		
		YukonUserContext userContext = 
			new SimpleYukonUserContext(user, locale, timeZone, "");
		
		return userContext;
	}
	
	@Autowired
	public void setSystemDateFormattingService(
			SystemDateFormattingService systemDateFormattingService) {
		this.systemDateFormattingService = systemDateFormattingService;
	}
	
}
