package com.cannontech.core.dao.impl;

public enum LoginStatusEnum {
    ENABLED("Enabled"),
    DISABLED("Disabled");

    private String sqlValue;
    
    LoginStatusEnum(String sqlValue){
        this.sqlValue = sqlValue;
    }
    
    public String getSqlValue(){
        return sqlValue;
    }

}