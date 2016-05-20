package com.cannontech.web.support.dao.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.web.support.dao.SystemMetricCriteriaDao;

public class SystemMetricCriteriaDaoImpl implements SystemMetricCriteriaDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public int getRfnMeterCount() {
        Set<PaoType> rfnMeterTypes = PaoType.getRfMeterTypes();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(Type)");
        sql.append("FROM YukonPaObject");
        sql.append("WHERE Type").in(rfnMeterTypes);
        
        return jdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public int getRfnLcrCount() {
        Set<PaoType> rfnLcrTypes = PaoType.getRfLcrTypes();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(Type)");
        sql.append("FROM YukonPaObject");
        sql.append("WHERE Type").in(rfnLcrTypes);
        
        return jdbcTemplate.queryForInt(sql);
    }
}
