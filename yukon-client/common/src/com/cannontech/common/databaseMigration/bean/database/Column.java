package com.cannontech.common.databaseMigration.bean.database;

public class Column{
    String name;
    ColumnTypeEnum columnType;
    String defaultValue;
    String tableRef;
    ReferenceTypeEnum refType;
    String nullId;
    
    // Name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // Column Type
    public ColumnTypeEnum getColumnType() {
        return columnType;
    }
    public void setColumnType(ColumnTypeEnum columnType) {
        this.columnType = columnType;
    }

    // defaultValue
    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    // Table Reference
    public String getTableRef() {
        return tableRef;
    }
    public void setTableRef(String tableRef) {
        this.tableRef = tableRef;
    }

    // Reference Type
    public ReferenceTypeEnum getRefType() {
        return refType;
    }
    public void setRefType(ReferenceTypeEnum refType) {
        this.refType = refType;
    }
    
    // Null Id
    public String getNullId() {
        return nullId;
    }
    public void setNullId(String nullId) {
        this.nullId = nullId;
    }
    
    public String toString(){
        return "Name = "+this.name+
               ", Column Type = "+this.columnType+
               ", Table Reference = "+this.tableRef+
               ", Reference Type = "+this.refType+
               ", Null Id = "+this.nullId;
    }
}