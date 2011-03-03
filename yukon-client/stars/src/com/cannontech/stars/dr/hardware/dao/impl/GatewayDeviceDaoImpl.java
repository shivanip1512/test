package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.DigiGateway;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.TransactionException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.stars.dr.hardware.dao.GatewayDeviceDao;
import com.cannontech.stars.dr.hardware.model.ZigbeeDeviceAssignment;

public class GatewayDeviceDaoImpl implements GatewayDeviceDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
    private PaoDao paoDao;
    private DeviceDao deviceDao;

    public DigiGateway getDigiGateway(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT DG.DeviceId,DG.DigiId,ZG.FirmwareVersion,ZG.MacAddress");
        sql.append("FROM DigiGateway DG ");
        sql.append("JOIN ZBGateway ZG ON DG.DeviceId = ZG.DeviceId");
        sql.append("WHERE DG.DeviceId").eq(deviceId);
        
        DigiGateway digiGateway = yukonJdbcTemplate.queryForObject(sql, new YukonRowMapper<DigiGateway>()
            {
                @Override
                public DigiGateway mapRow(YukonResultSet rs)
                        throws SQLException {
                    DigiGateway digiGateway = new DigiGateway();
                    
                    int deviceId = rs.getInt("DeviceId");
                    
                    digiGateway.setPaoIdentifier(new PaoIdentifier(deviceId, PaoType.DIGIGATEWAY));
                    digiGateway.setDigiId(rs.getInt("DigiId"));
                    digiGateway.setMacAddress(rs.getString("MacAddress"));
                    digiGateway.setFirmwareVersion(rs.getString("FirmwareVersion"));
                    
                    return digiGateway;
                }
            }
        );

        return digiGateway;
    }
    
    @Override
    public void createDigiGateway(DigiGateway digiGateway) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        PaoIdentifier paoIdentifier = digiGateway.getPaoIdentifier();

        paoDao.addYukonPao(paoIdentifier, digiGateway.getMacAddress());
        paoDao.addYukonDevice(paoIdentifier);
        
        addZBGateway(digiGateway);
        
        sql.append("INSERT INTO DigiGateway");
        sql.values(paoIdentifier.getPaoId(), digiGateway.getDigiId());
        
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

        deviceDao.removeDevice(digiGateway);
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
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("UPDATE DigiGateway");
        sql.append("SET DigiId").eq(digiGateway.getDigiId());
        sql.append("WHERE DeviceId").eq(digiGateway.getPaoIdentifier().getPaoId());
        
        yukonJdbcTemplate.update(sql);
        
        paoDao.updateYukonPao(digiGateway.getPaoIdentifier(), digiGateway.getMacAddress());
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
    public List<ZigbeeDeviceAssignment> getAssignedDevices(int gatewayId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT DeviceId,GatewayId");
        sql.append("FROM ZBGatewayToDeviceMapping");
        sql.append("WHERE GatewayId").eq(gatewayId);
        
        return yukonJdbcTemplate.query(sql, new YukonRowMapper<ZigbeeDeviceAssignment>()
                {
                    @Override
                    public ZigbeeDeviceAssignment mapRow(YukonResultSet rs)
                            throws SQLException {
                    	ZigbeeDeviceAssignment assignment = new ZigbeeDeviceAssignment();
                        
                    	assignment.setDeviceId(rs.getInt("DeviceId"));
                    	assignment.setGatewayId(rs.getInt("GatewayId"));
                        
                        return assignment;
                    }
                }
            );
    }
    
    @Override
    public void assignDeviceToGateway(int deviceId, int gatewayId) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("INSERT INTO ZBGatewayToDeviceMapping ").values(gatewayId, deviceId);
    	
    	yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void unassignDeviceFromGateway(int deviceId) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("DELETE FROM ZBGatewayToDeviceMapping WHERE DeviceId").eq(deviceId);

    	yukonJdbcTemplate.update(sql);
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
}
