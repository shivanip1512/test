package com.cannontech.common.device.groups.editor.dao.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.util.YukonDeviceToIdMapper;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class DeviceGroupEditorDaoImpl implements DeviceGroupEditorDao, DeviceGroupMemberEditorDao, PartialDeviceGroupDao {
    private SimpleJdbcOperations jdbcTemplate;
    private PaoGroupsWrapper paoGroupsWrapper;
    private NextValueHelper nextValueHelper;
    
    private StoredDeviceGroup rootGroupCache = null;
    
    public void addDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices) {
        for (YukonDevice device : devices) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("insert into DeviceGroupMember");
            sql.append("(DeviceGroupId, YukonPaoId)");
            sql.append("values");
            sql.append("(?, ?)");
            
            try {
                jdbcTemplate.update(sql.toString(), group.getId(), device.getDeviceId());
            } catch (DataIntegrityViolationException e) {
                // ignore - tried to insert duplicate
            }
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
        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        List<PartialDeviceGroup> groups = jdbcTemplate.query(sql.toString(), mapper, group.getId());

        PartialGroupResolver resolver = new PartialGroupResolver(this);
        resolver.addKnownGroups(group);
        List<StoredDeviceGroup> resolvedPartials = resolver.resolvePartials(groups);
        
        return resolvedPartials;
    }
    
    @Override
    public List<StoredDeviceGroup> getNonStaticGroups(StoredDeviceGroup group) {
        // we're going to let the database ignore the parent group because we can do it easier in code
        // on second thought, we'll exclude the parent, but otherwise ignore!
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroup dg");
        sql.append("where dg.Type != 'STATIC'");
        sql.append("and DeviceGroupId != ?");
        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        List<PartialDeviceGroup> groups = jdbcTemplate.query(sql.toString(), mapper, group.getId());
        
        PartialGroupResolver resolver = new PartialGroupResolver(this);
        resolver.addKnownGroups(group);
        List<StoredDeviceGroup> resolvedPartials = resolver.resolvePartials(groups);
        
        // alright, now we have to filter, but first we'll check if base is root
        if (group.getParent() == null) {
            // well, everything is a descendant of root, so lets just return everything
            return resolvedPartials;
        }
        
        Iterator<StoredDeviceGroup> iterator = resolvedPartials.iterator();
        while (iterator.hasNext()) {
            StoredDeviceGroup next = iterator.next();
            if (!next.isDescendantOf(group)) {
                // I don't really care that this is an array list, because this will usually get called on the top group
                iterator.remove();
            }
        }
        
        return resolvedPartials;
    }

    public void addDevices(StoredDeviceGroup group, YukonDevice... device) {
        List<YukonDevice> devices = Arrays.asList(device);
        addDevices(group, devices);
    }
    
    @Override
    public List<StoredDeviceGroup> getStaticGroups(StoredDeviceGroup group) {
        // we're going to let the database ignore the parent group because we can do it easier in code
        // on second thought, we'll exclude the parent, but otherwise ignore!
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from DeviceGroup");
        sql.append("where Type = ?");
        sql.append("and DeviceGroupId != ?");
        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        List<PartialDeviceGroup> groups = jdbcTemplate.query(sql.toString(), mapper, "STATIC", group.getId());
        
        PartialGroupResolver resolver = new PartialGroupResolver(this);
        resolver.addKnownGroups(group);
        List<StoredDeviceGroup> resolvedPartials = resolver.resolvePartials(groups);
        
        // alright, now we have to filter, but first we'll check if base is root
        if (group.getParent() == null) {
            // well, everything is a descendant of root, so lets just return everything
            return resolvedPartials;
        }
        Iterator<StoredDeviceGroup> iterator = resolvedPartials.iterator();
        while (iterator.hasNext()) {
            StoredDeviceGroup next = iterator.next();
            if (!next.isDescendantOf(group)) {
                // I don't really care that this is probably an array list, 
                // because this will usually get called on the top group
                iterator.remove();
            }
        }
        
        return resolvedPartials;
    }
    
    public synchronized StoredDeviceGroup getRootGroup() {
        if (rootGroupCache == null) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("select dg.*");
            sql.append("from DeviceGroup dg");
            sql.append("where dg.parentdevicegroupid is null");
            PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
            PartialDeviceGroup group = jdbcTemplate.queryForObject(sql.toString(), mapper);
            PartialGroupResolver resolver = new PartialGroupResolver(this);
            rootGroupCache = resolver.resolvePartial(group);
        }
        return rootGroupCache;
    }
    
    public StoredDeviceGroup getGroupByName(StoredDeviceGroup parent, String groupName) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroup dg");
        sql.append("where dg.parentdevicegroupid = ? and dg.groupname = ?");
        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        PartialDeviceGroup group = jdbcTemplate.queryForObject(sql.toString(), mapper, parent.getId(), groupName);
        PartialGroupResolver resolver = new PartialGroupResolver(this);
        resolver.addKnownGroups(parent);
        StoredDeviceGroup resolvedGroup = resolver.resolvePartial(group);
        return resolvedGroup;
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

    public void updateGroup(StoredDeviceGroup group) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE DeviceGroup");
        sql.append("SET GroupName = ?");
        sql.append("WHERE");
        sql.append("DeviceGroupId = ?");
        
        jdbcTemplate.update(sql.toString(), group.getName(), group.getId());
    }

    @Transactional
    public void removeGroup(StoredDeviceGroup group) {
        
        List<StoredDeviceGroup> childGroups = getChildGroups(group);
        for(StoredDeviceGroup childGroup : childGroups){
            removeGroup(childGroup);
        }
        
        String sql1 = "DELETE FROM DeviceGroupMember where DeviceGroupId = ?";
        jdbcTemplate.update(sql1, group.getId());
        
        String sql2 = "DELETE FROM DeviceGroup WHERE DeviceGroupId = ?";
        jdbcTemplate.update(sql2, group.getId());
        
    }

    public void moveGroup(StoredDeviceGroup group, StoredDeviceGroup parentGroup) {
        String sql = "UPDATE DeviceGroup SET ParentDeviceGroupId = ? WHERE DeviceGroupId = ?";
        jdbcTemplate.update(sql, parentGroup.getId(), group.getId());
    }
    
    public void removeDevices(StoredDeviceGroup group, YukonDevice... device) {
        List<YukonDevice> devices = Arrays.asList(device);
        removeDevices(group, devices);
    }
    
    public void removeDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices) {
        List<Integer> deviceIds = new MappingList<YukonDevice, Integer>(devices, new YukonDeviceToIdMapper());
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from DeviceGroupMember");
        sql.append("where DeviceGroupId = ?");
        sql.append("and YukonPaoId in (", deviceIds, ")");
        
        jdbcTemplate.update(sql.toString(), group.getId());
    }

    public void updateDevices(StoredDeviceGroup group, YukonDevice... device) {
        List<YukonDevice> devices = Arrays.asList(device);
        updateDevices(group, devices);
    }
    
    public void updateDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from DeviceGroupMember");
        sql.append("where DeviceGroupId = ?");
        
        jdbcTemplate.update(sql.toString(), group.getId());
        
        addDevices(group, devices);
    }
    
    /**
     * Deprecated via interface.
     */
    @Transactional(propagation=Propagation.REQUIRED)
    public Set<StoredDeviceGroup> getGroups(StoredDeviceGroup base, YukonDevice device) {
        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroupMember dgm");
        sql.append("join DeviceGroup dg on dg.devicegroupid = dgm.devicegroupid");
        sql.append("where dgm.yukonpaoid = ? and dg.parentdevicegroupid = ?");
        List<PartialDeviceGroup> groups = jdbcTemplate.query(sql.toString(), 
                                                            mapper, 
                                                            device.getDeviceId(),
                                                            base.getId());
        HashSet<StoredDeviceGroup> result = new HashSet<StoredDeviceGroup>();
        PartialGroupResolver resolver = new PartialGroupResolver(this);
        resolver.addKnownGroups(base);
        resolver.resolvePartials(groups, result);        
        return result;
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    public Set<StoredDeviceGroup> getGroupMembership(StoredDeviceGroup base, YukonDevice device) {
        // The thinking behind this implementation is that no matter how popular
        // a device is, it is likely to only be in a few groups. Therefor, we
        // might as well retrieve all of those groups and then filter out the groups
        // which are not descendants of base (also, in most cases this is called with
        // base being the root group, so we handle that as a special case).
        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroupMember dgm");
        sql.append("join DeviceGroup dg on dg.devicegroupid = dgm.devicegroupid");
        sql.append("where dgm.yukonpaoid = ?");
        List<PartialDeviceGroup> groups = jdbcTemplate.query(sql.toString(), 
                                                            mapper, 
                                                            device.getDeviceId());
        Set<StoredDeviceGroup> result = new HashSet<StoredDeviceGroup>();
        PartialGroupResolver resolver = new PartialGroupResolver(this);
        resolver.addKnownGroups(base);
        resolver.resolvePartials(groups, result);
        
        // alright, now we have to filter, but first we'll check if base is root
        if (base.getParent() == null) {
            // well, everything is a descendant of root, so lets just return everything
            return result;
        }
        
        Iterator<StoredDeviceGroup> iterator = result.iterator();
        while (iterator.hasNext()) {
            StoredDeviceGroup next = iterator.next();
            if (!next.isDescendantOf(base)) {
                iterator.remove();
            }
        }
        return result;
    }
    
    @Transactional
    public StoredDeviceGroup getGroupById(int groupId) {
        PartialGroupResolver resolver = new PartialGroupResolver(this);
        resolver.addKnownGroups(getRootGroup());
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroup dg");
        sql.append("where dg.devicegroupid = ?");
        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        PartialDeviceGroup group = jdbcTemplate.queryForObject(sql.toString(), mapper, groupId);
        
        StoredDeviceGroup storedDeviceGroup = resolver.resolvePartial(group);
        
        return storedDeviceGroup;
    }
    
    public Set<PartialDeviceGroup> getPartialGroupsById(Set<Integer> neededIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroup dg");
        sql.append("where dg.devicegroupid in (", neededIds, ")");
        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        List<PartialDeviceGroup> groups = jdbcTemplate.query(sql.toString(), mapper);
        
        Set<PartialDeviceGroup> result = new HashSet<PartialDeviceGroup>(groups);
        return result;
    }

    
    public StoredDeviceGroup getGroup(SystemGroupEnum group) {
        return getGroupById(group.getId());
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
