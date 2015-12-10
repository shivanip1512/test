package com.cannontech.thirdparty.digi.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.LmHardwareInventoryIdentifierMapper;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.RowMapper;
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
    @Autowired private YukonJdbcTemplate jdbcTemplate;

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
    
    private SqlStatementBuilder buildBaseZigbeeDeviceStatementBuilder() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT ZBG.DeviceId,ZBG.MacAddress,YPO.Type,YPO.PaoName");
        sql.append("FROM ZBGateway ZBG");
        sql.append(  "JOIN YukonPAObject YPO on ZBG.DeviceId = YPO.PaObjectId");
        
        return sql;
    }
    
    @Override
    public ZigbeeDevice getZigbeeGateway(int gatewayId) {
        SqlStatementBuilder sql = buildBaseZigbeeDeviceStatementBuilder();
        sql.append("WHERE ZBG.DeviceId").eq(gatewayId);
        
        ZigbeeDevice gateway = jdbcTemplate.queryForObject(sql, zigbeeDeviceRowMapper);
        
        return gateway;
    }
    
    @Override
    public List<ZigbeeDevice> getAllGateways() {
        SqlStatementBuilder sql = buildBaseZigbeeDeviceStatementBuilder();
        
        return jdbcTemplate.query(sql, zigbeeDeviceRowMapper);
    }
    
    @Override
    public List<ZigbeeDevice> getZigbeeGateways(Collection<Integer> gatewayIds) {
        SqlStatementBuilder sql = buildBaseZigbeeDeviceStatementBuilder();
        
        sql.append("WHERE ZBG.DeviceId").in(gatewayIds);
        
        return jdbcTemplate.query(sql, zigbeeDeviceRowMapper);
    }
    
    @Override
    public DigiGateway getDigiGateway(int deviceId) {
        SqlStatementBuilder sql = buildDigiGatewayStatement();
        sql.append("WHERE DG.DeviceId").eq(deviceId);
        
        DigiGateway digiGateway = jdbcTemplate.queryForObject(sql, digiGatewayRowMapper);

        return digiGateway;
    }
    
    @Override
    public DigiGateway getDigiGateway(String macAddress) {
        SqlStatementBuilder sql = buildDigiGatewayStatement();
        sql.append("WHERE ZG.MacAddress").eq(macAddress);
        
        DigiGateway digiGateway = jdbcTemplate.queryForObject(sql, digiGatewayRowMapper);

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
    public List<DigiGateway> getGatewaysForAccount(int accountId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT DG.DeviceId, DG.DigiId, ZG.FirmwareVersion, ZG.MacAddress, YPO.Type, HB.ManufacturerSerialNumber");
        sql.append("FROM DigiGateway DG ");
        sql.append(  "JOIN ZBGateway ZG ON DG.DeviceId = ZG.DeviceId");
        sql.append(  "JOIN YukonPAObject YPO ON DG.DeviceId = YPO.PAObjectID");
        sql.append(  "JOIN InventoryBase IB ON DG.DeviceId = IB.DeviceID");
        sql.append(  "JOIN LMHardwareBase HB ON IB.InventoryID = HB.InventoryID");
        sql.append("WHERE IB.AccountId").eq(accountId);
        
        return jdbcTemplate.query(sql, digiGatewayRowMapper);
    }
    
    @Override
    public List<ZigbeeDeviceAssignment> getZigbeeDevicesForAccount(int accountId, List<Integer> hardwareTypeIds) {
        // Let's not error out even if there are no Zigbee device types set up yet.
        if (CollectionUtils.isEmpty(hardwareTypeIds)) {
            return Collections.emptyList();
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT ib.DeviceId, zb.GatewayId");
        sql.append("FROM InventoryBase ib");
        sql.append(  "JOIN LMHardwareBase lmhb on lmhb.InventoryId = ib.InventoryId");
        sql.append(  "LEFT OUTER JOIN ZBGatewayToDeviceMapping zb on zb.DeviceId = ib.DeviceId");
        sql.append("WHERE ib.AccountId").eq(accountId);
        sql.append(  "AND lmhb.LMHardwareTypeId").in(hardwareTypeIds);
        
        return jdbcTemplate.query(sql, new YukonRowMapper<ZigbeeDeviceAssignment>() {
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
        
        return jdbcTemplate.query(sql, zigbeeDeviceRowMapper);
    }
    
    @Override
    public void updateDeviceToGatewayAssignment(int deviceId, Integer gatewayId) {
    	unassignDeviceFromGateway(deviceId);
    	
    	if (gatewayId != null) {
    	    SqlStatementBuilder sql = new SqlStatementBuilder();
        	sql =  new SqlStatementBuilder("INSERT INTO ZBGatewayToDeviceMapping").values(gatewayId, deviceId);
        	jdbcTemplate.update(sql);
    	}
    }
    
    @Override
    public void removeDevicesFromGateway(int gatewayId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ZBGatewayToDeviceMapping WHERE GatewayId").eq(gatewayId);
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public void unassignDeviceFromGateway(int deviceId) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("DELETE FROM ZBGatewayToDeviceMapping WHERE DeviceId").eq(deviceId);

    	jdbcTemplate.update(sql);
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
            return jdbcTemplate.queryForObject(sql, new LmHardwareInventoryIdentifierMapper());
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
            return jdbcTemplate.queryForInt(sql);
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
        
        return jdbcTemplate.query(sql,zigbeeDeviceRowMapper); 
    }
    
    @Override
    public List<ZigbeeDevice> getZigbeeGatewaysForInventoryIds(Collection<Integer> inventoryIds) {
        ChunkingSqlTemplate chunkingSqlTemplate = new ChunkingSqlTemplate(jdbcTemplate);
        
        List<ZigbeeDevice> gateways = chunkingSqlTemplate.query(new SqlFragmentGenerator<Integer>(){

            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                
                //We want to return the ZigbeeDevice object for inventoryIds that are EndPoints AND Gateways themselves.
                sql.append("SELECT ZBG.DeviceId, ZBG.MacAddress, YPO.Type, YPO.PaoName");
                sql.append("FROM InventoryBase IB");
                sql.append(  "JOIN ZBGateway ZBG on ZBG.DeviceId = IB.DeviceId");
                sql.append(  "JOIN YukonPaObject YPO on ZBG.DeviceId = YPO.PaObjectId");
                sql.append("WHERE IB.InventoryId").in(subList);
                sql.append("UNION");
                sql.append("SELECT ZBG2.DeviceId, ZBG2.MacAddress, YPO2.Type, YPO2.PaoName");
                sql.append("FROM InventoryBase IB2");
                sql.append(  "JOIN ZBEndPoint ZBEP on ZBEP.DeviceId = IB2.DeviceId");
                sql.append(  "JOIN ZBGatewayToDeviceMapping ZBGTDM on ZBGTDM.DeviceId = ZBEP.DeviceId");
                sql.append(  "JOIN ZBGateway ZBG2 on ZBG2.DeviceId = ZBGTDM.GatewayId");
                sql.append(  "JOIN YukonPaObject YPO2 on ZBG2.DeviceId = YPO2.PaObjectId");
                sql.append("WHERE IB2.InventoryId").in(subList);
                
                return sql;
            }
            
        }, inventoryIds, zigbeeDeviceRowMapper); 

        return  gateways;
    }
    
    @Override
    public List<Integer> getLMGroupIdByEndPointId(int endPointId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT LMHCG.LMGroupId");
        sql.append("FROM LMHardwareControlGroup LMHCG");
        sql.append(  "JOIN InventoryBase IB ON LMHCG.InventoryID = IB.InventoryID ");
        sql.append(  "JOIN ZBEndPoint ZEP on ZEP.DeviceId = IB.DeviceId");
        sql.append(  "JOIN YukonPaObject YPO on ZEP.DeviceId = YPO.PaObjectId");
        sql.append("WHERE YPO.PaObjectId").eq(endPointId);
        sql.append(  "AND LMHCG.GroupEnrollStop IS NULL");
        
        return jdbcTemplate.query(sql, RowMapper.INTEGER);
    }
    
    @Override
    public List<Integer> getLMGroupIdByGatewayId(int gatewayId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT LMHCG.LMGroupId");
        sql.append("FROM LMHardwareControlGroup LMHCG");
        sql.append(  "JOIN InventoryBase IB ON LMHCG.InventoryID = IB.InventoryID ");
        sql.append(  "JOIN ZBGateway ZG on ZG.DeviceId = IB.DeviceId");
        sql.append(  "JOIN YukonPaObject YPO on ZEP.DeviceId = YPO.PaObjectId");
        sql.append("WHERE YPO.PaObjectId").eq(gatewayId);
        sql.append(  "AND LMHCG.GroupEnrollStop IS NULL");
        
        return jdbcTemplate.query(sql, RowMapper.INTEGER);
    }
    
    @Override
    public void updateDigiId(PaoIdentifier paoIdentifier, int digiId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.update("DigiGateway");
        params.addValue("DigiId", digiId);
        
        sql.append("WHERE DeviceId").eq(paoIdentifier.getPaoId());
    
        jdbcTemplate.update(sql);
    }
    
    @Override
    public void updateFirmwareVersion(PaoIdentifier paoIdentifier, String firmwareVersion) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.update("ZBGateway");
        // This check is to make sure null is never set in firmwareVersion for YUK-14417
        if (StringUtils.isBlank(firmwareVersion)) {
            firmwareVersion ="";
        }
        params.addValue("FirmwareVersion", firmwareVersion);
        
        sql.append("WHERE DeviceId").eq(paoIdentifier.getPaoId());
    
        jdbcTemplate.update(sql);
    }
}
