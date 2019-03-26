package com.cannontech.common.device.groups.editor.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.device.groups.util.YukonPaoToIdMapper;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.MappingCollection;
import com.cannontech.common.util.ReverseList;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.device.Device;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.cannontech.message.DbChangeManager;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class DeviceGroupEditorDaoImpl implements DeviceGroupEditorDao, DeviceGroupMemberEditorDao, PartialDeviceGroupDao {
    private final Logger log = YukonLogManager.getLogger(DeviceGroupEditorDaoImpl.class);
    
    @Autowired private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private DeviceGroupService deviceGroupService;
    private ChunkingSqlTemplate chunkyJdbcTemplate;
    
    private StoredDeviceGroup rootGroupCache = null;
    private final Map<SystemGroupEnum, String> systemGroupPaths = new HashMap<>();
    
    @PostConstruct
    public void init() throws Exception {
        chunkyJdbcTemplate= new ChunkingSqlTemplate(jdbcTemplate);
    }

    @PostConstruct
    private void initialize() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroup dg");
        sql.append("where dg.SystemGroupEnum is not null");
        List<PartialDeviceGroup> groups = jdbcTemplate.query(sql, new PartialDeviceGroupRowMapper());
        Map<Integer, PartialDeviceGroup> groupsById = new HashMap<>();

        for (PartialDeviceGroup group : groups) {
            groupsById.put(group.getStoredDeviceGroup().getId(), group);
        }

        for (PartialDeviceGroup group : groups) {
            SystemGroupEnum systemGroupEnum = group.getStoredDeviceGroup().getSystemGroupEnum();
            Integer parentProupId = group.getParentGroupId();
            String path = group.getStoredDeviceGroup().getName() + "/";
            while (parentProupId != null) {
                PartialDeviceGroup parentGroup = groupsById.get(parentProupId);
                parentProupId = parentGroup.getParentGroupId();
                path = parentGroup.getStoredDeviceGroup().getName() + "/" + path;
            }
            systemGroupPaths.put(systemGroupEnum, path);
        }
        log.debug("Loaded "+groups.size()+" System Groups");
    }
    
    private final SqlStatementBuilder deviceGroupMemberInsertSql = new SqlStatementBuilder();
    {
        deviceGroupMemberInsertSql.append("insert into DeviceGroupMember");
        deviceGroupMemberInsertSql.append("(DeviceGroupId, YukonPaoId)");
        deviceGroupMemberInsertSql.append("values");
        deviceGroupMemberInsertSql.append("(?, ?)");
    }
    
    @Override
    @Transactional
    public void addDevices(StoredDeviceGroup group, Iterable<? extends YukonPao> yukonPaos) {
        addDevices(group, yukonPaos.iterator());
    }
    

    @Override
    @Transactional
    public void addDevices(StoredDeviceGroup group, Iterator<? extends YukonPao> paos) {
        
        Collection<? extends YukonPao> validDevices = getValidDevicesToAdd(paos);
        if (!validDevices.isEmpty()) {
            boolean success = true;
            log.debug("Devices to add=" + validDevices.size());

            List<List<YukonPao>> devices =
                Lists.partition(Lists.newArrayList(validDevices), ChunkingSqlTemplate.DEFAULT_SIZE);
            for (List<YukonPao> subList : devices) {
                log.debug("Batch=" + subList.size());
                try {
                    jdbcTemplate.batchUpdate(deviceGroupMemberInsertSql.toString(), new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            YukonPao device = subList.get(i);
                            ps.setInt(1, group.getId());
                            ps.setInt(2, device.getPaoIdentifier().getPaoId());
                        }

                        @Override
                        public int getBatchSize() {
                            return subList.size();
                        }
                    });
                } catch (DataIntegrityViolationException e) {
                    success = false;
                    log.debug("Attempted to insert a duplicate device in a batch");
                }
            }
        }
    }
        
    @Override
    @Transactional
    public int addDevice(StoredDeviceGroup group, YukonPao device) {

        int rowsAffected = 0;
        log.debug("Attempted to insert device=" + device + " group =" + group);
        if (!getValidDevicesToAdd(Lists.newArrayList(device).iterator()).isEmpty()) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(deviceGroupMemberInsertSql);
            try {
                rowsAffected +=
                    jdbcTemplate.update(sql.toString(), group.getId(), device.getPaoIdentifier().getPaoId());
            } catch (DataIntegrityViolationException e) {
                log.debug("Duplicate device=" + device);
                // ignore - tried to insert duplicate
            }
            log.debug("rowsAffected=" + rowsAffected);
        }
        return rowsAffected;
    }

    private Collection<? extends YukonPao> getValidDevicesToAdd(Iterator<? extends YukonPao> paos) {
        return Collections2.filter(Lists.newArrayList(paos), new Predicate<YukonPao>() {
            @Override
            public boolean apply(YukonPao pao) {
                PaoIdentifier paoId = pao.getPaoIdentifier();
                if (paoId == null || paoId.getPaoId() == Device.SYSTEM_DEVICE_ID
                    || paoId.getPaoType().getPaoCategory() != PaoCategory.DEVICE) {
                    return false;
                }
                return true;
            }
        });
    }
    
    @Override
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
    public boolean isChildDevice(StoredDeviceGroup group, YukonPao yukonPao) {
        return isChildDevice(group, yukonPao.getPaoIdentifier().getPaoId());
    }
    
    @Override
    public boolean isChildDevice(StoredDeviceGroup group, int paoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM DeviceGroupMember dgm");
        sql.append("WHERE dgm.devicegroupid").eq(group.getId());
        sql.append(  "AND dgm.yukonpaoid").eq(paoId);

        int count = jdbcTemplate.queryForInt(sql);
        return count > 0;
    }

    @Override
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

    @Override
    public void addDevices(StoredDeviceGroup group, YukonPao... yukonPao) {
        List<YukonPao> yukonPaos = Arrays.asList(yukonPao);
        addDevices(group, yukonPaos);
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
    
    @Override
    public List<StoredDeviceGroup> getAllGroups(StoredDeviceGroup group) {
        // no reason to mess with all of this fancy SQL if we just want everything
        if (group.equals(getRootGroup())) return getAllGroupsrUnderRoot();
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
        
        // This query uses Oracle's proprietary syntax for recursive queries
        SqlBuilder oracleSql = builder.buildForAllOracleDatabases();
        oracleSql.append("SELECT *");
        oracleSql.append("FROM DeviceGroup");
        oracleSql.append("START WITH ParentDeviceGroupId").eq(group.getId()); // starting point
        oracleSql.append("CONNECT BY PRIOR DeviceGroupId = ParentDeviceGroupId"); //recursive join
        
        SqlBuilder otherSql = builder.buildOther(); //MSSQL
        // This query use a Common Table Expression to achieve a recursive query
        // http://msdn.microsoft.com/en-us/library/ms190766.aspx
        otherSql.append("WITH DeviceGroup_CTE AS (");
        otherSql.append("  SELECT dg.ParentDeviceGroupId, dg.DeviceGroupId");
        otherSql.append("  FROM DeviceGroup AS dg");
        otherSql.append("  WHERE dg.ParentDeviceGroupId").eq(group.getId()); // starting point
        otherSql.append("  UNION ALL");
        otherSql.append("  SELECT dg.ParentDeviceGroupId, dg.DeviceGroupId");
        otherSql.append("  FROM DeviceGroup AS dg");
        otherSql.append("    JOIN DeviceGroup_CTE AS dgcte ON dg.ParentDeviceGroupId = dgcte.DeviceGroupId"); // recursive join
        otherSql.append(")");
        otherSql.append("SELECT dg_real.*");
        otherSql.append("FROM DeviceGroup_CTE"); // bring it all together
        otherSql.append("  JOIN DeviceGroup AS dg_real ON DeviceGroup_CTE.DeviceGroupId = dg_real.DeviceGroupId");

        PartialDeviceGroupRowMapper mapper = new PartialDeviceGroupRowMapper();
        List<PartialDeviceGroup> groups = jdbcTemplate.query(builder, mapper);
        
        PartialGroupResolver resolver = new PartialGroupResolver(this, group);
        List<StoredDeviceGroup> result = resolver.resolvePartials(groups);
        
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
    
    @Override
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
    
    @Override
    public StoredDeviceGroup getGroupByName(StoredDeviceGroup parent, 
            String groupName) throws NotFoundException, IllegalGroupNameException {
        
        DeviceGroupUtil.validateName(groupName);
        
        return getGroupByName(parent, groupName, false);
    }
    
    @Override
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
            String fullName = parent.getFullName()+"/"+groupName;

            // throw special exception if the group is child of Hidden group (temp group)
            if (parent.isHidden()) {
                throw new TemporaryDeviceGroupNotFoundException("Group \"" + fullName + "\" could not be found");
            }

            throw new NotFoundException("Group \"" + fullName + "\" could not be found");
        }
        PartialGroupResolver resolver = new PartialGroupResolver(this, parent);
        StoredDeviceGroup resolvedGroup = resolver.resolvePartial(group);
        return resolvedGroup;
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED, noRollbackFor={DuplicateException.class})
    public StoredDeviceGroup addGroup(StoredDeviceGroup parentGroup, DeviceGroupType type, String groupName) throws IllegalGroupNameException {
        DeviceGroupPermission permission = type.equals(DeviceGroupType.STATIC) ? DeviceGroupPermission.EDIT_MOD : DeviceGroupPermission.EDIT_NOMOD;
        return addGroup(parentGroup, type, groupName, permission);
    }
    
    @Override
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
            jdbcTemplate.update(sql);
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

    @Override
    @Transactional
    public void updateGroup(StoredDeviceGroup group) throws IllegalGroupNameException {
        Validate.isTrue(group.getParent() != null, "The root group cannot be updated.");
        
        DeviceGroupUtil.validateName(group.getName());
        String previousGroupName = getGroupById(group.getId()).getFullName();
        
        StoredDeviceGroup parentGroup = getStoredGroup(group.getParent());
        String groupName = SqlUtils.convertStringToDbValue(group.getName());

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE DeviceGroup");
        sql.append("SET GroupName").eq(groupName);
        sql.append(", ParentDeviceGroupId").eq(parentGroup.getId());
        sql.append("WHERE DeviceGroupId").eq(group.getId());

        List<String> tablesToUpdate = ImmutableList.of("DeviceGroupComposedGroup",
                                                       "DeviceDataMonitor",
                                                       "OutageMonitor",
                                                       "TamperFlagMonitor",
                                                       "StatusPointMonitor",
                                                       "PorterResponseMonitor",
                                                       "ValidationMonitor");

        List<SqlStatementBuilder> updateStatement = new ArrayList<>();
        SqlStatementBuilder jobPropertiesUpdate = new SqlStatementBuilder();
        jobPropertiesUpdate.append("UPDATE JobProperty")
                           .append("SET Value = REPLACE(Value,")
                           .appendArgument(previousGroupName)
                           .append(",")
                           .appendArgument(group.getFullName())
                           .append(")")
                           .append("WHERE (Value").startsWith(previousGroupName)
                           .append("AND Name").eq("deviceGroup")
                           .append(") OR (Value").contains(previousGroupName)
                           .append("AND Name").eq("deviceGroupNames")
                           .append(")");
        updateStatement.add(jobPropertiesUpdate);
        
        SqlStatementBuilder deviceGroupNamesUpdate = new SqlStatementBuilder();
        deviceGroupNamesUpdate.append("UPDATE JobProperty")
                              .append("SET Value = REPLACE(Value,")
                              .appendArgument(previousGroupName)
                              .append(",")
                              .appendArgument(group.getFullName())
                              .append(")")
                              .append("WHERE Value").contains(previousGroupName)
                              .append("AND Name").eq("deviceGroupNames");
        updateStatement.add(deviceGroupNamesUpdate);

        SqlStatementBuilder updateWidgetSettingsQuery = new SqlStatementBuilder();
        updateWidgetSettingsQuery.append("UPDATE WidgetSettings")
                                 .append("SET Value = REPLACE(Value,")
                                 .appendArgument(previousGroupName)
                                 .append(",")
                                 .appendArgument(group.getFullName())
                                 .append(")")
                                 .append("WHERE Value").startsWith(previousGroupName)
                                 .append("AND Name").eq("deviceGroup");
        updateStatement.add(updateWidgetSettingsQuery);

        for (String tableName : tablesToUpdate) {
            SqlStatementBuilder updatetable = new SqlStatementBuilder();
            updatetable.append("UPDATE ").append(tableName)
                       .append("SET GroupName = REPLACE(GroupName,")
                       .appendArgument(previousGroupName)
                       .append(",")
                       .appendArgument(group.getFullName())
                       .append(")")
                       .append("WHERE GroupName").startsWith(previousGroupName);
            updateStatement.add(updatetable);
        }

        try {
            jdbcTemplate.update(sql);
            for (SqlStatementBuilder statement : updateStatement) {
                jdbcTemplate.update(statement);
            }
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Cannot change group name to the same name as an existing group with the same parent.", e);
        }
    }
    
    @Override
    public void removeGroup(StoredDeviceGroup group) {
        removeGroup(group, false);
    }
    
    @Override
    @Transactional
    public void removeGroup(StoredDeviceGroup group, boolean deleteNonEditable) {
        Validate.isTrue(group.getParent() != null, "The root group cannot be deleted.");
        if (!deleteNonEditable) {
            Validate.isTrue(group.isEditable(), "Non-editable groups cannot be deleted.");
        }
        
        List<StoredDeviceGroup> childGroups = getChildGroups(group);
        for(StoredDeviceGroup childGroup : childGroups){
            removeGroup(childGroup, deleteNonEditable);
        }
        
        log.trace("Deleting device group with id " + group.getId());
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM DeviceGroup");
        sql.append("WHERE DeviceGroupId").eq(group.getId());
        
        jdbcTemplate.update(sql);
    }

    @Override
    public int removeDevices(StoredDeviceGroup group, YukonPao... yukonPao) {
        List<YukonPao> yukonPaos = Arrays.asList(yukonPao);
        return removeDevices(group, yukonPaos);
    }
    
    @Override
    public int removeDevices(StoredDeviceGroup group, Collection<? extends YukonPao> yukonPaos) {
        Collection<Integer> paoIds = new MappingCollection<YukonPao, Integer>(yukonPaos, new YukonPaoToIdMapper());
        return removeDevicesById(group, paoIds);
    }
    
    @Override
    public int removeDevicesById(StoredDeviceGroup group, Collection<Integer> deviceIds) {

        int rowsAffected = chunkyJdbcTemplate.update(new RemoveDevicesByIdSqlGenerator(group.getId()), deviceIds);
        return rowsAffected;
    }

    private class RemoveDevicesByIdSqlGenerator implements SqlFragmentGenerator<Integer> {
        private int groupId;

        public RemoveDevicesByIdSqlGenerator(int groupId) {
            this.groupId = groupId;
        }

        @Override
        public SqlFragmentSource generate(List<Integer> deviceIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM DeviceGroupMember");
            sql.append("WHERE DeviceGroupId").eq(groupId);
            sql.append(" AND YukonPaoId").in(deviceIds);
            return sql;
        }
    }

    @Override
    public void removeAllChildDevices(StoredDeviceGroup group) {
        String sql = "DELETE FROM DeviceGroupMember where DeviceGroupId = ?";
        jdbcTemplate.update(sql, group.getId());
    }

    /**
     * Deprecated via interface.
     */
    @Override
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
    
    @Override
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
    
    @Override
    @Transactional
    public StoredDeviceGroup getGroupById(int groupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dg.*");
        sql.append("from DeviceGroup dg");
        sql.append("where dg.devicegroupid = ?");
        
        StoredDeviceGroup storedDeviceGroup = queryForDeviceGroup(sql.toString(), groupId);
        
        return storedDeviceGroup;
    }
    
    @Override
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
    @Transactional
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
    
    @Override
    @Transactional
    public StoredDeviceGroup getStoredGroup(SystemGroupEnum systemGroupEnum, String groupName, boolean create) throws NotFoundException {
        
        String basePath = deviceGroupService.getFullPath(systemGroupEnum);
        String fullName = basePath + groupName;
        return getStoredGroup(fullName, create);
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
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public StoredDeviceGroup getSystemGroup(SystemGroupEnum systemGroupEnum) throws NotFoundException {
        String groupName = deviceGroupService.getFullPath(systemGroupEnum);
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
    
    @Override
    public String getFullPath(SystemGroupEnum systemGroupEnum) {
        String fullPath = systemGroupPaths.get(systemGroupEnum);

        if (fullPath == null) {
            throw new NotFoundException("Full path was not found for SystemGroupEnum = " + systemGroupEnum);
        }

        return fullPath;
    }
}
