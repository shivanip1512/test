package com.cannontech.common.databaseMigration.bean.database;

public enum ColumnTypeEnum{
    PRIMARY_KEY("primaryKey"),
    IDENTIFIER("identifier"),
    DATA("data");
    
    private String xmlKey;
    
    ColumnTypeEnum(String xmlKey){
        this.xmlKey = xmlKey;
    }
    
    public String getXMLKey(){
        return xmlKey;
    }
}