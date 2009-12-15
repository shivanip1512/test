package com.cannontech.common.databaseMigration.service;

import java.io.File;

import com.cannontech.common.databaseMigration.bean.config.ConfigurationTable;
import com.cannontech.common.databaseMigration.bean.data.template.DataTableTemplate;

public interface ConfigurationParserService {

    /**
     * Generates a java representation of a configuration file.
     * 
     * @param configurationXMLFile
     * @return
     */
    public ConfigurationTable buildConfigurationTemplate(File configurationXMLFile);

    /**
     * This method takes a configuration file and generate a DataTableTemplate.
     * This template holds the link representations generated from the configuration file
     * and fills in all the needed table level links from the databaseDefinition xml file.
     * 
     * @param configFileTableElement
     * @return
     */
    public DataTableTemplate buildDataTableTemplate(ConfigurationTable configFileTableElement);

}