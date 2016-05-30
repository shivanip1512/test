package com.cannontech.common.databaseMigration.bean.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.core.io.Resource;

import com.cannontech.clientutils.CTILogger;

public class DatabaseDefinition {
    // Table name to def
    private Map<String, TableDefinition> databaseMap = new TreeMap<String, TableDefinition>();
    
    @SuppressWarnings("unchecked")
    public DatabaseDefinition(Resource databaseDefinitionXML){
        // Setup JDOM Element
        SAXBuilder saxBuilder = new SAXBuilder();
        Document databaseDefinitionXMLDocument;

        try {
            databaseDefinitionXMLDocument = saxBuilder.build(databaseDefinitionXML.getInputStream());
            Element databaseRoot = databaseDefinitionXMLDocument.getRootElement();

            // Build Database from JDOM Object
            List<Element> tables = databaseRoot.getChildren();
            for (Element tableElmenet : tables) {
                String name = tableElmenet.getAttributeValue("name");

                // If the table value is null then we just use the name
                String table = tableElmenet.getAttributeValue("table");
                if (table == null) {
                    table = name;
                }

                TableDefinition tableDef = new TableDefinition(name, table, tableElmenet);
                this.databaseMap.put(name, tableDef);
            }
        
        } catch (JDOMException e) {
            CTILogger.error("Error parsing database definition file", e);
        } catch (IOException e) {
        	CTILogger.error("Error processing database definition file", e);
        }
    }
    
    
    public Map<String, TableDefinition> getDatabaseMap() {
        return databaseMap;
    }
    public void addTable(TableDefinition table) {
        this.databaseMap.put(table.getName(), table);
    }

    public TableDefinition getTable(String dbTableName) {
        return this.databaseMap.get(dbTableName);
    }

    public List<String> getConnectedTables(TableDefinition table) {
        List<String> connectedTables = new ArrayList<String>();
        if(table != null){
            connectedTables.add(table.getName());
            
            List<Column> allColumns = table.getAllColumns();
            for (Column column : allColumns) {
                if (column.getTableRef() != null){
                    connectedTables.addAll(getConnectedTables(getTable(column.getTableRef())));
                }
            }
        }        
        return connectedTables;
    }
}
