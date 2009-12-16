package com.cannontech.common.databaseMigration.service;

import java.io.File;
import java.util.List;

import com.cannontech.common.databaseMigration.TableChangeCallback;
import com.cannontech.common.databaseMigration.bean.ExportDatabaseMigrationStatus;
import com.cannontech.common.databaseMigration.bean.ImportDatabaseMigrationStatus;
import com.cannontech.common.databaseMigration.bean.WarningProcessingEnum;
import com.cannontech.common.databaseMigration.model.DatabaseMigrationContainer;
import com.cannontech.common.databaseMigration.model.DisplayableExportType;
import com.cannontech.common.databaseMigration.model.ExportTypeEnum;
import com.cannontech.common.search.SearchResult;
import com.cannontech.user.YukonUserContext;

public interface DatabaseMigrationService {
    
    public ImportDatabaseMigrationStatus validateImportFile(File importFile);
    
    public ImportDatabaseMigrationStatus processImportDatabaseMigration (File importFile,
                                                                         WarningProcessingEnum warningProcessingEnum);
    
    public ExportDatabaseMigrationStatus processExportDatabaseMigration(ExportTypeEnum exportType, 
                                                                        List<Integer> exportIdList, 
                                                                        YukonUserContext userContext);
    
    public List<DisplayableExportType> getAvailableExportTypes();
    
    public void addDBTableListener(String tableName, TableChangeCallback tableChangeCallback);
    
    public ExportDatabaseMigrationStatus getExportStatus(String id);
    public ImportDatabaseMigrationStatus getImportStatus(String id);
    public List<ImportDatabaseMigrationStatus> getAllImportStatuses();
    
    public SearchResult<DatabaseMigrationContainer> search(ExportTypeEnum exportType, 
                                                           String searchText, 
                                                           int startIndex, int count, 
                                                           YukonUserContext userContext);
    
    public List<DatabaseMigrationContainer> getItemsByIds(ExportTypeEnum exportType, 
                                                          List<Integer> idList, 
                                                          YukonUserContext userContext);
    
}