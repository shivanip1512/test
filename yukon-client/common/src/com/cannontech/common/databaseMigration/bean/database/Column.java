package com.cannontech.common.databaseMigration.bean.database;

public class Column{
    private String name;
    private ColumnTypeEnum columnType;
    private String defaultValue;
    private String tableRef;
    private ReferenceTypeEnum refType;
    private String nullId;
    private String filterValue;
    private boolean escapingNeeded = false;
    private boolean addToDisplayLabels = false;
    private boolean unique = false;
    
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
    
    // Filter Value
    public String getFilterValue() {
        return filterValue;
    }
    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }
    
    // Escaping Needed
    public boolean isEscapingNeeded() {
        return escapingNeeded;
    }
    public void setEscapingNeeded(boolean escapingNeeded) {
        this.escapingNeeded = escapingNeeded;
    }
    
    // Add to Display Labels
    public boolean isAddToDisplayLabels() {
        return addToDisplayLabels;
    }
    public void setAddToDisplayLabels(boolean addToDisplayLabels) {
        this.addToDisplayLabels = addToDisplayLabels;
    }
    
    public boolean isUnique() {
		return unique;
	}
    
    public void setUnique(boolean unique) {
		this.unique = unique;
	}

    public String toString(){
        return "Name = " + this.name +
               ", Column Type = " + this.columnType +
               ", Table Reference = " + this.tableRef +
               ", Reference Type = " + this.refType +
               ", Null Id = " + this.nullId;
    }
}