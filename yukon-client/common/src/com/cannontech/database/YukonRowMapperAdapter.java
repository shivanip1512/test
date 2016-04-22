package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public final class YukonRowMapperAdapter<T> implements RowMapper<T> {

    private final YukonRowMapper<T> rm;

    public YukonRowMapperAdapter(YukonRowMapper<T> rm) {
        this.rm = rm;
    }
    
    public static <F> YukonRowMapperAdapter<F> create(YukonRowMapper<F> rm) {
        return new YukonRowMapperAdapter<F>(rm);
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rm.mapRow(new YukonResultSet(rs));
    }

}