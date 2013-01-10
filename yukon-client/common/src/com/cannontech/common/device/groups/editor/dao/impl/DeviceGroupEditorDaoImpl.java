package com.cannontech.common.device.groups.editor.dao.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.IllegalGroupNameException;
import com.cannontech.common.device.groups.TemporaryDeviceGroupNotFoundException;
import com.cannontech.common.device.groups.dao.DeviceGroupPermission;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.device.groups.util.YukonDeviceToIdMapper;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.MappingCollection;
import com.cannontech.common.util.ReverseList;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.device.Device;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.google.common.collect.Lists;

public class DeviceGroupEditorDaoImpl implements DeviceGroupEditorDao, DeviceGroupMemberEditorDao, PartialDeviceGroupDao {
    private final Logger log = YukonLogManager.getLogger(DeviceGroupEditorDaoImpl.class);
    
    private YukonJdbcOperations jdbcTemplate;
    private NextValueHelper nextValueHelper;
    private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    
    private StoredDeviceGroup rootGroupCache = null;
    
    @Transactional(propagation=Propagation.REQUIRED)
    public void addDevices(StoredDeviceGroup group, Iterable<? extends YukonDevice> devices) {
        addDevices(group, devices.iterator());
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    public void addDevices(StoredDeviceGroup group, Iterator<? extends YukonDevice> devices) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("insert into DeviceGroupMember");
        sql.append("(DeviceGroupId, YukonPaoId)");
        sql.append("values");
        sql.append("(?, ?)");
        while (devices.hasNext()) {
            YukonDevice device = devices.next();
            if (!isDeviceValid(device)) {
                log.info("skipping invalid device, group=" + group + ", deviceId=" + device);
                continue;
            }
            try {
                jdbcTemplate.update(sql.toString(), group.getId(), device.getPaoIdentifier().getPaoId());
            } catch (DataIntegrityViolationException e) {
                // ignore - tried to insert duplicate
            }
        }
    }

    private boolean isDeviceValid(YukonDevice device) {
        if (device == null) {
            return false;
        }
        boolean systemDevice = device.getPaoIdentifier().getPaoId() == Device.SYSTEM_DEVICE_ID;
        if (systemDevice) {
            return false;
        }
        return true;
    }

