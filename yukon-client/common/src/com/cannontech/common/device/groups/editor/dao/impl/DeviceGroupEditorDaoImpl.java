package com.cannontech.common.device.groups.editor.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.model.StaticDeviceGroup;
import com.cannontech.common.device.groups.util.YukonDeviceToIdMapper;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class DeviceGroupEditorDaoImpl implements DeviceGroupEditorDao {
    private SimpleJdbcOperations jdbcTemplate;
    private PaoGroupsWrapper paoGroupsWrapper;
    private NextValueHelper nextValueHelper;

    public void addDevices(StaticDeviceGroup group, List<? extends YukonDevice> devices) {
        for (YukonDevice device : devices) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("insert into DeviceGroupMember");
            sql.append("(DeviceGroupId, YukonPaoId)");
            sql.append("values");
            sql.append("(?, ?)");
            
            jdbcTemplate.update(sql.toString(), group.getId(), device.getDeviceId());
        }
    }

    public List<YukonDevice> getChildDevices(StaticDeviceGroup group) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.paobjectid, ypo.type");
        sql.append("from DeviceGroupMember dgm");
        sql.append("join Device d on dgm.yukonpaoid = d.deviceid");
        sql.append("join YukonPaObject ypo on d.deviceid = ypo.paobjectid");
        sql.append("where dgm.devicegroupid = ?");
        YukonDeviceRowMapper mapper = new YukonDeviceRowMapper(paoGroupsWrapper);
        List<YukonDevice> devices = jdbcTemplate.query(sql.toString(), mapper, group.getId());
        return devices;
    }

    public List<StaticDeviceGroup> getChildGroups(StaticDeviceGroup group) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroup dg");
        sql.append("where dg.parentdevicegroupid = ?");
        StaticDeviceGroupRowMapper mapper = new StaticDeviceGroupRowMapper(group);
        List<StaticDeviceGroup> groups = jdbcTemplate.query(sql.toString(), mapper, group.getId());
        return groups;
    }
    
    public StaticDeviceGroup getRootGroup() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroup dg");
        sql.append("where dg.parentdevicegroupid is null");
        StaticDeviceGroupRowMapper mapper = new StaticDeviceGroupRowMapper(null);
        StaticDeviceGroup group = jdbcTemplate.queryForObject(sql.toString(), mapper);
        return group;
    }
    
    public StaticDeviceGroup addGroup(StaticDeviceGroup group, String groupName) {
        int nextValue = nextValueHelper.getNextValue("DeviceGroup");
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("insert into DeviceGroup");
        sql.append("(DeviceGroupId, GroupName, ParentDeviceGroupId, SystemGroup, Type)");
        sql.append("values");
        sql.append("(?, ?, ?, ?, ?)");
        
        jdbcTemplate.update(sql.toString(), nextValue, groupName, group.getId(), "N", DeviceGroupType.STATIC.name());
        StaticDeviceGroup result = new StaticDeviceGroup();
        result.setId(nextValue);
        result.setName(groupName);
        result.setParent(group);
        result.setSystemGroup(false);
        result.setType(DeviceGroupType.STATIC);
        return result;
    }

    public void removeDevices(StaticDeviceGroup group, List<? extends YukonDevice> devices) {
        List<Integer> deviceIds = new MappingList<YukonDevice, Integer>(devices, new YukonDeviceToIdMapper());
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from DeviceGroupMember");
        sql.append("where DeviceGroupId = ?");
        sql.append("and YukonPaoId in (", deviceIds, ")");
        
        jdbcTemplate.update(sql.toString(), group.getId());
    }

    public void updateDevices(StaticDeviceGroup group, List<? extends YukonDevice> devices) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from DeviceGroupMember");
        sql.append("where DeviceGroupId = ?");
        
        jdbcTemplate.update(sql.toString(), group.getId());
        
        addDevices(group, devices);
    }
    
    @Required
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Required
    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }
    
    @Required
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

}
