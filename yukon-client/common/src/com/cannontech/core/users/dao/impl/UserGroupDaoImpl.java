package com.cannontech.core.users.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.user.UserGroup;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.Multimap;

public class UserGroupDaoImpl implements UserGroupDao {
    
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private RoleDao roleDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private static final Logger log = YukonLogManager.getLogger(UserGroupDaoImpl.class);
        
    private SimpleTableAccessTemplate<com.cannontech.database.db.user.UserGroup> userGroupTemplate;
    
    private final FieldMapper<com.cannontech.database.db.user.UserGroup> userGroupFieldMapper = new FieldMapper<com.cannontech.database.db.user.UserGroup>() {
        @Override
        public void extractValues(MapSqlParameterSource p, com.cannontech.database.db.user.UserGroup userGroup) {
            p.addValue("Name", userGroup.getUserGroupName());
            p.addValue("Description", userGroup.getUserGroupDescription());
        }
        
        @Override
        public Number getPrimaryKey(com.cannontech.database.db.user.UserGroup userGroup) {
            return userGroup.getUserGroupId();
        }
        
        @Override
        public void setPrimaryKey(com.cannontech.database.db.user.UserGroup userGroup, int newId) {
            userGroup.setUserGroupId(newId);
        }
    };
    
    @PostConstruct
    public void init() throws Exception {
        userGroupTemplate = new SimpleTableAccessTemplate<com.cannontech.database.db.user.UserGroup>(jdbcTemplate, nextValueHelper);
        userGroupTemplate.setTableName("UserGroup");
        userGroupTemplate.setPrimaryKeyField("UserGroupId");
        userGroupTemplate.setFieldMapper(userGroupFieldMapper);
    }
    
    //Row Mappers
    
    public static class DBUserGroupRowMapper implements YukonRowMapper<com.cannontech.database.db.user.UserGroup> {
        @Override
        public com.cannontech.database.db.user.UserGroup mapRow(YukonResultSet rs) throws SQLException {
            
            com.cannontech.database.db.user.UserGroup userGroup = new com.cannontech.database.db.user.UserGroup();
            userGroup.setUserGroupId(rs.getInt("UserGroupId"));
            userGroup.setUserGroupName(rs.getString("Name"));
            userGroup.setUserGroupDescription(rs.getStringSafe("Description"));
            
            return userGroup;
        }
    }
    
    private class UserGroupRowMapper implements YukonRowMapper<UserGroup> {

        @Override
        public UserGroup mapRow(YukonResultSet rs) throws SQLException {
            
            UserGroup userGroup = new UserGroup();
            userGroup.setUserGroupId(rs.getInt("UserGroupId"));
            userGroup.setUserGroup(new DBUserGroupRowMapper().mapRow(rs));
            
            Multimap<YukonRole, LiteYukonGroup> rolesToGroupsMap = roleDao.getRolesAndRoleGroupsForUserGroup(userGroup.getUserGroup().getUserGroupId());
            try {
                userGroup.putAllRolesToGroupMap(rolesToGroupsMap);
            } catch (ConfigurationException e) {
                log.error("The user group "+userGroup.getUserGroup().getUserGroupName()+" has conflicting role groups", e);
            }
            
            return userGroup;
        }
    }
    
    @Override
    public int create(final com.cannontech.database.db.user.UserGroup userGroup) {
        int userGroupId = userGroupTemplate.insert(userGroup);
        userGroup.setUserGroupId(userGroupId);
        dbChangeManager.processDbChange(userGroup.getUserGroupId(), 
                DBChangeMsg.CHANGE_USER_GROUP_DB,
                DBChangeMsg.CAT_USER_GROUP, 
                DBChangeMsg.CAT_USER_GROUP, 
                DbChangeType.ADD);
        
        return userGroupId;
    }
    
    @Override
    public void update(com.cannontech.database.db.user.UserGroup userGroup) {
        userGroupTemplate.update(userGroup);
        dbChangeManager.processDbChange(userGroup.getUserGroupId(), 
                                        DBChangeMsg.CHANGE_USER_GROUP_DB,
                                        DBChangeMsg.CAT_USER_GROUP, 
                                        DBChangeMsg.CAT_USER_GROUP, 
                                        DbChangeType.UPDATE);
    }
    
    @Override
    public void delete(int userGroupId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserGroup");
        sql.append("WHERE UserGroupId").eq(userGroupId);
        
        jdbcTemplate.update(sql);
        
        dbChangeManager.processDbChange(userGroupId, 
                                        DBChangeMsg.CHANGE_USER_GROUP_DB,
                                        DBChangeMsg.CAT_USER_GROUP, 
                                        DBChangeMsg.CAT_USER_GROUP, 
                                        DbChangeType.DELETE);
    }
    
    @Override
    public com.cannontech.database.db.user.UserGroup getDBUserGroup(int userGroupId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserGroupId, Name, Description");
        sql.append("FROM UserGroup");
        sql.append("WHERE UserGroupId").eq(userGroupId);
        com.cannontech.database.db.user.UserGroup userGroup = jdbcTemplate.queryForObject(sql, new DBUserGroupRowMapper());
        
        return userGroup;
    }
    
