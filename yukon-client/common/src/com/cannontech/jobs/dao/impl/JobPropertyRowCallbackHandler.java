package com.cannontech.jobs.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.database.SqlUtils;

class JobPropertyRowCallbackHandler implements RowCallbackHandler {
    private final Map<String, String> propertyMap;

    public JobPropertyRowCallbackHandler(Map<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }

    public void processRow(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        String value = SqlUtils.convertDbValueToString(rs, "value");
        propertyMap.put(name, value);
    }
}