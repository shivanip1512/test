package com.cannontech.common.device.port.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.device.port.dao.PortDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.db.port.PortTerminalServer;
import com.cannontech.database.db.port.PortTerminalServer.EncodingType;

public class PortDaoImpl implements PortDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    private static final YukonRowMapper<PortTerminalServer> portTerminalServerRowMapper = rs -> {
        PortTerminalServer row = new PortTerminalServer();
        row.setPortID(rs.getInt("PortId"));
        row.setIpAddress(rs.getString("IpAddress"));
        row.setSocketPortNumber(rs.getInt("SocketPortNumber"));
        row.setEncodingKey(rs.getString("EncodingKey"));
        row.setEncodingType(rs.getEnum("EncodingType", EncodingType.class));
        return row;
    };

    @Override
    public PortTerminalServer findPortTerminalServer(String ipAddress, Integer port) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * ");
        sql.append("FROM PortTerminalServer");
        sql.append("WHERE IpAddress").eq(ipAddress);
        sql.append("  AND SocketPortNumber").eq(port);
        try {
            return jdbcTemplate.queryForObject(sql, portTerminalServerRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
