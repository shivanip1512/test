package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;

public class YukonGroupDaoImpl implements YukonGroupDao {
    
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private SimpleTableAccessTemplate<LiteYukonGroup> tableTemplate;
    
    private final static FieldMapper<LiteYukonGroup> fieldMapper = new FieldMapper<LiteYukonGroup>() {
        
        @Override
        public Number getPrimaryKey(LiteYukonGroup group) {
            return group.getGroupID();
        }
        
        @Override
        public void setPrimaryKey(LiteYukonGroup group, int groupId) {
            group.setGroupID(groupId);
        }
        
        @Override
        public void extractValues(MapSqlParameterSource parameterHolder, LiteYukonGroup group) {
            parameterHolder.addValue("GroupName", group.getGroupName());
            parameterHolder.addValue("GroupDescription", group.getGroupDescription());
        }
    };
    
    private final YukonRowMapper<Map.Entry<Integer, LiteYukonGroup>> mapEntryMapper =
        new YukonRowMapper<Map.Entry<Integer, LiteYukonGroup>>() {
        @Override
        public Map.Entry<Integer, LiteYukonGroup> mapRow(YukonResultSet rs) throws SQLException {
            
            int groupId = rs.getInt("groupId");
            String groupName = rs.getString("groupName");
            
            LiteYukonGroup group = new LiteYukonGroup();
            group.setGroupID(groupId);
            if (groupName != null) {
                group.setGroupName(groupName);
            }
            
            return Maps.immutableEntry(groupId, group);
        }
    };
    
    @PostConstruct
    public void init() {
        tableTemplate = new SimpleTableAccessTemplate<LiteYukonGroup>(jdbcTemplate, nextValueHelper);
        tableTemplate.setTableName("YukonGroup");
        tableTemplate.setFieldMapper(fieldMapper);
        tableTemplate.setPrimaryKeyField("GroupId");
        tableTemplate.setPrimaryKeyValidNotEqualTo(0);
    }
    
    public static YukonRowMapper<LiteYukonGroup> mapper = new YukonRowMapper<LiteYukonGroup> () {
        
        @Override
        public LiteYukonGroup mapRow(YukonResultSet rs) throws SQLException {
            
            LiteYukonGroup group = new LiteYukonGroup();
            group.setGroupID(rs.getInt("GroupId"));
            group.setGroupName(rs.getString("GroupName"));
            group.setGroupDescription(rs.getString("GroupDescription"));
            
            return group;
        }
    };
    
    @Override
    public List<LiteYukonGroup> getGroupsForUser(LiteYukonUser user) {
    	return getGroupsForUser(user.getUserID());
    }
    
    @Override
    public List<LiteYukonGroup> getAllGroups() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GroupId, GroupName, GroupDescription");
        sql.append("FROM YukonGroup");
        
        List<LiteYukonGroup> groupList = jdbcTemplate.query(sql, mapper);
        
        return groupList;
    }
    
    @Override
    public List<LiteYukonGroup> getGroupsForUser(int userId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YU.UserId, YG.GroupId, YG.GroupName, YG.GroupDescription ");
        sql.append("FROM YukonGroup YG");
        sql.append("  JOIN UserGroupToYukonGroupMapping UGYGM ON YG.GroupId = UGYGM.GroupId");
        sql.append("  JOIN YukonUser YU ON UGYGM.UserGroupId = YU.UserGroupId");
        sql.append("WHERE YU.UserId").eq(userId);
        
        List<LiteYukonGroup> groupList = jdbcTemplate.query(sql, mapper);
        
        return groupList;
    }
    
    @Override
    public LiteYukonGroup getLiteYukonGroup(int groupId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder(); 
        sql.append("SELECT GroupId, GroupName, GroupDescription");
        sql.append("FROM YukonGroup");
        sql.append("WHERE GroupId").eq(groupId);
        
        LiteYukonGroup group = jdbcTemplate.queryForObject(sql, mapper);
        
        return group;
    }
    
    @Override
    public Map<Integer, LiteYukonGroup> getLiteYukonGroups(Iterable<Integer> groupIds) {
        
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);
        
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT groupId, groupName FROM yukonGroup");
                sql.append("WHERE groupId IN (").appendList(subList).append(")");
                
                return sql;
            }
        };
        
        Function<Integer, Integer> typeMapper = Functions.identity();
        
        Map<Integer, LiteYukonGroup> retVal =
            template.mappedQuery(sqlGenerator, groupIds, mapEntryMapper, typeMapper);
        
        return retVal;
    }
    
    @Override
    public LiteYukonGroup getLiteYukonGroupByName(String groupName) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GroupId, GroupName, GroupDescription");
        sql.append("FROM YukonGroup");
        sql.append("WHERE GroupName").eq(groupName);
        
        LiteYukonGroup group;
        try {
            group = jdbcTemplate.queryForObject(sql, mapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Role group name: " + groupName + " not found.", e);
        }
        
        return group;
    }
    
    @Override
    public LiteYukonGroup findLiteYukonGroupByName(String groupName) {
        try {
            return getLiteYukonGroupByName(groupName);
        } catch (NotFoundException e) {}
        
        return null;
    }
    
    @Override
    public void save(LiteYukonGroup group) {
        
        boolean update = tableTemplate.saveWillUpdate(group);
        tableTemplate.save(group);
        
        dbChangeManager.processDbChange(group.getGroupID(),
                                        DBChangeMsg.CHANGE_YUKON_USER_DB,
                                        DBChangeMsg.CAT_YUKON_USER_GROUP,
                                        DBChangeMsg.CAT_YUKON_USER,
                                        update ? DbChangeType.UPDATE : DbChangeType.ADD);
    }
    
    @Override
    @Transactional
    public void delete(int groupId) {
        
        /* YukonGroupRole */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM YukonGroupRole");
        sql.append("WHERE GroupId").eq(groupId);
        jdbcTemplate.update(sql);
        
        /* YukonGroup */
        sql = new SqlStatementBuilder();
        sql.append("DELETE FROM YukonGroup");
        sql.append("WHERE GroupId").eq(groupId);
        jdbcTemplate.update(sql);
        
        dbChangeManager.processDbChange(groupId,
                                        DBChangeMsg.CHANGE_YUKON_USER_DB,
                                        DBChangeMsg.CAT_YUKON_USER_GROUP,
                                        DBChangeMsg.CAT_YUKON_USER,
                                        DbChangeType.DELETE);
    }
    
    @Override
    public List<LiteYukonGroup> getRoleGroupsForUserGroupId(int userGroupId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YG.GroupId, YG.GroupName, YG.GroupDescription ");
        sql.append("FROM UserGroupToYukonGroupMapping UGYGM");
        sql.append("  JOIN YukonGroup YG ON YG.GroupId = UGYGM.GroupId");
        sql.append("WHERE UGYGM.UserGroupId").eq(userGroupId);
        
        List<LiteYukonGroup> roleGroupList = jdbcTemplate.query(sql, mapper);
        
        return roleGroupList;
    }
    
    @Override
    public List<LiteYukonGroup> getDistinctRoleGroupsForUserGroupIds(List<Integer> userGroupIds) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT YG.GroupId, YG.GroupName, YG.GroupDescription ");
        sql.append("FROM UserGroupToYukonGroupMapping UGYGM");
        sql.append("  JOIN YukonGroup YG ON YG.GroupId = UGYGM.GroupId");
        sql.append("WHERE UGYGM.UserGroupId").in(userGroupIds);

        List<LiteYukonGroup> roleGroupList = jdbcTemplate.query(sql, mapper);
        
        return roleGroupList;
    }
    
}