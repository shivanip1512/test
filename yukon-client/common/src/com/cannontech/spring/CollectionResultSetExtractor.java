package com.cannontech.spring;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

public class CollectionResultSetExtractor implements ResultSetExtractor {
    private final Collection results;
    private final RowMapper rowMapper;
    public CollectionResultSetExtractor(Collection holder, RowMapper rowMapper) {
        this.results = holder;
        this.rowMapper = rowMapper;
    }
    
    @SuppressWarnings("unchecked")
    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
        int rowNum = 0;
        while (rs.next()) {
            results.add(this.rowMapper.mapRow(rs, rowNum++));
        }
        return results;
    }
}