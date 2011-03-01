package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.ZigbeeThermostat;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.stars.dr.hardware.dao.GatewayDeviceDao;
import com.cannontech.stars.dr.hardware.dao.ZigbeeDeviceDao;

public class ZigbeeDeviceDaoImpl implements ZigbeeDeviceDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
    private PaoDao paoDao;
    private GatewayDeviceDao gatewayDeviceDao;
    
    @Override
    public ZigbeeThermostat getZigbeeUtilPro(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT DeviceId,InstallCode ");
        sql.append("FROM ZBDevice");
        sql.append("WHERE DeviceId").eq(deviceId);
        
        ZigbeeThermostat tstat = yukonJdbcTemplate.queryForObject(sql, new YukonRowMapper<ZigbeeThermostat>()
            {
                @Override
                public ZigbeeThermostat mapRow(YukonResultSet rs)
                        throws SQLException {
                    ZigbeeThermostat zigbeeThermostat = new ZigbeeThermostat();
                    
                    int deviceId = rs.getInt("DeviceId");
                    
                    zigbeeThermostat.setPaoIdentifier(new PaoIdentifier(deviceId, PaoType.ZIGBEEUTILPRO));
                    zigbeeThermostat.setInstallCode(rs.getString("InstallCode"));
                    
                    return zigbeeThermostat;
                }
            }
        );

        return tstat;
    }
    
    @Override
    public void createZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat) {
        PaoIdentifier pao = zigbeeThermostat.getPaoIdentifier();
        
        paoDao.addYukonPao(zigbeeThermostat.getPaoIdentifier(), zigbeeThermostat.getName());
        paoDao.addYukonDevice(pao);
        addZBDevice(pao, zigbeeThermostat.getInstallCode());
        
        return;
    }

    private void addZBDevice(PaoIdentifier paoIdentifier, String installCode) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("INSERT INTO ZBDevice (DeviceId,InstallCode)");
        sql.values(paoIdentifier.getPaoId(),installCode);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void updateZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat) {
        PaoIdentifier pao = zigbeeThermostat.getPaoIdentifier();
        
        paoDao.updateYukonPao(pao, zigbeeThermostat.getName());
        updateZBDevice(pao,zigbeeThermostat.getInstallCode());
        
        return;
    }
    
    private void updateZBDevice(PaoIdentifier paoIdentifier, String installCode) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("UPDATE ZBDevice");
        sql.append("SET InstallCode").eq(installCode);
        sql.append("WHERE DeviceId").eq(paoIdentifier.getPaoId());
        
        yukonJdbcTemplate.update(sql);
    }    
    
    @Override
    public void deleteZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat) {
        PaoIdentifier pao = zigbeeThermostat.getPaoIdentifier();
        
        deleteZBDevice(pao);
        
        //Remove device from any gateways
        gatewayDeviceDao.unassignDeviceFromGateway(pao.getPaoId());
        
        paoDao.deleteYukonDevice(pao);
        paoDao.deleteYukonPao(pao);
        
        return;
    }
    
    private void deleteZBDevice(PaoIdentifier paoIdentifier) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("DELETE FROM ZBDevice");
        sql.append("WHERE DeviceId").eq(paoIdentifier.getPaoId());
        
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
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
    	this.gatewayDeviceDao = gatewayDeviceDao;
    }
}
