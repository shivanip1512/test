package com.cannontech.watchdog.dao.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.watchdog.dao.WatchdogWatcherDao;

public class WatchdogWatcherDaoImpl implements WatchdogWatcherDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public boolean paoClassPaoExists(PaoClass paoClass) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) ");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE PAOClass ").eq_k(paoClass);

        return jdbcTemplate.queryForInt(sql) > 0;
    }
    
    @Override
    public RfnIdentifier getIdForLatestGateway() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SerialNumber, Manufacturer, Model");
        sql.append("FROM RfnAddress");
        sql.append("WHERE deviceId = (");
        sql.append("SELECT MAX(PAObjectID)");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE Type").in_k(PaoType.getRfGatewayTypes());
        sql.append(")");

        try {
            return jdbcTemplate.queryForObject(sql, new YukonRowMapper<RfnIdentifier>() {
                @Override
                public RfnIdentifier mapRow(YukonResultSet rs) throws SQLException {
                    RfnIdentifier rfnIdentifier = new RfnIdentifier(
                        rs.getStringSafe("SerialNumber"),
                        rs.getStringSafe("Manufacturer"), 
                        rs.getStringSafe("Model"));
                    return rfnIdentifier;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("RFN Gateway not available");
        }
    }

}
