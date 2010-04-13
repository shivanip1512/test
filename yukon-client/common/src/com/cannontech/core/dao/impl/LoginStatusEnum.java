package com.cannontech.core.dao.impl;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.data.lite.LiteYukonUser;

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
            if (loginStatusEnum.sqlValue.equalsIgnoreCase(dbValue)) {
                return loginStatusEnum;
            }
        }

        return null;
    }
    
    public static boolean isDisabled(LiteYukonUser liteYukonUser) {
        if (liteYukonUser == null) {
            return true;
        } else {
            return LoginStatusEnum.DISABLED.equals(liteYukonUser.getLoginStatus());
        }
    }
    
    
}