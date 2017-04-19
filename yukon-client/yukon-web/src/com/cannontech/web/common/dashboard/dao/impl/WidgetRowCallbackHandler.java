package com.cannontech.web.common.dashboard.dao.impl;

import java.sql.SQLException;

import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.web.common.dashboard.model.Dashboard;
import com.cannontech.web.common.dashboard.model.Widget;
import com.cannontech.web.common.dashboard.model.WidgetType;

/**
 * Applies the widgets from the result set to the specified dashboard, putting them in the appropriate columns.
 */
public final class WidgetRowCallbackHandler implements YukonRowCallbackHandler {
    private final Dashboard dashboard;
    
    public WidgetRowCallbackHandler(Dashboard dashboard) {
        this.dashboard = dashboard;
    }
    
    @Override
    public void processRow(YukonResultSet rs) throws SQLException {
        Widget widget = new Widget();
        widget.setDashboardId(rs.getInt("DashboardId"));
        widget.setId(rs.getInt("WidgetId"));
        widget.setType(rs.getEnum("WidgetType", WidgetType.class));
        
        int ordering = rs.getInt("Ordering");
        if (ordering < 200) {
            dashboard.addColumn1Widget(widget);
        } else {
            dashboard.addColumn2Widget(widget);
        }
    }
}