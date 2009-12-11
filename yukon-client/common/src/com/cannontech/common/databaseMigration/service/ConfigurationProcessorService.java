package com.cannontech.common.databaseMigration.service;

import java.util.List;

import com.cannontech.common.databaseMigration.bean.data.DataTable;
import com.cannontech.common.databaseMigration.bean.data.template.DataTableTemplate;

public interface ConfigurationProcessorService {

    public Iterable<DataTable> processDataTableTemplate(DataTableTemplate template,
                                                        List<Integer> primaryKeyList);
   
    
}
