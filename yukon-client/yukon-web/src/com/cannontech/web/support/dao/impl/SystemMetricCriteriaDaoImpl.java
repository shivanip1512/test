package com.cannontech.web.support.dao.impl;

import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.web.support.dao.SystemMetricCriteriaDao;

public class SystemMetricCriteriaDaoImpl implements SystemMetricCriteriaDao {
    private Integer cachedMeterCount = 0;
    private Instant meterCountLastUpdateTime = new Instant(0);
    private Integer cachedLcrCount = 0;
    private Instant lcrCountLastUpdateTime = new Instant(0);
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public int getRfnMeterCount() {
        synchronized (cachedMeterCount) {
            if (isMoreThanOneHourSince(meterCountLastUpdateTime)) {
                Set<PaoType> rfnMeterTypes = PaoType.getRfMeterTypes();
                
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT COUNT(Type)");
                sql.append("FROM YukonPaObject");
                sql.append("WHERE Type").in(rfnMeterTypes);
                
                int newMeterCount = jdbcTemplate.queryForInt(sql);
                cachedMeterCount = newMeterCount;
            }
        }
        return cachedMeterCount;
    }
    
    @Override
    public int getRfnLcrCount() {
        synchronized (cachedLcrCount) {
            if (isMoreThanOneHourSince(lcrCountLastUpdateTime)) {
                Set<PaoType> rfnLcrTypes = PaoType.getRfLcrTypes();
                
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT COUNT(Type)");
                sql.append("FROM YukonPaObject");
                sql.append("WHERE Type").in(rfnLcrTypes);
                
                int newLcrCount = jdbcTemplate.queryForInt(sql);
                cachedLcrCount = newLcrCount;
            }
        }
        return cachedLcrCount;
    }
    
    private static final Duration oneHour = Duration.standardHours(1);
    private static boolean isMoreThanOneHourSince(Instant timeToCheck) {
        if (Instant.now().isAfter(timeToCheck.plus(oneHour))) {
            return true;
        }
        return false;
    }
}
