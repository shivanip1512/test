package com.cannontech.web.picker.v2;

import java.util.List;

import com.cannontech.common.databaseMigration.model.DatabaseMigrationContainer;
import com.cannontech.common.databaseMigration.model.ExportTypeEnum;
import com.cannontech.common.databaseMigration.service.DatabaseMigrationService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.user.YukonUserContext;

public class DatabaseMigrationPicker extends BasePicker<DatabaseMigrationContainer> {

	private ExportTypeEnum exportType;
	private List<OutputColumn> outputColumns;
	private DatabaseMigrationService databaseMigrationService;

	@Override
	public String getIdFieldName() {
		return "databaseMigrationId";
	}

	@Override
	public List<OutputColumn> getOutputColumns() {
		return outputColumns;
	}

	@Override
	public SearchResult<DatabaseMigrationContainer> search(String ss,
			int start, int count, String extraArgs, YukonUserContext userContext) {

		SearchResult<DatabaseMigrationContainer> searchResult = 
            databaseMigrationService.search(exportType, ss, start, count, userContext);
        
		return searchResult;
	}
	
	public void setExportType(ExportTypeEnum exportType) {
		this.exportType = exportType;
	}
	
	public void setOutputColumns(List<OutputColumn> outputColumns) {
		this.outputColumns = outputColumns;
	}
	
	public void setDatabaseMigrationService(DatabaseMigrationService databaseMigrationService) {
		this.databaseMigrationService = databaseMigrationService;
	}

}
