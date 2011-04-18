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
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.thirdparty.digi.model.ZigbeeDeviceAssignment;

public class GatewayDeviceDaoImpl implements GatewayDeviceDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
    public static YukonRowMapper<DigiGateway> digiGatewayRowMapper = new YukonRowMapper<DigiGateway>() {
        @Override
        public DigiGateway mapRow(YukonResultSet rs) throws SQLException {
            DigiGateway digiGateway = new DigiGateway();
            
            int deviceId = rs.getInt("DeviceId");
            String typeStr = rs.getString("Type");
            PaoType paoType = PaoType.getForDbString(typeStr);
            
            digiGateway.setPaoIdentifier(new PaoIdentifier(deviceId, paoType));
            digiGateway.setDigiId(rs.getInt("DigiId"));
            digiGateway.setMacAddress(rs.getString("MacAddress"));
            digiGateway.setFirmwareVersion(rs.getString("FirmwareVersion"));
            digiGateway.setName(rs.getString("ManufacturerSerialNumber"));
            
            return digiGateway;
        }
    };
    
    public DigiGateway getDigiGateway(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT DG.DeviceId,DG.DigiId,ZG.FirmwareVersion,");
        sql.append("           ZG.MacAddress,YPO.Type,HB.ManufacturerSerialNumber");
        sql.append("FROM DigiGateway DG ");
        sql.append("JOIN ZBGateway ZG ON DG.DeviceId = ZG.DeviceId");
        sql.append("JOIN YukonPAObject YPO ON DG.DeviceId = YPO.PAObjectID");
        sql.append("JOIN InventoryBase IB ON DG.DeviceId = IB.DeviceID");
        sql.append("JOIN LMHardwareBase HB ON IB.InventoryID = HB.InventoryID");
        sql.append("WHERE DG.DeviceId").eq(deviceId);
        
        DigiGateway digiGateway = yukonJdbcTemplate.queryForObject(sql, digiGatewayRowMapper
        );

        return digiGateway;
    }
    
    @Override
    public void createDigiGateway(DigiGateway digiGateway) {

        addZBGateway(digiGateway);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO DigiGateway");
        sql.values(digiGateway.getPaoIdentifier().getPaoId(), digiGateway.getDigiId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    private void addZBGateway(DigiGateway digiGateway) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        PaoIdentifier paoIdentifier = digiGateway.getPaoIdentifier();
        
        sql.append("INSERT INTO ZBGateway ");
        sql.values(paoIdentifier.getPaoId(),digiGateway.getFirmwareVersion(),digiGateway.getMacAddress());
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void deleteDigiGateway(DigiGateway digiGateway) {
        int paoId = digiGateway.getPaoIdentifier().getPaoId();
        
        deleteZBGateway(paoId);

    }

    private void deleteZBGateway(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("DELETE FROM ZBGateway");
        sql.append("WHERE DeviceId").eq(deviceId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void updateDigiGateway(DigiGateway digiGateway) {
        
    	updateZBGateway(digiGateway);
    	//TODO this and create should use SimpleTableAccessTemplate
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("UPDATE DigiGateway");
        sql.append("SET DigiId").eq(digiGateway.getDigiId());
        sql.append("WHERE DeviceId").eq(digiGateway.getPaoIdentifier().getPaoId());
        
        yukonJdbcTemplate.update(sql);
    }

    private void updateZBGateway(DigiGateway digiGateway) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("UPDATE ZBGateway");
    	sql.append("SET FirmwareVersion").eq(digiGateway.getFirmwareVersion()).append(",");
        sql.append("    MacAddress").eq(digiGateway.getMacAddress());
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
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
}