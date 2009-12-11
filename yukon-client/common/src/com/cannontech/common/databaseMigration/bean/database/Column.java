package com.cannontech.common.databaseMigration.bean.database;

public class Column{
    String name;
    String tableRef;
    String nullId;
    ColumnTypeEnum columnType;
    ReferenceTypeEnum refType;
    
    // Name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // Table Reference
    public String getTableRef() {
        return tableRef;
    }
    public void setTableRef(String tableRef) {
        this.tableRef = tableRef;
    }

    // Null Id
    public String getNullId() {
        return nullId;
    }
    public void setNullId(String nullId) {
        this.nullId = nullId;
    }
    
    // Column Type
    public ColumnTypeEnum getColumnType() {
        return columnType;
    }
    public void setColumnType(ColumnTypeEnum columnType) {
        this.columnType = columnType;
    }
    
    // Reference Type
    public ReferenceTypeEnum getRefType() {
        return refType;
    }
    public void setRefType(ReferenceTypeEnum refType) {
        this.refType = refType;
    }
    
    public String toString(){
        return "Name = "+this.name+
               ", Column Type = "+this.columnType+
               ", Table Reference = "+this.tableRef+
               ", Reference Type = "+this.refType+
               ", Null Id = "+this.nullId;
    }
}