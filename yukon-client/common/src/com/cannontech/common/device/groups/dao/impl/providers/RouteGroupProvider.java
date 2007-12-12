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
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

public class RouteGroupProvider extends DeviceGroupProviderBase {
    private SimpleJdbcOperations jdbcTemplate;
    private PaoGroupsWrapper paoGroupsWrapper;
    private PaoDao paoDao;

    @Override
    public List<YukonDevice> getChildDevices(DeviceGroup group) {
        
        if (group instanceof RouteDeviceGroup) {
            
            RouteDeviceGroup routeGroup = (RouteDeviceGroup) group;
            
            // return devices that belong to this route
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT ypo.paobjectid, ypo.type");
            sql.append("FROM DeviceMeterGroup d");
            sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
            sql.append("JOIN DeviceRoutes dr ON (d.deviceid = dr.deviceid)");
            sql.append("WHERE dr.routeid = ?");
            
            YukonDeviceRowMapper mapper = new YukonDeviceRowMapper(paoGroupsWrapper);
            List<YukonDevice> devices = jdbcTemplate.query(sql.toString(), mapper, routeGroup.routeId);
            
            return Collections.unmodifiableList(devices);
        }
        
        // this must be our parent group
        return Collections.emptyList();
    }

    @Override
    public List<DeviceGroup> getChildGroups(DeviceGroup group) {
        if (group instanceof RouteDeviceGroup) {
            return Collections.emptyList();
        }
        
        final DeviceGroup groupForMapper = group;
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paoname, ypo.paobjectid");
        sql.append("FROM YukonPaObject ypo");
        sql.append("WHERE ypo.category = ?");
        sql.append("ORDER BY ypo.paoname");

        ParameterizedRowMapper<DeviceGroup> mapper = new ParameterizedRowMapper<DeviceGroup>() {
            public DeviceGroup mapRow(ResultSet rs, int rowNum) throws SQLException {

                RouteDeviceGroup routeGroup = new RouteDeviceGroup();
                routeGroup.routeId = rs.getInt("paobjectid");
                routeGroup.setName(rs.getString("paoname"));
                routeGroup.setParent(groupForMapper);
                routeGroup.setType(groupForMapper.getType());
                return routeGroup;
            }
        };
        
        List<DeviceGroup> resultList = jdbcTemplate.query(sql.toString(), mapper, PAOGroups.getCategory(PAOGroups.CAT_ROUTE));
        
        return Collections.unmodifiableList(resultList);
    }
    
    public Set<DeviceGroup> getGroupMembership(DeviceGroup base, YukonDevice device) {
        
        LiteYukonPAObject devicePao = paoDao.getLiteYukonPAO(device.getDeviceId());
        LiteYukonPAObject routePao = paoDao.getLiteYukonPAO(devicePao.getRouteID());
        
        RouteDeviceGroup routeGroup = new RouteDeviceGroup();
        routeGroup.routeId = routePao.getLiteID();
        routeGroup.setName(routePao.getPaoName());
        routeGroup.setParent(base);
        routeGroup.setType(base.getType());
    
        // helps the singleton method be happy
        DeviceGroup result = routeGroup;
    
        return Collections.singleton(result);
    }

    private class RouteDeviceGroup extends DeviceGroup {
        public int routeId;

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
        
        if (deviceGroup instanceof RouteDeviceGroup) {
            RouteDeviceGroup routeGroup = (RouteDeviceGroup) deviceGroup;
            if (devicePao.getRouteID() == routeGroup.routeId) {
                return true;
            } else {
                return false;
            }
        } else if (deviceGroup.getType().equals(DeviceGroupType.ROUTE)) {

            if(devicePao.getRouteID() > 0 ){
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

	@Override
	public String getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
	    
	    if (group instanceof RouteDeviceGroup) {
    		RouteDeviceGroup routeGroup = (RouteDeviceGroup) group;
            int routeId = routeGroup.routeId;
            String whereString = identifier + " IN ( " +
            					" SELECT dr_dg.deviceid " +
                                " FROM DeviceRoutes dr_dg " + 
            					" WHERE dr_dg.routeid = " + routeId + ") "; 
            return whereString;
	    }
	    else {
	        // because there are no child devices under this dynamic group
            return "0 = 1";
	    }
	}
	
	@Override
    public String getDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        
        if (group instanceof RouteDeviceGroup) {
            return super.getDeviceGroupSqlWhereClause(group, identifier);
        }
        else {
            // because the nature of this group is that it contains all devices
            return "1=1";
        }
    }
}
