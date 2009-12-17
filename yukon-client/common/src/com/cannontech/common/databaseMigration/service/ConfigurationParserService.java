package com.cannontech.common.databaseMigration.service;

import java.io.InputStream;

import org.springframework.core.io.Resource;

import com.cannontech.common.databaseMigration.bean.config.ConfigurationTable;
import com.cannontech.common.databaseMigration.bean.data.template.DataTableTemplate;

public interface ConfigurationParserService {

    /**
     * Generates a java representation of a configuration file.
     * 
     * @param configurationXMLFile
     * @return
     */
    public ConfigurationTable buildConfigurationTemplate(Resource configurationResource);

    /**
     * This method takes a configuration file and generate a DataTableTemplate.
     * This template holds the link representations generated from the configuration file
     * and fills in all the needed table level links from the databaseDefinition xml file.
     * 
     * @param configFileTableElement
     * @return
     */
    public DataTableTemplate buildDataTableTemplate(ConfigurationTable configFileTableElement);

    
    /**
     * This method is used to create a reference DataTableTemplate when using an include.
     * 
     * @param referenceTable
     */
    public void buildDatabaseMapReferenceTemplate(DataTableTemplate referenceTable);
                                                  
    
}