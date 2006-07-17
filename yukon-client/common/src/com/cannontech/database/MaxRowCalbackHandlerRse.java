package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

/**
 * This class is for use with Spring's JdbcTemplate class. It can wrap
 * either a RowMapper or a RowCallbackHandler and be used wherever a
 * ResultSetExtractor is expected. It differs from
 * RowCallbackHandlerResultSetExtractor (defined in JdbcTemplate)
 * in that it will stop after maxRows have been processed.
 */
public class MaxRowCalbackHandlerRse implements ResultSetExtractor {

    private final int maxRows;
    private int loadedRows = 0;
    RowCallbackHandler rch;

    public MaxRowCalbackHandlerRse(RowCallbackHandler rch, int maxRows) {
        this.rch = rch;
        this.maxRows = maxRows;
    }

    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
        while (loadedRows++ < maxRows && rs.next()) {
            this.rch.processRow(rs);
        }
        return null;
    }

    public int getLoadedRows() {
        return loadedRows;
    }


}
