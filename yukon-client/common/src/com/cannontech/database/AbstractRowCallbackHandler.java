package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowCallbackHandler;

/**
 * This class automates the row number incrementing so that it can easily delegate 
 * to a RowMapper.
 */
public abstract class AbstractRowCallbackHandler implements RowCallbackHandler {
    int rowNum = 0;

    public AbstractRowCallbackHandler() {
        super();
    }

    final public void processRow(ResultSet rs) throws SQLException {
        processRow(rs, rowNum++);
    }

    abstract public void processRow(ResultSet rs, int rowNum) throws SQLException;

}
