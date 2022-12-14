package com.cannontech.database;

import java.sql.SQLException;

import com.cannontech.common.util.SimpleCallback;

/**
 * This is a YukonRowMapper version of the MappingRowCallbackHandler, which allows
 * you to pass in a callback/function to be processed over the data set instead of actually
 * retrieving everything like you would with a rowMapper.
 */
public class YukonMappingRowCallbackHandler<I> implements YukonRowCallbackHandler {

    private final YukonRowMapper<I> mapper;
    private final SimpleCallback<I> callback;
    
    public YukonMappingRowCallbackHandler(YukonRowMapper<I> mapper, SimpleCallback<I> callback) {
        this.mapper = mapper;
        this.callback = callback;
    }

    @Override
    public void processRow(YukonResultSet rs) throws SQLException {
        I mapRow = mapper.mapRow(rs);
        try {
            callback.handle(mapRow);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Caught exception call callback's handle method", e);
        }
    }
}
