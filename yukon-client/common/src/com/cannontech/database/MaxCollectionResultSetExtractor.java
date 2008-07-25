package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class MaxCollectionResultSetExtractor<T> implements ResultSetExtractor {
    
    private final ParameterizedRowMapper<T> rowMapper;
    private final Collection<? super T> result;
    private final int maxRows;

    public MaxCollectionResultSetExtractor(Collection<? super T> collection, ParameterizedRowMapper<T> rowMapper, int maxRows) {
        this.rowMapper = rowMapper;
        this.maxRows = maxRows;
        this.result = collection;
    }
    
    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
        int i = 0;
        while (i++ < maxRows && rs.next()) {
            T object = rowMapper.mapRow(rs, i);
            result.add(object);
        }
        return null;
    }

}
