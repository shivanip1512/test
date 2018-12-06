package com.cannontech.web.common.dashboard.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;

/**
 * Builds a map of widget parameters for each widget id, which can then be extracted and applied to the widgets.
 */
public class WidgetSettingCallbackHandler implements YukonRowCallbackHandler {
    private final Map<Integer, Map<String, String>> parameterMap = new HashMap<>();
    
    @Override
    public void processRow(YukonResultSet rs) throws SQLException {
        int widgetId = rs.getInt("WidgetId");
        String name = rs.getString("Name");
        String value = rs.getStringSafe("Value");
        Map<String, String> parameters = parameterMap.get(widgetId);
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(name, value);
        parameterMap.put(widgetId, parameters);
    }
    
    public Map<Integer, Map<String, String>> getWidgetParameters() {
        return parameterMap;
    }
}