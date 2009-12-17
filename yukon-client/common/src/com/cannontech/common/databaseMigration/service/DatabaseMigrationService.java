package com.cannontech.common.databaseMigration.service;

import java.io.File;
import java.util.List;
import java.util.Set;

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
    
    /**
     * This method retrieves all the database tables associated with a given component.
     * 
     * @return
     */
    public Set<DisplayableExportType> getAvailableExportTypes();
    
    /**
     * This method adds a dbTableListener that allows us to process db changes among other things
     * what a given table is processed by the importer
     * 
     * @param tableName
     * @param tableChangeCallback
     */
    public void addDBTableListener(String tableName, TableChangeCallback tableChangeCallback);
    
    
    public ExportDatabaseMigrationStatus getExportStatus(String id);
    
    public ImportDatabaseMigrationStatus getImportStatus(String id);
    public List<ImportDatabaseMigrationStatus> getAllImportStatuses();
    
    /**
     * This method is used to get the items shown in the picker.  This also handles the
     * searching aspect of the picker
     * 
     * @param exportType
     * @param searchText
     * @param startIndex
     * @param count
     * @param userContext
     * @return
     */
    public SearchResult<DatabaseMigrationContainer> search(ExportTypeEnum exportType, 
                                                           String searchText, 
                                                           int startIndex, int count, 
                                                           YukonUserContext userContext);
    
    public List<DatabaseMigrationContainer> getItemsByIds(ExportTypeEnum exportType, 
                                                          List<Integer> idList, 
                                                          YukonUserContext userContext);
    
}