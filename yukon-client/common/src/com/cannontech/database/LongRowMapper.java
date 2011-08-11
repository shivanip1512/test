package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public final class LongRowMapper implements ParameterizedRowMapper<Long> {

    private final boolean nullable;

    public LongRowMapper() {
        this.nullable = false;
    }

    public LongRowMapper(boolean nullable) {
        this.nullable = nullable;
    }
    
    @Override
    public final Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        long result = rs.getLong(1);
        if (nullable && rs.wasNull()) {
            return null;
        }
        return Long.valueOf(result);
    }
    
}
