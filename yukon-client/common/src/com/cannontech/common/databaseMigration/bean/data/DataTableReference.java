package com.cannontech.common.databaseMigration.bean.data;

import java.util.Map;

public class DataTableReference implements DataTableEntity {
    private Map<String, DataTableEntity> tableColumns;
}
