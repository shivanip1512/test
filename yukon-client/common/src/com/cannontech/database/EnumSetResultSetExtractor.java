package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class EnumSetResultSetExtractor<T extends Enum<T>> implements ResultSetExtractor {
    
    private final ParameterizedRowMapper<T> mapper;
    private EnumSet<T> result;

    public EnumSetResultSetExtractor(Class<T> enumClass, ParameterizedRowMapper<T> mapper) {
        this.mapper = mapper;
        this.result = EnumSet.noneOf(enumClass);
    }

    @Override
    public Object extractData(ResultSet rs) throws SQLException,
            DataAccessException {
        int row = 0;
        while (rs.next()) {
            T value = mapper.mapRow(rs, row++);
            result.add(value);
        }
        return result;
    }
    
    public Set<T> getResult() {
        return Collections.unmodifiableSet(result);
    }

}
