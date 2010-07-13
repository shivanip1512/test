/**
 * 
 */
package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public final class YukonRowMapperAdapter<T> implements ParameterizedRowMapper<T> {

    private final YukonRowMapper<T> rm;

    public YukonRowMapperAdapter(YukonRowMapper<T> rm) {
        this.rm = rm;
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rm.mapRow(new YukonResultSet(rs));
    }

}