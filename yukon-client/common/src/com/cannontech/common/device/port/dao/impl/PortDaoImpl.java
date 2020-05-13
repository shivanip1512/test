package com.cannontech.common.device.port.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.port.dao.PortDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.impl.DeviceBaseModelRowMapper;
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

    @Override
    public List<DeviceBaseModel> getDevicesAssignedPort(Integer portId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ddcs.DeviceId, ypo.type, ypo.PaoName, ypo.DisableFlag");
        sql.append("FROM DeviceDirectCommSettings ddcs");
        sql.append("JOIN YukonPAObject ypo");
        sql.append("ON ddcs.DeviceId = ypo.PaObjectId");
        sql.append("WHERE PortId").eq(portId);

        return jdbcTemplate.query(sql, new DeviceBaseModelRowMapper());
    }
}
