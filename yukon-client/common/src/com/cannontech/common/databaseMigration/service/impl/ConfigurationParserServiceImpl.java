package com.cannontech.common.databaseMigration.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.core.io.Resource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.databaseMigration.bean.config.ConfigurationIncludeTable;
import com.cannontech.common.databaseMigration.bean.config.ConfigurationTable;
import com.cannontech.common.databaseMigration.bean.data.ElementCategoryEnum;
import com.cannontech.common.databaseMigration.bean.data.template.DataTableTemplate;
import com.cannontech.common.databaseMigration.bean.data.template.DataValueTemplate;
import com.cannontech.common.databaseMigration.bean.database.Column;
import com.cannontech.common.databaseMigration.bean.database.ColumnTypeEnum;
import com.cannontech.common.databaseMigration.bean.database.DatabaseDefinition;
import com.cannontech.common.databaseMigration.bean.database.ReferenceTypeEnum;
import com.cannontech.common.databaseMigration.bean.database.TableDefinition;
import com.cannontech.common.databaseMigration.service.ConfigurationParserService;

public class ConfigurationParserServiceImpl implements ConfigurationParserService{

    private static Logger log = YukonLogManager.getLogger(ConfigurationParserServiceImpl.class);
    private DatabaseDefinition database;
    
    public ConfigurationTable buildConfigurationTemplate(Resource configurationResource) {
        SAXBuilder saxBuilder = new SAXBuilder();
        
        try {
            // Convert all the xml files to a document format to make them easier to use.
            Document configXMLDocument = saxBuilder.build(configurationResource.getInputStream());
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
            log.error("An parsing error occured while parsing the " + configurationResource + " configuration file.",e);
        } catch (IOException e) {
            log.error("An issue occured when trying to parse the " + configurationResource + " configuration file.",e);
        }
        return null;
    }

