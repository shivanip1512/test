package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.user.YukonGroupRole;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

/**
 * A collection of methods to handle Roles and property lookups for Groups and Users
 */
public class RoleDaoImpl implements RoleDao {
    
    private IDatabaseCache databaseCache;
    private DBPersistentDao dbPersistentDao;
    private RolePropertyDao rolePropertyDao;
    private YukonGroupDao yukonGroupDao;
    private YukonJdbcTemplate yukonJdbcTemplate;

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.RoleDao#getRoleProperties(int)
     */
	public LiteYukonRoleProperty[] getRoleProperties( int roleID_ )
	{
        ArrayList<LiteYukonRoleProperty> list = new ArrayList<LiteYukonRoleProperty>(64);
        synchronized (databaseCache) {
            List<LiteYukonRoleProperty> props = databaseCache.getAllYukonRoleProperties();

            for (int i = 0; i < props.size(); i++) {
                LiteYukonRoleProperty p = props.get(i);
                if (p.getRoleID() == roleID_)
                    list.add(p);
            }
        }

        LiteYukonRoleProperty[] retVal = new LiteYukonRoleProperty[list.size()];
        return list.toArray(retVal);
    }

	@Deprecated
	public String getGlobalPropertyValue( int rolePropertyID_ ) {
	    String value = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.getForId(rolePropertyID_), null);
	    return value;
	}
	
	@Override
	public boolean checkGlobalRoleProperty(int rolePropertyID) {
	    YukonRoleProperty property = YukonRoleProperty.getForId(rolePropertyID);
	    if (rolePropertyDao.isCheckPropertyCompatible(property)) {
	        return rolePropertyDao.checkProperty(property, null);
	    } else {
	        // uh oh, the property must not be Boolean
	        // print a complaint in the log and try the old code
	        CTILogger.warn("Property " + property + " improperly accessed with a check method");
	        return !CtiUtilities.isFalse(getGlobalPropertyValue(rolePropertyID));
	    }
	}

	public LiteYukonRoleProperty getRoleProperty(int propid) {
        
        synchronized(databaseCache) {
            for(Iterator<LiteYukonRoleProperty> i = databaseCache.getAllYukonRoleProperties().iterator(); i.hasNext();) {
                LiteYukonRoleProperty p = i.next();
                if(p.getRolePropertyID() == propid) {
                    return p;
                }
            }
        }
        return null;
    }

    public LiteYukonRole getLiteRole(Integer rolePropID) {
        List<LiteYukonRoleProperty> roleProps = Collections.emptyList();
        List<LiteYukonRole> roles = Collections.emptyList();
        synchronized (databaseCache) {
            roleProps = databaseCache.getAllYukonRoleProperties();
            roles = databaseCache.getAllYukonRoles();
            for (LiteYukonRoleProperty property : roleProps) {
                if (property.getRolePropertyID() == rolePropID) {
                    for (LiteYukonRole role : roles) {
                        if (property.getRoleID() == role.getRoleID()) {
                            return role;
                        }
                    }
                }
            }
        }
        throw new NotFoundException("Role ID Could not be found");
    }

    @Deprecated
    public String getRolePropValueGroup(LiteYukonGroup group, int rolePropertyID, String defaultValue) {
        synchronized (databaseCache) {
            LiteYukonRoleProperty roleProperty = getRoleProperty(rolePropertyID);

            Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>>> lookupMap = databaseCache.getYukonGroupRolePropertyMap();

            Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>> roleMap = lookupMap.get(group);

            if (roleMap != null) {
                for (Map<LiteYukonRoleProperty, String> propMap : roleMap.values()) {
                    String val = propMap.get(roleProperty);
                    if (val != null) {
                        return val;
                    }
                }
            }
        }
        // I'm not sure when this would happen as the loader seems to take care
        // of the default
        return defaultValue;
    }

    @Deprecated
    public String getRolePropValueGroup(int groupId, int rolePropertyId, String defaultValue) {
        LiteYukonGroup liteYukonGroup = getGroup(groupId);
        Validate.notNull(liteYukonGroup, "Could not find a valid LiteYukonGroup for groupId=" + groupId);
        return getRolePropValueGroup(liteYukonGroup, rolePropertyId, defaultValue);
    }
    
    
    public LiteYukonGroup getGroup(String groupName) {
        
        synchronized (databaseCache) {
            java.util.Iterator<LiteYukonGroup> it = databaseCache.getAllYukonGroups().iterator();
            while (it.hasNext()) {
                LiteYukonGroup group = it.next();
                if (group.getGroupName().equalsIgnoreCase( groupName ))
                    return group;
            }
        }
        
        return null;
    }

    public LiteYukonGroup getGroup(int grpID_) 
    {
          synchronized (databaseCache) 
          {
            java.util.Iterator<LiteYukonGroup> it = databaseCache.getAllYukonGroups().iterator();
            while (it.hasNext()) {
                LiteYukonGroup group = it.next();
                if (group.getGroupID() == grpID_ )
                    return group;
            }
          }
        
          return null;
    }

    public boolean updateGroupRoleProperty(LiteYukonGroup group, int roleID, int rolePropertyId, String newVal) {
        
        newVal = SqlUtils.convertStringToDbValue(newVal);
        YukonGroupRole groupRole = findYukonGroupRole(group.getGroupID(), rolePropertyId);        
        
        if (groupRole != null) {
            groupRole.setValue( newVal );
            dbPersistentDao.performDBChange(groupRole, TransactionType.UPDATE);
            
        } else {
            groupRole = new YukonGroupRole(null, group.getGroupID(), roleID, rolePropertyId, newVal);
            dbPersistentDao.performDBChange(groupRole, TransactionType.INSERT);
            
        }
        
        return true;
    }

    /**
     * Return yukonGroupRole for groupId and rolePropertyId
     * If rolePropertyId does not exist for the YukonGroupRole (groupId), then null is returned.
     * @param groupId
     * @param rolePropertyId
     * @return
     */
    public YukonGroupRole findYukonGroupRole(int groupId, int rolePropertyId) {

        String sql = " SELECT YGR.groupRoleId, YGR.groupId, YGR.roleId, "+
                     " YGR.rolePropertyId, YGR.value "+
                     " FROM YukonGroupRole YGR "+
                     " WHERE YGR.GroupId = ? "+
                     " AND YGR.rolePropertyId = ? ";
        try {
            YukonGroupRole groupRole = yukonJdbcTemplate.queryForObject(sql, new YukonGroupRoleRowMapper(), groupId, rolePropertyId);
            return groupRole;
        } catch (EmptyResultDataAccessException e) {
            // return null when result is _empty_
            return null;
        }
    }
    
    @Deprecated
    public <E extends Enum<E>> E getGlobalRolePropertyValue(Class<E> class1, int rolePropertyID) {
        E enumValue = rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.getForId(rolePropertyID), class1, null);
        return enumValue;
    }

    @Override
    public SetMultimap<LiteYukonGroup, YukonRole> getGroupsAndRolesForUser(LiteYukonUser user) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YUG.GroupId");
        sql.append("FROM YukonUserGroup YUG");
        sql.append("WHERE YUG.UserId").eq(user.getUserID());
        List<Integer> groupIdList = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        
        SetMultimap<LiteYukonGroup, YukonRole> results = HashMultimap.create();
        for (Integer groupId : groupIdList) {
            LiteYukonGroup liteYukonGroup = yukonGroupDao.getLiteYukonGroup(groupId);
            Set<YukonRole> rolesForGroup = getRolesForGroup(liteYukonGroup);
            results.putAll(liteYukonGroup, rolesForGroup);
        }
        
        return results;

    }

    @Override
    public Set<YukonRole> getRolesForGroup(LiteYukonGroup liteYukonGroup) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT RoleId");
        sql.append("FROM YukonGroupRole YGR");
        sql.append("WHERE YGR.GroupId").eq(liteYukonGroup.getGroupID());
        List<YukonRole> rolesForGroup = yukonJdbcTemplate.query(sql, new YukonRoleRowMapper());
        
        return Sets.newHashSet(rolesForGroup);
    }
    
    // Row Mappers
    private class YukonRoleRowMapper implements YukonRowMapper<YukonRole> {

        @Override
        public YukonRole mapRow(YukonResultSet rs) throws SQLException {
            return rs.getEnum("roleId", YukonRole.class);
        }

    }
    
    private class YukonGroupRoleRowMapper implements ParameterizedRowMapper<YukonGroupRole> {

        public YukonGroupRole mapRow(ResultSet rs, int row) throws SQLException {
            YukonGroupRole groupRole = new YukonGroupRole();
            groupRole.setGroupRoleID(rs.getInt("groupRoleId"));
            groupRole.setGroupID(rs.getInt("groupId"));
            groupRole.setRoleID(rs.getInt("roleID"));
            groupRole.setRolePropertyID(rs.getInt("rolePropertyId"));
            groupRole.setValue(SqlUtils.convertDbValueToString(rs.getString("value")));
            return groupRole;
        }
    }
    
    // DI Setters
    @Autowired
    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }

    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