    @Override
    public LiteUserGroup getLiteUserGroup(int userGroupId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserGroupId, Name, Description");
        sql.append("FROM UserGroup");
        sql.append("WHERE UserGroupId").eq(userGroupId);
        LiteUserGroup userGroup = jdbcTemplate.queryForObject(sql, new LiteUserGroupRowMapper());
        
        return userGroup;
    }
    
    @Override
    public UserGroup getUserGroup(int userGroupId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserGroupId, Name, Description");
        sql.append("FROM UserGroup");
        sql.append("WHERE UserGroupId").eq(userGroupId);
        UserGroup userGroup = jdbcTemplate.queryForObject(sql, new UserGroupRowMapper());
        
        return userGroup;
    }
    
    @Override
    public int getNumberOfUsers(int userGroupId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM UserGroup UG");
        sql.append("JOIN YukonUser YU ON YU.UserGroupId = UG.UserGroupId");
        sql.append("WHERE UG.UserGroupId").eq(userGroupId);
        int userCount = jdbcTemplate.queryForInt(sql);
        
        return userCount;
    }
    
    @Override
    public List<UserGroup> getUserGroupsByRoleGroupId(int roleGroupId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM UserGroup UG");
        sql.append("JOIN UserGroupToYukonGroupMapping UGYGM ON UGYGM.UserGroupId = UG.UserGroupId");
        sql.append("WHERE UGYGM.GroupId").eq(roleGroupId);
        List<UserGroup> userGroups = jdbcTemplate.query(sql, new UserGroupRowMapper());
        
        return userGroups;
    }
    
    @Override
    public LiteUserGroup getLiteUserGroupByUserId(int userId) {
        
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT UG.UserGroupId, UG.Name, UG.Description");
            sql.append("FROM UserGroup UG");
            sql.append("JOIN YukonUser YU ON UG.UserGroupId = YU.UserGroupId");
            sql.append("WHERE YU.UserId").eq(userId);
            LiteUserGroup userGroup = jdbcTemplate.queryForObject(sql, new LiteUserGroupRowMapper());
            
            return userGroup;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public List<LiteUserGroup> getLiteUserGroupsByRoleGroupId(int roleGroupId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM UserGroup UG");
        sql.append("JOIN UserGroupToYukonGroupMapping UGYGM ON UGYGM.UserGroupId = UG.UserGroupId");
        sql.append("WHERE UGYGM.GroupId").eq(roleGroupId);
        List<LiteUserGroup> liteUserGroups = jdbcTemplate.query(sql, new LiteUserGroupRowMapper());
        
        return liteUserGroups;
    }
    
    @Override
    public LiteUserGroup getLiteUserGroupByUserGroupName(String userGroupName) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserGroupId, Name, Description");
        sql.append("FROM UserGroup");
        sql.append("WHERE Name").eq(userGroupName);
        LiteUserGroup userGroup = jdbcTemplate.queryForObject(sql, new LiteUserGroupRowMapper());
        
        return userGroup;
    }
    
    @Override
    public LiteUserGroup findLiteUserGroupByUserGroupName(String userGroupName) {
        try {
            return getLiteUserGroupByUserGroupName(userGroupName);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public com.cannontech.database.db.user.UserGroup getDBUserGroupByUserGroupName(String userGroupName) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserGroupId, Name, Description");
        sql.append("FROM UserGroup");
        sql.append("WHERE Name").eq(userGroupName);
        
        com.cannontech.database.db.user.UserGroup userGroup = 
                jdbcTemplate.queryForObject(sql, new DBUserGroupRowMapper());
        return userGroup;
    }
    
    @Override
    public List<LiteUserGroup> getAllLiteUserGroups() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserGroupId, Name, Description");
        sql.append("FROM UserGroup UG");
        List<LiteUserGroup> userGroups = jdbcTemplate.query(sql, new LiteUserGroupRowMapper());
        
        return userGroups;
    }
    
    @Override
    public void createUserGroupToYukonGroupMappng(int userGroupId, int roleGroupId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO UserGroupToYukonGroupMapping");
        sql.values(userGroupId, roleGroupId);
        
        jdbcTemplate.update(sql);
        
        dbChangeManager.processDbChange(userGroupId, 
                                        DBChangeMsg.CHANGE_USER_GROUP_DB,
                                        DBChangeMsg.CAT_USER_GROUP, 
                                        DBChangeMsg.CAT_USER_GROUP, 
                                        DbChangeType.UPDATE);
    }
    
    @Override
    public void deleteUserGroupToYukonGroupMappng(int userGroupId, int roleGroupId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserGroupToYukonGroupMapping");
        sql.append("WHERE UserGroupId").eq(userGroupId);
        sql.append("  AND groupId").eq(roleGroupId);
        
        jdbcTemplate.update(sql);
        
        dbChangeManager.processDbChange(userGroupId, 
                                        DBChangeMsg.CHANGE_USER_GROUP_DB,
                                        DBChangeMsg.CAT_USER_GROUP, 
                                        DBChangeMsg.CAT_USER_GROUP, 
                                        DbChangeType.UPDATE);
    }
    
}