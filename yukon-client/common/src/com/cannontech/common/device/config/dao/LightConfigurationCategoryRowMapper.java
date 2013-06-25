package com.cannontech.common.device.config.dao;

import java.sql.SQLException;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.device.config.model.LightConfigurationCategory;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;

public class LightConfigurationCategoryRowMapper extends 
        AbstractRowMapperWithBaseQuery<LightConfigurationCategory> {

    @Override
    public SqlFragmentSource getBaseQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DCC.DeviceConfigurationCategoryId, DCC.CategoryType, DCC.Name");
        sql.append("FROM DeviceConfigurationCategory DCC");
        
        return sql;
    }
    
    @Override
    public LightConfigurationCategory mapRow(YukonResultSet rs) throws SQLException {
        int categoryId = rs.getInt("DeviceConfigurationCategoryId");
        String categoryType = rs.getString("CategoryType");
        String name = rs.getString("Name");
        
        LightConfigurationCategory category = new LightConfigurationCategory(categoryId, categoryType, name);
        
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
