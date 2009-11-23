package com.cannontech.web.updater.databaseMigration;

import org.apache.commons.lang.StringUtils;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class DatabaseMigrationBackingService implements UpdateBackingService {

	@Override
	public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

		String[] idParts = StringUtils.split(identifier, "/");
        //int id = Integer.parseInt(idParts[0]);
        String updaterTypeStr = idParts[1];
        
        if (updaterTypeStr.equals("COMPLETED_ITEMS")) {
        	return "5";
        } else if (updaterTypeStr.equals("STATUS_TEXT")) {
        	return "Complete";
        } else {
        	return "N/A";
        }
	}
	
	@Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
}
