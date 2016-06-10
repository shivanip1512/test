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
    private Integer cachedDaCount = 0;
    private Instant daCountLastUpdateTime = new Instant(0);
    private Integer cachedGatewayCount = 0;
    private Instant gatewayCountLastUpdateTime = new Instant(0);
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public int getRfnMeterCount() {
        synchronized (cachedMeterCount) {
            if (isMoreThanOneHourSince(meterCountLastUpdateTime)) {
                cachedMeterCount = getDeviceCountByTypes(PaoType.getRfMeterTypes());
            }
        }
        return cachedMeterCount;
    }
    
    @Override
    public int getRfnLcrCount() {
        synchronized (cachedLcrCount) {
            if (isMoreThanOneHourSince(lcrCountLastUpdateTime)) {
                cachedLcrCount = getDeviceCountByTypes(PaoType.getRfLcrTypes());
            }
        }
        return cachedLcrCount;
    }
    
    @Override
    public int getRfDaCount() {
        synchronized (cachedDaCount) {
            if (isMoreThanOneHourSince(daCountLastUpdateTime)) {
                cachedDaCount = getDeviceCountByTypes(PaoType.getRfDaTypes());
            }
        }
        return cachedDaCount;
    }
    
    @Override
    public int getRfGatewayCount() {
        synchronized (cachedGatewayCount) {
            if (isMoreThanOneHourSince(gatewayCountLastUpdateTime)) {
                cachedGatewayCount = getDeviceCountByTypes(PaoType.getRfGatewayTypes());
            }
        }
        return cachedGatewayCount;
    }
    
    private int getDeviceCountByTypes(Set<PaoType> types) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(Type)");
        sql.append("FROM YukonPaObject");
        sql.append("WHERE Type").in(types);
        
        return jdbcTemplate.queryForInt(sql);
    }
    
    private static final Duration oneHour = Duration.standardHours(1);
    private static boolean isMoreThanOneHourSince(Instant timeToCheck) {
        if (Instant.now().isAfter(timeToCheck.plus(oneHour))) {
            return true;
        }
        return false;
    }
}
