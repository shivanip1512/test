package com.cannontech.common.device.groups.dao.impl.providers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

public class DeviceTypeGroupProvider extends DeviceGroupProviderBase {
    private SimpleJdbcOperations jdbcTemplate;
    private PaoGroupsWrapper paoGroupsWrapper;
    private PaoDao paoDao;

    @Override
    public List<YukonDevice> getChildDevices(DeviceGroup group) {
        
        if (group instanceof DeviceTypeDeviceGroup) {
            
            DeviceTypeDeviceGroup deviceTypeGroup = (DeviceTypeDeviceGroup) group;
            
            // return devices that belong to this route
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT ypo.paobjectid, ypo.type");
            sql.append("FROM Device d");
            sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
            sql.append("JOIN devicecarriersettings dcs ON (dcs.deviceid = ypo.paobjectid)");
            sql.append("JOIN devicemetergroup dmg ON (dmg.deviceid = ypo.paobjectid)");
            sql.append("JOIN DeviceRoutes dr ON (d.deviceid = dr.deviceid)");
            sql.append("WHERE ypo.type = ?");
            
            
            YukonDeviceRowMapper mapper = new YukonDeviceRowMapper(paoGroupsWrapper);
            List<YukonDevice> devices = jdbcTemplate.query(sql.toString(), mapper, deviceTypeGroup.type);
            
            return devices;
        }
        
        // this must be our parent group
        return Collections.emptyList();
    }

    @Override
    public List<? extends DeviceGroup> getChildGroups(DeviceGroup group) {
        if (group instanceof DeviceTypeDeviceGroup) {
            return Collections.emptyList();
        }
        
        final DeviceGroup groupForMapper = group;
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT ypo.type");
        sql.append("FROM Device d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("JOIN devicecarriersettings dcs ON (dcs.deviceid = ypo.paobjectid)");
        sql.append("JOIN devicemetergroup dmg ON (dmg.deviceid = ypo.paobjectid)");
        sql.append("JOIN DeviceRoutes dr ON (d.deviceid = dr.deviceid)");
        sql.append("ORDER BY ypo.type");

        ParameterizedRowMapper<DeviceTypeDeviceGroup> mapper = new ParameterizedRowMapper<DeviceTypeDeviceGroup>() {
            public DeviceTypeDeviceGroup mapRow(ResultSet rs, int rowNum) throws SQLException {

                DeviceTypeDeviceGroup deviceTypeGroup = new DeviceTypeDeviceGroup();
                deviceTypeGroup.type = rs.getString("type");
                deviceTypeGroup.setName(rs.getString("type"));
                deviceTypeGroup.setParent(groupForMapper);
                deviceTypeGroup.setType(groupForMapper.getType());
                return deviceTypeGroup;
            }
        };
        List<DeviceTypeDeviceGroup> resultList = jdbcTemplate.query(sql.toString(), mapper);
          
        
        return resultList;
    }
    
    public Set<? extends DeviceGroup> getGroups(DeviceGroup base, YukonDevice device) {
        
        LiteYukonPAObject devicePao = paoDao.getLiteYukonPAO(device.getDeviceId());
        String type = paoGroupsWrapper.getPAOTypeString(devicePao.getType());
        
        DeviceTypeDeviceGroup routeGroup = new DeviceTypeDeviceGroup();
        routeGroup.type = type;
        routeGroup.setName(type);
        routeGroup.setParent(base);
        routeGroup.setType(base.getType());
    
        return Collections.singleton(routeGroup);
    }

    private class DeviceTypeDeviceGroup extends DeviceGroup {
        public String type;

        @Override
        public boolean isEditable() {
            return false;
        }

        @Override
        public boolean isModifiable() {
            return false;
        }
    }

    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }

    public boolean isDeviceInGroup(DeviceGroup deviceGroup, YukonDevice device) {
        
        LiteYukonPAObject devicePao = paoDao.getLiteYukonPAO(device.getDeviceId());

        if (deviceGroup instanceof DeviceTypeDeviceGroup) {
            DeviceTypeDeviceGroup deviceTypeGroup = (DeviceTypeDeviceGroup) deviceGroup;
            if(PAOGroups.getPAOTypeString(devicePao.getType()) == deviceTypeGroup.type){
                return true;
            }else{
                return false;
            }
        }
        else if (deviceGroup.getType().equals(DeviceGroupType.DEVICETYPE)) {
            int deviceType = devicePao.getType();
            if(DeviceTypesFuncs.isMCT(deviceType) || DeviceTypesFuncs.isMeter(deviceType)){
                return true;
            }
            return false;
        }
        else {
            return false;
        }
    }

	@Override
	public String getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {

        DeviceTypeDeviceGroup deviceTypeGroup = (DeviceTypeDeviceGroup) group;
        String type = deviceTypeGroup.type;
        String whereString = identifier + " IN ( " +
        					" SELECT ypo_dg.paobjectid " +
                            " FROM YukonPAObject ypo_dg " + 
        					" WHERE ypo_dg.type = '" + type + "') "; 
        return whereString;
	}
}
