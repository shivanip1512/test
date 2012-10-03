package com.cannontech.common.databaseMigration.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigHelper;
import com.cannontech.common.databaseMigration.TableChangeCallback;
import com.cannontech.common.databaseMigration.bean.ExportDatabaseMigrationStatus;
import com.cannontech.common.databaseMigration.bean.ImportDatabaseMigrationStatus;
import com.cannontech.common.databaseMigration.bean.SqlHolder;
import com.cannontech.common.databaseMigration.bean.WarningProcessingEnum;
import com.cannontech.common.databaseMigration.bean.config.ConfigurationTable;
import com.cannontech.common.databaseMigration.bean.data.DataTable;
import com.cannontech.common.databaseMigration.bean.data.template.DataEntryTemplate;
import com.cannontech.common.databaseMigration.bean.data.template.DataTableTemplate;
import com.cannontech.common.databaseMigration.bean.database.Column;
import com.cannontech.common.databaseMigration.bean.database.ColumnTypeEnum;
import com.cannontech.common.databaseMigration.bean.database.DatabaseDefinition;
import com.cannontech.common.databaseMigration.bean.database.TableDefinition;
import com.cannontech.common.databaseMigration.dao.DatabaseMigrationDao;
import com.cannontech.common.databaseMigration.exception.ConfigurationErrorException;
import com.cannontech.common.databaseMigration.exception.ConfigurationWarningException;
import com.cannontech.common.databaseMigration.model.DatabaseMigrationContainer;
import com.cannontech.common.databaseMigration.model.DatabaseMigrationPicker;
import com.cannontech.common.databaseMigration.model.DisplayableExportType;
import com.cannontech.common.databaseMigration.model.ExportTypeEnum;
import com.cannontech.common.databaseMigration.service.ConfigurationParserService;
import com.cannontech.common.databaseMigration.service.ConfigurationProcessorService;
import com.cannontech.common.databaseMigration.service.DatabaseMigrationService;
import com.cannontech.common.databaseMigration.service.ExportXMLGeneratorService;
import com.cannontech.common.events.loggers.DatabaseMigrationEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.FormattingTemplateProcessor;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.YukonSettingsDao;
import com.cannontech.user.SystemUserContext;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class DatabaseMigrationServiceImpl implements DatabaseMigrationService, ResourceLoaderAware, InitializingBean{
    
    private static Logger log = YukonLogManager.getLogger(DatabaseMigrationServiceImpl.class);
    
    private static String LOG_FILE_DATE_FORMAT = "yyyyMMddHHmmss";
    
    private Multimap<String, TableChangeCallback> tableChangeCallbacks = ArrayListMultimap.create();

    private DatabaseDefinition databaseDefinition;
    // config name to definition
    private Map<ExportTypeEnum, TableDefinition> configurationIdentityMap = Maps.newHashMap();
    // config name to template
    private Map<ExportTypeEnum, DataTableTemplate> configurationMap = Maps.newHashMap();

    private Resource databaseDefinitionResource;
    private List<Resource> configurationResourceList;
    
    private ConfigurationProcessorService configurationProcessorService;
    private ConfigurationParserService configurationParserService;
    private DatabaseMigrationDao databaseMigrationDao;
    private DatabaseMigrationEventLogService databaseMigrationEventLogService;
    private ExportXMLGeneratorService exportXMLGeneratorService;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private TransactionOperations transactionTemplate;
    private TemplateProcessorFactory templateProcessorFactory;
    private ScheduledExecutor scheduledExecutor = null;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private YukonSettingsDao yukonSettingsDao;
    
    private RecentResultsCache<ExportDatabaseMigrationStatus> exportStatusCache = 
    			new RecentResultsCache<ExportDatabaseMigrationStatus>();
    private RecentResultsCache<ImportDatabaseMigrationStatus> validationStatusCache = 
    			new RecentResultsCache<ImportDatabaseMigrationStatus>();
    private RecentResultsCache<ImportDatabaseMigrationStatus> importStatusCache = 
    			new RecentResultsCache<ImportDatabaseMigrationStatus>();
    
    private Comparator<ImportDatabaseMigrationStatus> importDatabaseMigrationStatusComparator = 
		    	new Comparator<ImportDatabaseMigrationStatus>() {
					@Override
					public int compare(ImportDatabaseMigrationStatus o1, ImportDatabaseMigrationStatus o2) {
						return o2.getStartTime().compareTo(o1.getStartTime());
					}
				};
    
    @Override
    public ExportDatabaseMigrationStatus getExportStatus(String id) {
    	return exportStatusCache.getResult(id);
    }
    
    @Override
    public ImportDatabaseMigrationStatus getValidationStatus(String id) {
    	return validationStatusCache.getResult(id);
    }
    
    @Override
    public List<ImportDatabaseMigrationStatus> getAllValidationStatuses() {
    	
    	List<ImportDatabaseMigrationStatus> all = Lists.newArrayList();
    	all.addAll(validationStatusCache.getPending());
    	all.addAll(validationStatusCache.getCompleted());
		Collections.sort(all, importDatabaseMigrationStatusComparator);
    	return all;
    }
    
    @Override
    public ImportDatabaseMigrationStatus getImportStatus(String id) {
    	return importStatusCache.getResult(id);
    }
    
    @Override
    public List<ImportDatabaseMigrationStatus> getAllImportStatuses() {
    	
    	List<ImportDatabaseMigrationStatus> all = Lists.newArrayList();
    	all.addAll(importStatusCache.getPending());
    	all.addAll(importStatusCache.getCompleted());
		Collections.sort(all, importDatabaseMigrationStatusComparator);
    	return all;
    }
    
    
    @Override
    public void afterPropertiesSet() throws Exception {

        // Parse xml into databaseDefinition object and cache it
        this.databaseDefinition = new DatabaseDefinition(databaseDefinitionResource);
        
        // Parse xml into tableDefinition and dataTableTemplate objects and cache them
        SAXBuilder saxBuilder = new SAXBuilder();
        for(Resource configurationResource : configurationResourceList) {
            try {
                
                Document configurationDocument = 
                	saxBuilder.build(configurationResource.getInputStream());

                // Get the name of the configuration
                Element rootElement = configurationDocument.getRootElement();
                Element configurationElement = rootElement.getChild("configuration");
                String exportTypeString = configurationElement.getAttribute("name").getValue();
                ExportTypeEnum exportType = ExportTypeEnum.valueOf(exportTypeString);
                
                // build tableDefinition map
                @SuppressWarnings("unchecked")
                Iterator<Element> tableElements = 
                	configurationDocument.getDescendants(new ElementFilter("table"));
                if(!tableElements.hasNext()) {
                    throw new IllegalArgumentException("The configuration file: " + 
                    		configurationResource + " does not have a table element.");
                }                
                String baseTableName = tableElements.next().getAttributeValue("name");
                TableDefinition tableDefinition = databaseDefinition.getTable(baseTableName);
                configurationIdentityMap.put(exportType, tableDefinition);
                
                // build databaseTableTemplate map
                ConfigurationTable baseTableElement = 
                    configurationParserService.buildConfigurationTemplate(configurationResource);
                DataTableTemplate databaseTableTemplate = 
                    configurationParserService.buildDataTableTemplate(baseTableElement);
                
                configurationMap.put(exportType, databaseTableTemplate);

            } catch (JDOMException e) {
                log.error("An parsing error occured while parsing the " + configurationResource + 
                		" configuration file.", e);
            } catch (IOException e) {
                log.error("An issue occured when trying to parse the " + configurationResource + 
                		" configuration file.", e);
            }
        }
    }

    public ImportDatabaseMigrationStatus validateImportFile(File importFile, YukonUserContext userContext){
    	
        databaseMigrationEventLogService.startingValidation(userContext.getYukonUser(), 
        													importFile.getName());
        
        String exportTypeString = getConfigurationNameValue(importFile);
        ExportTypeEnum exportType = ExportTypeEnum.valueOf(exportTypeString);
        List<Element> importItemList = getElementListFromFile(importFile);

        ImportDatabaseMigrationStatus importDatabaseMigrationStatus = 
        	new ImportDatabaseMigrationStatus(importItemList.size(), importFile);
        importDatabaseMigrationStatus.setExportType(exportType);
        validationStatusCache.addResult(importDatabaseMigrationStatus.getId(), 
        								importDatabaseMigrationStatus);
        validateElementList(importItemList, exportType, importDatabaseMigrationStatus);
        
        return importDatabaseMigrationStatus;
    }
    
    /**
     * Helper method to attempt an insert/update of each import item in the list.  Validation fails 
     * for the item if it cannot be inserted/updated.  The inserts/updates are rolled back after 
     * validation.
     */
    private ImportDatabaseMigrationStatus validateElementList(final List<Element> importItemList, 
                                                              final ExportTypeEnum exportType,
                                                              final ImportDatabaseMigrationStatus importDatabaseMigrationStatus) {
        
        scheduledExecutor.execute(new Runnable() {
			
			@Override
			public void run() {

				transactionTemplate.execute(new TransactionCallback() {
		            public Object doInTransaction(TransactionStatus status) {
		                for (Element element : importItemList) {
		                    
		                    String label;
		                    try {
		                        label = getElementLabel(element, exportType);
		                        importDatabaseMigrationStatus.addLabelListEntry(label);
		                    } catch (IllegalArgumentException e) {
		                        importDatabaseMigrationStatus.addErrorListEntry("Invalid entry", e.getMessage());
		                        continue;
		                    }
		                    
		                    try {
		                        processElement(element, null, importDatabaseMigrationStatus);
		                    } catch (ConfigurationErrorException e) {
		                    	// Log to webserver log
		                    	log.error("Validation Error (" + label + ") --> ", e);
		                    	
		                    	// Add to error list for display on the page
		                        importDatabaseMigrationStatus.addErrorListEntry(label, e.getMessage());
		                    } catch (Exception e) {
		                    	// Log to webserver log
		                    	log.error("Validation Error (" + label + ") --> ", e);
		                    	
		                    	// Add to error list for display on the page
		                    	importDatabaseMigrationStatus.addErrorListEntry(label, e.getMessage());
		                    }
		                    if (importDatabaseMigrationStatus.getWarningCount() > 0) {
		                        log.error("Validation Warning (" + label + ") --> " + 
		                        			importDatabaseMigrationStatus.getWarningsMap().get(label));
		                    }

		                    importDatabaseMigrationStatus.incrementProcessed();
		                }

		                importDatabaseMigrationStatus.complete();
		                
		                status.setRollbackOnly();
		                return null;
		            }
		        });
			}
		});

        return importDatabaseMigrationStatus;
    }
        
    @Override
    public ImportDatabaseMigrationStatus processImportDatabaseMigration (final File importFile,
                                                                         final WarningProcessingEnum warningProcessingEnum,
                                                                         YukonUserContext userContext){

        databaseMigrationEventLogService.startingImport(userContext.getYukonUser(), importFile.getName());
        final PrintWriter logFileWriter = 
        	getLogFileWriter(userContext, importFile, DatabaseMigrationActionEnum.IMPORT);
        final String exportTypeString = getConfigurationNameValue(importFile);
        final ExportTypeEnum exportType = ExportTypeEnum.valueOf(exportTypeString);
        
        final ImportDatabaseMigrationStatus importDatabaseMigrationStatus = 
        	new ImportDatabaseMigrationStatus(0, importFile);
        importStatusCache.addResult(importDatabaseMigrationStatus.getId(), importDatabaseMigrationStatus);
        
        final List<Element> importItemList = getElementListFromFile(importFile);
        importDatabaseMigrationStatus.setTotalCount(importItemList.size());
        importDatabaseMigrationStatus.setWarningProcessing(warningProcessingEnum);
        
        scheduledExecutor.execute(new Runnable() {
            @Override
            public void run() {
                processElementList(importItemList, exportType, importDatabaseMigrationStatus, logFileWriter);
            }
        });
        
        return importDatabaseMigrationStatus;
    }
    
    /**
     * Helper method to insert/update each item in the import list
     */
    private void processElementList(final List<Element> importItemList,
                                    ExportTypeEnum exportType,
                                    final ImportDatabaseMigrationStatus importDatabaseMigrationStatus,
                                    final PrintWriter logFileWriter) {

        for (final Element element : importItemList) {
            
            final String label;
            try {
                label = getElementLabel(element, exportType);
                importDatabaseMigrationStatus.addLabelListEntry(label);
            } catch (IllegalArgumentException e) {
                importDatabaseMigrationStatus.addErrorListEntry("Invalid entry", e.getMessage());
                continue;
            }
            
            // Uses a transaction so we can roll back any items that have issues while processing
            transactionTemplate.execute(new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    try {
                        processElement(element, null, importDatabaseMigrationStatus);
                    } catch (ConfigurationErrorException e) {
                    	// Add to error list for display on the page
                        importDatabaseMigrationStatus.addErrorListEntry(label, e.getMessage());
                        
                        String errorMessage = "Error (" + label + ") --> ";
                        // Log to webserver log
                        log.error(errorMessage, e);
                        
                        // Log to migration specific log
                        logFileWriter.println(errorMessage);
                        e.printStackTrace(logFileWriter);
                        logFileWriter.println();
                        
                        status.setRollbackOnly();
                        
                    } catch (Exception e) {
                    	// Add to error list for display on the page
                        importDatabaseMigrationStatus.addErrorListEntry(label, e.getMessage());
                        
                        String errorMessage = "Error (" + label + ") --> ";
                        // Log to webserver log
                        log.error(errorMessage, e);
                        
                        // Log to migration specific log
                        logFileWriter.println(errorMessage);
                        e.printStackTrace(logFileWriter);
                        logFileWriter.println();
                        
                        status.setRollbackOnly();
                    }

                    WarningProcessingEnum warningProcessing = 
                    	importDatabaseMigrationStatus.getWarningProcessing();
                    if (importDatabaseMigrationStatus.getWarningsMap().containsKey(label)) {
                        if (warningProcessing.equals(WarningProcessingEnum.USE_EXISTING)) {
                            String warningMessage = "Preserving the entry " + label + ".";
                            log.error(warningMessage);
                            logFileWriter.println(warningMessage);
                        } else if (warningProcessing.equals(WarningProcessingEnum.OVERWRITE)) {
                            String warningMessage = 
                            	"Overwriting the entry " + label + " was sucessful.";
                            log.error(warningMessage);
                            logFileWriter.println(warningMessage);
                        }
                    } else {
                        String message = "Generating the entry " + label + " was sucessful.";
                        log.error(message);
                    }
                    
                    return null;
                }
            });
            importDatabaseMigrationStatus.incrementProcessed();
        }

        logFileWriter.close();
        
        importDatabaseMigrationStatus.complete();
    }

    private Integer processElement(Element importItem, 
                                   Map<String, String> primaryKeyColumnValuePair,
                                   ImportDatabaseMigrationStatus importDatabaseMigrationStatus) 
    	throws ConfigurationErrorException{
        
    	if (!importItem.getName().equals("item")) {
            throw new IllegalArgumentException("Invalid import configuration file structure. Items " +
            		"must start with an item tag. " + importItem.getName());
        }
        
        String initialTableName = importItem.getAttributeValue("name");
        TableDefinition table = databaseDefinition.getTable(initialTableName);
        return processElement(table, importItem, primaryKeyColumnValuePair, importDatabaseMigrationStatus);
    }

    
    
    private Integer processElement(TableDefinition table,
                                   Element importItem, 
                                   Map<String, String> primaryKeyTableValuePair,
                                   ImportDatabaseMigrationStatus importDatabaseMigrationStatus) 
    	throws ConfigurationErrorException{

        List<Column> allColumns = table.getAllColumns();
        List<String> columnNames = TableDefinition.getColumnNames(allColumns);
        List<?> children = importItem.getChildren();
        Map<String, String> columnValueMap = table.getDefaultColumnValueMap();
        
        // fill in primary key value if it exists
        if(primaryKeyTableValuePair != null){
            Object[] primaryKeyColumnValuePairEntryArray = primaryKeyTableValuePair.entrySet().toArray();
            @SuppressWarnings("unchecked")
            Entry<String, String> primaryKeyColumnValuePairEntry = 
            	(Entry<String, String>)primaryKeyColumnValuePairEntryArray[0];
            for ( Column column : allColumns) {
                if (primaryKeyColumnValuePairEntry.getKey().equals(column.getTableRef())) {
                    columnValueMap.put(column.getName(), primaryKeyColumnValuePairEntry.getValue());
                    break;
                }
            }
        }

        for (Object child : children) {
            if (!(child instanceof Element)){continue;}
                
            Element childElement = (Element)child;
            String childElementType = childElement.getName();
            String childColumnName = childElement.getAttributeValue("field");
            
            if (childElementType.equals("references")) { continue; }
            
            if (!columnNames.contains(childColumnName)){
                throw new IllegalArgumentException("The supplied column was not found in database " +
                		"definition file. (" + table.getName() + "." + childColumnName + ")");
            }

            for (Column column : allColumns) {

                if (column.getName().equals(childColumnName)) {
                    if (childElementType.equals("value")) {
                        String content = childElement.getText();
                        if (content != null) {
                            
                            // Check to see if the column needs to be escaped and escape the value
                            if (column.isEscapingNeeded()) {
                                content = StringEscapeUtils.unescapeJava(content);
                            }

                            content = SqlUtils.convertStringToDbValue(content);
                            columnValueMap.put(column.getName(), content);
                        }
                    } else if (childElementType.equals("item")) {
                        if(column.getTableRef() == null) {
                            throw new IllegalArgumentException("The supplied column does not have" +
                            		" a table reference in the database definition file. (" +
                            		table.getName() + "." + childColumnName + ")");
                        }

                        TableDefinition childTable = databaseDefinition.getTable(column.getTableRef());
                        Integer primaryKeyId = 
                        	processElement(childTable, childElement, null, importDatabaseMigrationStatus);
                        columnValueMap.put(column.getName(),primaryKeyId.toString());
                    } else if (childElementType.equals("reference")) {
                        if(column.getTableRef() == null){
                            throw new IllegalArgumentException("The supplied column does not have" +
                            		" a table reference in the database definition file. (" + 
                            		table.getName() + "." + childColumnName + ")");
                        }

                        TableDefinition childTable = databaseDefinition.getTable(column.getTableRef());
                        Integer primaryKeyId = processReferenceElement(childTable, childElement);
                        columnValueMap.put(column.getName(), primaryKeyId.toString());

                    } else {
                        throw new IllegalArgumentException("The supplied element type is not " +
                        		"a valid. (" + childElementType + ")");
                    }
                    continue;
                }
            }
        }
        
        Integer primaryKeyId = processTableEntry(columnValueMap, table, importItem, importDatabaseMigrationStatus);

        // handle references
        Map<String, String> newPrimaryKeyTableValuePair = 
        	Collections.singletonMap(table.getName(), primaryKeyId.toString());
        
        Element referencesElement = importItem.getChild("references");
        if (referencesElement != null) {
            List<?> referencesItems = referencesElement.getChildren("item");
            for (Object referencesItem : referencesItems) {
                if (!(referencesItem instanceof Element)){continue;}
                Element referencesItemElement = (Element) referencesItem;
                processElement(referencesItemElement, 
                			   newPrimaryKeyTableValuePair, 
                			   importDatabaseMigrationStatus);
            }
        }
        
        return primaryKeyId;
    }

    private Integer processReferenceElement(TableDefinition table, Element importItem) 
    	throws ConfigurationErrorException{

        List<Column> allColumns = table.getAllColumns();
        List<String> columnNames = TableDefinition.getColumnNames(allColumns);
        List<?> children = importItem.getChildren();
        Map<String, String> columnValueMap = table.getDefaultColumnValueMap();
        
        // Null reference found. Return the null id;
        if (children.size() == 0){
            Column primaryKeyColumn = table.getColumns(ColumnTypeEnum.PRIMARY_KEY).get(0);
            if (primaryKeyColumn.getNullId() != null){
                return Integer.valueOf(primaryKeyColumn.getNullId());
            }
        }
        
        for (Object child : children) {
            if (!(child instanceof Element)){continue;}
                
            Element childElement = (Element)child;
            String childElementType = childElement.getName();
            String childColumnName = childElement.getAttributeValue("field");
            
            if (!columnNames.contains(childColumnName)){
                throw new IllegalArgumentException("The supplied column was not found in database " +
                		"definition file. (" + table.getName() + "." + childColumnName + ")");
            }

            for (Column column : allColumns) {

                if (column.getName().equals(childColumnName)) {
                    if (childElementType.equals("value")) {
                        String content = childElement.getText();
                        if (content != null) {
                            columnValueMap.put(column.getName(), content);
                        }
                    } else if (childElementType.equals("reference")) {
                        if(column.getTableRef() == null){
                            throw new IllegalArgumentException("The supplied column does not have" +
                            		" a table reference in the database definition file. (" + 
                            		table.getName() + "." + childColumnName + ")");
                        }

                        TableDefinition childTable = databaseDefinition.getTable(column.getTableRef());
                        Integer primaryKeyId = processReferenceElement(childTable, childElement);
                        columnValueMap.put(column.getName(), primaryKeyId.toString());
                    } else {
                        throw new IllegalArgumentException("The supplied element type is not a " +
                        		"valid. (" + childElementType + ")");
                    }
                }
            }
        }
        
        return processReferenceTableEntry(columnValueMap, table, importItem);
    }
    
    private Integer processReferenceTableEntry(Map<String, String> columnValueMap,
                                               TableDefinition table,
                                               Element importItem) throws ConfigurationErrorException{
        
        // Check and make sure all the identifier keys are supplied.
        List<String> referenceColumnNames = 
        	TableDefinition.getColumnNames(table.getColumns(ColumnTypeEnum.IDENTIFIER));
        for (String referenceColumn : referenceColumnNames) {
            if(!columnValueMap.containsKey(referenceColumn)){
                throw new ConfigurationErrorException("The necessary identifiers were not " +
                		"supplied for the table being imported.  (" + table.getName() + "." + 
                		referenceColumn + ") ");
            }
        }
        
        return buildAndProcessReferenceSQL(columnValueMap, table);
        
    }
    
    /**
     * Builds up the SQL for a reference item and returns the id for the primary key
     */
    private Integer buildAndProcessReferenceSQL(Map<String, String> columnValueMap,
                                                TableDefinition table) 
    	throws ConfigurationErrorException {
        
        SqlHolder sqlHolder = new SqlHolder();

        List<String> primaryKeyColumnNames = 
        	TableDefinition.getColumnNames(table.getColumns(ColumnTypeEnum.PRIMARY_KEY));
        String primaryKeyColumnName = primaryKeyColumnNames.get(0);
        sqlHolder.addSelectClause(primaryKeyColumnName);

        sqlHolder.addFromClause(table.getTable());

        List<Object> whereParameterValues = new ArrayList<Object>();
        for (Entry<String, String> nameValuePair : columnValueMap.entrySet()){
            sqlHolder.addWhereClause(nameValuePair.getKey() + " = ?");
            whereParameterValues.add(nameValuePair.getValue());
        }
        
        SqlStatementBuilder selectSQL = sqlHolder.buildSelectSQL();
        Integer primaryKey = 0;
        try {
            primaryKey = yukonJdbcTemplate.queryForInt(selectSQL.getSql(), whereParameterValues.toArray());
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new ConfigurationErrorException("The reference in the import file does not exist" +
            		" or returns too many entries. Table: " + table.getName() + ", column/values: " + columnValueMap.toString(), e);
        }
            
        return primaryKey;
    }

    /**
     * Processes the validation for an inline entry
     */
    private Integer processTableEntry(Map<String, String> columnValueMap,
                                      TableDefinition table,
                                      Element importItem,
                                      ImportDatabaseMigrationStatus importDatabaseMigrationStatus) 
    	throws ConfigurationErrorException {

        // Check and make sure all the identifier keys are supplied.
        List<String> referenceColumnNames = 
        	TableDefinition.getColumnNames(table.getColumns(ColumnTypeEnum.IDENTIFIER));
        for (String referenceColumn : referenceColumnNames) {
            if(!columnValueMap.containsKey(referenceColumn)){
                throw new ConfigurationErrorException("The necessary identifiers were not supplied" +
                		" for the table being imported.  (" + table.getName() + "." + 
                		referenceColumn + ") ");
            }
        }
        
        // Check to see if the primary keys were supplied;  If they weren't we need 
        // to grab the max value.
        return buildAndProcessPrimaryKeySQL(columnValueMap, table, importDatabaseMigrationStatus);
    }

    /**
     * @param columnValueMap Column Name to data value
     */
    private Integer buildAndProcessPrimaryKeySQL(Map<String, String> columnValueMap,
                                                 TableDefinition table,
                                                 ImportDatabaseMigrationStatus importDatabaseMigrationStatus) 
    	throws ConfigurationErrorException {
    	
        List<Column> primaryKeyColumns = table.getColumns(ColumnTypeEnum.PRIMARY_KEY);
        List<String> primaryKeyColumnNames = TableDefinition.getColumnNames(primaryKeyColumns);
        WarningProcessingEnum warningProcessing = importDatabaseMigrationStatus.getWarningProcessing();
        
        // Counting the number of missing primaryKeys this is used for special cases 
        // like dual primary keys.
        int missingPKCount = 0;
        List<String> missingPrimaryKeyNameList = new ArrayList<String>();
        for (String primaryKeyColumnName : primaryKeyColumnNames) {
            if (!columnValueMap.containsKey(primaryKeyColumnName)) {
                missingPKCount++;
                missingPrimaryKeyNameList.add(primaryKeyColumnName);
            }
        }
        
        if (missingPKCount > 1) {
            throw new ConfigurationErrorException("More than one primary key was found missing" +
            		" on the input entry. (" + table.getName() + ") ");

        } else if (missingPKCount == 0) {
        	// PrimaryKey was supplied, use the primary key to gather the information and 
        	// then check for existing information

            Map<String, Object> primaryKeySelectResultMap = 
                databaseMigrationDao.findResultSetForPrimaryKeyValues(columnValueMap, table);

            // comparing data sets against each other using the table object;
            if (primaryKeySelectResultMap != null) {
                try {
                    compareResultTableDataToImportTableData(table, columnValueMap, primaryKeySelectResultMap);
                } catch (ConfigurationWarningException e) {
                    
                    List<String> labelList = importDatabaseMigrationStatus.getLabelList();
                    String label = labelList.get(labelList.size()-1);
                    importDatabaseMigrationStatus.addWarningListEntry(label, 
                                                                      "The supplied item already" +
                                                                      " exists in the system.  (" + 
                                                                      table.getName() + ")");
                    if (warningProcessing.equals(WarningProcessingEnum.OVERWRITE)) {
                        databaseMigrationDao.updateTableEntry(table, columnValueMap);
                        if (primaryKeyColumnNames.size() == 1) {
                            String primaryKeyValue = columnValueMap.get(primaryKeyColumnNames.get(0));
                            handleRowChange(table.getName(), Integer.valueOf(primaryKeyValue), DbTransactionEnum.UPDATE);
                        }
                    }
                    
                    if (primaryKeyColumnNames.size() == 1){
                        return Integer.valueOf(columnValueMap.get(primaryKeyColumnNames.get(0)));
                    } else {
                        return null;
                    }
                }
                return Integer.valueOf(columnValueMap.get(primaryKeyColumnNames.get(0)));

            } else {
            	// The imported item does not exist.  Creating new instance.
                Integer primaryKeyIds = databaseMigrationDao.insertNewTableEntry(table, columnValueMap);
                if (!warningProcessing.equals(WarningProcessingEnum.VALIDATE)) {
                    handleRowChange(table.getName(), primaryKeyIds, DbTransactionEnum.INSERT);
                }
                return primaryKeyIds;
            }

        } else {
        	// One primary key was missing.  Get the max primary key to insert.
            String missingPrimaryKey = missingPrimaryKeyNameList.get(0);

            // Using the identifiers to see if the object currently exists in the system.
            Map<String, Object> identifierSelectResultMap =
                databaseMigrationDao.findResultSetForIdentifierValues(columnValueMap, table, missingPrimaryKey);
        
            // Checks to see if the imported item exists in the system,
            // and then checks to see if a user action was submitted.
            if (identifierSelectResultMap != null) {
                List<String> labelList = importDatabaseMigrationStatus.getLabelList();
                String label = labelList.get(labelList.size()-1);
                importDatabaseMigrationStatus.addWarningListEntry(label,
                                                                  "The supplied item already " +
                                                                  "exists in the system.  (" + 
                                                                  table.getName() + ")");
                if (warningProcessing.equals(WarningProcessingEnum.VALIDATE)) {
                    Integer primaryKeyValue = 
                    	Integer.valueOf(identifierSelectResultMap.get(missingPrimaryKey).toString());
                    return primaryKeyValue;

                } else if (warningProcessing.equals(WarningProcessingEnum.USE_EXISTING)) {
                    Integer primaryKeyValue = 
                    	Integer.valueOf(identifierSelectResultMap.get(missingPrimaryKey).toString());
                    return primaryKeyValue;

                } else if (warningProcessing.equals(WarningProcessingEnum.OVERWRITE)) {
                    String primaryKeyValue = identifierSelectResultMap.get(missingPrimaryKey).toString();
                    columnValueMap.put(missingPrimaryKey, primaryKeyValue);
                    databaseMigrationDao.updateTableEntry(table, columnValueMap);
                    handleRowChange(table.getName(), Integer.valueOf(primaryKeyValue), DbTransactionEnum.UPDATE);
                    return Integer.valueOf(primaryKeyValue);
                }

            // The imported item does not exist.  Creating new instance.
            } else {
                
            	List<Column> uniqueColumns = table.getUniqueColumns();
            	for(Column column : uniqueColumns) {
            		String columnName = column.getName();
            		String columnValue = columnValueMap.get(columnName);
            		String nullId = column.getNullId();
            		String tableName = table.getTable();
            		String tableAlias = table.getName();
            		
            		if(!columnValue.equals(nullId)) {
            			// If the value is the 'nullId', unique does not apply
	            		boolean isNotUnique = 
	            			databaseMigrationDao.uniqueColumnHasExistingValues(tableName, columnName, columnValue);
	            		
	            		if(isNotUnique) {
	            			throw new ConfigurationErrorException("The " + tableAlias + " item, " + 
	            					columnName + " field value (" + columnValue + ") must be unique " +
	            					"but is already used in the destination database." );
	            		}
            		}
            		
            	}
            	
                // Finding next primary key for insert and inserting it into the columnValueMap
                Integer nextPrimaryKeyId = 0;
                try {
                    nextPrimaryKeyId = nextValueHelper.getNextValue(table.getName());
                } catch (IllegalArgumentException e) {
                    // The value was not found in the nextValueHelper.  Using the select 
                	// method to figure out the next value.
                    log.debug("Next value not found in next value helper.  Using select method " +
                    		"to generate the primary key for " + table.getName());
                    nextPrimaryKeyId = 
                        databaseMigrationDao.getNextMissingPrimaryKeyValue(columnValueMap, 
                        												   table, 
                        												   missingPrimaryKey);
                }
                columnValueMap.put(missingPrimaryKey, nextPrimaryKeyId.toString());
                
                Integer primaryKeyIds = databaseMigrationDao.insertNewTableEntry(table, columnValueMap);
                if (!warningProcessing.equals(WarningProcessingEnum.VALIDATE)) {
                    handleRowChange(table.getName(), primaryKeyIds, DbTransactionEnum.INSERT);
                }
                return primaryKeyIds;
                
            }
        }
        return null;
    }
    
    private void compareResultTableDataToImportTableData(TableDefinition tableDefinition,
                                                         Map<String, String> columnValueMap,
                                                         Map<String, Object> resultMap) 
    	throws ConfigurationErrorException, ConfigurationWarningException {
        
        
        List<String> warningList = new ArrayList<String>();
        List<String> errorList = new ArrayList<String>();
        boolean tableMatched = true;

        // Compare primary key values
        List<String> primaryKeyColumnNames = 
        	TableDefinition.getColumnNames(tableDefinition.getColumns(ColumnTypeEnum.PRIMARY_KEY));
        for (String primaryKeyColumnName : primaryKeyColumnNames) {
            String sqlColumnValue = resultMap.get(primaryKeyColumnName).toString();
            String importColumnValue = columnValueMap.get(primaryKeyColumnName);

            if(importColumnValue == null ||
               sqlColumnValue.equals(importColumnValue)){
                continue;
            }

            if(sqlColumnValue == null) {}
            if(importColumnValue == null) {}
            tableMatched = false;
            errorList.add("The column " + tableDefinition.getName() + "." + primaryKeyColumnName + 
            			  " does not match the value in the database for an existing object. " +
                          " (Database value = " + sqlColumnValue + ", Import value = " + 
                          importColumnValue + ") " );
        }

        // Compare foreign key values
        List<String> identifierColumnNames = 
        	TableDefinition.getColumnNames(tableDefinition.getColumns(ColumnTypeEnum.IDENTIFIER));
        for (String identifierColumnName : identifierColumnNames) {
            String sqlColumnValue = resultMap.get(identifierColumnName).toString();
            String importColumnValue = columnValueMap.get(identifierColumnName);

            if(sqlColumnValue.equals(importColumnValue)){
                continue;
            }
            
            if(sqlColumnValue == null) {}
            if(importColumnValue == null) {}
            errorList.add("The column " + tableDefinition.getName() + "." + identifierColumnName + 
            			  " does not match the value in the database for an existing object. " +
                          " (Database value = " + sqlColumnValue + ", Import value = " + 
                          importColumnValue + ") " );
            tableMatched = false;
        }

        
        List<String> dataColumnNames = 
        	TableDefinition.getColumnNames(tableDefinition.getColumns(ColumnTypeEnum.DATA));
        for (String dataColumnName : dataColumnNames) {
            String sqlColumnValue = resultMap.get(dataColumnName).toString();
            String importColumnValue = columnValueMap.get(dataColumnName);
            
            if(sqlColumnValue.equals(importColumnValue)){
                continue;
            }
            
            if(sqlColumnValue == null) {}
            if(importColumnValue == null) {}
            
            warningList.add("The column " + tableDefinition.getName() + "." + dataColumnName + 
            				" does not match the value in the database for an existing object. " +
            				" (Database value = " + sqlColumnValue + ", Import value = " + 
            				importColumnValue + ") ");
            tableMatched = false;
        }

        if (!tableMatched) {
            if (errorList.size() > 0) {
                throw new ConfigurationErrorException(errorList.toString());
            }
            if (warningList.size() > 0) {
                throw new ConfigurationWarningException(warningList.toString());
            }
        }
    }
    

    /********************************/
    
    private String getConfigurationNameValue(File importFile){
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document importXMLDocument = saxBuilder.build(importFile);
            Element importRoot = importXMLDocument.getRootElement();
            Element configurationElement = importRoot.getChild("configuration");
            
            return configurationElement.getAttributeValue("name");
        } catch (JDOMException e) {
            CTILogger.error("Error parsing configuration name", e);
        } catch (IOException e) {
        	CTILogger.error("Error processing configuration name", e);
        }
        return null;
    }
    
    private List<Element> getElementListFromFile(File importFile){
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document importXMLDocument = saxBuilder.build(importFile);
            Element importRoot = importXMLDocument.getRootElement();
            Element configurationElement = importRoot.getChild("configuration");
            
            List<?> children = configurationElement.getChildren();
            List<Element> elementList = new ArrayList<Element>();
            for (Object child : children) {
                elementList.add((Element)child);
            }

            return elementList;
        } catch (JDOMException e) {
        	CTILogger.error("Error parsing element list", e);
        } catch (IOException e) {
        	CTILogger.error("Error processing element list", e);
        }
        return null;
    }
    
    
    /**
     * Returns the identifier for a given element that is in the user's format, 
     * which was processed through the template process.
     */
    private String getElementLabel(Element element, ExportTypeEnum exportType) {
        // Check to make sure the format is correct and we can get the initial table.
        if(element.getName() != "item" || element.getAttributeValue("name") == null) {
        	throw new IllegalArgumentException("The configuration element is not the currect format.");
        }
        String initialTableName = element.getAttributeValue("name");
        TableDefinition table = databaseDefinition.getTable(initialTableName);
        
        Map<String, String> labelValueMap = getElementLabelValueMap(element, table);

        // Builds up the label by using the database migration message template 
        // and the key value pairs.
        SystemUserContext systemUserContext = new SystemUserContext();
        FormattingTemplateProcessor formattingTemplateProcessor = 
        	templateProcessorFactory.getFormattingTemplateProcessor(systemUserContext);
        MessageSourceAccessor messageSourceAccessor = 
        	messageSourceResolver.getMessageSourceAccessor(systemUserContext);

        String message = messageSourceAccessor.getMessage(exportType.getItemKey());

        String label = formattingTemplateProcessor.process(message, labelValueMap);
        return label;
    }

    /**
     * This method iterates through a list of identifying columns and builds up a column name 
     * value map, which contains the column name as a key and the value of that element as the 
     * value. 
     */
    private Map<String, String> getElementLabelValueMap(Element element, TableDefinition table) {
        Map<String, String> labelValueMap = Maps.newLinkedHashMap();

        List<?> children = element.getChildren();
        for (Object child : children) {
            if (!(child instanceof Element)) {continue;}
            Element childElement = (Element) child;
            String elementColumnName = childElement.getAttributeValue("field");
            
            List<Column> identifyingColumns = 
            	table.getColumns(ColumnTypeEnum.PRIMARY_KEY, ColumnTypeEnum.IDENTIFIER);
            identifyingColumns.addAll(table.getAddToDisplayLabelsColumns());
            for (Column identifyingColumn : identifyingColumns) {
                if (!(identifyingColumn.getName().equals(elementColumnName))) {continue;}
                
                if (identifyingColumn.getTableRef() != null) {
                    String referenceTableName = identifyingColumn.getTableRef();
                    TableDefinition referenceTable = databaseDefinition.getTable(referenceTableName);
                    
                    labelValueMap.putAll(getElementLabelValueMap(childElement, referenceTable));
                    break;
                } else {
                    if(elementColumnName != null) {
                        labelValueMap.put(identifyingColumn.getName(), childElement.getTextTrim());
                    }
                    break;
                }
            }
        }
        return labelValueMap;
    }    
    
    
    
    
    public ExportDatabaseMigrationStatus processExportDatabaseMigration(final ExportTypeEnum exportType, 
                                                                        final List<Integer> exportIdList, 
                                                                        final YukonUserContext userContext) {

        // create file
    	final File xmlDataFile = createBlankExportFile(exportType, userContext);
    	
    	databaseMigrationEventLogService.startingExport(userContext.getYukonUser(), xmlDataFile.getName());
    	
    	// init status
        final ExportDatabaseMigrationStatus exportDatabaseMigrationStatus = 
            new ExportDatabaseMigrationStatus(exportIdList.size()+1, xmlDataFile, exportType);
        exportStatusCache.addResult(exportDatabaseMigrationStatus.getId(), exportDatabaseMigrationStatus);
        
        // run file generation in background thread
        scheduledExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				
			    final PrintWriter logFileWriter = 
			        getLogFileWriter(userContext, xmlDataFile, DatabaseMigrationActionEnum.EXPORT);
			     
			    try {
                    DataTableTemplate dataTableTemplate = configurationMap.get(exportType);
    		        
    		        // build data
    		        Iterable<DataTable> data = 
    		            configurationProcessorService.processDataTableTemplate(dataTableTemplate, 
    		                                                                   exportIdList, 
    		                                                                   exportDatabaseMigrationStatus,
    		                                                                   logFileWriter);
    		        Element generatedXMLFile = 
    		            exportXMLGeneratorService.buildXmlElement(data, exportType.name());
    
    		        // write data to file
    		        Format format = Format.getPrettyFormat(  );
    		        XMLOutputter xmlOutputter = new XMLOutputter(format);
    		        
    		        try {
    		        	xmlOutputter.output(generatedXMLFile, new FileWriter(xmlDataFile));
    		        } catch (IOException e) {
    		        	log.error("Error outputting xml file.", e);
    		        }
    		        
    		        exportDatabaseMigrationStatus.addCurrentCount();
    		        
			    } catch (RuntimeException t) {
			        log.error(t);
			        exportDatabaseMigrationStatus.setError(t.getMessage());
			        
			    } finally {
			        logFileWriter.close();
			    }
			    
			}
		});

        return exportDatabaseMigrationStatus;
    }

    public List<Object> getPossibleChoices(){
        
        return null;
    }
    
    public void addDBTableListener(String tableName, TableChangeCallback tableChangeCallback){
        tableChangeCallbacks.put(tableName, tableChangeCallback);
    }
    
    
    @Override
    public List<Integer> getAllSearchIds(ExportTypeEnum exportType) {
    	
    	List<Map<String, Object>> results = getConfigurationSearchResultMaps(exportType, null);
    	
        String primaryKeyColumnName = getPrimaryKeyColumnNameForExportType(exportType);
        
    	List<Integer> ids = Lists.newArrayListWithCapacity(results.size());
    	for(Map<String, Object> valueMap : results) {
            int id = Integer.valueOf(valueMap.get(primaryKeyColumnName).toString());
            ids.add(id);
        }
    	
    	return ids;
    }
    
    @Override
    public SearchResult<DatabaseMigrationContainer> search(ExportTypeEnum exportType, 
                                                           String searchText, 
                                                           int startIndex, int count, 
                                                           YukonUserContext userContext) {
        
        List<Map<String, Object>> results = getConfigurationSearchResultMaps(exportType, searchText);
        
        String primaryKeyColumnName = getPrimaryKeyColumnNameForExportType(exportType);
        
        List<DatabaseMigrationPicker> pickerList = Lists.newArrayList();
        for(Map<String, Object> valueMap : results) {

            Object object = valueMap.get(primaryKeyColumnName);
            String id = object.toString();
            
            DatabaseMigrationPicker picker = new DatabaseMigrationPicker(Integer.valueOf(id), valueMap);
            pickerList.add(picker);
        }
        
        SearchResult<DatabaseMigrationContainer> searchResult = 
            new SearchResult<DatabaseMigrationContainer>();
        
        searchResult.setBounds(startIndex, count, pickerList.size());
        
        int toIndex = startIndex + count;
        pickerList = 
            pickerList.subList(startIndex, (toIndex > pickerList.size()) ? pickerList.size() : toIndex);
        List<DatabaseMigrationContainer> containerList = 
            this.convertPickerToContainer(exportType, pickerList, userContext);
        searchResult.setResultList(containerList);
        
        return searchResult;
    }
    
    private List<Map<String, Object>> getConfigurationSearchResultMaps(ExportTypeEnum exportType, 
    																   String searchText) {
    	
    	TableDefinition tableDefinition = configurationIdentityMap.get(exportType);
        SqlHolder sqlHolder = generateIdentifiersAndInfoSQL(tableDefinition);
        
        SqlStatementBuilder sql = sqlHolder.buildSelectSQL();
        
        if(!StringUtils.isBlank(searchText)) {
            if(sqlHolder.hasWhereClause()) {
                sql.append(" AND (");
            } else {
                sql.append(" WHERE (");
            }
            
            List<String> searchColumnSql = getConfigurationItemColumnIdentifiers(tableDefinition);
            Iterator<String> iterator = searchColumnSql.iterator();
            while(iterator.hasNext()) {
                String columnSql = iterator.next();
                sql.append(" UPPER(" + columnSql + ") ").startsWith(searchText.toUpperCase());
    
                if(iterator.hasNext()) {
                    sql.append(" OR ");
                }
            }
            sql.append(")");
        }
        
        return yukonJdbcTemplate.queryForList(sql.getSql(), sql.getArguments());
    }
    
    @Override
    public List<DatabaseMigrationContainer> getItemsByIds(ExportTypeEnum exportType, 
                                                          List<Integer> idList, 
                                                          YukonUserContext userContext) {
        
        TableDefinition tableDefinition = configurationIdentityMap.get(exportType);
        SqlHolder sqlHolder = generateIdentifiersAndInfoSQL(tableDefinition);
        
        SqlStatementBuilder sql = sqlHolder.buildSelectSQL();
        
        String primaryKeyColumnName = getPrimaryKeyColumnNameForExportType(exportType);
        
        if(sqlHolder.hasWhereClause()) {
            sql.append(" AND ");
        } else {
            sql.append(" WHERE ");
        }
        sql.append(primaryKeyColumnName).in(idList);
        
        List<Map<String, Object>> results = 
        	yukonJdbcTemplate.queryForList(sql.getSql(), sql.getArguments());

        List<DatabaseMigrationPicker> pickerList = Lists.newArrayList();
        for(Map<String, Object> valueMap : results) {

            Object object = valueMap.get(primaryKeyColumnName);
            String id = object.toString();
            
            DatabaseMigrationPicker picker = 
                new DatabaseMigrationPicker(Integer.valueOf(id), valueMap);
            pickerList.add(picker);
        }
        
        List<DatabaseMigrationContainer> containerList = 
            this.convertPickerToContainer(exportType, pickerList, userContext);
        
        return containerList;
    }
    
    private String getPrimaryKeyColumnNameForExportType(ExportTypeEnum exportType) {
    	
    	TableDefinition tableDefinition = configurationIdentityMap.get(exportType);
        List<Column> primaryKeyColumns = tableDefinition.getColumns(ColumnTypeEnum.PRIMARY_KEY);
        Column primaryKeyColumn = primaryKeyColumns.get(0);
        return primaryKeyColumn.getName();
    }
    
    private List<DatabaseMigrationContainer> convertPickerToContainer(ExportTypeEnum exportType, 
                                                                      List<DatabaseMigrationPicker> input, 
                                                                      YukonUserContext userContext) {

        FormattingTemplateProcessor formattingTemplateProcessor = 
            templateProcessorFactory.getFormattingTemplateProcessor(userContext);
        List<DatabaseMigrationContainer> result = Lists.newArrayList();
        
        for (DatabaseMigrationPicker databaseMigrationPicker : input) {
            
            MessageSourceAccessor messageSourceAccessor = 
                messageSourceResolver.getMessageSourceAccessor(userContext);
            String message = messageSourceAccessor.getMessage(exportType.getItemKey());
            
            String itemLabel = formattingTemplateProcessor.process(message, 
                                                                   databaseMigrationPicker.getIdentifierColumnValueMap());
            
            DatabaseMigrationContainer databaseMigrationContainer =
                new DatabaseMigrationContainer(databaseMigrationPicker.getDatabaseMigrationId(),
                                               itemLabel);

            result.add(databaseMigrationContainer);
        }
        
        return result;
    }
    
    private List<String> getConfigurationItemColumnIdentifiers(TableDefinition table){
        List<String> columnList = new ArrayList<String>();

        List<Column> identifyingColumns = 
            table.getColumns(ColumnTypeEnum.PRIMARY_KEY, ColumnTypeEnum.IDENTIFIER);
        for (Column identifyingColumn : identifyingColumns) {
            if(identifyingColumn.getTableRef() != null) {
                TableDefinition referencedTable = databaseDefinition.getTable(identifyingColumn.getTableRef());
                columnList.addAll(getConfigurationItemColumnIdentifiers(referencedTable));
            } else {
                if(!identifyingColumn.getColumnType().equals(ColumnTypeEnum.PRIMARY_KEY))
                    columnList.add(identifyingColumn.getName());
            }
        }
        return columnList;
    }

    private SqlHolder generateIdentifiersAndInfoSQL(TableDefinition tableDefinition) {
        
        SqlHolder holder = new SqlHolder();
        for (Column primaryKeyColumn : tableDefinition.getColumns(ColumnTypeEnum.PRIMARY_KEY)) {
            holder.addSelectClause(tableDefinition.getName() + "." + primaryKeyColumn.getName());
        }
        this.buildIdentifingSQL(tableDefinition, holder);

        return holder;
    }

    private void buildIdentifingSQL(TableDefinition tableDefinition, SqlHolder holder) {
        
        holder.addFromClause(tableDefinition.getName());
        
        List<Column> identifyingColumns = 
            tableDefinition.getColumns(ColumnTypeEnum.PRIMARY_KEY, ColumnTypeEnum.IDENTIFIER);
        
        identifyingColumns.addAll(tableDefinition.getAddToDisplayLabelsColumns());
        
        for (Column identifyingColumn : identifyingColumns) {
            if(identifyingColumn.getTableRef() != null) {
                // This column is a foreign key
                TableDefinition referencedTable = 
                    databaseDefinition.getTable(identifyingColumn.getTableRef());

                // Get the primary key from the referenced table so we can add our join to the 
                // where clause
                List<Column> referencePrimaryKeys = referencedTable.getColumns(ColumnTypeEnum.PRIMARY_KEY);
                if(referencePrimaryKeys.size() != 1) {
                    throw new RuntimeException("Foreign key references table with multiple primary keys.");
                }
                
                Column primaryKey = referencePrimaryKeys.get(0);
                holder.addWhereClause(tableDefinition.getName() + "." + identifyingColumn.getName() +
                                      " = " + identifyingColumn.getTableRef() + "." + 
                                      primaryKey.getName());
                
                buildIdentifingSQL(referencedTable, holder);
                
            } else {
                if(!identifyingColumn.getColumnType().equals(ColumnTypeEnum.PRIMARY_KEY))
                    // Add column to select if it isn't the primary key
                    holder.addSelectClause(tableDefinition.getName() + "." + identifyingColumn.getName());
            }
        }
    }
    
    public Set<DisplayableExportType> getAvailableExportTypes(){
        
        Set<DisplayableExportType> exportTypeList = Sets.newTreeSet();
        
        for(ExportTypeEnum exportType : configurationIdentityMap.keySet()) {
            DataTableTemplate dataTableTemplate = configurationMap.get(exportType);
            
            DisplayableExportType displayableExportType = new DisplayableExportType();
            displayableExportType.setExportType(exportType);
            displayableExportType.setTableNameSet(getConnectedTables(dataTableTemplate));
            
            exportTypeList.add(displayableExportType);
        }

        return exportTypeList;
    }
    
    /**
     * Returns all the tables for a given exportType
     */
    private Set<String> getConnectedTables(DataTableTemplate dataTableTemplate) {
        Set<String> results = Sets.newTreeSet();
        results.add(dataTableTemplate.getTable());
        
        Map<String, DataEntryTemplate> tableColumns = dataTableTemplate.getTableColumns();
        for (Entry<String, DataEntryTemplate> dataEntryColumn : tableColumns.entrySet()) {
            if (dataEntryColumn.getValue() instanceof DataTableTemplate) {
                DataTableTemplate nextDataTableTemplate = 
                    (DataTableTemplate) dataEntryColumn.getValue();
                results.addAll(getConnectedTables(nextDataTableTemplate));
            }
        }
        
        List<DataTableTemplate> tableReferences = dataTableTemplate.getTableReferences();
        for (DataTableTemplate referencesDataTableTemplate : tableReferences) {
            results.addAll(getConnectedTables(referencesDataTableTemplate));
        }
        
        return results;
    }
    
    /** 
     * This method forces the label to exist.
     */
    public String getConfigurationLabel(File configurationXmlFile){
        
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document configurationDocument = saxBuilder.build(configurationXmlFile);
            Element rootElement = configurationDocument.getRootElement();
        
            Element firstLabelElement = rootElement.getChild("configuration");
            return firstLabelElement.getAttribute("name").getValue();

        } catch (JDOMException e) {
            log.error("An parsing error occured while parsing the " + configurationXmlFile.getName() + 
            		" configuration file.", e);
        } catch (IOException e) {
            log.error("An issue occured when trying to parse the " + configurationXmlFile.getName() + 
            		" configuration file.", e);
        }
        return null;
    }
    
    /**
     * Creates a new file which will be used for creating the export file.
     */
    private File createBlankExportFile(ExportTypeEnum exportType, YukonUserContext userContext) {
        
        String exportFilePath = getFileBasePath(userContext);
        
        String databaseName = MasterConfigHelper.getConfiguration().getRequiredString("DB_SQLSERVER");
        String schemaUsername = PoolManager.getInstance().getPrimaryUser();
        
        String currentDate = new DateTime().toString(LOG_FILE_DATE_FORMAT);
        
        String defaultDataFileName = 
            databaseName + "_" + schemaUsername + "_" + exportType.getName() + "_" + currentDate + ".xml";
        
        // Replace invalid fileaname characters with "_".
        // Invalid characters can appear in databaseName when instance explicitly called out (Ex: PSPL-123456\SQLEXPRESS
        defaultDataFileName = defaultDataFileName.replace("\\", "_");
        
        File file = null;
        try {
            file = new File(exportFilePath + defaultDataFileName);
            file.createNewFile();
            
        } catch (IOException e) {
            throw new RuntimeException("Could not create new export file", e);
        }
        return file;
    }
    
    /**
     * Creates a new logger which will be used for the export or import process.
     */
    private PrintWriter getLogFileWriter(YukonUserContext userContext, 
								   	     File importFile, 
								   	     DatabaseMigrationActionEnum databaseMigrationAction) {
        
        String logFilePath = getFileBasePath(userContext);
        
        String databaseName = MasterConfigHelper.getConfiguration().getRequiredString("DB_SQLSERVER");
        String schemaUsername = PoolManager.getInstance().getPrimaryUser();
        
        String currentDate = new DateTime().toString(LOG_FILE_DATE_FORMAT);
        String defaultDataFileName = 
            databaseName + "_" + schemaUsername + "_" + currentDate + "_" + 
            databaseMigrationAction.name() + ".log";

        PrintWriter printWriter;
		try {
			printWriter = new PrintWriter(new File(logFilePath + defaultDataFileName));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not create custom log file: " + 
					logFilePath + defaultDataFileName, e);
		}
        
        printWriter.write(databaseMigrationAction.getLabel() + " " + importFile.getName() + 
        		" by user " + userContext.getYukonUser().getUsername() + "\n");
        
        return printWriter;
    }
    
    /**
     * Returns a string that represents the path where the export and log files will be placed
     */
    private String getFileBasePath(YukonUserContext userContext) {
        String exportFilePath = 
            yukonSettingsDao.getSettingStringValue(YukonSetting.DATABASE_MIGRATION_FILE_LOCATION);
        return CtiUtilities.getYukonBase() + exportFilePath;
        
    }
    
    private void handleRowChange(String tableName, int primaryKey, DbTransactionEnum dbTransactionEnum) {
        Collection<TableChangeCallback> collection = tableChangeCallbacks.get(tableName);
        for (TableChangeCallback tableChangeCallback : collection) {
            if (dbTransactionEnum.equals(DbTransactionEnum.INSERT)) {
                tableChangeCallback.rowInserted(primaryKey);
            }
            if (dbTransactionEnum.equals(DbTransactionEnum.UPDATE)) {
                tableChangeCallback.rowUpdated(primaryKey);
            }
        }
    }

    @Autowired
    public void setDatabaseMigrationDao(DatabaseMigrationDao databaseMigrationDao) {
        this.databaseMigrationDao = databaseMigrationDao;
    }
    
    @Autowired
    public void setConfigurationParserService(ConfigurationParserService configurationParserService) {
        this.configurationParserService = configurationParserService;
    }
    
    @Autowired
    public void setConfigurationProcessorService(ConfigurationProcessorService configurationProcessorService) {
        this.configurationProcessorService = configurationProcessorService;
    }
    
    @Autowired
    public void setExportXMLGeneratorService(ExportXMLGeneratorService exportXMLGeneratorService) {
        this.exportXMLGeneratorService = exportXMLGeneratorService;
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
    
    public void setDatabaseDefinitionXML(Resource databaseDefinitionResource){
        this.databaseDefinitionResource = databaseDefinitionResource; 
    }

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {

            List<Resource> configurationResourceList = Lists.newArrayList();
            configurationResourceList.add(resourceLoader.getResource(
            		"classpath:com/cannontech/database/configurations/ApplianceCategoriesAndRelatedPrograms.xml"));
            configurationResourceList.add(resourceLoader.getResource(
            		"classpath:com/cannontech/database/configurations/DirectLoadProgram.xml"));
            configurationResourceList.add(resourceLoader.getResource(
            		"classpath:com/cannontech/database/configurations/ExpresscomLoadGroup.xml"));
            configurationResourceList.add(resourceLoader.getResource(
            		"classpath:com/cannontech/database/configurations/LoadManagementToLoginGroupPermissions.xml"));
            configurationResourceList.add(resourceLoader.getResource(
            		"classpath:com/cannontech/database/configurations/YukonLoginGroup.xml"));
            this.configurationResourceList = configurationResourceList;
    }
    
    @Autowired
    public void setTransactionTemplate(TransactionOperations transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
    
    @Autowired
    public void setTemplateProcessorFactory(TemplateProcessorFactory templateProcessorFactory) {
        this.templateProcessorFactory = templateProcessorFactory;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    @Qualifier("main")
    public void setScheduledExecutor(ScheduledExecutor scheduledExecutor) {
		this.scheduledExecutor = scheduledExecutor;
	}
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setDatabaseMigrationEventLogService(DatabaseMigrationEventLogService databaseMigrationEventLogService) {
        this.databaseMigrationEventLogService = databaseMigrationEventLogService;
    }
    
    private enum DbTransactionEnum {
        INSERT,
        UPDATE;
    }
    
    private enum DatabaseMigrationActionEnum {
        EXPORT("Exporting"),
        IMPORT("Importing"),
        Validation("Validating");
        
        String label;
        
        DatabaseMigrationActionEnum(String label) {
            this.label = label;
        }
        
        String getLabel(){
            return this.label;
        }
        
    }
    
}
