package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.db.user.YukonGroupRole;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.YukonGroupRoleDefs;
import com.cannontech.yukon.IDatabaseCache;

/**
 * A collection of methods to handle Roles and property lookups for Groups and Users
 * @author rneuharth
 */
public class RoleDaoImpl implements RoleDao
{
    private IDatabaseCache databaseCache;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private DBPersistentDao dbPersistentDao;

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

	public String getGlobalPropertyValue( int rolePropertyID_ )
	{
		LiteYukonRoleProperty p = getRoleProperty( rolePropertyID_ );

		String val = null;
		if( p != null )
			val = getRolePropValueGroup(
				getGroup(YukonGroupRoleDefs.GRP_YUKON),
				p.getRolePropertyID(),
				p.getDefaultValue() );

		return val;
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

    public boolean updateGroupRoleProperty(LiteYukonGroup group, int roleID, int rolePropertyId, String newVal) 
        throws CommandExecutionException, TransactionException {
        
        newVal = SqlUtils.convertStringToDbValue(newVal);
        YukonGroupRole groupRole = getYukonGroupRole(group.getGroupID(), rolePropertyId);        
        
        if (groupRole != null) {
            groupRole.setValue( newVal );
            dbPersistentDao.performDBChange(groupRole, DBChangeMsg.CHANGE_TYPE_UPDATE);
            
        } else {
            groupRole = new YukonGroupRole(null, group.getGroupID(), roleID, rolePropertyId, newVal);
            dbPersistentDao.performDBChange(groupRole, DBChangeMsg.CHANGE_TYPE_ADD);
            
        }
        
        return true;
    }

    public YukonGroupRole getYukonGroupRole(int groupId, int rolePropertyId) {

        String sql = " SELECT YGR.groupRoleId, YGR.groupId, YGR.roleId, "+
                     " YGR.rolePropertyId, YGR.value "+
                     " FROM YukonGroupRole YGR "+
                     " WHERE YGR.GroupId = ? "+
                     " AND YGR.rolePropertyId = ? ";
        
        YukonGroupRole groupRole = simpleJdbcTemplate.queryForObject(sql, new YukonGroupRoleRowMapper(), groupId, rolePropertyId);

        return groupRole;
    }
    
    public <E extends Enum<E>> E getGlobalRolePropertyValue(Class<E> class1, int rolePropertyID) {
        String rolePropertyValue = this.getGlobalPropertyValue(rolePropertyID);
        E enumValue = Enum.valueOf(class1, rolePropertyValue);
        return enumValue;
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
    
    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }

    @Required
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    
    @Required
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
}
