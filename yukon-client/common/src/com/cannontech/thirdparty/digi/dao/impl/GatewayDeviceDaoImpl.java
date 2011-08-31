package com.cannontech.thirdparty.digi.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.LmHardwareInventoryIdentifierMapper;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.thirdparty.model.GenericZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeDeviceAssignment;

public class GatewayDeviceDaoImpl implements GatewayDeviceDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
    
    public static YukonRowMapper<DigiGateway> digiGatewayRowMapper = new YukonRowMapper<DigiGateway>() {
        @Override
        public DigiGateway mapRow(YukonResultSet rs) throws SQLException {
            DigiGateway digiGateway = new DigiGateway();
            
            int deviceId = rs.getInt("DeviceId");
            PaoType paoType = rs.getEnum("Type", PaoType.class);
            
            digiGateway.setPaoIdentifier(new PaoIdentifier(deviceId, paoType));
            digiGateway.setDigiId(rs.getInt("DigiId"));
            digiGateway.setMacAddress(rs.getString("MacAddress"));
            digiGateway.setFirmwareVersion(rs.getString("FirmwareVersion"));
            digiGateway.setName(rs.getString("ManufacturerSerialNumber"));
            
            return digiGateway;
        }
    };
    
    public static YukonRowMapper<ZigbeeDevice> zigbeeDeviceRowMapper = new YukonRowMapper<ZigbeeDevice>() {
        @Override
        public ZigbeeDevice mapRow(YukonResultSet rs) throws SQLException {
            GenericZigbeeDevice device = new GenericZigbeeDevice();
            
            int paoId = rs.getInt("DeviceId");
            PaoType paoType = rs.getEnum("Type", PaoType.class);
            
            device.setPaoIdentifier(new PaoIdentifier(paoId, paoType));
            device.setZigbeeMacAddress(rs.getString("MacAddress"));
            device.setName(rs.getString("PaoName"));
            
            return device;
        }
    };
    
    public ZigbeeDevice getZigbeeGateway(int gatewayId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ZBG.DeviceId,ZBG.MacAddress,YPO.Type,YPO.PaoName");
        sql.append("FROM ZBGateway ZBG");
        sql.append(  "JOIN YukonPAObject YPO on ZBG.DeviceId = YPO.PaObjectId");
        sql.append("WHERE ZBG.DeviceId").eq(gatewayId);
        
        ZigbeeDevice gateway = yukonJdbcTemplate.queryForObject(sql, zigbeeDeviceRowMapper);
        
        return gateway;
    }
    
    public List<ZigbeeDevice> getAllGateways() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ZBG.DeviceId,ZBG.MacAddress,YPO.Type,YPO.PaoName");
        sql.append("FROM ZBGateway ZBG");
        sql.append(  "JOIN YukonPAObject YPO on ZBG.DeviceId = YPO.PaObjectId");
        
        return yukonJdbcTemplate.query(sql, zigbeeDeviceRowMapper);
    }
    
    public DigiGateway getDigiGateway(int deviceId) {
        SqlStatementBuilder sql = buildDigiGatewayStatement();
        sql.append("WHERE DG.DeviceId").eq(deviceId);
        
        DigiGateway digiGateway = yukonJdbcTemplate.queryForObject(sql, digiGatewayRowMapper);

        return digiGateway;
    }
    
    public DigiGateway getDigiGateway(String macAddress) {
        SqlStatementBuilder sql = buildDigiGatewayStatement();
        sql.append("WHERE ZG.MacAddress").eq(macAddress);
        
        DigiGateway digiGateway = yukonJdbcTemplate.queryForObject(sql, digiGatewayRowMapper);

        return digiGateway;
    }
    
    private SqlStatementBuilder buildDigiGatewayStatement() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT DG.DeviceId,DG.DigiId,ZG.FirmwareVersion,");
        sql.append("           ZG.MacAddress,YPO.Type,HB.ManufacturerSerialNumber");
        sql.append("FROM DigiGateway DG ");
        sql.append(  "JOIN ZBGateway ZG ON DG.DeviceId = ZG.DeviceId");
        sql.append(  "JOIN YukonPAObject YPO ON DG.DeviceId = YPO.PAObjectID");
        sql.append(  "JOIN InventoryBase IB ON DG.DeviceId = IB.DeviceID");
        sql.append(  "JOIN LMHardwareBase HB ON IB.InventoryID = HB.InventoryID");
        
        return sql;
    }
    
    @Override
    public void createDigiGateway(DigiGateway digiGateway) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.insertInto("DigiGateway");
        params.addValue("DeviceId", digiGateway.getPaoIdentifier().getPaoId());
        params.addValue("DigiId", digiGateway.getDigiId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void createZigbeeGateway(DigiGateway digiGateway) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        PaoIdentifier paoIdentifier = digiGateway.getPaoIdentifier();
        
        SqlParameterSink params = sql.insertInto("ZBGateway");
        params.addValue("DeviceId", paoIdentifier.getPaoId());
        params.addValue("FirmwareVersion", digiGateway.getFirmwareVersion());
        params.addValue("MacAddress", digiGateway.getMacAddress().toUpperCase());
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void deleteDigiGateway(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("DELETE FROM ZBGateway");
        sql.append("WHERE DeviceId").eq(deviceId);
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void updateDigiGateway(DigiGateway digiGateway) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink param = sql.update("DigiGateway");
        param.addValue("DigiId", digiGateway.getDigiId());

        sql.append("WHERE DeviceId").eq(digiGateway.getPaoIdentifier().getPaoId());
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void updateZigbeeGateway(DigiGateway digiGateway) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	SqlParameterSink params = sql.update("ZBGateway");
    	params.addValue("FirmwareVersion",digiGateway.getFirmwareVersion());
    	params.addValue("MacAddress",digiGateway.getMacAddress().toUpperCase());

        sql.append("WHERE DeviceId").eq(digiGateway.getPaoIdentifier().getPaoId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public List<DigiGateway> getGatewaysForAccount(int accountId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT DG.DeviceId, DG.DigiId, ZG.FirmwareVersion, ZG.MacAddress, YPO.Type, HB.ManufacturerSerialNumber");
        sql.append("FROM DigiGateway DG ");
        sql.append(  "JOIN ZBGateway ZG ON DG.DeviceId = ZG.DeviceId");
        sql.append(  "JOIN YukonPAObject YPO ON DG.DeviceId = YPO.PAObjectID");
        sql.append(  "JOIN InventoryBase IB ON DG.DeviceId = IB.DeviceID");
        sql.append(  "JOIN LMHardwareBase HB ON IB.InventoryID = HB.InventoryID");
        sql.append("WHERE IB.AccountId").eq(accountId);
        
        return yukonJdbcTemplate.query(sql, digiGatewayRowMapper);
    }
    
    @Override
    public List<ZigbeeDeviceAssignment> getZigbeeDevicesForAccount(int accountId, List<Integer> hardwareTypeIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT ib.DeviceId, zb.GatewayId");
        sql.append("FROM InventoryBase ib");
        sql.append(  "JOIN LMHardwareBase lmhb on lmhb.InventoryId = ib.InventoryId");
        sql.append(  "LEFT OUTER JOIN ZBGatewayToDeviceMapping zb on zb.DeviceId = ib.DeviceId");
        sql.append("WHERE ib.AccountId").eq(accountId);
        sql.append(  "AND lmhb.LMHardwareTypeId").in(hardwareTypeIds);
        
        return yukonJdbcTemplate.query(sql, new YukonRowMapper<ZigbeeDeviceAssignment>() {
                @Override
                public ZigbeeDeviceAssignment mapRow(YukonResultSet rs) throws SQLException {
                	ZigbeeDeviceAssignment assignment = new ZigbeeDeviceAssignment();
                	assignment.setDeviceId(rs.getInt("DeviceId"));
                	Integer gatewayId = rs.getNullableInt("GatewayId");
                	assignment.setGatewayId(gatewayId);
                    return assignment;
                }
            }
        );
    }
    
    @Override
    public List<ZigbeeDevice> getAssignedZigbeeDevices(int gatewayId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT ZEP.DeviceId,ZEP.MacAddress,YPO.Type,YPO.PaoName");
        sql.append("FROM ZBEndPoint ZEP");
        sql.append(  "JOIN YukonPAObject YPO on ZEP.DeviceId = YPO.PaObjectId");
        sql.append(  "JOIN ZBGatewayToDeviceMapping ZBDM on ZBDM.DeviceId = ZEP.DeviceId");
        sql.append("WHERE ZBDM.GatewayId").eq(gatewayId);
        
        return yukonJdbcTemplate.query(sql, zigbeeDeviceRowMapper);
    }
    
    @Override
    public void updateDeviceToGatewayAssignment(int deviceId, Integer gatewayId) {
    	unassignDeviceFromGateway(deviceId);
    	
    	if (gatewayId != null) {
    	    SqlStatementBuilder sql = new SqlStatementBuilder();
        	sql =  new SqlStatementBuilder("INSERT INTO ZBGatewayToDeviceMapping").values(gatewayId, deviceId);
        	yukonJdbcTemplate.update(sql);
    	}
    }
    
    @Override
    public void removeDevicesFromGateway(int gatewayId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ZBGatewayToDeviceMapping WHERE GatewayId").eq(gatewayId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void unassignDeviceFromGateway(int deviceId) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("DELETE FROM ZBGatewayToDeviceMapping WHERE DeviceId").eq(deviceId);

    	yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public InventoryIdentifier findGatewayByDeviceMapping(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.InventoryId, yle.YukonDefinitionId");
        sql.append("FROM InventoryBase ib");
        sql.append(  "JOIN LMHardwareBase hb ON hb.InventoryId = ib.InventoryId");
        sql.append(  "JOIN YukonListEntry yle ON yle.EntryId = hb.LmHardwareTypeId");
        sql.append(  "JOIN ZBGatewayToDeviceMapping zb on zb.GatewayId = ib.DeviceId");
        sql.append("WHERE zb.DeviceId").eq(deviceId);
        
        try {
            return yukonJdbcTemplate.queryForObject(sql, new LmHardwareInventoryIdentifierMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public Integer findGatewayIdForDeviceId(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GatewayId");
        sql.append("FROM ZBGatewayToDeviceMapping");
        sql.append("WHERE DeviceId").eq(deviceId);
        
        try {
            return yukonJdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public List<ZigbeeDevice> getZigbeeGatewaysForGroupId(int groupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT ZG.DeviceId,ZG.MacAddress,YPO.Type,YPO.PaoName");
        sql.append("FROM LMHardwareControlGroup LMHCG");
        sql.append(  "JOIN InventoryBase IB ON LMHCG.InventoryID = IB.InventoryID ");
        sql.append(  "JOIN ZBGatewayToDeviceMapping ZB on ZB.DeviceId = IB.DeviceId");
        sql.append(  "JOIN ZBGateway ZG on ZG.DeviceId = ZB.GatewayId");
        sql.append(  "JOIN YukonPaObject YPO on ZG.DeviceId = YPO.PaObjectId");
        sql.append("WHERE LMHCG.LMGroupId").eq(groupId);
        sql.append(  "AND LMHCG.GroupEnrollStop IS NULL");
        
        return yukonJdbcTemplate.query(sql,zigbeeDeviceRowMapper); 
    }
    
    @Override
    public int getLMGroupIdByDeviceId(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT LMHCG.LMGroupId");
        sql.append("FROM LMHardwareControlGroup LMHCG");
        sql.append(  "JOIN InventoryBase IB ON LMHCG.InventoryID = IB.InventoryID ");
        sql.append(  "JOIN ZBEndPoint ZEP on ZEP.DeviceId = IB.DeviceId");
        sql.append(  "JOIN YukonPaObject YPO on ZEP.DeviceId = YPO.PaObjectId");
        sql.append("WHERE YPO.PaObjectId").eq(deviceId);
        sql.append(  "AND LMHCG.GroupEnrollStop IS NULL");
        
        return yukonJdbcTemplate.queryForInt(sql);
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
}