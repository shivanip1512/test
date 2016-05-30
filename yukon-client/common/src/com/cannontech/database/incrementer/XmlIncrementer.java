package com.cannontech.database.incrementer;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.exception.BadConfigurationException;

public class XmlIncrementer implements KeyedIncrementer {
    private Map<String,MultiTableIncrementer> sequenceLookup = 
        new TreeMap<String, MultiTableIncrementer>();
    private URL configFile;
    private DataSource dataSource;
    private RuntimeException initializationException = null;
    
    
    public URL getConfigFile() {
        return configFile;
    }
    public void setConfigFile(URL configFile) {
        this.configFile = configFile;
    }
    
    @PostConstruct
    public void parse() {
        try {
            
            SAXBuilder builder = new SAXBuilder();
            Document configDoc = builder.build(configFile);
            Element sequenceTableRoot = configDoc.getRootElement();
            final List sequenceTables = sequenceTableRoot.getChildren("sequencetable");
            for (Iterator iter = sequenceTables.iterator(); iter.hasNext();) {
                Element sequenceTable = (Element) iter.next();
                // foreach sequence table
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
                
                final List sequences = sequenceTable.getChildren("sequence");
                for (Iterator iter2 = sequences.iterator(); iter2.hasNext();) {
                    Element sequence = (Element) iter2.next();
                    // foreach sequence
                    String sequenceName = sequence.getAttributeValue("name");
                    
                    MultiTableIncrementer currentIncrementer = new CachingTableIncrementer(dataSource);
                    currentIncrementer.setSequenceTableName(sequenceTableName);
                    currentIncrementer.setValueColumnName(valueColumnName);
                    currentIncrementer.setKeyColumnName(keyColumnName);
                    currentIncrementer.setSequenceKey(sequenceName);

                    
                    List tables = sequence.getChildren("table");
                    for (Iterator iter3 = tables.iterator(); iter3.hasNext();) {
                        Element table = (Element) iter3.next();
                        // foreach table
                        String tableName = table.getAttributeValue("name");
                        if (StringUtils.isBlank(tableName)) {
                            throw new RuntimeException("name attribute must be set on all table elements");
                        }
                        String identityColumn = table.getAttributeValue("identitycolumn");
                        if (StringUtils.isBlank(identityColumn)) {
                            throw new RuntimeException("identitycolumn attribute must be set on table element: " + tableName);
                        }
                        boolean init = BooleanUtils.toBoolean(table.getAttributeValue("initsequence"));
                        if (init) {
                            currentIncrementer.initializeSequence(tableName, identityColumn);
                        }
                        addIncrementerForTable(tableName, currentIncrementer);
                    }
                }
            }
        } catch (Exception e) {
            initializationException =  
                new BadConfigurationException("Delayed exception: " +
                                              "XMLIncrementer threw an exception durring startup", 
                                              e);
            CTILogger.error("Could not parse XMLIncrementer configuration. The following " +
                            "error will be thrown when it is first accessed.", e);
        }
    }

    
    private void addIncrementerForTable(String tableName, MultiTableIncrementer currentIncrementer) {
        sequenceLookup.put(tableName.toLowerCase(), currentIncrementer);
    }
    
    private MultiTableIncrementer getIncrementerForTable(String tableName) {
        String lcTableName = tableName.toLowerCase();
        if (!sequenceLookup.containsKey(lcTableName)) {
            throw new IllegalArgumentException("Table '" + tableName + "' is not contained in " + configFile);
        }
        return sequenceLookup.get(lcTableName);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int getNextValue(String tableName) {
        if (initializationException != null) {
            throw initializationException;
        }
        MultiTableIncrementer incrementer = getIncrementerForTable(tableName);
        return incrementer.getNextValue(tableName);
    }
    

}
