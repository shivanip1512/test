package com.cannontech.thirdparty.digi.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeThermostat;

public class ZigbeeDeviceDaoImpl implements ZigbeeDeviceDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
    private GatewayDeviceDao gatewayDeviceDao;

    private static YukonRowMapper<ZigbeeThermostat> zigbeeThermostatRowMapper  = new YukonRowMapper<ZigbeeThermostat>(){
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
            zigbeeThermostat.setNodeId(rs.getNullableInt("NodeId"));
            zigbeeThermostat.setDestinationEndPointId(rs.getInt("DestinationEndPointId"));
            
            return zigbeeThermostat;
        }
    };
    
    @Override
    public ZigbeeDevice getZigbeeDevice(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ZE.DeviceId,ZE.MacAddress,YPO.Type");
        sql.append("FROM ZBEndPoint ZE");
        sql.append(  "JOIN YukonPAObject YPO ON ZE.DeviceId = YPO.PAObjectID");
        sql.append("WHERE DeviceId").eq(deviceId);
        
        ZigbeeDevice zigbeeDevice = yukonJdbcTemplate.queryForObject(sql, GatewayDeviceDaoImpl.zigbeeDeviceRowMapper);
        
        return zigbeeDevice;
    }
    
    @Override
    public List<ZigbeeDevice> getZigbeeDevicesForGroupId(int groupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT ZE.DeviceId,ZE.MacAddress,YPO.Type");
        sql.append("FROM LMHardwareControlGroup LMHCG");
        sql.append(  "JOIN InventoryBase IB ON LMHCG.InventoryID = IB.InventoryID ");
        sql.append(  "JOIN ZBGatewayToDeviceMapping ZB on ZB.DeviceId = IB.DeviceId");
        sql.append(  "JOIN ZBEndPoint ZE on ZE.DeviceId = ZB.DeviceId");
        sql.append(  "JOIN YukonPaObject YPO on ZE.DeviceId = YPO.PaObjectId");
        sql.append("WHERE LMHCG.LMGroupId").eq(groupId);
        sql.append(  "AND LMHCG.GroupEnrollStop IS NULL");
        
        List<ZigbeeDevice> zigbeeDevice = yukonJdbcTemplate.query(sql, GatewayDeviceDaoImpl.zigbeeDeviceRowMapper);
        
        return zigbeeDevice;
    }
    
    @Override
    public ZigbeeThermostat getZigbeeUtilPro(int deviceId) {
        SqlStatementBuilder sql = buildZigbeeUtilProStatement();
        
        sql.append("WHERE ZE.DeviceId").eq(deviceId);
        
        ZigbeeThermostat tstat = yukonJdbcTemplate.queryForObject(sql, zigbeeThermostatRowMapper);

        return tstat;
    }
    
    public ZigbeeThermostat getZigbeeUtilProByMACAddress(String macAddress) {
        SqlStatementBuilder sql = buildZigbeeUtilProStatement();        
        
        sql.append("WHERE ZE.MacAddress").eq(macAddress);
        
        ZigbeeThermostat tstat = yukonJdbcTemplate.queryForObject(sql, zigbeeThermostatRowMapper);

        return tstat; 
    }
    
    private SqlStatementBuilder buildZigbeeUtilProStatement() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT ZE.DeviceId,ZE.InstallCode,ZE.MacAddress,ZE.NodeId,ZE.DestinationEndPointId,");
        sql.append(           "YPO.Type,HB.ManufacturerSerialNumber, DM.GatewayId");
        sql.append("FROM ZBEndPoint ZE");
        sql.append(  "JOIN YukonPAObject YPO ON ZE.DeviceId = YPO.PAObjectID");
        sql.append(  "JOIN InventoryBase IB ON ZE.DeviceId = IB.DeviceID");
        sql.append(  "JOIN LMHardwareBase HB ON IB.InventoryID = HB.InventoryID");
        sql.append(  "LEFT JOIN ZBGatewayToDeviceMapping DM ON DM.DeviceId = ZE.DeviceID");
        
        return sql;
    }
    
    @Override
    @Transactional
    public void createZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("INSERT INTO ZBEndPoint (DeviceId,InstallCode,MacAddress,NodeId,DestinationEndPointId)");
        sql.values(zigbeeThermostat.getPaoIdentifier().getPaoId(),
                      zigbeeThermostat.getInstallCode().toUpperCase(),
                      zigbeeThermostat.getMacAddress().toUpperCase(),
                      zigbeeThermostat.getNodeId(),
                      zigbeeThermostat.getDestinationEndPointId()
                      );
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void updateZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("UPDATE ZBEndPoint");
        sql.append("SET InstallCode").eq(zigbeeThermostat.getInstallCode().toUpperCase()).append(",");
        sql.append("MacAddress").eq(zigbeeThermostat.getMacAddress().toUpperCase()).append(",");
        sql.append("NodeId").eq(zigbeeThermostat.getNodeId()).append(",");
        sql.append("DestinationEndPointId").eq(zigbeeThermostat.getDestinationEndPointId());
        sql.append("WHERE DeviceId").eq(zigbeeThermostat.getPaoIdentifier().getPaoId());
        
        yukonJdbcTemplate.update(sql);
        
        gatewayDeviceDao.updateDeviceToGatewayAssignment(zigbeeThermostat.getPaoIdentifier().getPaoId(), zigbeeThermostat.getGatewayId());
    }

    @Override
    public void deleteZigbeeUtilPro(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("DELETE FROM ZBEndPoint");
        sql.append("WHERE DeviceId").eq(deviceId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public int getDeviceIdForMACAddress(String macAddress) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT DeviceId");
        sql.append("FROM ZBEndPoint");
        sql.append("WHERE MacAddress").eq(macAddress);
        
        int deviceId = yukonJdbcTemplate.queryForInt(sql);
        
        return deviceId;
    }
    
    @Override
    public List<Integer> getDeviceIdsForMACAddresses(List<String> macAddresses) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT DeviceId");
        sql.append("FROM ZBEndPoint");
        sql.append("WHERE MacAddress").in(macAddresses);
        
        List<Integer> deviceIds = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        
        return deviceIds;
    }
    
    @Override
    public Integer findGatewayIdForInventory(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GatewayId");
        sql.append("FROM ZBGatewayToDeviceMapping DM");
        sql.append(  "JOIN InventoryBase I on I.DeviceID = DM.DeviceId");
        sql.append("WHERE I.InventoryID").eq(inventoryId);
        
        try {
            return yukonJdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
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