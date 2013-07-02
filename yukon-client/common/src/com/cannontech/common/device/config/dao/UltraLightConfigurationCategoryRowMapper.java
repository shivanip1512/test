package com.cannontech.common.device.config.dao;

import java.sql.SQLException;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.device.config.model.UltraLightConfigurationCategory;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;

public class UltraLightConfigurationCategoryRowMapper extends 
        AbstractRowMapperWithBaseQuery<UltraLightConfigurationCategory> {

    @Override
    public SqlFragmentSource getBaseQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DCC.DeviceConfigCategoryId, DCC.CategoryType, DCC.Name");
        sql.append("FROM DeviceConfigCategory DCC");
        
        return sql;
    }
    
    @Override
    public UltraLightConfigurationCategory mapRow(YukonResultSet rs) throws SQLException {
        int categoryId = rs.getInt("DeviceConfigCategoryId");
        String categoryType = rs.getString("CategoryType");
        String name = rs.getString("Name");
        
        UltraLightConfigurationCategory category = new UltraLightConfigurationCategory(categoryId, categoryType, name);
        
        return category;
    }
    
    @Override
    public SqlFragmentSource getOrderBy() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("ORDER BY LOWER(DCC.Name)");
        
        return sql;
    }
    
    @Override
    public boolean needsWhere() {
        return true;
    }
}
