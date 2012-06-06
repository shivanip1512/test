package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.util.SimpleCallback;

public class MappingRowCallbackHandler<I> extends AbstractRowCallbackHandler {

    private final ParameterizedRowMapper<I> mapper;
    private final SimpleCallback<I> callback;

    public MappingRowCallbackHandler(ParameterizedRowMapper<I> mapper, SimpleCallback<I> callback) {
        this.mapper = mapper;
        this.callback = callback;
    }

    @Override
    public void processRow(ResultSet rs, int rowNum) throws SQLException {
        I mapRow = mapper.mapRow(rs, rowNum);
        try {
            callback.handle(mapRow);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Caught exception call callback's handle method", e);
        }
    }
}
