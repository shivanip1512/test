package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * Row Mapper to map a result set row with a single value into a Boolean.
 */
public final class BooleanRowMapper implements ParameterizedRowMapper<Boolean> {

    @Override
    public final Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Boolean result = rs.getBoolean(1);
        return result;
    }
    
}
