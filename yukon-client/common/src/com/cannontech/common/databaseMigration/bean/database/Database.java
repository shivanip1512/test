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

public class Database{
    Map<String, Table> databaseMap = new TreeMap<String, Table>();
    
    @SuppressWarnings("unchecked")
    public Database(Resource databaseDefinitionXML){
        // Setup JDOM Element
        SAXBuilder saxBuilder = new SAXBuilder();
        Document databaseDefinitionXMLDocument;

        try {
            databaseDefinitionXMLDocument = saxBuilder.build(databaseDefinitionXML.getFile());
            Element databaseRoot = databaseDefinitionXMLDocument.getRootElement();

            // Build Database from JDOM Object
            List<Element> tables = databaseRoot.getChildren();
            for (Element tableElmenet : tables) {
                String tableName = tableElmenet.getAttributeValue("name");
                Table table = new Table(tableName, tableElmenet);
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
    
    
    public Map<String, Table> getDatabaseMap() {
        return databaseMap;
    }
    public void addTable(Table table) {
        this.databaseMap.put(table.getTableName(), table);
    }

    public Table getTable(String dbTableName) {
        return this.databaseMap.get(dbTableName);
    }

    public List<String> getConnectedTables(Table table) {
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
