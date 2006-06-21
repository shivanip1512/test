package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultReader;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultReader;

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

    public MaxRowCalbackHandlerRse(RowMapper rm, int maxRows) {
        super();
        this.maxRows = maxRows;
        this.rch = new RowMapperResultReader(rm);
    }
    
    public MaxRowCalbackHandlerRse(RowCallbackHandler rch, int maxRows) {
        this.rch = rch;
        this.maxRows = maxRows;
    }

    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
        while (loadedRows++ < maxRows && rs.next()) {
            this.rch.processRow(rs);
        }
        if (this.rch instanceof ResultReader) {
            return ((ResultReader) this.rch).getResults();
        }
        else {
            return null;
        }
    }

    public int getLoadedRows() {
        return loadedRows;
    }


}
