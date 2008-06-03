package com.cannontech.common.device.groups.editor.dao.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupPermission;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.util.YukonDeviceToIdMapper;
import com.cannontech.common.util.MappingCollection;
import com.cannontech.common.util.ReverseList;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class DeviceGroupEditorDaoImpl implements DeviceGroupEditorDao, DeviceGroupMemberEditorDao, PartialDeviceGroupDao {
    private SimpleJdbcOperations jdbcTemplate;
    private PaoGroupsWrapper paoGroupsWrapper;
    private NextValueHelper nextValueHelper;
    
    private StoredDeviceGroup rootGroupCache = null;
    
    @Transactional(propagation=Propagation.REQUIRED)
    public void addDevices(StoredDeviceGroup group, Collection<? extends YukonDevice> devices) {
        Collection<Integer> deviceIds = new MappingCollection<YukonDevice, Integer>(devices, new YukonDeviceToIdMapper());
        addDevicesById(group, deviceIds.iterator());
    }
    
    @Override
    public void addDevicesById(StoredDeviceGroup group, Iterator<Integer> deviceIds) {
        
        if (!group.isModifiable()) {
            throw new UnsupportedOperationException("Cannot add devices to a non-modifiable group.");
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("insert into DeviceGroupMember");
        sql.append("(DeviceGroupId, YukonPaoId)");
        sql.append("values");
        sql.append("(?, ?)");
        while (deviceIds.hasNext()) {
            Integer deviceId = deviceIds.next();
            try {
                jdbcTemplate.update(sql.toString(), group.getId(), deviceId);
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
        sql.append("where dg.Type != ?");
        sql.append("and DeviceGroupId != ?");
        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        List<PartialDeviceGroup> groups = jdbcTemplate.query(sql.toString(), mapper, DeviceGroupType.STATIC.name(), group.getId());
        
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
        List<PartialDeviceGroup> groups = jdbcTemplate.query(sql.toString(), mapper, DeviceGroupType.STATIC.name(), group.getId());
        
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
    
    public StoredDeviceGroup getGroupByName(StoredDeviceGroup parent, 
            String groupName) throws NotFoundException{
        return getGroupByName(parent, groupName, false);
    }
    
    public StoredDeviceGroup getGroupByName(StoredDeviceGroup parent, 
            String groupName, boolean addGroup) throws NotFoundException{
        String rawName = SqlUtils.convertStringToDbValue(groupName);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroup dg");
        sql.append("where dg.parentdevicegroupid = ? and lower(dg.groupname) = lower(?)");
        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        PartialDeviceGroup group = null;
        try{
            group = jdbcTemplate.queryForObject(sql.toString(), mapper, parent.getId(), rawName);
        } catch (EmptyResultDataAccessException erdae) {
            if (addGroup) {
                StoredDeviceGroup addedGroup = addGroup(parent, DeviceGroupType.STATIC, groupName);
                return addedGroup;
            }
            else
                throw new NotFoundException(parent.getFullName()+"/"+groupName);
        }
        PartialGroupResolver resolver = new PartialGroupResolver(this);
        resolver.addKnownGroups(parent);
        StoredDeviceGroup resolvedGroup = resolver.resolvePartial(group);
        return resolvedGroup;
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    public StoredDeviceGroup addGroup(StoredDeviceGroup group, DeviceGroupType type, String groupName) {
        int nextValue = nextValueHelper.getNextValue("DeviceGroup");
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("insert into DeviceGroup");
        sql.append("(DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type)");
        sql.append("values");
        sql.append("(?, ?, ?, ?, ?)");
        
        String rawName = SqlUtils.convertStringToDbValue(groupName);

        try {
            jdbcTemplate.update(sql.toString(), nextValue, rawName, group.getId(), DeviceGroupPermission.EDIT_MOD.toString(), type.name());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Cannot create group with the same name as an existing group with the same parent.", e);
        }
        
        StoredDeviceGroup result = new StoredDeviceGroup();
        result.setId(nextValue);
        result.setName(groupName);
        result.setParent(group);
        result.setPermission(DeviceGroupPermission.EDIT_MOD);
        result.setType(type);
        return result;
    }

    public void updateGroup(StoredDeviceGroup group) {
        Validate.isTrue(group.isEditable(), "Non-editable groups cannot be updated.");
        Validate.isTrue(group.getParent() != null, "The root group cannot be updated.");
        
        StoredDeviceGroup parentGroup = getStoredGroup(group.getParent());

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE DeviceGroup");
        sql.append("SET GroupName = ?, ParentDeviceGroupId = ?");
        sql.append("WHERE");
        sql.append("DeviceGroupId = ?");
        
        String rawName = SqlUtils.convertStringToDbValue(group.getName());
        
        try {
            jdbcTemplate.update(sql.toString(), rawName, parentGroup.getId(), group.getId());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Cannot change group name to the same name as an existing group with the same parent.", e);
        }
    }

    @Transactional
    public void removeGroup(StoredDeviceGroup group) {
        Validate.isTrue(group.isEditable(), "Non-editable groups cannot be deleted.");
        Validate.isTrue(group.getParent() != null, "The root group cannot be deleted.");
        
        List<StoredDeviceGroup> childGroups = getChildGroups(group);
        for(StoredDeviceGroup childGroup : childGroups){
            removeGroup(childGroup);
        }
        
        String sql1 = "DELETE FROM DeviceGroupMember where DeviceGroupId = ?";
        jdbcTemplate.update(sql1, group.getId());
        
        String sql2 = "DELETE FROM DeviceGroup WHERE DeviceGroupId = ?";
        jdbcTemplate.update(sql2, group.getId());
        
    }

    public void removeDevices(StoredDeviceGroup group, YukonDevice... device) {
        List<YukonDevice> devices = Arrays.asList(device);
        removeDevices(group, devices);
    }
    
    public void removeDevices(StoredDeviceGroup group, Collection<? extends YukonDevice> devices) {
        Collection<Integer> deviceIds = new MappingCollection<YukonDevice, Integer>(devices, new YukonDeviceToIdMapper());
        removeDevicesById(group, deviceIds);
    }
    
    @Override
    public void removeDevicesById(StoredDeviceGroup group,
            Collection<Integer> deviceIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from DeviceGroupMember");
        sql.append("where DeviceGroupId = ?");
        sql.append("and YukonPaoId in (", deviceIds, ")");
        
        jdbcTemplate.update(sql.toString(), group.getId());
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
        // a device is, it is likely to only be in a few groups. Therefore, we
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
        
        StoredDeviceGroup storedDeviceGroup = queryForDeviceGroup(sql.toString(), groupId);
        
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
    
    @Override
    public StoredDeviceGroup getStoredGroup(DeviceGroup group) throws NotFoundException {
        if (group instanceof StoredDeviceGroup) {
            return (StoredDeviceGroup) group;
        } else {
            StoredDeviceGroup storedGroup = getStoredGroup(group.getFullName(), false);
            return storedGroup;
        }
    }
    
    @Override
    public StoredDeviceGroup getStoredGroup(String fullName, boolean create) throws NotFoundException {
        Validate.isTrue(fullName.startsWith("/"), "Group name isn't valid, must start with '/': ", fullName);
        fullName = fullName.substring(1);
        
        if (StringUtils.isEmpty(fullName)) {
            return getRootGroup();
        }
        
        String[] strings = fullName.split("/");
        List<String> names = Arrays.asList(strings);
        return getOrCreateGroup(names, create);
    }

    private StoredDeviceGroup getOrCreateGroup(List<String> names, boolean create) throws NotFoundException {
        if (names.isEmpty()) {
            return getRootGroup();
        }
        List<String> arguments = new ReverseList<String>(names);
        Object[] reversedNames = arguments.toArray();
        String sql = getRelativeGroupSql(names.size(), false);
        
        try {
            StoredDeviceGroup result = queryForDeviceGroup(sql, reversedNames);
            return result;
        } catch (EmptyResultDataAccessException e) {
            if (create) {
                StoredDeviceGroup thisParentGroup = getOrCreateGroup(names.subList(0, names.size() - 1), create);
                String thisName = names.get(names.size() - 1);
                return addGroup(thisParentGroup, DeviceGroupType.STATIC, thisName);
            } else {
                throw new NotFoundException("Group \"/" + StringUtils.join(names, "/") + "\" could not be found", e);
            }
        }
    }
    
    public StoredDeviceGroup getSystemGroup(SystemGroupEnum systemGroupEnum) throws NotFoundException {
        String groupName = systemGroupEnum.getFullPath();
        StoredDeviceGroup storedGroup = getStoredGroup(groupName, true);
        return storedGroup;
    }

    private StoredDeviceGroup queryForDeviceGroup(String sql, Object... arguments) {
        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        PartialDeviceGroup group = jdbcTemplate.queryForObject(sql.toString(), mapper, arguments);
        PartialGroupResolver resolver = new PartialGroupResolver(this);
        StoredDeviceGroup result = resolver.resolvePartial(group);
        return result;
    }

    public String getRelativeGroupSql(int count, boolean justId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        if (justId) {
            sql.append("select dg.deviceGroupId");
        } else {
            sql.append("select dg.*");
        }
        sql.append("from DeviceGroup dg");
        if (count == 0) {
            sql.append("where dg.parentdevicegroupid is null");
        } else {
            sql.append("where lower(dg.groupname) = lower(?)");
            sql.append(" and dg.parentdevicegroupid = (", getRelativeGroupSql(count - 1, true), ")");
        }
        
        return sql.toString();
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
