package com.cannontech.common.databaseMigration.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.core.io.Resource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.databaseMigration.bean.config.ConfigurationIncludeTable;
import com.cannontech.common.databaseMigration.bean.config.ConfigurationTable;
import com.cannontech.common.databaseMigration.bean.data.ElementCategoryEnum;
import com.cannontech.common.databaseMigration.bean.data.template.DataTableTemplate;
import com.cannontech.common.databaseMigration.bean.data.template.DataValueTemplate;
import com.cannontech.common.databaseMigration.bean.database.Column;
import com.cannontech.common.databaseMigration.bean.database.ColumnTypeEnum;
import com.cannontech.common.databaseMigration.bean.database.Database;
import com.cannontech.common.databaseMigration.bean.database.ReferenceTypeEnum;
import com.cannontech.common.databaseMigration.bean.database.Table;
import com.cannontech.common.databaseMigration.service.ConfigurationParserService;

public class ConfigurationParserServiceImpl implements ConfigurationParserService{

    private static Logger log = YukonLogManager.getLogger(ConfigurationParserServiceImpl.class);
    private Database database;
    
    public ConfigurationTable buildConfigurationTemplate(File configurationXMLFile) {
        SAXBuilder saxBuilder = new SAXBuilder();
        
        try {
            // Convert all the xml files to a document format to make them easier to use.
            Document configXMLDocument = saxBuilder.build(configurationXMLFile);
            Element configRoot = configXMLDocument.getRootElement();
            
            // Get the first configuration label
            Element firstConfigurationElement = configRoot.getChild("configuration");
            String firstConfigurationLabel = firstConfigurationElement.getAttributeValue("name");
            
            // Get the first table entry
            Element firstTableElement = firstConfigurationElement.getChild("table");
            String firstTableName = firstTableElement.getAttributeValue("name");

            // Build base tableElement
            ConfigurationTable baseTableElement = new ConfigurationTable();
            baseTableElement.setLabel(firstConfigurationLabel);
            baseTableElement.setTableName(firstTableName);
            
            buildConfigurationTemplate(baseTableElement, firstTableElement);
            return baseTableElement;
            
        } catch (JDOMException e) {
            log.error("An parsing error occured while parsing the "+configurationXMLFile.getName()+" configuration file.",e);
        } catch (IOException e) {
            log.error("An issue occured when trying to parse the "+configurationXMLFile.getName()+" configuration file.",e);
        }
        return null;
    }

    /**
     * builds configuration object using the TableElement.
     * 
     * @param tableElement
     * @param configFileElement
     */
    private void buildConfigurationTemplate(ConfigurationTable tableElement,
                                            Element configFileElement){

        List<?> children = configFileElement.getChildren();
        for (int i = 0; i < children.size(); i++) {
            
            Object object = children.get(i);
            if (object instanceof Element) {
                Element configElement = (Element) object;
                if (configElement.getName().equals("include")){

                    ConfigurationIncludeTable includeElement = new ConfigurationIncludeTable();
                    includeElement.setLabel(tableElement.getLabel());
                    includeElement.setTableName(configElement.getAttributeValue("name"));

                    tableElement.getIncludeElementList().add(includeElement);
                    buildConfigurationTemplate(includeElement, configElement);
                } else if (configElement.getName().equals("references")){
                    buildConfigurationTemplateReferencesElement(tableElement, configElement);
                } else
                    throw new IllegalArgumentException("The element in the configuration file does not exist or is not a valid element compared to its parent element. ("+configElement.getName()+")");
            } else {
                throw new IllegalArgumentException("A none element was supplied in the configuration file. ("+object+")");
            }
        }
    }
    
