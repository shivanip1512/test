package com.cannontech.common.databaseMigration.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.springframework.core.io.Resource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.databaseMigration.bean.data.DataTable;
import com.cannontech.common.databaseMigration.bean.data.DataTableEntity;
import com.cannontech.common.databaseMigration.bean.data.DataTableValue;
import com.cannontech.common.databaseMigration.bean.data.ElementCategoryEnum;
import com.cannontech.common.databaseMigration.bean.database.Column;
import com.cannontech.common.databaseMigration.bean.database.ColumnTypeEnum;
import com.cannontech.common.databaseMigration.bean.database.DatabaseDefinition;
import com.cannontech.common.databaseMigration.bean.database.TableDefinition;
import com.cannontech.common.databaseMigration.service.ExportXMLGeneratorService;

public class ExportXMLGeneratorServiceImpl implements ExportXMLGeneratorService{
 
    private static Logger log = YukonLogManager.getLogger(ExportXMLGeneratorServiceImpl.class);
    private DatabaseDefinition database;
    private boolean showPrimaryKeys = false;

    public Element buildXmlElement(Iterable<DataTable> data, String label) {
        // Create base data XML Element
        Element baseElement = new Element("data");
        
        // Add configurationElement with a name of the label supplied
        Element configurationElement = new Element("configuration");
        configurationElement.setAttribute("name", label);
        baseElement.addContent(configurationElement);
        
        buildXmlElement(data, configurationElement);
        
        return baseElement;
    }
    
    /**
     * This piece of the buildXmlElement generates the first item tag underneath
     * a configuration element or a references element.  The name field is the
     * name of the table the xml starts at.
     */
    private void buildXmlElement(Iterable<DataTable> data, Element element){
        for (DataTable dataTable : data) {
            log.debug("buildingXmlElement - " + dataTable.toString());
            String tableName = dataTable.getName();
			Element itemElement = new Element("item");
			itemElement.setAttribute("name", tableName);
            element.addContent(itemElement);

            buildXmlElement(itemElement, dataTable);
        }
    }
    
    /**
     * This is the heart of the recursion process for this method.  This piece iterates over itself
     * to build the tree of data columns into the corresponding xml element.
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
                TableDefinition table = database.getTable(dataTable.getName());
                List<Column> primaryKeyColumns = table.getColumns(ColumnTypeEnum.PRIMARY_KEY);
                
                // Checks to see if we should display primaryKeys in the XML file or not
                if (!showPrimaryKeys) { 
                    boolean skipPrimaryKey = false;
                    for (Column primaryKeyColumn : primaryKeyColumns) {
                        if (primaryKeyColumn.getName().equals(columnName) &&
                            primaryKeyColumn.getFilterValue() == null) {
    
                            skipPrimaryKey = true;
                            break;
                        }
                    }
                    if (skipPrimaryKey) {
                        continue;
                    }
                }
                
                DataTableValue dataTableValue = (DataTableValue) columnEntry.getValue();
				Element valueElement = new Element("value");
				valueElement.setAttribute("field", columnEntry.getKey());
                valueElement.addContent(dataTableValue.getValue());
                element.addContent(valueElement);

            } else if (columnEntry.getValue() instanceof DataTable) {
                DataTable columnDataTable = (DataTable) columnEntry.getValue();
                Element valueElement = new Element("reference");
                valueElement.setAttribute("field", columnName);

                // Checks to see if the elementCategory is null.  If this is the case
                // we want to inherit the type from the element above this element.
                if (columnDataTable.getElementCategory() == null) {
                    if(element.getName().equals(ElementCategoryEnum.REFERENCE.toString())) {
                        element.addContent(valueElement);
                        buildXmlElement(valueElement, columnDataTable);
                    } else {
                        Element itemElement = new Element("item");
						itemElement.setAttribute("field", columnName);
                        element.addContent(itemElement);
                        buildXmlElement(itemElement, columnDataTable);
                    }

                // Processes the Reference Element found in the dataTable
                } else if (columnDataTable.getElementCategory().equals(ElementCategoryEnum.REFERENCE)) {
                    element.addContent(valueElement);
                    buildXmlElement(valueElement, columnDataTable);
                    
                // Processes the Include Element found in the dataTable
                } else if (columnDataTable.getElementCategory().equals(ElementCategoryEnum.INCLUDE)) {
                    element.addContent(valueElement);
                    buildXmlElement(valueElement, columnDataTable);
                }
            }
        }
        
        // Processes the References Element found in the dataTable
        if (tableReferences.size() > 0 ){
            Element referencesElement = new Element("references");
            element.addContent(referencesElement);

            buildXmlElement(tableReferences, referencesElement);
        }        
    }

    public void setDatabaseDefinitionXML(Resource databaseDefinitionXML){
        database = new DatabaseDefinition(databaseDefinitionXML);
    }

}