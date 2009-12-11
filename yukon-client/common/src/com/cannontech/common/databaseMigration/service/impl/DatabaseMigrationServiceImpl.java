package com.cannontech.common.databaseMigration.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import com.cannontech.common.config.MasterConfigHelper;
import com.cannontech.common.databaseMigration.TableChangeCallback;
import com.cannontech.common.databaseMigration.bean.DatabaseMigrationImportCallback;
import com.cannontech.common.databaseMigration.bean.SQLHolder;
import com.cannontech.common.databaseMigration.bean.config.ConfigurationTable;
import com.cannontech.common.databaseMigration.bean.data.DataTable;
import com.cannontech.common.databaseMigration.bean.data.template.DataTableTemplate;
import com.cannontech.common.databaseMigration.bean.database.Column;
import com.cannontech.common.databaseMigration.bean.database.ColumnTypeEnum;
import com.cannontech.common.databaseMigration.bean.database.Database;
import com.cannontech.common.databaseMigration.bean.database.Table;
import com.cannontech.common.databaseMigration.service.ConfigurationParserService;
import com.cannontech.common.databaseMigration.service.ConfigurationProcessorService;
import com.cannontech.common.databaseMigration.service.DatabaseMigrationService;
import com.cannontech.common.databaseMigration.service.ExportXMLGeneratorService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.PoolManager;
import com.cannontech.user.SystemUserContext;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class DatabaseMigrationServiceImpl implements DatabaseMigrationService, ResourceLoaderAware{
    Multimap<String, TableChangeCallback> tableChangeCallbacks = ArrayListMultimap.create();

    private ConfigurationParserService configurationParserService;
    private ConfigurationProcessorService configurationProcessorService;
    private Database database;
    private DateFormattingService dateFormattingService;
    private ExportXMLGeneratorService exportXMLGeneratorService;
    private JdbcTemplate jdbcTemplate;
    private PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver; 
    private ResourceLoader resourceLoader;
    private RolePropertyDao rolePropertyDao;
    private TransactionOperations transactionTemplate;

    public void validateImportFile(File importFile){
        
        List<Element> importItemList = getElementListFromFile(importFile);

        validateElementList(importItemList);
        
    }

    private void validateElementList(final List<Element> importItemList) {
        final DatabaseMigrationImportCallback databaseMigrationImportCallback = new DatabaseMigrationImportCallback();
        transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                for (Element element : importItemList) {
                    databaseMigrationImportCallback.incrementProcessed();
                    try {
                        processElement(element);
                    } catch (Exception e) {
                        databaseMigrationImportCallback.addErrorListEntry(e.getMessage());
                    }
                }
                status.setRollbackOnly();
                return null;
            }
        });
    }
 
    /********************/
    
    public void processImportDatabaseMigration (File importFile){
        
        int primaryKey = 0;
        String tableName = "";
        
        List<Element> importItemList = getElementListFromFile(importFile);
        processElementList(importItemList);
        
        handleRowInserted(tableName, primaryKey);
    }
    
    private void processElementList(final List<Element> importItemList) {
        final DatabaseMigrationImportCallback databaseMigrationImportCallback = new DatabaseMigrationImportCallback();
        for (final Element element : importItemList) {
            databaseMigrationImportCallback.incrementProcessed();
            transactionTemplate.execute(new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    try {
                        processElement(element);
                    } catch (Exception e) {
                        databaseMigrationImportCallback.addErrorListEntry(e.getMessage());
                    }
                    status.setRollbackOnly();
                    return null;
                }
            });
        }
    }
    
    /*****************************/
    
    private void processElement(Element importItem){
        if (!importItem.getName().equals("item")) {
            throw new IllegalArgumentException("Invalid import configuration file structure. Items must start with an item tag. "+importItem.getName());
        }
        
        String initialTableName = importItem.getAttributeValue("name");
        Table table = database.getTable(initialTableName);
        processElement(table, importItem);
    }

    
    
    private Integer processElement(Table table, Element importItem){
        List<Column> allColumns = table.getAllColumns();
        List<String> columnNames = Table.getColumnNames(allColumns);
        @SuppressWarnings("unchecked")
        List children = importItem.getChildren();
        Map<String, String> columnValueMap = Maps.newHashMap();
        for (Object child : children) {
            if (child instanceof Element){
                Element childElement = (Element)child;
                String childElementType = childElement.getName();
                if (!childElementType.equals("references")) {
                    String childColumnName = childElement.getAttributeValue("field");
                    if (!columnNames.contains(childColumnName)){
                        throw new IllegalArgumentException("The supplied column was not found in database definition file. ("+table.getTableName()+"."+childColumnName+")");
                    }

                    for (Column column : allColumns) {

                        if (column.getName().equals(childColumnName)) {
                            if (childElementType.equals("value")) {
                                @SuppressWarnings("unchecked")
                                List content = childElement.getContent();
                                if (content != null) {
                                    columnValueMap.put(column.getName(), content.get(0).toString());
                                }
                            } else if (childElementType.equals("item") ||
                                       childElementType.equals("reference")) {
                                if(column.getTableRef() != null){
                                    Table childTable = database.getTable(column.getTableRef());
                                    Integer primaryKeyId = processElement(childTable, childElement);
                                    columnValueMap.put(column.getName(),primaryKeyId.toString());
                                } else {
                                    throw new IllegalArgumentException("The supplied column does not have a table reference in the database definition file. ("+table.getTableName()+"."+childColumnName+")");
                                }
                            } else {
                                throw new IllegalArgumentException("The supplied element type is not a valid. ("+childElementType+")");
                            }
                        }
                    }
                } else {

                }
            }
        }
        
        return processTableEntry(columnValueMap, table, importItem);
    }
    
    
    private Integer processTableEntry(Map<String, String> columnValueMap,
                                       Table table,
                                       Element importItem) {

        // Check and make sure all the identifier keys are supplied.
        List<String> referenceColumnNames = Table.getColumnNames(table.getColumns(ColumnTypeEnum.identifier));
        for (String referenceColumn : referenceColumnNames) {
            if(!columnValueMap.containsKey(referenceColumn)){
                throw new IllegalArgumentException("The necessary identifiers were not supplied for the table being imported.  ("+table.getTableName()+"."+referenceColumn+") ");
            }
        }
        
        if (importItem.getName().equals("reference")) {
            return buildAndProcessReferenceSQL(columnValueMap, table);
        }

        // Check to see if the primary keys were supplied;  If they weren't we need to grab the max value.
        List<String> primaryColumnNames = Table.getColumnNames(table.getColumns(ColumnTypeEnum.primaryKey));
        for (String primaryColumnName : primaryColumnNames) {
            if(columnValueMap.containsKey(primaryColumnName)) {

            } else {

            }
        }

        
        return null;        
    }

    /**
     * Builds up the SQL for a reference item and returns the id for the primary key
     * 
     * @param columnValueMap
     * @param table
     * @return
     */
    private Integer buildAndProcessReferenceSQL(Map<String, String> columnValueMap,
                                                Table table) {
        
        SQLHolder sqlHolder = new SQLHolder();

        List<String> primaryKeyColumnNames = Table.getColumnNames(table.getColumns(ColumnTypeEnum.primaryKey));
        String primaryKeyColumnName = primaryKeyColumnNames.get(0);
        sqlHolder.addSelectClause(primaryKeyColumnName);

        sqlHolder.addFromClause(table.getTableName());

        for (Entry<String, String> nameValuePair : columnValueMap.entrySet()){
            sqlHolder.addWhereClause(nameValuePair.getKey()+" = "+nameValuePair.getValue());
        }
        
        SqlStatementBuilder selectSQL = sqlHolder.buildSQL();
        
        Integer primaryKey = jdbcTemplate.queryForInt(selectSQL.getSql());
        
        if (primaryKey == null) {
//            throw new Exception(The reference does not exist)
        }
        
        return primaryKey;
    }

    /********************************/
    
    private List<Element> getElementListFromFile(File importFile){
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document importXMLDocument = saxBuilder.build(importFile);
            Element importRoot = importXMLDocument.getRootElement();
            
            Element dataElement = importRoot.getChild("data");
            Element configurationElement = dataElement.getChild("configuration");
            
            @SuppressWarnings("unchecked")
            List children = configurationElement.getChildren();
            List<Element> elementList = new ArrayList<Element>();
            for (Object child : children) {
                elementList.add((Element)child);
            }

            return elementList;
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void processExportDatabaseMigration(File configurationXMLFile, List<Integer> primaryKeyList, YukonUserContext userContext) throws IOException{
        // Create export file
        File xmlDataFile = createFile(configurationXMLFile,userContext);

        // Parses the configuration file into a java object and uses that object to generate a template for processing
        ConfigurationTable baseTableElement = configurationParserService.buildConfigurationTemplate(configurationXMLFile);
        DataTableTemplate databaseMapTemplateWithMappingKeys = configurationParserService.buildDatabaseMapTemplate(baseTableElement);

        Iterable<DataTable> data = configurationProcessorService.processDataTableTemplate(databaseMapTemplateWithMappingKeys, primaryKeyList);

        Element generatedXMLFile = null;
        generatedXMLFile = exportXMLGeneratorService.buildXMLFile(data, baseTableElement.getLabel());

        // Output the configuration to the XML file
        Format format = Format.getPrettyFormat(  );
        XMLOutputter xmlOutputter = new XMLOutputter(format);
        xmlOutputter.output(generatedXMLFile, new FileWriter(xmlDataFile));

    }

    public List<Object> getPossibleChoices(){
        
        return null;
    }
    
    public void addDBTableListener(String tableName, TableChangeCallback tableChangeCallback){
        tableChangeCallbacks.put(tableName, tableChangeCallback);
    }
    
    public List<Map<String, Object>> getConfigurationItems(String configurationName){
        Resource configurationResource = getAvailableConfigurationMap().get(configurationName);
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            // Get the first table entry
            Document configurationDocument = saxBuilder.build(configurationResource.getFile());
            @SuppressWarnings("unchecked")
            Iterator<Element> tableElements = configurationDocument.getDescendants(new ElementFilter("table"));
            String baseTableName = null;
            if(tableElements.hasNext()) {
                baseTableName = tableElements.next().getAttributeValue("name");
            } else {
                throw new IllegalArgumentException("The configuration file does not have a table element.");
            }

            Table table = database.getTable(baseTableName);
            return getTableIdentifiersAndInfo(table);

        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }

    private List<Map<String, Object>> getTableIdentifiersAndInfo(Table table) {

        List<String> selectColumns = new ArrayList<String>();
        for (Column primaryKeyColumn : table.getColumns(ColumnTypeEnum.primaryKey))
            selectColumns.add(primaryKeyColumn.getName());
        List<String> fromEntries = new ArrayList<String>();
        List<String> whereClauses = new ArrayList<String>();
        buildIdentifingSQL(table, selectColumns, fromEntries, whereClauses);
        
        // Create select part of the SQL
        SqlStatementBuilder selectSQL = new SqlStatementBuilder();
        selectSQL.append("SELECT ");
        for (int i = 0; i < selectColumns.size()-1; i++) {
            String columnName = selectColumns.get(i);
            selectSQL.append(columnName+", ");
        }
        selectSQL.append(selectColumns.get(selectColumns.size()-1));
        
        // Create from part of the SQL
        selectSQL.append("FROM ");
        for (int i = 0; i < fromEntries.size()-1; i++) {
            String tableName = fromEntries.get(i);
            selectSQL.append(tableName+", ");
        }
        selectSQL.append(fromEntries.get(fromEntries.size()-1));
        
        // Create where clause of the SQL
        if(whereClauses.size() > 0) {
            selectSQL.append("WHERE "+whereClauses.get(0)+" ");
            for (int i = 1; i < whereClauses.size(); i++) {
                String whereClause = whereClauses.get(i);
                selectSQL.append("AND "+whereClause+" ");
            }
        }       
        
        // Process SQL Query
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(selectSQL.getSql());
        return queryForList;
    }

    private void buildIdentifingSQL(Table table, List<String> selectColumns, List<String> fromEntries, List<String> whereClauses){
        fromEntries.add(table.getTableName());
        List<Column> identifyingColumns = 
            table.getColumns(ColumnTypeEnum.primaryKey, ColumnTypeEnum.identifier);
        for (int i = 0; i < identifyingColumns.size(); i++) {
            Column identifyingColumn = identifyingColumns.get(i);
            if(identifyingColumn.getTableRef() != null) {
                Table referencedTable = database.getTable(identifyingColumn.getTableRef());
                List<Column> referencePrimaryKeys = referencedTable.getColumns(ColumnTypeEnum.primaryKey);
                for (Column primaryKey : referencePrimaryKeys) {
                    whereClauses.add(table.getTableName()+"."+identifyingColumn.getName()+" = "+identifyingColumn.getTableRef()+"."+primaryKey.getName());
                    buildIdentifingSQL(referencedTable, selectColumns, fromEntries, whereClauses);
                }
            } else {
                if(!identifyingColumn.getColumnType().equals(ColumnTypeEnum.primaryKey))
                    selectColumns.add(identifyingColumn.getName());
            }
        }
    }
    
    public Map<String, Resource> getAvailableConfigurationMap() {
        TreeMap<String, Resource> configurationMap = new TreeMap<String, Resource>();
        
        try {
            List<Resource> configurationResources = getConfigurationFiles();
            for (Resource configurationResource : configurationResources) {
                String configurationLabel = getConfigurationLabel(configurationResource.getFile());
                configurationMap.put(configurationLabel, configurationResource);
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        
        return configurationMap;
    }
    
    public SortedMap<String, SortedSet<String>> getAvailableConfigurationDatabaseTableMap(){
        SortedMap<String, SortedSet<String>> configNameToDatabaseTablesMap = new TreeMap<String, SortedSet<String>>();
        
        try {
            List<Resource> configurationResources = getConfigurationFiles();
            for (Resource configurationResource : configurationResources) {
                String configurationLabel = getConfigurationLabel(configurationResource.getFile());
                
                SortedSet<String> tables = getDBTables(configurationResource.getFile());
                if (tables != null)
                    configNameToDatabaseTablesMap.put(configurationLabel, tables);
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        
        return configNameToDatabaseTablesMap;
    }
    
    private SortedSet<String> getDBTables(File configurationXMLFile){
        SortedSet<String> tables = new TreeSet<String>();
        
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document configurationDocument = saxBuilder.build(configurationXMLFile);
        
            @SuppressWarnings("unchecked")
            Iterator<Element> descendants = configurationDocument.getDescendants(new ElementFilter("table")); 
            while (descendants.hasNext()){
                Element element = descendants.next();
                String tableName = element.getAttributeValue("name");
                
                Table table = database.getTable(tableName);
                tables.addAll(database.getConnectedTables(table));
            }

            return tables;
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
        
    }
    
    private List<Resource> getConfigurationFiles() throws IOException {
        Resource[] resources = pathMatchingResourcePatternResolver.getResources("classpath:com/cannontech/database/configurations/*.xml");
        return Arrays.asList(resources);
    }
    
    /** 
     * This method forces the label to exist!!!!!
     * 
     * @param configurationXMLFile
     * @return
     */
    public String getConfigurationLabel(File configurationXMLFile){
        
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document configurationDocument = saxBuilder.build(configurationXMLFile);
            Element rootElement = configurationDocument.getRootElement();
        
            Element firstLabelElement = rootElement.getChild("configuration");
            return firstLabelElement.getAttribute("name").getValue();

        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    private File createFile(File configurationXMLFile, YukonUserContext userContext) throws IOException{
//        rolePropertyDao.getPropertyStringValue(YukonRoleProperty.DATABASE_MIGRATION_PATH, yukonUser);
        String rolePropertyPath = "/Server/Export";
        String path = CtiUtilities.getYukonBase()+rolePropertyPath;
        
        String databaseName = MasterConfigHelper.getConfiguration().getRequiredString("DB_SQLSERVER");
        String schemaUsername = PoolManager.getInstance().getPrimaryUser();
        String component = getConfigurationLabel(configurationXMLFile);
        
        String currentDate = dateFormattingService.format(new Date(), DateFormatEnum.LONG_BOTH, new SystemUserContext());
        String defaultDataFileName = 
            databaseName+"_"+schemaUsername+"_"+component+"_"+currentDate+".xml";;

        File file = new File(path+"/"+defaultDataFileName);
        file.createNewFile();
        return file;
    }
    
    private void handleRowInserted(String tableName, int primaryKey) {
        Collection<TableChangeCallback> collection = tableChangeCallbacks.get(tableName);
        for (TableChangeCallback tableChangeCallback : collection) {
            tableChangeCallback.rowInserted(primaryKey);
        }
    }

    public void setDatabaseDefinitionXML(Resource databaseDefinitionXML){
        database = new Database(databaseDefinitionXML);
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
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

    @Autowired
    public void setExportXMLGeneratorService(ExportXMLGeneratorService exportXMLGeneratorService) {
        this.exportXMLGeneratorService = exportXMLGeneratorService;
    }


    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.pathMatchingResourcePatternResolver = 
            new PathMatchingResourcePatternResolver(resourceLoader);
    }
    
    @Autowired
    public void setTransactionTemplate(TransactionOperations transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}

class CountHolder{
    private int count = 0;
    
    public CountHolder(int startingValue){this.count = startingValue;}
    public int getCount(){return this.count;}
    public void add(){this.count++;}
}