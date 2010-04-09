package com.cannontech.common.databaseMigration.service;

import java.io.PrintWriter;
import java.util.List;

import com.cannontech.common.databaseMigration.bean.ExportDatabaseMigrationStatus;
import com.cannontech.common.databaseMigration.bean.data.DataTable;
import com.cannontech.common.databaseMigration.bean.data.template.DataTableTemplate;

public interface ConfigurationProcessorService {

    /**
     * This method creates a java object that matches the DataTableTemplate, 
     * and fills in all the information from the primary keys supplied.
     */
    public Iterable<DataTable> processDataTableTemplate(DataTableTemplate template,
                                                        List<Integer> primaryKeyList,
                                                        ExportDatabaseMigrationStatus status,
                                                        PrintWriter logFileWriter);
   
    
}