    /**
     * This is a recursive call the goes through the configuration 
     * and builds the include elements into a java representation.  
     * This method also handles configuration validation
     * 
     * @param includeElement
     * @param configFileElement
     */
    private void buildConfigurationTemplate(ConfigurationIncludeTable includeElement,
                                            Element configFileElement) {

        List<?> children = configFileElement.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Object object = children.get(i);
            if (!(object instanceof Element)) {
                throw new IllegalArgumentException("A none element was supplied in the configuration file. ("+object+")");
            }

            Element configElement = (Element) object;
            if (configElement.getName().equals("include")){
                ConfigurationIncludeTable includedIncludeElement = new ConfigurationIncludeTable();
                includedIncludeElement.setLabel(includeElement.getLabel());
                includedIncludeElement.setTableName(configElement.getAttributeValue("name"));
                includeElement.getIncludeElementList().add(includedIncludeElement);

                buildConfigurationTemplate(includedIncludeElement, configElement);

            } else if (configElement.getName().equals("references")){
                ConfigurationTable configurationReferencesTable = new ConfigurationTable();
                configurationReferencesTable.setLabel(includeElement.getLabel());
                configurationReferencesTable.setTableName(includeElement.getTableName());
                includeElement.getReferencesTables().add(configurationReferencesTable);

                buildConfigurationTemplateReferencesElement(configurationReferencesTable, 
                                                            configElement);
            } else {
                throw new IllegalArgumentException("The element in the configuration file does not exist or is not a valid element compared to its parent element. ("+configElement.getName()+")");
            }
        }
    }

    /**
     * This is a recursive call the goes through the configuration 
     * and builds the references elements into a java representation.  
     * This method also handles configuration validation
     * 
     * @param includeElement
     * @param configFileElement
     */    
    private void buildConfigurationTemplateReferencesElement(ConfigurationTable configurationTable,
                                                            Element configFileElement) {

        List<?> children = configFileElement.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Object object = children.get(i);
            if (!(object instanceof Element)) {
                throw new IllegalArgumentException("A none element was supplied in the configuration file. ("+object+")");
            }

            Element configElement = (Element) object;
            if (configElement.getName().equals("table")){
                ConfigurationTable tableElement = new ConfigurationTable();
                tableElement.setLabel(configurationTable.getLabel());
                tableElement.setTableName(configElement.getAttributeValue("name"));
                configurationTable.getReferencesTables().add(tableElement);

                buildConfigurationTemplate(tableElement, configElement);
            } else {
                throw new IllegalArgumentException("The element in the configuration file does not exist or is not a valid element compared to its parent element. ("+configElement.getName()+")");
            }
        }
    }


    public DataTableTemplate buildDataTableTemplate(ConfigurationTable configFileTableElement){

        String tableName = configFileTableElement.getTableName();
        
        CountHolder countHolder = new CountHolder(1);
        DataTableTemplate baseDataTable = 
            new DataTableTemplate(ElementCategoryEnum.BASE, countHolder.getCount(), tableName);
        buildDataTableTemplate(baseDataTable, configFileTableElement, countHolder, null);
        
        return baseDataTable;
    }

    /**
     * This method will drill down into a table, creating all the links, until it 
     * reaches the finalTableInDrillDown.  If the finalTableInDrillDown is null
     * the method will travel all the way down the database tree until it no longer 
     * has a table references.
     *  
     */
    private void buildDataTableTemplate(DataTableTemplate dataTable, 
                                        ConfigurationTable configFileTableElement, 
                                        CountHolder countHolder,
                                        Table finalTableInDrillDown){
        Table table = database.getTable(dataTable.getTableName());
        List<Column> allColumns = table.getAllColumns();
        for (Column column : allColumns) {
            if (column.getTableRef() != null){

                // Checks to see if we have reached our desired reference.  If so we want to keep this entry blank.
                if (finalTableInDrillDown != null &&
                        column.getTableRef().equals(finalTableInDrillDown.getTableName())) {
                    continue;
                }

                // Checks to see if the column has a one to one relationship or if the item does not have a definied relationship
                // that symbolizes a foreign key from the primary key.  If so we want to create an inline item. 
                Table tableRefTable = database.getTable(column.getTableRef());
                if (column.getRefType() == null ||
                    column.getRefType().equals(ReferenceTypeEnum.ONE_TO_ONE)){
                    processInlineDataTableTemplate(dataTable, 
                                           configFileTableElement,
                                           countHolder, 
                                           column,
                                           tableRefTable,
                                           finalTableInDrillDown);

                // Checks to see if the column has a one to many relationship.  If is does we want to use an include or reference item.
                } else if (column.getRefType().equals(ReferenceTypeEnum.MANY_TO_ONE)){
                    // Checking to see if the table reference is in the include element list
                    boolean isTableInIncludes = false;
                    List<ConfigurationIncludeTable> configIncludeElementList = configFileTableElement.getIncludeElementList();
                    for (ConfigurationIncludeTable configIncludeElement : configIncludeElementList) {
                        if (configIncludeElement.getTableName().equals(tableRefTable.getTableName())) {
                            processIncludeDataTableTemplate(dataTable,
                                                    configIncludeElement,
                                                    countHolder,
                                                    column,
                                                    tableRefTable,
                                                    finalTableInDrillDown);
                            isTableInIncludes = true;
                            continue;
                        }
                    }

                    // The table reference does not exist in the include list; Using reference as default.
                    if(!isTableInIncludes){
                        processReferenceDataTable(dataTable,
                                                  configFileTableElement,
                                                  countHolder,
                                                  column,
                                                  tableRefTable,
                                                  finalTableInDrillDown);
                    }

                } else {
                    throw new IllegalArgumentException("Incorrect reference type found in the database definition XML file. ("+table.getTableName()+"."+column.getName()+" => "+column.getRefType()+")");
                }
            } else {
                DataValueTemplate dataValueTemplate = new DataValueTemplate();
                if (column.getNullId() != null) {
                    dataValueTemplate.setNullId(column.getNullId());
                }
                dataTable.putTableColumn(column.getName(), dataValueTemplate);
            }
        }

        // Check to see if any elements are in the references list
        List<ConfigurationTable> configurationTableList = configFileTableElement.getReferencesTables();
        for (ConfigurationTable configurationTable : configurationTableList) {
            Table referencesTable = database.getTable(configurationTable.getTableName());
            List<Column> referencesTableColumns = referencesTable.getAllColumns();
            for (Column referencesTableColumn : referencesTableColumns) {
                if (table.getTableName().equals(referencesTableColumn.getTableRef())){

                    processReferencesDataTableTemplate(dataTable,
                                               configurationTable,
                                               table,
                                               countHolder,
                                               referencesTable);
                    break;
                }
            }
        }
    }

    /**
     * This generates the inline information and then sends the next part of the 
     * configurationTable back into the recursive method
     * 
     * @param dataTable
     * @param configFileTableElement
     * @param countHolder
     * @param column
     * @param tableRefTable
     * @param finalTableInDrillDown
     */
    private void processInlineDataTableTemplate(DataTableTemplate dataTable,
                                                ConfigurationTable configFileTableElement,
                                                CountHolder countHolder, 
                                                Column column,
                                                Table tableRefTable,
                                                Table finalTableInDrillDown) {
        DataTableTemplate inlineTable = 
            new DataTableTemplate(null, 0, tableRefTable.getTableName());
        dataTable.putTableColumn(column.getName(), inlineTable);
        buildDataTableTemplate(inlineTable, 
                               configFileTableElement, 
                               countHolder,
                               finalTableInDrillDown);
    }

    /**
     * This generates the include information and then sends the next part of the 
     * configurationTable back into the recursive method
     * 
     * @param dataTable
     * @param configIncludeElement
     * @param countHolder
     * @param column
     * @param tableRefTable
     * @param finalTableInDrillDown
     */
    private void processIncludeDataTableTemplate(DataTableTemplate dataTable,
                                                 ConfigurationIncludeTable configIncludeElement,
                                                 CountHolder countHolder, 
                                                 Column column,
                                                 Table tableRefTable,
                                                 Table finalTableInDrillDown) {
        countHolder.add();
        DataTableTemplate includeTable = 
            new DataTableTemplate(ElementCategoryEnum.INCLUDE, 
                          countHolder.getCount(),
                          tableRefTable.getTableName());
        dataTable.putTableColumn(column.getName(), includeTable);
        
        buildDataTableTemplate(includeTable, 
                               configIncludeElement,
                               countHolder,
                               finalTableInDrillDown);
    }

    /**
     * This generates the reference information and then sends the next part of the 
     * configurationTable back into the recursive method
     * 
     * @param dataTable
     * @param configFileTableElement
     * @param countHolder
     * @param column
     * @param tableRefTable
     * @param finalTableInDrillDown
     */
    private void processReferenceDataTable(DataTableTemplate dataTable,
                                          ConfigurationTable configFileTableElement,
                                          CountHolder countHolder, 
                                          Column column,
                                          Table tableRefTable,
                                          Table finalTableInDrillDown) {
        countHolder.add();
        DataTableTemplate referenceTable = 
            new DataTableTemplate(ElementCategoryEnum.REFERENCE, countHolder.getCount(), tableRefTable.getTableName());
        dataTable.putTableColumn(column.getName(), referenceTable);
        buildDatabaseMapReferenceTemplate(referenceTable, countHolder);
    }
    
    /**
     * This generates the references information and then sends the next part of the 
     * configurationTable back into the recursive method
     * 
     * @param dataTable
     * @param configFileTableElement
     * @param countHolder
     * @param column
     * @param tableRefTable
     * @param finalTableInDrillDown
     */
    private void processReferencesDataTableTemplate(DataTableTemplate dataTable, 
                                                    ConfigurationTable configFileTableElement,
                                                    Table finalTableInDrillDown,
                                                    CountHolder countHolder,
                                                    Table referencesTable) {
        countHolder.add();
        DataTableTemplate referenceTable = 
            new DataTableTemplate(ElementCategoryEnum.REFERENCES, countHolder.getCount(), referencesTable.getTableName());
        dataTable.addTableReferences(referenceTable);
        buildDataTableTemplate(referenceTable, 
                               configFileTableElement,
                               countHolder,
                               finalTableInDrillDown);
    }

    /**
     * This method handles the recursion case of references.
     * 
     * @param referenceTable
     * @param countHolder
     */
    private void buildDatabaseMapReferenceTemplate(DataTableTemplate referenceTable,
                                                   CountHolder countHolder){
        Table table = database.getTable(referenceTable.getTableName());
        List<Column> identifierColumns = table.getColumns(ColumnTypeEnum.PRIMARY_KEY, ColumnTypeEnum.IDENTIFIER);
        for (Column identifierColumn : identifierColumns) {
            if (identifierColumn.getTableRef() != null){
                processReferenceReferenceDataTableTemplate(referenceTable,
                                                           identifierColumn,
                                                           countHolder);
            } else {
                referenceTable.putTableColumn(identifierColumn.getName(), new DataValueTemplate());
            }
        }
    }

    /**
     * This generates the Reference information and then sends the next part of the 
     * configurationTable back into the reference recursive method
     * 
     * @param referenceTable
     * @param identifierColumn
     * @param countHolder
     */
    private void processReferenceReferenceDataTableTemplate(DataTableTemplate referenceTable,
                                                            Column identifierColumn,
                                                            CountHolder countHolder) {
        countHolder.add();

        DataTableTemplate nextReferenceTable = 
            new DataTableTemplate(ElementCategoryEnum.REFERENCE, countHolder.getCount(), identifierColumn.getTableRef());
        referenceTable.putTableColumn(identifierColumn.getName(), nextReferenceTable);
        buildDatabaseMapReferenceTemplate(nextReferenceTable, 
                                          countHolder);
    }

    public void setDatabaseDefinitionXML(Resource databaseDefinitionXML){
        database = new Database(databaseDefinitionXML);
    }
    
}