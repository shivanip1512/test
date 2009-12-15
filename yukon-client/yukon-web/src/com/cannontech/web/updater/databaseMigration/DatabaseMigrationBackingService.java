package com.cannontech.web.updater.databaseMigration;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.databaseMigration.bean.ExportDatabaseMigrationStatus;
import com.cannontech.common.databaseMigration.service.DatabaseMigrationService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class DatabaseMigrationBackingService implements UpdateBackingService {
	
	DatabaseMigrationService databaseMigrationService;

	@Override
	public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

		String[] idParts = StringUtils.split(identifier, "/");
        String id = idParts[0];
        String updaterTypeStr = idParts[1];
        
        if (updaterTypeStr.equals("COMPLETED_ITEMS")) {
        	
        	ExportDatabaseMigrationStatus status = databaseMigrationService.getExportStatus(id);
        	int currentCount = status.getCurrentCount();
        	return String.valueOf(currentCount);
        	
        } else if (updaterTypeStr.equals("STATUS_TEXT")) {
        	
        	ExportDatabaseMigrationStatus status = databaseMigrationService.getExportStatus(id);
        	
        	if (status.isComplete()) {
        		return "Complete";
        	} else {
        		return "In Progress";
        	}
        	
        } else {
        	return "N/A";
        }
	}
	
	@Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
	
	@Autowired
	public void setDatabaseMigrationService(DatabaseMigrationService databaseMigrationService) {
		this.databaseMigrationService = databaseMigrationService;
	}
}
