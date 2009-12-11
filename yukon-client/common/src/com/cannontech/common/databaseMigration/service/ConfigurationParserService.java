package com.cannontech.common.databaseMigration.service;

import java.io.File;

import com.cannontech.common.databaseMigration.bean.config.ConfigurationTable;
import com.cannontech.common.databaseMigration.bean.data.template.DataTableTemplate;

public interface ConfigurationParserService {

    public ConfigurationTable buildConfigurationTemplate(File configurationXMLFile);

    public DataTableTemplate buildDatabaseMapTemplate(ConfigurationTable configFileTableElement);

}