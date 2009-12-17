package com.cannontech.common.databaseMigration.bean.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.core.io.Resource;

public class DatabaseDefinition {
    // Table name to def
    Map<String, TableDefinition> databaseMap = new TreeMap<String, TableDefinition>();
    
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
                String tableName = tableElmenet.getAttributeValue("name");
                TableDefinition table = new TableDefinition(tableName, tableElmenet);
                this.databaseMap.put(tableName, table);
            }
        
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    public Map<String, TableDefinition> getDatabaseMap() {
        return databaseMap;
    }
    public void addTable(TableDefinition table) {
        this.databaseMap.put(table.getTableName(), table);
    }

    public TableDefinition getTable(String dbTableName) {
        return this.databaseMap.get(dbTableName);
    }

    public List<String> getConnectedTables(TableDefinition table) {
        List<String> connectedTables = new ArrayList<String>();
        if(table != null){
            connectedTables.add(table.getTableName());
            
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
