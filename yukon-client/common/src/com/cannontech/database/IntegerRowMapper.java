package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * @deprecated Use {@link RowMapper#INTEGER} or {@link RowMapper#INTEGER_NULLABLE}
 */
@Deprecated
public final class IntegerRowMapper implements ParameterizedRowMapper<Integer> {

    private final boolean nullable;

    public IntegerRowMapper() {
        this.nullable = false;
    }

    public IntegerRowMapper(boolean nullable) {
        this.nullable = nullable;
    }
    
    @Override
    public final Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
        int result = rs.getInt(1);
        if (nullable && rs.wasNull()) {
            return null;
        }
        return Integer.valueOf(result);
    }
    
}
