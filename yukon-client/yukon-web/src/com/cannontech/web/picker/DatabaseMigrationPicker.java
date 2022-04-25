package com.cannontech.web.picker;

import java.util.Collection;
import java.util.List;

import com.cannontech.common.databaseMigration.model.DatabaseMigrationContainer;
import com.cannontech.common.databaseMigration.model.ExportTypeEnum;
import com.cannontech.common.databaseMigration.service.DatabaseMigrationService;
import com.cannontech.common.model.Direction;
import com.cannontech.common.search.result.SearchResults;
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
    public SearchResults<DatabaseMigrationContainer> search(String ss,
            int start, int count, String extraArgs, YukonUserContext userContext) {

        SearchResults<DatabaseMigrationContainer> searchResult = databaseMigrationService.search(exportType, ss, start, count,
                userContext);

        return searchResult;
    }

    @Override
    public SearchResults<DatabaseMigrationContainer> search(Collection<Integer> initialIds,
            String extraArgs, YukonUserContext userContext) {
        throw new UnsupportedOperationException("DatabaseMigrationPicker doesn't support initial ids");
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

    @Override
    public SearchResults<DatabaseMigrationContainer> search(Collection<Integer> initialIds, String extraArgs, String sortBy,
            Direction direction, YukonUserContext userContext) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public SearchResults<DatabaseMigrationContainer> search(String ss, int start, int count,
            String extraArgs, String sortBy, Direction direction, YukonUserContext userContext) {
       throw new UnsupportedOperationException();
    }

}
