package com.cannontech.web.updater.databaseMigration;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.databaseMigration.bean.ExportDatabaseMigrationStatus;
import com.cannontech.common.databaseMigration.bean.ImportDatabaseMigrationStatus;
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
        
        // EXPORT
        if (updaterTypeStr.equals("EXPORT_COMPLETED_ITEMS")) {
        	
        	ExportDatabaseMigrationStatus status = databaseMigrationService.getExportStatus(id);
        	int currentCount = status.getCurrentCount();
        	return String.valueOf(currentCount);
        	
        } else if (updaterTypeStr.equals("EXPORT_STATUS_TEXT")) {
        	
        	ExportDatabaseMigrationStatus status = databaseMigrationService.getExportStatus(id);
        	
        	if (status.isComplete()) {
        		return "Complete";
        	} else if (status.isExceptionOccured()) {
        	    return "Error";
        	} else {
        		return "In Progress";
        	}
        } else if (updaterTypeStr.equals("EXPORT_STATUS_CLASS")) {
            
            ExportDatabaseMigrationStatus status = databaseMigrationService.getExportStatus(id);
            
            if (status.isComplete()) {
                return "OkMsg";

            } else if (status.isExceptionOccured()) {
                return "ErrorMsg";

            } else {
                return "";
            }
        
        // Exporting Errors
        } else if (updaterTypeStr.equals("EXPORT_ERROR_TEXT")) {

            ExportDatabaseMigrationStatus status = databaseMigrationService.getExportStatus(id);

            if (status.isExceptionOccured()) {
                return status.getError();
            } else {
                return "";
            }
            
        // VALIDATION
        } else if (updaterTypeStr.equals("VALIDATION_COMPLETED_ITEMS")) {
        	
        	ImportDatabaseMigrationStatus status = databaseMigrationService.getValidationStatus(id);
        	int currentCount = status.getCurrentCount();
        	return String.valueOf(currentCount);
        	
        } else if (updaterTypeStr.equals("VALIDATION_STATUS_TEXT")) {
        	
        	ImportDatabaseMigrationStatus status = databaseMigrationService.getValidationStatus(id);
        	
        	if (status.isComplete()) {
        		return "Complete";
        	} else {
        		return "In Progress";
        	}
        	
        // IMPORT
        } else if (updaterTypeStr.equals("IMPORT_COMPLETED_ITEMS")) {
        	
        	ImportDatabaseMigrationStatus status = databaseMigrationService.getImportStatus(id);
        	int currentCount = status.getCurrentCount();
        	return String.valueOf(currentCount);
        	
        } else if (updaterTypeStr.equals("IMPORT_STATUS_TEXT")) {
        	
        	ImportDatabaseMigrationStatus status = databaseMigrationService.getImportStatus(id);
        	
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
