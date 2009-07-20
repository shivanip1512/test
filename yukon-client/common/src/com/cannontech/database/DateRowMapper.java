package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public final class DateRowMapper implements ParameterizedRowMapper<Date> {

    public DateRowMapper() {
        
    }

    @Override
    public final Date mapRow(ResultSet rs, int rowNum) throws SQLException {
        Date result = rs.getTimestamp(1);
        return result;
    }
    
}