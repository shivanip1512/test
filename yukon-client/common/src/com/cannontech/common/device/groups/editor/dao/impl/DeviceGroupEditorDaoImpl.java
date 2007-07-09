package com.cannontech.common.device.groups.editor.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.util.YukonDeviceToIdMapper;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class DeviceGroupEditorDaoImpl implements DeviceGroupEditorDao, DeviceGroupMemberEditorDao {
    private SimpleJdbcOperations jdbcTemplate;
    private PaoGroupsWrapper paoGroupsWrapper;
    private NextValueHelper nextValueHelper;

    public void addDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices) {
        for (YukonDevice device : devices) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("insert into DeviceGroupMember");
            sql.append("(DeviceGroupId, YukonPaoId)");
            sql.append("values");
            sql.append("(?, ?)");
            
            jdbcTemplate.update(sql.toString(), group.getId(), device.getDeviceId());
        }
    }

    public List<YukonDevice> getChildDevices(StoredDeviceGroup group) {
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

    public List<StoredDeviceGroup> getChildGroups(StoredDeviceGroup group) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroup dg");
        sql.append("where dg.parentdevicegroupid = ?");
        sql.append("order by GroupName");
        StoredDeviceGroupRowMapper mapper = new StoredDeviceGroupRowMapper(group);
        List<StoredDeviceGroup> groups = jdbcTemplate.query(sql.toString(), mapper, group.getId());
        return groups;
    }
    
    public StoredDeviceGroup getRootGroup() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroup dg");
        sql.append("where dg.parentdevicegroupid is null");
        StoredDeviceGroupRowMapper mapper = new StoredDeviceGroupRowMapper(null);
        StoredDeviceGroup group = jdbcTemplate.queryForObject(sql.toString(), mapper);
        return group;
    }
    
    public StoredDeviceGroup addGroup(StoredDeviceGroup group, DeviceGroupType type, String groupName) {
        int nextValue = nextValueHelper.getNextValue("DeviceGroup");
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("insert into DeviceGroup");
        sql.append("(DeviceGroupId, GroupName, ParentDeviceGroupId, SystemGroup, Type)");
        sql.append("values");
        sql.append("(?, ?, ?, ?, ?)");
        
        jdbcTemplate.update(sql.toString(), nextValue, groupName, group.getId(), "N", type.name());
        StoredDeviceGroup result = new StoredDeviceGroup();
        result.setId(nextValue);
        result.setName(groupName);
        result.setParent(group);
        result.setSystemGroup(false);
        result.setType(type);
        return result;
    }

    public void removeGroup(StoredDeviceGroup group) {
        // does this call the provider or something???
    }
    
    public void removeDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices) {
        List<Integer> deviceIds = new MappingList<YukonDevice, Integer>(devices, new YukonDeviceToIdMapper());
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from DeviceGroupMember");
        sql.append("where DeviceGroupId = ?");
        sql.append("and YukonPaoId in (", deviceIds, ")");
        
        jdbcTemplate.update(sql.toString(), group.getId());
    }

    public void updateDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from DeviceGroupMember");
        sql.append("where DeviceGroupId = ?");
        
        jdbcTemplate.update(sql.toString(), group.getId());
        
        addDevices(group, devices);
    }
    
    public Set<StoredDeviceGroup> getGroups(YukonDevice device) {
        ResolvingDeviceGroupRowMapper mapper = new ResolvingDeviceGroupRowMapper(this);
        return getGroups(device, mapper);
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    public Set<StoredDeviceGroup> getGroups(YukonDevice device, ResolvingDeviceGroupRowMapper mapper) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroupMember dgm");
        sql.append("join DeviceGroup dg on dg.devicegroupid = dgm.devicegroupid");
        sql.append("where dgm.yukonpaoid = ?");
        List<StoredDeviceGroup> groups = jdbcTemplate.query(sql.toString(), mapper, device.getDeviceId());
        return new HashSet<StoredDeviceGroup>(groups);
    }
    
    public StoredDeviceGroup getGroupById(int groupId) {
        ResolvingDeviceGroupRowMapper mapper = new ResolvingDeviceGroupRowMapper(this);
        return getGroupById(groupId, mapper);
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    public StoredDeviceGroup getGroupById(int groupId, ResolvingDeviceGroupRowMapper mapper) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroup dg");
        sql.append("where dg.devicegroupid = ?");
        StoredDeviceGroup group = jdbcTemplate.queryForObject(sql.toString(), mapper, groupId);
        return group;
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
