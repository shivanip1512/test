package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public final class IntegerRowMapper implements ParameterizedRowMapper<Integer> {

    public IntegerRowMapper() {
        
    }

    @Override
    public final Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer result = rs.getInt(1);
        return result;
    }
    
}
