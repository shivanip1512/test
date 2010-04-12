package com.cannontech.core.dao.impl;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum LoginStatusEnum implements DatabaseRepresentationSource {
    ENABLED("Enabled"),
    DISABLED("Disabled");

    private String sqlValue;
    
    LoginStatusEnum(String sqlValue){
        this.sqlValue = sqlValue;
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return sqlValue;
    }

    public static LoginStatusEnum retrieveLoginStatus(String dbValue) {
        LoginStatusEnum[] values = values();
        for (LoginStatusEnum loginStatusEnum : values) {
            if (loginStatusEnum.sqlValue.equals(dbValue)) {
                return loginStatusEnum;
            }
        }

        return null;
    }
}