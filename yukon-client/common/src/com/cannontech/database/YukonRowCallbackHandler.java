package com.cannontech.database;

import java.sql.SQLException;

public interface YukonRowCallbackHandler {
    public void processRow(YukonResultSet rs) throws SQLException;
}
