package com.cannontech.common.databaseMigration.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.springframework.core.io.Resource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.databaseMigration.bean.data.DataTable;
import com.cannontech.common.databaseMigration.bean.data.DataTableEntity;
import com.cannontech.common.databaseMigration.bean.data.DataTableValue;
import com.cannontech.common.databaseMigration.bean.data.ElementCategoryEnum;
import com.cannontech.common.databaseMigration.bean.database.ColumnTypeEnum;
import com.cannontech.common.databaseMigration.bean.database.Database;
import com.cannontech.common.databaseMigration.bean.database.Table;
import com.cannontech.common.databaseMigration.service.ExportXMLGeneratorService;

public class ExportXMLGeneratorServiceImpl implements ExportXMLGeneratorService{
 
    private static Logger log = YukonLogManager.getLogger(ExportXMLGeneratorServiceImpl.class);
    private Database database;
    private boolean showPrimaryKeys = false;

    public Element buildXmlElement(Iterable<DataTable> data, String label) {
        // Create base data XML Element
        Element baseElement = new Element("data");
        
        // Add configurationElement with a name of the label supplied
        Element configurationElement = generateConfigurationElement(label);
        baseElement.addContent(configurationElement);
        
        buildXmlElement(data, configurationElement);
        
        return baseElement;
    }
    
    /**
     * This piece of the buildXmlElement generates the first item tag underneath
     * a configuration element or a references element.  The name field is the
     * name of the table the xml starts at.
     * 
     * @param data
     * @param element
     */
    private void buildXmlElement(Iterable<DataTable> data, Element element){
        for (DataTable dataTable : data) {
            log.debug("buildingXmlElement - "+dataTable.toString());
            String tableName = dataTable.getTableName();
            Element itemElement = generateItemElementName(tableName);
            element.addContent(itemElement);

            buildXmlElement(itemElement, dataTable);
        }
    }
    
    /**
     * This is the heart of the recursion process for this method.  This piece iterates over itself
     * to build the tree of data columns into the corresponding xml element.
     * 
     * @param element
     * @param dataTable
     */
    private void buildXmlElement(Element element,
                              DataTable dataTable) {
        Map<String, DataTableEntity> tableColumns = dataTable.getTableColumns();
        List<DataTable> tableReferences = dataTable.getTableReferences();
        
        // The is a special case that checks for a null reference.
        // This is symbolized in the databaseDefinition.xml as a nullId attribute.
        if (tableColumns.size() == 0){
            if (!element.getName().equals("item") ||
                 element.getAttributeValue("name") == null) {

                element.setName("reference");
                return;
            }
        }

        for (Entry<String, DataTableEntity> columnEntry: tableColumns.entrySet()) {
            String columnName = columnEntry.getKey();
            
            // Checks to see if the table is just a value 
            // or if it ties to another table of information
            if (columnEntry.getValue() instanceof DataTableValue) {
                Table table = database.getTable(dataTable.getTableName());
                
                // Checks to see if we should display primaryKeys in the XML file or not
                List<String> primaryKeyColumnNames = Table.getColumnNames(table.getColumns(ColumnTypeEnum.PRIMARY_KEY));
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

                // Checks to see if the elementCategory is null.  If this is the case
                // we want to inherit the type from the element above this element.
                if (columnDataTable.getElementCategory() == null) {
                    if(element.getName().equals(ElementCategoryEnum.REFERENCE.toString())) {
                        Element referenceElement = generateReferenceElement(columnName);
                        element.addContent(referenceElement);
                        
                        buildXmlElement(referenceElement, columnDataTable);
                    } else {
                        Element itemElement = generateItemElementField(columnName);
                        element.addContent(itemElement);
                        
                        buildXmlElement(itemElement, columnDataTable);
                    }

                // Processes the Reference Element found in the dataTable
                } else if (columnDataTable.getElementCategory().equals(ElementCategoryEnum.REFERENCE)) {
                    Element referenceElement = generateReferenceElement(columnName);
                    element.addContent(referenceElement);
                    
                    buildXmlElement(referenceElement, columnDataTable);
                    
                // Processes the Include Element found in the dataTable
                } else if (columnDataTable.getElementCategory().equals(ElementCategoryEnum.INCLUDE)) {
                    Element referenceElement = generateReferenceElement(columnName);
                    element.addContent(referenceElement);
                    
                    buildXmlElement(referenceElement, columnDataTable);
                }
            }
        }
        
        // Prosses the References Element found in the dataTable
        if (tableReferences.size() > 0 ){
            Element referencesElement = generateReferencesElement();
            element.addContent(referencesElement);

            buildXmlElement(tableReferences, referencesElement);
        }        
    }

    /**
     * Builds a references element (<references>)
     * 
     * @param nameField
     * @return
     */
    private Element generateReferencesElement(){
        return new Element("references");
    }
    
    /**
     * Builds a reference element (<reference field="${referenceFieldName}">)
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
     * Builds a value element (<value field="${fieldValue}">)
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
     * Builds an item element with a field attribute (<item field="${fieldValue}">)
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
     * Builds an item element with a name attribute (<item name="${nameField}">)
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