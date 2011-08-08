package com.cannontech.thirdparty.digi.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeEndPoint;

public class ZigbeeDeviceDaoImpl implements ZigbeeDeviceDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
    private GatewayDeviceDao gatewayDeviceDao;
    
    private static YukonRowMapper<ZigbeeEndPoint> zigbeeEndPointRowMapper  = new YukonRowMapper<ZigbeeEndPoint>(){
        @Override
        public ZigbeeEndPoint mapRow(YukonResultSet rs) throws SQLException {
            ZigbeeEndPoint zigbeeEndPoint = new ZigbeeEndPoint();
            
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
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ZE.DeviceId,ZE.MacAddress,YPO.Type,YPO.PaoName");
        sql.append("FROM ZBEndPoint ZE");
        sql.append(  "JOIN YukonPAObject YPO ON ZE.DeviceId = YPO.PAObjectID");
        sql.append("WHERE DeviceId").eq(deviceId);
        
        ZigbeeDevice zigbeeDevice = yukonJdbcTemplate.queryForObject(sql, GatewayDeviceDaoImpl.zigbeeDeviceRowMapper);
        
        return zigbeeDevice;
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
    public ZigbeeEndPoint getZigbeeEndPointByInventoryId(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();        
        
        sql.appendFragment(buildZigbeeEndPointStatement());
        sql.append("WHERE IB.InventoryID").eq(inventoryId);
        
        ZigbeeEndPoint tstat = yukonJdbcTemplate.queryForObject(sql, zigbeeEndPointRowMapper);

        return tstat;
    }
    
    @Override
    public ZigbeeEndPoint getZigbeeEndPoint(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();        
        
        sql.appendFragment(buildZigbeeEndPointStatement());
        sql.append("WHERE ZE.DeviceId").eq(deviceId);
        
        ZigbeeEndPoint endpoint = yukonJdbcTemplate.queryForObject(sql, zigbeeEndPointRowMapper);

        return endpoint;
    }
    
    public ZigbeeEndPoint getZigbeeEndPointByMACAddress(String macAddress) {
        SqlStatementBuilder sql = new SqlStatementBuilder();        
        
        sql.appendFragment(buildZigbeeEndPointStatement());
        sql.append("WHERE ZE.MacAddress").eq(macAddress);
        
        ZigbeeEndPoint endpoint = yukonJdbcTemplate.queryForObject(sql, zigbeeEndPointRowMapper);

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
    @Transactional
    public void createZigbeeEndPoint(ZigbeeEndPoint zigbeeEndPoint) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.insertInto("ZBEndPoint");
        params.addValue("DeviceId", zigbeeEndPoint.getPaoIdentifier().getPaoId());
        params.addValue("InstallCode", zigbeeEndPoint.getInstallCode().toUpperCase());
        params.addValue("MacAddress", zigbeeEndPoint.getMacAddress().toUpperCase());
        params.addValue("NodeId", zigbeeEndPoint.getNodeId());
        params.addValue("DestinationEndPointId", zigbeeEndPoint.getDestinationEndPointId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void updateZigbeeEndPoint(ZigbeeEndPoint zigbeeEndPoint) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.update("ZBEndPoint");
        params.addValue("InstallCode", zigbeeEndPoint.getInstallCode().toUpperCase());
        params.addValue("MacAddress", zigbeeEndPoint.getMacAddress().toUpperCase());
        params.addValue("NodeId", zigbeeEndPoint.getNodeId());
        params.addValue("DestinationEndPointId", zigbeeEndPoint.getDestinationEndPointId());

        sql.append("WHERE DeviceId").eq(zigbeeEndPoint.getPaoIdentifier().getPaoId());
        
        yukonJdbcTemplate.update(sql);
        
        gatewayDeviceDao.updateDeviceToGatewayAssignment(zigbeeEndPoint.getPaoIdentifier().getPaoId(), zigbeeEndPoint.getGatewayId());
    }

    @Override
    public void deleteZigbeeEndPoint(int deviceId) {
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