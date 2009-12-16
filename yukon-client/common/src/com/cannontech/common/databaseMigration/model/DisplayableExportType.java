package com.cannontech.common.databaseMigration.model;

import java.util.Set;

public class DisplayableExportType {

    private ExportTypeEnum exportType;
    private Set<String> tableNameSet;

    public ExportTypeEnum getExportType() {
        return exportType;
    }

    public void setExportType(ExportTypeEnum exportType) {
        this.exportType = exportType;
    }

    public Set<String> getTableNameSet() {
        return tableNameSet;
    }

    public void setTableNameSet(Set<String> tableNameSet) {
        this.tableNameSet = tableNameSet;
    }

}
