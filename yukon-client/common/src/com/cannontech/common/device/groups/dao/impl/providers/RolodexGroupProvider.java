package com.cannontech.common.device.groups.dao.impl.providers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

public class RolodexGroupProvider extends DeviceGroupProviderBase {
    private SimpleJdbcOperations jdbcTemplate;
    private PaoGroupsWrapper paoGroupsWrapper;
    private PaoDao paoDao;

    @Override
    public List<YukonDevice> getChildDevices(DeviceGroup group) {
        if (group instanceof RolodexLetterDeviceGroup) {
            RolodexLetterDeviceGroup rolodexGroup = (RolodexLetterDeviceGroup) group;
            // return devices that start with some leter
            String matcher = Character.toUpperCase(rolodexGroup.firstLetter) + "%";
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("select ypo.paobjectid, ypo.type");
            sql.append("from Device d");
            sql.append("join YukonPaObject ypo on d.deviceid = ypo.paobjectid");
            sql.append("where upper(ypo.PAOName) like ?");
            YukonDeviceRowMapper mapper = new YukonDeviceRowMapper(paoGroupsWrapper);
            List<YukonDevice> devices = jdbcTemplate.query(sql.toString(), mapper, matcher);
            return Collections.unmodifiableList(devices);
        }
        // this must be our parent group
        return Collections.emptyList();
    }

    @Override
    public List<DeviceGroup> getChildGroups(DeviceGroup group) {
        if (group instanceof RolodexLetterDeviceGroup) {
            return Collections.emptyList();
        }
        List<DeviceGroup> resultList = new ArrayList<DeviceGroup>(26);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select distinct SUBSTRING(ypo.PAOName, 1, 1)");
        sql.append("from YukonPaObject ypo");
        sql.append("order by SUBSTRING(ypo.PAOName, 1, 1)");
        List<String> leadCharacters = jdbcTemplate.query(sql.toString(), new ParameterizedRowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(1);
            }
        });
                
        for (String currentLetter : leadCharacters) {
            if(!currentLetter.isEmpty()){
                RolodexLetterDeviceGroup letterGroup = new RolodexLetterDeviceGroup();
                letterGroup.firstLetter = currentLetter.charAt(0);
                letterGroup.setName(currentLetter);
                letterGroup.setParent(group);
                letterGroup.setType(group.getType());
                resultList.add(letterGroup);
            }
        }
        
        return Collections.unmodifiableList(resultList);
    }

    
    
    public Set<DeviceGroup> getGroupMembership(DeviceGroup base, YukonDevice device) {
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(device.getDeviceId());
        
        char currentLetter = liteYukonPAO.getPaoName().charAt(0);
        RolodexLetterDeviceGroup letterGroup = new RolodexLetterDeviceGroup();
        letterGroup.firstLetter = currentLetter;
        letterGroup.setName(String.valueOf(currentLetter));
        letterGroup.setParent(base);
        letterGroup.setType(base.getType());
    
        // helps the singleton method be happy
        DeviceGroup result = letterGroup;
    
        return Collections.singleton(result);
    }

    private class RolodexLetterDeviceGroup extends DeviceGroup {
        public char firstLetter;

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
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(device.getDeviceId());
        char deviceFirstLetter = liteYukonPAO.getPaoName().charAt(0);
        char groupFirstLetter = deviceGroup.getName().charAt(0);
        if(groupFirstLetter == deviceFirstLetter){
            return true;
        }else{
            return false;
        }
    }

	@Override
	public String getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
	    
	    if (group instanceof RolodexLetterDeviceGroup) {
    		RolodexLetterDeviceGroup rolodexGroup = (RolodexLetterDeviceGroup) group;
            // return devices that start with some leter
            String matcher = Character.toUpperCase(rolodexGroup.firstLetter) + "%";
            String whereString = identifier + " IN ( " +
            					" SELECT DISTINCT PAO.PAOBJECTID FROM YUKONPAOBJECT PAO " + 
            					" WHERE UPPER(PAO.PAONAME) like '" + matcher + "') "; 
            return whereString;
	    }
	    else {
	        // because there are no child devices under this dynamic group
	        return "0 = 1";
	    }
	}
	
	@Override
    public String getDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        
        if (group instanceof RolodexLetterDeviceGroup) {
            return super.getDeviceGroupSqlWhereClause(group, identifier);
        }
        else {
            // because the nature of this group is that it contains all devices
            return "1=1";
        }
    }
}
