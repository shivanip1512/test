package com.cannontech.thirdparty.digi.dao.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.model.ZigbeeThermostat;

public class ZigbeeDeviceDaoImpl implements ZigbeeDeviceDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
    private GatewayDeviceDao gatewayDeviceDao;

    @Override
    public ZigbeeThermostat getZigbeeUtilPro(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT ZE.DeviceId,ZE.InstallCode,ZE.MacAddress,");
        sql.append(           "YPO.Type,HB.ManufacturerSerialNumber, DM.GatewayId");
        sql.append("FROM ZBEndPoint ZE");
        sql.append(  "JOIN YukonPAObject YPO ON ZE.DeviceId = YPO.PAObjectID");
        sql.append(  "JOIN InventoryBase IB ON ZE.DeviceId = IB.DeviceID");
        sql.append(  "JOIN LMHardwareBase HB ON IB.InventoryID = HB.InventoryID");
        sql.append(  "LEFT OUTER JOIN ZBGatewayToDeviceMapping DM ON DM.DeviceId = ZE.DeviceID");
        sql.append("WHERE ZE.DeviceId").eq(deviceId);
        
        ZigbeeThermostat tstat = yukonJdbcTemplate.queryForObject(sql, new YukonRowMapper<ZigbeeThermostat>() {
                @Override
                public ZigbeeThermostat mapRow(YukonResultSet rs) throws SQLException {
                    ZigbeeThermostat zigbeeThermostat = new ZigbeeThermostat();
                    
                    int deviceId = rs.getInt("DeviceId");
                    PaoType paoType = rs.getEnum("Type", PaoType.class);

                    zigbeeThermostat.setPaoIdentifier(new PaoIdentifier(deviceId, paoType));
                    zigbeeThermostat.setInstallCode(rs.getString("InstallCode"));
                    zigbeeThermostat.setMacAddress(rs.getString("MacAddress"));
                    zigbeeThermostat.setName(rs.getString("ManufacturerSerialNumber"));
                    zigbeeThermostat.setGatewayId(rs.getNullableInt("GatewayId"));
                    
                    return zigbeeThermostat;
                }
            }
        );

        return tstat;
    }
    
    @Override
    @Transactional
    public void createZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("INSERT INTO ZBEndPoint (DeviceId,InstallCode,MacAddress)");
        sql.values(zigbeeThermostat.getPaoIdentifier().getPaoId(),
                      zigbeeThermostat.getInstallCode(),
                      zigbeeThermostat.getMacAddress());
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void updateZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("UPDATE ZBEndPoint");
        sql.append("SET InstallCode").eq(zigbeeThermostat.getInstallCode()).append(",");
        sql.append("MacAddress").eq(zigbeeThermostat.getMacAddress());
        sql.append("WHERE DeviceId").eq(zigbeeThermostat.getPaoIdentifier().getPaoId());
        
        yukonJdbcTemplate.update(sql);
        
        gatewayDeviceDao.updateDeviceToGatewayAssignment(zigbeeThermostat.getPaoIdentifier().getPaoId(), zigbeeThermostat.getGatewayId());
    }

    @Override
    public void deleteZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("DELETE FROM ZBEndPoint");
        sql.append("WHERE DeviceId").eq(zigbeeThermostat.getPaoIdentifier().getPaoId());
        
        yukonJdbcTemplate.update(sql);
        
        
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
    }
    
}