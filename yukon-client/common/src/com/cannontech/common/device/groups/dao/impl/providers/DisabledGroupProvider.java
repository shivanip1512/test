package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.MutableDeviceGroup;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

public class DisabledGroupProvider extends DeviceGroupProviderBase {
    
    private SimpleJdbcOperations jdbcTemplate;
    private PaoGroupsWrapper paoGroupsWrapper;
    private PaoDao paoDao;

    @Override
    public List<YukonDevice> getChildDevices(DeviceGroup group) {
        
            // return devices that belong to this route
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT ypo.paobjectid, ypo.type");
            sql.append("FROM YukonPAObject ypo");
            sql.append("JOIN DeviceMeterGroup dmg ON ypo.PAObjectID = dmg.DEVICEID");
            sql.append("LEFT OUTER JOIN DeviceCarrierSettings dcs ON ypo.PAObjectID = dcs.DEVICEID");
            sql.append("LEFT OUTER JOIN DeviceRoutes dr ON ypo.PAObjectID = dr.DEVICEID");
            sql.append("WHERE ypo.DisableFlag = ?");
            
            YukonDeviceRowMapper mapper = new YukonDeviceRowMapper(paoGroupsWrapper);
            List<YukonDevice> devices = jdbcTemplate.query(sql.toString(), mapper, DisabledDeviceGroup.disableFlag);
            return devices;
    }

    @Override
    public List<DeviceGroup> getChildGroups(DeviceGroup group) {
        
        return Collections.emptyList();
    }
    
    public Set<DeviceGroup> getGroupMembership(DeviceGroup base, YukonDevice device) {
        
        LiteYukonPAObject devicePao = paoDao.getLiteYukonPAO(device.getDeviceId());
        
        if (!devicePao.getDisableFlag().equals(DisabledDeviceGroup.disableFlag)) {
            return Collections.emptySet();
        }
        
        DisabledDeviceGroup disabledDeviceGroup = new DisabledDeviceGroup();
        disabledDeviceGroup.setName(base.getName());
        disabledDeviceGroup.setParent(base);
        disabledDeviceGroup.setType(base.getType());
        
        // helps the singleton method be happy
        DeviceGroup result = disabledDeviceGroup;
    
        return Collections.singleton(result);
    }

    private class DisabledDeviceGroup extends MutableDeviceGroup {
        
        public final static String disableFlag = "Y";

        @Override
        public boolean isEditable() {
            return false;
        }

        @Override
        public boolean isModifiable() {
            return false;
        }
        
        @Override
        public boolean isHidden() {
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
        
        if (deviceGroup instanceof DisabledDeviceGroup) {
            
            if (devicePao.getDisableFlag().equals(DisabledDeviceGroup.disableFlag)) {
                return true;
            }
        } else if (deviceGroup.getType().equals(DeviceGroupType.DISABLED)) {

            if(devicePao.getDisableFlag().equals(DisabledDeviceGroup.disableFlag)){
                return true;
            }
        } 
        
        return false;
    }

    @Override
	public String getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
	    
        String whereString = identifier + " IN ( " +
                            " SELECT ypo.PAObjectID " +
                            " FROM YukonPAObject ypo " + 
                            " JOIN DeviceMeterGroup dmg ON ypo.PAObjectID = dmg.DEVICEID " +
                            " LEFT OUTER JOIN DeviceCarrierSettings dcs ON ypo.PAObjectID = dcs.DEVICEID " +
                            " LEFT OUTER JOIN DeviceRoutes dr ON ypo.PAObjectID = dr.DEVICEID " +
                            " WHERE ypo.DisableFlag = '" + DisabledDeviceGroup.disableFlag + "') "; 
        return whereString;
	    
	}
	
	@Override
    public String getDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        
        if (group instanceof DisabledDeviceGroup) {
            return getChildDeviceGroupSqlWhereClause(group, identifier);
        }
        else {
            return "1=1";
        }
    }
}
