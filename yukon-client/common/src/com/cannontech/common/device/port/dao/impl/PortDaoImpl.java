package com.cannontech.common.device.port.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.device.port.dao.PortDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;

public class PortDaoImpl implements PortDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public Integer findUniquePortTerminalServer(String ipAddress, Integer port) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PortId ");
        sql.append("FROM PortTerminalServer");
        sql.append("WHERE IpAddress").eq(ipAddress);
        sql.append("  AND SocketPortNumber").eq(port);
        try {
            return jdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
