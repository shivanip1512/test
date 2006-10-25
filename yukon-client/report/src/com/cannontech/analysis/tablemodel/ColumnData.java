package com.cannontech.analysis.tablemodel;

interface ColumnData {

    public Class<?> getColumnType(Class<?> clazz);

    public Object getColumnValue(Object o);

    public String getColumnName();

}