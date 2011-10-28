package com.cannontech.thirdparty.digi.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeEndpoint;

public class ZigbeeDeviceDaoImpl implements ZigbeeDeviceDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
    
    private static YukonRowMapper<ZigbeeEndpoint> zigbeeEndPointRowMapper  = new YukonRowMapper<ZigbeeEndpoint>(){
        @Override
        public ZigbeeEndpoint mapRow(YukonResultSet rs) throws SQLException {
            ZigbeeEndpoint zigbeeEndPoint = new ZigbeeEndpoint();
            
            int deviceId = rs.getInt("DeviceId");
            PaoType paoType = rs.getEnum("Type", PaoType.class);

            zigbeeEndPoint.setPaoIdentifier(new PaoIdentifier(deviceId, paoType));
            zigbeeEndPoint.setInstallCode(rs.getString("InstallCode"));
            zigbeeEndPoint.setMacAddress(rs.getString("MacAddress"));
            zigbeeEndPoint.setName(rs.getString("ManufacturerSerialNumber"));
            zigbeeEndPoint.setGatewayId(rs.getNullableInt("GatewayId"));
            zigbeeEndPoint.setNodeId(rs.getInt("NodeId"));
            zigbeeEndPoint.setDestinationEndPointId(rs.getInt("DestinationEndPointId"));
            
            return zigbeeEndPoint;
        }
    };
    
    @Override
    public ZigbeeDevice getZigbeeDevice(int deviceId) {
        SqlStatementBuilder sql = buildBasicEndPointSql();
        sql.append("WHERE DeviceId").eq(deviceId);
        
        ZigbeeDevice zigbeeDevice = yukonJdbcTemplate.queryForObject(sql, GatewayDeviceDaoImpl.zigbeeDeviceRowMapper);
        
        return zigbeeDevice;
    }
    
    public List<ZigbeeDevice> getAllEndPoints() {
        SqlStatementBuilder sql = buildBasicEndPointSql();
        
        return yukonJdbcTemplate.query(sql, GatewayDeviceDaoImpl.zigbeeDeviceRowMapper);
    }
    
    private SqlStatementBuilder buildBasicEndPointSql() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT ZE.DeviceId,ZE.MacAddress,YPO.Type,YPO.PaoName");
        sql.append("FROM ZBEndPoint ZE");
        sql.append(  "JOIN YukonPAObject YPO ON ZE.DeviceId = YPO.PAObjectID");
        
        return sql;
    }
    
    @Override
    public List<ZigbeeDevice> getZigbeeDevicesForGroupId(int groupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT ZE.DeviceId,ZE.MacAddress,YPO.Type,YPO.PaoName");
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
    public ZigbeeEndpoint getZigbeeEndPointByInventoryId(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();        
        
        sql.appendFragment(buildZigbeeEndPointStatement());
        sql.append("WHERE IB.InventoryID").eq(inventoryId);
        
        ZigbeeEndpoint tstat = yukonJdbcTemplate.queryForObject(sql, zigbeeEndPointRowMapper);

        return tstat;
    }
    
    @Override
    public ZigbeeEndpoint getZigbeeEndPoint(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();        
        
        sql.appendFragment(buildZigbeeEndPointStatement());
        sql.append("WHERE ZE.DeviceId").eq(deviceId);
        
        ZigbeeEndpoint endpoint = yukonJdbcTemplate.queryForObject(sql, zigbeeEndPointRowMapper);

        return endpoint;
    }
    
    public ZigbeeEndpoint getZigbeeEndPointByMACAddress(String macAddress) {
        SqlStatementBuilder sql = new SqlStatementBuilder();        
        
        sql.appendFragment(buildZigbeeEndPointStatement());
        sql.append("WHERE ZE.MacAddress").eq(macAddress);
        
        ZigbeeEndpoint endpoint = yukonJdbcTemplate.queryForObject(sql, zigbeeEndPointRowMapper);

        return endpoint; 
    }
    
    private SqlFragmentSource buildZigbeeEndPointStatement() {
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
}