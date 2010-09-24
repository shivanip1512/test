package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowCallbackHandler;

public class YukonRowCallbackHandlerAdapter implements RowCallbackHandler {
    
    private YukonRowCallbackHandler yukonRowCallbackHandler;
    

    public YukonRowCallbackHandlerAdapter(YukonRowCallbackHandler yukonRowCallbackHandler) {
        super();
        this.yukonRowCallbackHandler = yukonRowCallbackHandler;
    }

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        yukonRowCallbackHandler.processRow(new YukonResultSet(rs));
    }

}
