package com.cannontech.common.events.model;

import java.sql.Types;

public enum EventLogColumnTypeEnum {
    STRING(Types.VARCHAR),
    NUMBER(Types.NUMERIC),
    DATE(Types.TIMESTAMP);
    
    private int sqlType;
    
    private EventLogColumnTypeEnum(int sqlType){
        this.sqlType = sqlType;
    }
    
    public int getSqlType() {
        return sqlType;
    }
}