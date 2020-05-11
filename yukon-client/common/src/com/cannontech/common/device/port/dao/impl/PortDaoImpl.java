package com.cannontech.common.device.port.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.port.dao.PortDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

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
    public List<DeviceBaseModel> getAllAssignedDevicesForPort(Integer portId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ddcs.DeviceId, ypo.type, ypo.PaoName, ypo.DisableFlag ");
        sql.append("FROM DeviceDirectCommSettings ddcs");
        sql.append("JOIN YukonPAObject ypo");
        sql.append("ON ddcs.DeviceId = ypo.PaObjectId");
        sql.append("WHERE PortId").eq_k(portId);

        return jdbcTemplate.query(sql, deviceBaseModelRowMapper);
    }

    private final YukonRowMapper<DeviceBaseModel> deviceBaseModelRowMapper = new YukonRowMapper<>() {
        @Override
        public DeviceBaseModel mapRow(YukonResultSet rs) throws SQLException {

            Integer deviceId = rs.getInt("deviceId");
            PaoType type = rs.getEnum("type", PaoType.class);
            String deviceName = rs.getString("paoName");
            Boolean enable = !rs.getBoolean("disableFlag");

            return new DeviceBaseModel(deviceId, type, deviceName, enable);
        }
    };
}