    public List<SimpleDevice> getChildDevices(StoredDeviceGroup group) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.paobjectid, ypo.type");
        sql.append("from DeviceGroupMember dgm");
        sql.append("join Device d on dgm.yukonpaoid = d.deviceid");
        sql.append("join YukonPaObject ypo on d.deviceid = ypo.paobjectid");
        sql.append("where dgm.devicegroupid = ?");
        YukonDeviceRowMapper mapper = new YukonDeviceRowMapper();
        List<SimpleDevice> devices = jdbcTemplate.query(sql.toString(), mapper, group.getId());
        return devices;
    }
    
    @Override
    public boolean isChildDevice(StoredDeviceGroup group, YukonDevice device) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select count(*)");
        sql.append("from DeviceGroupMember dgm");
        sql.append("where dgm.devicegroupid = ? and dgm.yukonpaoid = ?");
        int count = jdbcTemplate.queryForInt(sql.toString(), group.getId(), device.getPaoIdentifier().getPaoId());
        return count > 0;
    }

    public List<StoredDeviceGroup> getChildGroups(StoredDeviceGroup group) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroup dg");
        sql.append("where dg.parentdevicegroupid = ?");
        sql.append("order by GroupName");
        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        List<PartialDeviceGroup> groups = jdbcTemplate.query(sql.toString(), mapper, group.getId());

        PartialGroupResolver resolver = new PartialGroupResolver(this, group);
        List<StoredDeviceGroup> resolvedPartials = resolver.resolvePartials(groups);
        
        return resolvedPartials;
    }
    
    @Override
    public List<StoredDeviceGroup> getNonStaticGroups(StoredDeviceGroup group) {
        // we're going to let the database ignore the hierarchy because we can do it easier in code
        // (type != static is a pretty restrictive filter, this should at most return dozens of rows)
        // (yes, this could be rewritten to use fancier SQL, but I don't think it is worth it today)
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroup dg");
        sql.append("where dg.Type").neq(DeviceGroupType.STATIC);
        sql.append("and DeviceGroupId").neq(group.getId());
        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        List<PartialDeviceGroup> groups = jdbcTemplate.query(sql, mapper);
        
        PartialGroupResolver resolver = new PartialGroupResolver(this, getRootGroup());
        List<StoredDeviceGroup> resolvedPartials = resolver.resolvePartials(groups);
        
        List<StoredDeviceGroup> result = filterGroupsForBase(group, resolvedPartials);
        
        return result;
    }

    private List<StoredDeviceGroup> filterGroupsForBase(StoredDeviceGroup base,
            List<StoredDeviceGroup> allGroups) {
        // alright, now we have to filter, but first we'll check if base is root
        if (base.getParent() == null) {
            // well, everything is a descendant of root, so lets just return everything
            return allGroups;
        }
        
        List<StoredDeviceGroup> result = Lists.newArrayListWithExpectedSize(allGroups.size());
        for (StoredDeviceGroup storedDeviceGroup : allGroups) {
            if (storedDeviceGroup.isDescendantOf(base)) {
                result.add(storedDeviceGroup);
            }
        }
        
        return result;
    }

    public void addDevices(StoredDeviceGroup group, YukonDevice... device) {
        List<YukonDevice> devices = Arrays.asList(device);
        addDevices(group, devices);
    }
    
    @Override
    public List<StoredDeviceGroup> getStaticGroups(StoredDeviceGroup group) {
        List<StoredDeviceGroup> allGroups = getAllGroups(group);
        List<StoredDeviceGroup> result = Lists.newArrayListWithExpectedSize(allGroups.size());
        
        for (StoredDeviceGroup storedDeviceGroup : allGroups) {
            if (storedDeviceGroup.getType() == DeviceGroupType.STATIC) {
                result.add(storedDeviceGroup);
            }
        }
        
        return result;
    }
    
    public List<StoredDeviceGroup> getAllGroups(StoredDeviceGroup group) {
        // no reason to mess with all of this fancy SQL if we just want everything
        if (group.equals(getRootGroup())) return getAllGroupsrUnderRoot();
        
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
        SqlBuilder msSql = builder.buildFor(DatabaseVendor.MS2005, DatabaseVendor.MS2008);
        // This query use a Common Table Expression to achieve a recursive query
        // http://msdn.microsoft.com/en-us/library/ms190766.aspx
        msSql.append("WITH DeviceGroup_CTE AS (");
        msSql.append("  SELECT dg.ParentDeviceGroupId, dg.DeviceGroupId");
        msSql.append("  FROM DeviceGroup AS dg");
        msSql.append("  WHERE dg.ParentDeviceGroupId").eq(group.getId()); // starting point
        msSql.append("  UNION ALL");
        msSql.append("  SELECT dg.ParentDeviceGroupId, dg.DeviceGroupId");
        msSql.append("  FROM DeviceGroup AS dg");
        msSql.append("    JOIN DeviceGroup_CTE AS dgcte ON dg.ParentDeviceGroupId = dgcte.DeviceGroupId"); // recursive join
        msSql.append(")");
        msSql.append("SELECT dg_real.*");
        msSql.append("FROM DeviceGroup_CTE"); // bring it all together
        msSql.append("  JOIN DeviceGroup AS dg_real ON DeviceGroup_CTE.DeviceGroupId = dg_real.DeviceGroupId");
        
        // This query uses Oracle's proprietary syntax for recursive queries
        SqlBuilder oracleSql = builder.buildFor(DatabaseVendor.ORACLE11G, DatabaseVendor.ORACLE10G);
        oracleSql.append("SELECT *");
        oracleSql.append("FROM DeviceGroup");
        oracleSql.append("START WITH ParentDeviceGroupId").eq(group.getId()); // starting point
        oracleSql.append("CONNECT BY PRIOR DeviceGroupId = ParentDeviceGroupId"); //recursive join
        
        // This query will return all (but the base) groups, but because we
        // do a quick filter bellow, it ends up working the same as the
        // above queries
        SqlBuilder otherSql =  builder.buildOther();
        otherSql.append("SELECT *");
        otherSql.append("FROM DeviceGroup");
        otherSql.append("WHERE DeviceGroupId").neq(group.getId());

        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        List<PartialDeviceGroup> groups = jdbcTemplate.query(builder, mapper);
        
        PartialGroupResolver resolver = new PartialGroupResolver(this, group);
        List<StoredDeviceGroup> resolvedPartials = resolver.resolvePartials(groups);
        
        // quick filter in case we executed the "other" SQL
        List<StoredDeviceGroup> result = filterGroupsForBase(group, resolvedPartials);
        
        return result;
    }
    
    public List<StoredDeviceGroup> getAllGroupsrUnderRoot() {
        StoredDeviceGroup rootGroup = getRootGroup();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM DeviceGroup");
        sql.append("WHERE DeviceGroupId").neq(rootGroup.getId());
        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        List<PartialDeviceGroup> groups = jdbcTemplate.query(sql, mapper);
        
        PartialGroupResolver resolver = new PartialGroupResolver(this, rootGroup);
        List<StoredDeviceGroup> resolvedPartials = resolver.resolvePartials(groups);
        
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
            rootGroupCache = group.getStoredDeviceGroup();
        }
        return rootGroupCache;
    }
    
    public StoredDeviceGroup getGroupByName(StoredDeviceGroup parent, 
            String groupName) throws NotFoundException, IllegalGroupNameException {
        
        DeviceGroupUtil.validateName(groupName);
        
        return getGroupByName(parent, groupName, false);
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    public StoredDeviceGroup getGroupByName(StoredDeviceGroup parent, 
            String groupName, boolean addGroup) throws NotFoundException, IllegalGroupNameException {
        
        DeviceGroupUtil.validateName(groupName);
        
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
            else {
                
                String fullName = parent.getFullName()+"/"+groupName;
                
                // throw special exception if the grup is child of Hidden group (temp group)
                if ((parent.getFullName() + "/").equals(SystemGroupEnum.TEMPORARYGROUPS.getFullPath())) {
                    throw new TemporaryDeviceGroupNotFoundException("Group \"" + fullName + "\" could not be found");
                }
                
                throw new NotFoundException("Group \"" + fullName + "\" could not be found");
            }
        }
        PartialGroupResolver resolver = new PartialGroupResolver(this, parent);
        StoredDeviceGroup resolvedGroup = resolver.resolvePartial(group);
        return resolvedGroup;
    }
    
    @Transactional(propagation=Propagation.REQUIRED, noRollbackFor={DuplicateException.class})
    public StoredDeviceGroup addGroup(StoredDeviceGroup parentGroup, DeviceGroupType type, String groupName) throws IllegalGroupNameException {
        DeviceGroupPermission permission = type.equals(DeviceGroupType.STATIC) ? DeviceGroupPermission.EDIT_MOD : DeviceGroupPermission.EDIT_NOMOD;
        return addGroup(parentGroup, type, groupName, permission);
    }
    
    @Transactional(propagation=Propagation.REQUIRED, noRollbackFor={DuplicateException.class})
    public StoredDeviceGroup addGroup(StoredDeviceGroup parentGroup, DeviceGroupType type, String groupName, DeviceGroupPermission permission) throws IllegalGroupNameException {
        DeviceGroupUtil.validateName(groupName);
        
        Instant now = new Instant();
        int nextValue = nextValueHelper.getNextValue("DeviceGroup");
        String rawName = SqlUtils.convertStringToDbValue(groupName);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("DeviceGroup");
        sink.addValue("DeviceGroupId", nextValue);
        sink.addValue("GroupName", rawName);
        sink.addValue("ParentDeviceGroupId", parentGroup.getId());
        sink.addValue("Permission",  permission.name());
        sink.addValue("Type",  type.name());
        sink.addValue("CreatedDate", now);               
        try {
            yukonJdbcTemplate.update(sql);
        } catch (DataAccessException e) {
            throw new DuplicateException("Cannot create group with the same name as an existing group with the same parent.", e);
        }
        
        StoredDeviceGroup result = new StoredDeviceGroup();
        result.setId(nextValue);
        result.setName(groupName);
        result.setParent(parentGroup);
        result.setPermission(permission);
        result.setType(type);
        result.setCreatedDate(now);
        return result;
    }

    @Transactional(propagation=Propagation.REQUIRED)
    public void updateGroup(StoredDeviceGroup group) throws IllegalGroupNameException {
        Validate.isTrue(group.getParent() != null, "The root group cannot be updated.");
        
        DeviceGroupUtil.validateName(group.getName());
        
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
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM DeviceGroup");
        sql.append("WHERE DeviceGroupId").eq(group.getId());
        
        jdbcTemplate.update(sql);
        
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

    @Override
    public void removeAllChildDevices(StoredDeviceGroup group) {
        String sql = "DELETE FROM DeviceGroupMember where DeviceGroupId = ?";
        jdbcTemplate.update(sql, group.getId());
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
                                                            device.getPaoIdentifier().getPaoId(),
                                                            base.getId());
        HashSet<StoredDeviceGroup> result = new HashSet<StoredDeviceGroup>();
        PartialGroupResolver resolver = new PartialGroupResolver(this, base);
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
                                                             device.getPaoIdentifier().getPaoId());
        Set<StoredDeviceGroup> result = new HashSet<StoredDeviceGroup>();
        PartialGroupResolver resolver = new PartialGroupResolver(this, base);
        resolver.resolvePartials(groups, result);
        
        // alright, now we have to filter, but first we'll check if base is root
        if (base.getParent() == null) {
            // well, everything is a descendant of root, so lets just return everything
            return result;
        }
        
        Iterator<StoredDeviceGroup> iterator = result.iterator();
        while (iterator.hasNext()) {
            StoredDeviceGroup next = iterator.next();
            // note that we WANT to return base if it is in the collection
            if (!next.isEqualToOrDescendantOf(base)) {
                iterator.remove();
            }
        }
        return result;
    }
    
    @Transactional
    public StoredDeviceGroup getGroupById(int groupId) {
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
    @Transactional(propagation=Propagation.REQUIRED)
    public StoredDeviceGroup getStoredGroup(DeviceGroup group) throws NotFoundException {
        if (group instanceof StoredDeviceGroup) {
            // to be safe, we'll copy the group
            return new StoredDeviceGroup((StoredDeviceGroup) group);
        } else {
            StoredDeviceGroup storedGroup = getStoredGroup(group.getFullName(), false);
            return storedGroup;
        }
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED)
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
    
    @Transactional(propagation=Propagation.REQUIRED)
    public StoredDeviceGroup getSystemGroup(SystemGroupEnum systemGroupEnum) throws NotFoundException {
        String groupName = systemGroupEnum.getFullPath();
        StoredDeviceGroup storedGroup = getStoredGroup(groupName, true);
        return storedGroup;
    }

    private StoredDeviceGroup queryForDeviceGroup(String sql, Object... arguments) {
        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        PartialDeviceGroup group = jdbcTemplate.queryForObject(sql.toString(), mapper, arguments);
        PartialGroupResolver resolver = new PartialGroupResolver(this, getRootGroup());
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
    public void setJdbcTemplate(YukonJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Required
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setVendorSpecificSqlBuilderFactory(
            VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory) {
        this.vendorSpecificSqlBuilderFactory = vendorSpecificSqlBuilderFactory;
    }
}
