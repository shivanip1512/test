package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

public class MaxListResultSetExtractor<T> implements ResultSetExtractor {
    
    private final RowMapper<T> rowMapper;
    private final ArrayList<T> result = new ArrayList<T>();
    private final int maxRows;

    public MaxListResultSetExtractor(RowMapper<T> rowMapper, int maxRows) {
        this.rowMapper = rowMapper;
        this.maxRows = maxRows;
    }
    
    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
        int i = 0;
        while (i++ < maxRows && rs.next()) {
            T object = rowMapper.mapRow(rs, i);
            result.add(object);
        }
        return null;
    }

    public List<T> getResult() {
        return result;
    }
}
