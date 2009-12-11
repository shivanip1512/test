package com.cannontech.common.databaseMigration.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jdom.Element;
import org.springframework.core.io.Resource;

import com.cannontech.common.databaseMigration.bean.data.DataTable;
import com.cannontech.common.databaseMigration.bean.data.DataTableEntity;
import com.cannontech.common.databaseMigration.bean.data.DataTableValue;
import com.cannontech.common.databaseMigration.bean.data.ElementCategoryEnum;
import com.cannontech.common.databaseMigration.bean.database.ColumnTypeEnum;
import com.cannontech.common.databaseMigration.bean.database.Database;
import com.cannontech.common.databaseMigration.bean.database.Table;
import com.cannontech.common.databaseMigration.service.ExportXMLGeneratorService;

public class ExportXMLGeneratorServiceImpl implements ExportXMLGeneratorService{
 
//    private static Logger log = YukonLogManager.getLogger(ConfigurationProcessorServiceImpl.class);
    private Database database;
    private boolean showPrimaryKeys = false;

    // generates an XML file from the populated dataTable
    public Element buildXMLFile(Iterable<DataTable> data, String label) {
        // Create base XML Element
        Element baseElement = new Element("data");
        
        // Add configurationElement
        Element configurationElement = generateConfigurationElement(label);
        baseElement.addContent(configurationElement);
        
        buildXMLFile(data, configurationElement);
        
        return baseElement;
    }
    
    private void buildXMLFile(Iterable<DataTable> data, Element element){
        for (DataTable dataTable : data) {
            String tableName = dataTable.getTableName();
            Element itemElement = generateItemElementName(tableName);
            element.addContent(itemElement);

            buildXMLFile(itemElement, dataTable);
        }
    }
    
    private void buildXMLFile(Element element,
                              DataTable dataTable) {
        Map<String, DataTableEntity> tableColumns = dataTable.getTableColumns();
        List<DataTable> tableReferences = dataTable.getTableReferences();
        if (tableColumns.size() == 0){
            if(element.getParent() instanceof Element){
                Element parentElement = (Element)element.getParent();
                parentElement.setName("reference");
                return;
            }
        }

        for (Entry<String, DataTableEntity> columnEntry: tableColumns.entrySet()) {
            String columnName = columnEntry.getKey();
            if (columnEntry.getValue() instanceof DataTableValue) {
                Table table = database.getTable(dataTable.getTableName());
                
                // Checks to see if we should display primaryKeys in the XML file or not
                List<String> primaryKeyColumnNames = Table.getColumnNames(table.getColumns(ColumnTypeEnum.primaryKey));
                if (!showPrimaryKeys &&
                    primaryKeyColumnNames.contains(columnName)){
                    continue;
                }

                
                DataTableValue dataTableValue = (DataTableValue) columnEntry.getValue();
                Element valueElement = generateValueElement(columnEntry.getKey());
                valueElement.addContent(dataTableValue.getValue());
                element.addContent(valueElement);

            } else if (columnEntry.getValue() instanceof DataTable) {
                DataTable columnDataTable = (DataTable) columnEntry.getValue();

                if (columnDataTable.getElementCategory() == null) {
                    if(element.getName().equals(ElementCategoryEnum.reference.toString())) {
                        Element referenceElement = generateReferenceElement(columnName);
                        element.addContent(referenceElement);
                        
                        buildXMLFile(referenceElement, columnDataTable);
                    } else {
                        Element itemElement = generateItemElementField(columnName);
                        element.addContent(itemElement);
                        
                        buildXMLFile(itemElement, columnDataTable);
                    }

                } else if (columnDataTable.getElementCategory().equals(ElementCategoryEnum.reference)) {
                    Element referenceElement = generateReferenceElement(columnName);
                    element.addContent(referenceElement);
                    
                    buildXMLFile(referenceElement, columnDataTable);
                    
                } else if (columnDataTable.getElementCategory().equals(ElementCategoryEnum.include)) {
                    Element referenceElement = generateReferenceElement(columnName);
                    element.addContent(referenceElement);
                    
                    buildXMLFile(referenceElement, columnDataTable);
                }
            }
        }
        
        if (tableReferences.size() > 0 ){
            Element referencesElement = generateReferencesElement();
            element.addContent(referencesElement);

            buildXMLFile(tableReferences, referencesElement);
        }        
    }

    /**
     * Builds a value element (<item name="${nameField}">)
     * 
     * @param nameField
     * @return
     */
    private Element generateReferencesElement(){
        return new Element("references");
    }
    
    /**
     * Builds a value element (<item name="${nameField}">)
     * 
     * @param nameField
     * @return
     */
    private Element generateReferenceElement(String referenceFieldName){
        Element valueElement = new Element("reference");
        valueElement.setAttribute("field", referenceFieldName);
        return valueElement;
    }
    
    /**
     * Builds a value element (<item name="${nameField}">)
     * 
     * @param nameField
     * @return
     */
    private Element generateValueElement(String fieldValue){
        Element valueElement = new Element("value");
        valueElement.setAttribute("field", fieldValue);
        return valueElement;
    }
    
    /**
     * Builds an item element (<item field="${nameField}">)
     * 
     * @param fieldValue
     * @return
     */
    private Element generateItemElementField(String fieldValue){
        Element itemElement = new Element("item");
        itemElement.setAttribute("field", fieldValue);
        return itemElement;
    }
    
    /**
     * Builds an item element (<item name="${nameField}">)
     * 
     * @param nameValue
     * @return
     */
    private Element generateItemElementName(String nameValue){
        Element itemElement = new Element("item");
        itemElement.setAttribute("name", nameValue);
        return itemElement;
    }
    
    /**
     * Builds a configuration element (<configuration name="${nameField}">)
     * 
     * @param nameField
     * @return
     */
    private Element generateConfigurationElement(String nameField){
        Element configurationElement = new Element("configuration");
        configurationElement.setAttribute("name", nameField);
        return configurationElement;
    }
    
    public void setDatabaseDefinitionXML(Resource databaseDefinitionXML){
        database = new Database(databaseDefinitionXML);
    }

}