    /**
     * builds configuration object using the TableElement.
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
                    includeElement.setIncludeReferenceColumnName(configElement.getAttributeValue("field"));

                    Element child = configElement.getChild("table");
                    if (child == null ||
                        child.getAttributeValue("name") == null) {
                        throw new IllegalArgumentException("The table element was missing under an include element or the name attribute was not supplied");
                    }

                    String tableName = child.getAttributeValue("name");
                    includeElement.setTableName(tableName);
                    
                    tableElement.getIncludeElementList().add(includeElement);
                    buildConfigurationTemplate(includeElement, child);
                } else if (configElement.getName().equals("references")){
                    buildConfigurationTemplateReferencesElement(tableElement, configElement);
                } else
                    throw new IllegalArgumentException("The element in the configuration file does not exist or is not a valid element compared to its parent element. (" + configElement.getName() + ")");
            } else {
                throw new IllegalArgumentException("A none element was supplied in the configuration file. (" + object + ")");
            }
        }
    }
    
    /**
     * This is a recursive call the goes through the configuration 
     * and builds the include elements into a java representation.  
     * This method also handles configuration validation
     */
    private void buildConfigurationTemplate(ConfigurationIncludeTable includeElement,
                                            Element configFileElement) {

        List<?> children = configFileElement.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Object object = children.get(i);
            if (!(object instanceof Element)) {
                throw new IllegalArgumentException("A none element was supplied in the configuration file. (" + object + ")");
            }

            Element configElement = (Element) object;
            if (configElement.getName().equals("include")){
                ConfigurationIncludeTable includedIncludeElement = new ConfigurationIncludeTable();
                includedIncludeElement.setLabel(includeElement.getLabel());
                includedIncludeElement.setIncludeReferenceColumnName(configElement.getAttributeValue("field"));

                Element child = configElement.getChild("table");
                if (child == null ||
                    child.getAttributeValue("name") == null) {
                    throw new IllegalArgumentException("The table element was missing under an include element or the name attribute was not supplied");
                }

                String tableName = child.getAttributeValue("name");
                includedIncludeElement.setTableName(tableName);
                includeElement.getIncludeElementList().add(includedIncludeElement);

                buildConfigurationTemplate(includedIncludeElement, child);

            } else if (configElement.getName().equals("references")){
                buildConfigurationTemplateReferencesElement(includeElement, configElement);
            } else {
                throw new IllegalArgumentException("The element in the configuration file does not exist or is not a valid element compared to its parent element. (" + configElement.getName() + ")");
            }
        }
    }

    /**
     * This is a recursive call the goes through the configuration 
     * and builds the references elements into a java representation.  
     * This method also handles configuration validation
     */    
    private void buildConfigurationTemplateReferencesElement(ConfigurationTable configurationTable,
                                                            Element configFileElement) {

        List<?> children = configFileElement.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Object object = children.get(i);
            if (!(object instanceof Element)) {
                throw new IllegalArgumentException("A none element was supplied in the configuration file. (" + object + ")");
            }

            Element configElement = (Element) object;
            if (configElement.getName().equals("table")){
                ConfigurationTable tableElement = new ConfigurationTable();
                tableElement.setLabel(configurationTable.getLabel());
                tableElement.setTableName(configElement.getAttributeValue("name"));
                configurationTable.getReferencesTables().add(tableElement);

                buildConfigurationTemplate(tableElement, configElement);
            } else {
                throw new IllegalArgumentException("The element in the configuration file does not exist or is not a valid element compared to its parent element. (" + configElement.getName() + ")");
            }
        }
    }

    public DataTableTemplate buildDataTableTemplate(ConfigurationTable configFileTableElement){

        TableDefinition tableDef = database.getTable(configFileTableElement.getTableName());
        
        DataTableTemplate baseDataTable = 
            new DataTableTemplate(ElementCategoryEnum.BASE, 
            					  tableDef.getName(), 
            					  tableDef.getTable());
        buildDataTableTemplate(baseDataTable, configFileTableElement, null);
        
        return baseDataTable;
    }

    /**
     * This method will drill down into a table, creating all the links, until it 
     * reaches the finalTableInDrillDown.  If the finalTableInDrillDown is null
     * the method will travel all the way down the database tree until it no longer 
     * has a table references.
     */
    private void buildDataTableTemplate(DataTableTemplate dataTable, 
                                        ConfigurationTable configFileTableElement, 
                                        TableDefinition finalTableInDrillDown){
        TableDefinition table = database.getTable(dataTable.getName());
        List<Column> allColumns = table.getAllColumns();
        for (Column column : allColumns) {
            if (column.getTableRef() != null){

                // Checks to see if we have reached our desired reference.  If so we want to 
            	// keep this entry blank.
                if (finalTableInDrillDown != null &&
                    column.getTableRef().equals(finalTableInDrillDown.getName())) {
                    continue;
                }

                // Checks to see if the column has a one to one relationship or if the item does 
                // not have a defined relationship that symbolizes a foreign key from the primary 
                // key.  If so we want to create an inline item. 
                TableDefinition tableRefTable = database.getTable(column.getTableRef());
                if (column.getRefType() == null ||
                    column.getRefType().equals(ReferenceTypeEnum.ONE_TO_ONE)){
                    processInlineDataTableTemplate(dataTable, 
                                                   configFileTableElement,
                                                   column,
                                                   tableRefTable,
                                                   finalTableInDrillDown);

                // Checks to see if the column has a one to many relationship.  If is does we want 
                // to use an include or reference item.
                } else if (column.getRefType().equals(ReferenceTypeEnum.MANY_TO_ONE)){
                    // Checking to see if the table reference is in the include element list
                    boolean isTableInIncludes = false;
                    List<ConfigurationIncludeTable> configIncludeElementList = 
                    	configFileTableElement.getIncludeElementList();
                    for (ConfigurationIncludeTable configIncludeElement : configIncludeElementList) {
                        if (column.getName().equals(configIncludeElement.getIncludeReferenceColumnName())) {
                            processIncludeDataTableTemplate(dataTable,
                                                            configIncludeElement,
                                                            column,
                                                            tableRefTable,
                                                            null);
                            isTableInIncludes = true;
                            continue;
                        }
                    }

                    // The table reference does not exist in the include list; Using reference 
                    // as default.
                    if(!isTableInIncludes){
                        processReferenceDataTable(dataTable,
                                                  configFileTableElement,
                                                  column,
                                                  tableRefTable,
                                                  finalTableInDrillDown);
                    }

                } else {
                    throw new IllegalArgumentException("Incorrect reference type found in the database" +
                    		" definition XML file. (" + table.getName() + "." + column.getName() + 
                    		" => " + column.getRefType() + ")");
                }
            } else {
                DataValueTemplate dataValueTemplate = new DataValueTemplate();
                dataValueTemplate.setNullId(column.getNullId());
                dataValueTemplate.setFilterValue(column.getFilterValue());
                dataTable.putTableColumn(column.getName(), dataValueTemplate);
            }
        }

        // Check to see if any elements are in the references list
        List<ConfigurationTable> configurationTableList = configFileTableElement.getReferencesTables();
        for (ConfigurationTable configurationTable : configurationTableList) {
            TableDefinition referencesTable = database.getTable(configurationTable.getTableName());
            List<Column> referencesTableColumns = referencesTable.getAllColumns();
            for (Column referencesTableColumn : referencesTableColumns) {
                if (table.getName().equals(referencesTableColumn.getTableRef())){

                    processReferencesDataTableTemplate(dataTable,
                                               configurationTable,
                                               table,
                                               referencesTable);
                    break;
                }
            }
        }
    }

    /**
     * This generates the inline information and then sends the next part of the 
     * configurationTable back into the recursive method
     */
    private void processInlineDataTableTemplate(DataTableTemplate dataTable,
                                                ConfigurationTable configFileTableElement,
                                                Column column,
                                                TableDefinition tableRefTable,
                                                TableDefinition finalTableInDrillDown) {
        DataTableTemplate inlineTable = 
            new DataTableTemplate(null, tableRefTable.getName(), tableRefTable.getTable());
        dataTable.putTableColumn(column.getName(), inlineTable);
        buildDataTableTemplate(inlineTable, 
                               configFileTableElement, 
                               finalTableInDrillDown);
    }

    /**
     * This generates the include information and then sends the next part of the 
     * configurationTable back into the recursive method
     */
    private void processIncludeDataTableTemplate(DataTableTemplate dataTable,
                                                 ConfigurationIncludeTable configIncludeElement,
                                                 Column column,
                                                 TableDefinition tableRefTable,
                                                 TableDefinition finalTableInDrillDown) {
        DataTableTemplate includeTable = 
            new DataTableTemplate(ElementCategoryEnum.INCLUDE, 
                                  tableRefTable.getName(),
                                  tableRefTable.getTable());
        dataTable.putTableColumn(column.getName(), includeTable);
        
        buildDataTableTemplate(includeTable, 
                               configIncludeElement,
                               finalTableInDrillDown);
    }

    /**
     * This generates the reference information and then sends the next part of the 
     * configurationTable back into the recursive method
     */
    private void processReferenceDataTable(DataTableTemplate dataTable,
                                          ConfigurationTable configFileTableElement,
                                          Column column,
                                          TableDefinition tableRefTable,
                                          TableDefinition finalTableInDrillDown) {
        DataTableTemplate referenceTable = 
            new DataTableTemplate(ElementCategoryEnum.REFERENCE, 
            					  tableRefTable.getName(), 
            					  tableRefTable.getTable());
        dataTable.putTableColumn(column.getName(), referenceTable);
        buildDatabaseMapReferenceTemplate(referenceTable);
    }
    
    /**
     * This generates the references information and then sends the next part of the 
     * configurationTable back into the recursive method
     */
    private void processReferencesDataTableTemplate(DataTableTemplate dataTable, 
                                                    ConfigurationTable configFileTableElement,
                                                    TableDefinition finalTableInDrillDown,
                                                    TableDefinition referencesTable) {
        DataTableTemplate referenceTable = 
            new DataTableTemplate(ElementCategoryEnum.REFERENCES, 
            					  referencesTable.getName(), 
            					  referencesTable.getTable());
        dataTable.addTableReferences(referenceTable);
        buildDataTableTemplate(referenceTable, 
                               configFileTableElement,
                               finalTableInDrillDown);
    }

    /**
     * This method handles the recursion case of references.
     */
    public void buildDatabaseMapReferenceTemplate(DataTableTemplate referenceTable){
        TableDefinition table = database.getTable(referenceTable.getName());
        List<Column> identifierColumns = 
        	table.getColumns(ColumnTypeEnum.PRIMARY_KEY, ColumnTypeEnum.IDENTIFIER);
        for (Column identifierColumn : identifierColumns) {
            if (identifierColumn.getTableRef() != null){
                processReferenceReferenceDataTableTemplate(referenceTable, identifierColumn);
            } else {
                referenceTable.putTableColumn(identifierColumn.getName(), new DataValueTemplate());
            }
        }
    }

    /**
     * This generates the Reference information and then sends the next part of the 
     * configurationTable back into the reference recursive method
     */
    private void processReferenceReferenceDataTableTemplate(DataTableTemplate referenceTable,
                                                            Column identifierColumn) {

        TableDefinition referenceTableDef = database.getTable(identifierColumn.getTableRef());
        DataTableTemplate nextReferenceTable = 
            new DataTableTemplate(ElementCategoryEnum.REFERENCE, 
            					  referenceTableDef.getName(), 
            					  referenceTableDef.getTable());
        referenceTable.putTableColumn(identifierColumn.getName(), nextReferenceTable);
        buildDatabaseMapReferenceTemplate(nextReferenceTable);
    }

    public void setDatabaseDefinitionXML(Resource databaseDefinitionXML){
        database = new DatabaseDefinition(databaseDefinitionXML);
    }
}
