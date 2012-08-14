package com.cannontech.core.users.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.cannontech.spring.YukonSpringHook;

public class UserGroupDaoImpl implements UserGroupDao, InitializingBean {
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
        
    private SimpleTableAccessTemplate<LiteUserGroup> userGroupTemplate;
    
    private FieldMapper<LiteUserGroup> userGroupFieldMapper = new FieldMapper<LiteUserGroup>() {
        @Override
        public void extractValues(MapSqlParameterSource p, LiteUserGroup liteUserGroup) {
            p.addValue("UserGroupName", liteUserGroup.getUserGroupName());
            p.addValue("UserGroupDescription", liteUserGroup.getUserGroupDescription());
        }
        
        @Override
        public Number getPrimaryKey(LiteUserGroup liteUserGroup) {
            return liteUserGroup.getUserGroupId();
        }
        
        @Override
        public void setPrimaryKey(LiteUserGroup liteUserGroup, int newId) {
            liteUserGroup.setUserGroupId(newId);
        }
    };
    
    @Override
    public void afterPropertiesSet() throws Exception {
        userGroupTemplate = new SimpleTableAccessTemplate<LiteUserGroup>(yukonJdbcTemplate, nextValueHelper);
        userGroupTemplate.setTableName("UserGroup");
        userGroupTemplate.setPrimaryKeyField("UserGroupId");
        userGroupTemplate.setFieldMapper(userGroupFieldMapper);
        userGroupTemplate.setPrimaryKeyValidOver(0);
    }

    //Row Mappers
    public static class LiteUserGroupRowMapper implements YukonRowMapper<LiteUserGroup> {
        @Override
        public LiteUserGroup mapRow(YukonResultSet rs) throws SQLException {
            LiteUserGroup liteUserGroup = new LiteUserGroup();
            
            liteUserGroup.setUserGroupId(rs.getInt("UserGroupId"));
            liteUserGroup.setUserGroupName(rs.getString("UserGroupName"));
            liteUserGroup.setUserGroupDescription(rs.getStringSafe("UserGroupDescription"));

            return liteUserGroup;
        }
    }
    
    private static class UserGroupRowMapper implements YukonRowMapper<UserGroup> {
        private Logger log = YukonLogManager.getLogger(UserGroupRowMapper.class);

        @Override
        public UserGroup mapRow(YukonResultSet rs) throws SQLException {
            UserGroup userGroup = new UserGroup();
            userGroup.setUserGroupId(rs.getInt("UserGroupId"));
            userGroup.setLiteUserGroup(new LiteUserGroupRowMapper().mapRow(rs));
            
            RoleDao roleDao = YukonSpringHook.getBean("roleDao", RoleDao.class);
            Map<YukonRole, LiteYukonGroup> rolesToGroupsMap = 
                    roleDao.getRolesAndRoleGroupsForUserGroup(userGroup.getLiteUserGroup().getUserGroupId());
            try {
                userGroup.putAllRolesToGroupMap(rolesToGroupsMap);
            } catch (ConfigurationException e) {
                log.error("The user group "+userGroup.getLiteUserGroup().getUserGroupName()+" has conflicting role groups", e);
            }
            
            return userGroup;
        }
    }
    
    @Override
    public void create(final LiteUserGroup liteUserGroup) {
        userGroupTemplate.save(liteUserGroup);
    }

    @Override
    public void update(LiteUserGroup liteUserGroup) {
        userGroupTemplate.save(liteUserGroup);
    }

    @Override
    public void delete(LiteUserGroup liteUserGroup) {
        delete(liteUserGroup.getUserGroupId());
    }

    @Override
    public void delete(int liteUserGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserGroup");
        sql.append("WHERE UserGroupId").eq(liteUserGroupId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public LiteUserGroup getLiteUserGroup(int userGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM UserGroup UG");
        sql.append("WHERE UG.UserGroupId").eq(userGroupId);
        
        LiteUserGroup userGroup = yukonJdbcTemplate.queryForObject(sql, new LiteUserGroupRowMapper());
        return userGroup;
    }

    @Override
    public UserGroup getUserGroup(int userGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM UserGroup UG");
        sql.append("WHERE UG.UserGroupId").eq(userGroupId);
        
        UserGroup userGroup = yukonJdbcTemplate.queryForObject(sql, new UserGroupRowMapper());
        return userGroup;
    }
    
    @Override
    public List<UserGroup> getUserGroupsByRoleGroupId(int roleGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM UserGroup UG");
        sql.append("  JOIN UserGroupToYukonGroupMapping UGYGM ON UGYGM.UserGroupId = UG.UserGroupId");
        sql.append("WHERE UGYGM.GroupId").eq(roleGroupId);
        
        List<UserGroup> userGroups = yukonJdbcTemplate.query(sql, new UserGroupRowMapper());
        return userGroups;
    }
    
    @Override
    public LiteUserGroup getLiteUserGroupByUserId(int userId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UG.*");
        sql.append("FROM UserGroup UG");
        sql.append("  JOIN YukonUser YU ON UG.UserGroupId = YU.UserGroupId");
        sql.append("WHERE YU.UserId").eq(userId);
        
        LiteUserGroup userGroup = yukonJdbcTemplate.queryForObject(sql, new LiteUserGroupRowMapper());
        return userGroup;
    }

    @Override
    public LiteUserGroup getLiteUserGroupByUserGroupName(String userGroupName) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UG.*");
        sql.append("FROM UserGroup UG");
        sql.append("WHERE UG.UserGroupName").eq(userGroupName);
        
        LiteUserGroup userGroup = yukonJdbcTemplate.queryForObject(sql, new LiteUserGroupRowMapper());
        return userGroup;
    }

    @Override
    public List<LiteUserGroup> getAllLiteUserGroups() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UG.*");
        sql.append("FROM UserGroup UG");
        
        List<LiteUserGroup> userGroups = yukonJdbcTemplate.query(sql, new LiteUserGroupRowMapper());
        return userGroups;
    }

    @Override
    public void createUserGroupToYukonGroupMappng(int userGroupId, int roleGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO UserGroupToYukonGroupMapping");
        sql.values(userGroupId, roleGroupId);

        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void deleteUserGroupToYukonGroupMappng(int userGroupId, int roleGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserGroupToYukonGroupMapping");
        sql.append("WHERE UserGroupId").eq(userGroupId);
        sql.append("  AND groupId").eq(roleGroupId);

        yukonJdbcTemplate.update(sql);
    }
}