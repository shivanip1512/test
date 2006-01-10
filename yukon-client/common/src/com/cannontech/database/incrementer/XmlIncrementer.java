package com.cannontech.database.incrementer;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class XmlIncrementer implements KeyedIncrementer {
    private Map<String,TableData> sequenceLookup = new TreeMap<String, TableData>();
    private URL configFile;
    private DataSource dataSource;
    
    protected void addTable(String sequence, String tableName, 
            String primaryKeyName, KeyedIncrementer currentIncrementer) {
        TableData tableData = new TableData();
        tableData.primaryKeyName = primaryKeyName;
        tableData.tableName = tableName;
        tableData.sequenceName = sequence;
        tableData.incrementer = currentIncrementer;
        
        sequenceLookup.put(tableName, tableData);
    }
    
    public String lookupSequenceKey(String tableName) {
        TableData tableData = getTableData(tableName);
        return tableData.sequenceName;
    }

    protected TableData getTableData(String tableName) {
        TableData tableData = sequenceLookup.get(tableName);
        if (tableData == null) {
            throw new IllegalArgumentException("Unable to lookup sequence. " +
                    "Table '" + tableName + "' does not exist.");
        }
        return tableData;
    }
    
    public String lookupPrimaryKey(String tableName) {
        TableData tableData = getTableData(tableName);
        return tableData.primaryKeyName;
    }
    
    public URL getConfigFile() {
        return configFile;
    }
    public void setConfigFile(URL configFile) {
        this.configFile = configFile;
    }
    
    public void parse() {
        try {
            
            SAXBuilder builder = new SAXBuilder();
            Document configDoc = builder.build(configFile);
            Element sequenceTableRoot = configDoc.getRootElement();
            final List sequenceTables = sequenceTableRoot.getChildren("sequencetable");
            for (Iterator iter = sequenceTables.iterator(); iter.hasNext();) {
                Element sequenceTable = (Element) iter.next();
                String sequenceTableName = sequenceTable.getAttributeValue("name");
                if (StringUtils.isBlank(sequenceTableName)) {
                    throw new RuntimeException("name attribute must be set on sequencetable element");
                }
                String valueColumnName = sequenceTable.getAttributeValue("valuecolumn");
                if (StringUtils.isBlank(valueColumnName)) {
                    throw new RuntimeException("valuecolumn attribute must be set on sequencetable element");
                }
                String keyColumnName = sequenceTable.getAttributeValue("keycolumn");
                if (StringUtils.isBlank(keyColumnName)) {
                    throw new RuntimeException("keycolumn attribute must be set on sequencetable element");
                }
                MultiTableIncrementer currentIncrementer = new MultiTableIncrementer(dataSource);
                currentIncrementer.setSequenceTableName(sequenceTableName);
                currentIncrementer.setValueColumnName(valueColumnName);
                currentIncrementer.setKeyColumnName(keyColumnName);
                
                final List sequences = sequenceTable.getChildren("sequence");
                for (Iterator iter2 = sequences.iterator(); iter2.hasNext();) {
                    Element sequence = (Element) iter2.next();
                    String sequenceName = sequence.getAttributeValue("name");
                    List tables = sequence.getChildren("table");
                    for (Iterator iter3 = tables.iterator(); iter3.hasNext();) {
                        Element table = (Element) iter3.next();
                        String tableName = table.getAttributeValue("name");
                        String identityColumn = table.getAttributeValue("identitycolumn");
                        addTable(sequenceName, tableName, identityColumn, currentIncrementer);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to read sequence config file " + configFile, e);
        }
    }

    
    public class TableData {
        public String tableName;
        public String primaryKeyName;
        public String sequenceName;
        public KeyedIncrementer incrementer;
    }


    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int getNextValue(String tableName) {
        TableData tableData = getTableData(tableName);
        String key = tableData.sequenceName;
        return tableData.incrementer.getNextValue(key);
    }

}
