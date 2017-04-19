package com.cannontech.web.common.dashboard.dao.impl;

import java.sql.SQLException;

import com.cannontech.core.dao.impl.LiteYukonUserMapper;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.common.dashboard.model.Dashboard;
import com.cannontech.web.common.dashboard.model.DashboardPageType;
import com.cannontech.web.common.dashboard.model.Visibility;

public final class DashboardRowMapper implements YukonRowMapper<Dashboard> {
    private static final LiteYukonUserMapper userRowMapper = new LiteYukonUserMapper();
    
    @Override
    public Dashboard mapRow(YukonResultSet rs) throws SQLException {
        Dashboard dashboard = new Dashboard();
        dashboard.setDashboardId(rs.getInt("DashboardId"));
        dashboard.setDescription(rs.getString("Description"));
        dashboard.setName(rs.getString("Name"));
        dashboard.setVisibility(rs.getEnum("Visibility", Visibility.class));
        dashboard.setPageType(DashboardPageType.MAIN); //TODO 
        
        // If there's an assigned owner, get the LiteYukonUser, otherwise leave it null
        if (rs.getNullableInt("OwnerId") != null) {
            LiteYukonUser user = userRowMapper.mapRow(rs);
            dashboard.setOwner(user);
        }
        
        return dashboard;
    }
}