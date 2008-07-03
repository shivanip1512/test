package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

public class DisabledGroupProvider extends DeviceGroupProviderBase {
    
    private SimpleJdbcOperations jdbcTemplate;
    private PaoGroupsWrapper paoGroupsWrapper;
    private PaoDao paoDao;
    private final static String disableFlag = "Y";


    @Override
    public List<YukonDevice> getChildDevices(DeviceGroup group) {
        
            // return devices that belong to this route
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT ypo.paobjectid, ypo.type");
            sql.append("FROM YukonPAObject ypo");
            sql.append("JOIN DeviceMeterGroup dmg ON ypo.PAObjectID = dmg.DEVICEID");
            sql.append("WHERE ypo.DisableFlag = ?");
            
            YukonDeviceRowMapper mapper = new YukonDeviceRowMapper(paoGroupsWrapper);
            List<YukonDevice> devices = jdbcTemplate.query(sql.toString(), mapper, disableFlag);
            return devices;
    }

    @Override
    public List<DeviceGroup> getChildGroups(DeviceGroup group) {
        return Collections.emptyList();
    }
    
    @Override
    public boolean isChildDevice(DeviceGroup group, YukonDevice device) {
        boolean result = isDeviceDisabled(device);
        return result;
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

    private boolean isDeviceDisabled(YukonDevice device) {
        LiteYukonPAObject devicePao = paoDao.getLiteYukonPAO(device.getDeviceId());
        return devicePao.getDisableFlag().equals(disableFlag);
    }

    @Override
	public String getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
	    
        String whereString = identifier + " IN ( " +
                            " SELECT ypo.PAObjectID " +
                            " FROM YukonPAObject ypo " + 
                            " JOIN DeviceMeterGroup dmg ON ypo.PAObjectID = dmg.DEVICEID " +
                            " WHERE ypo.DisableFlag = '" + disableFlag + "') "; 
        return whereString;
	    
	}
	
}
