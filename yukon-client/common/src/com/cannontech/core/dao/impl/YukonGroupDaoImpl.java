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
import com.cannontech.core.authorization.dao.PaoPermissionDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.user.YukonGroup;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;

public class YukonGroupDaoImpl implements YukonGroupDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
    private SimpleTableAccessTemplate<LiteYukonGroup> simpleTableTemplate;
    private NextValueHelper nextValueHelper;
    private PaoPermissionDao<LiteYukonGroup> groupPaoPermissionDao;
    private DBPersistentDao dbPersistantDao;
    
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
    
    private YukonRowMapper<Map.Entry<Integer, LiteYukonGroup>> mapEntryRowMapper =
        new YukonRowMapper<Map.Entry<Integer, LiteYukonGroup>>() {
        public Map.Entry<Integer, LiteYukonGroup> mapRow(YukonResultSet rs) throws SQLException {
            int groupId = rs.getInt("groupId");
            String groupName = rs.getString("groupName");

            LiteYukonGroup group = new LiteYukonGroup();
            group.setGroupID(groupId);
            if (groupName != null)
                group.setGroupName(groupName);
            return Maps.immutableEntry(groupId, group);
        }
    };
    
    @PostConstruct
    public void init() {
        simpleTableTemplate = new SimpleTableAccessTemplate<LiteYukonGroup>(yukonJdbcTemplate, nextValueHelper);
        simpleTableTemplate.setTableName("YukonGroup");
        simpleTableTemplate.setFieldMapper(fieldMapper);
        simpleTableTemplate.setPrimaryKeyField("GroupId");
        simpleTableTemplate.setPrimaryKeyValidNotEqualTo(0);
    }
    
    /**
     * Mapping class to process a result set row into a LiteYukonGroup
     */
    public static YukonRowMapper<LiteYukonGroup> liteYukonGroupRowMapper = new YukonRowMapper<LiteYukonGroup> () {

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
    
    public List<LiteYukonGroup> getGroupsForUser(int userId) {
        return getGroupsForUser(userId, false);
    }
    
    @Override
    public List<LiteYukonGroup> getAllGroups() {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GroupId, GroupName, GroupDescription");
        sql.append("FROM YukonGroup");

        List<LiteYukonGroup> groupList = yukonJdbcTemplate.query(sql, liteYukonGroupRowMapper);

        return groupList;
    }
    
    @Override
    public List<LiteYukonGroup> getGroupsForUser(int userId, boolean excludeYukonGroup) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT yug.UserId, yug.GroupId, yg.GroupName, yg.GroupDescription ");
        sql.append("FROM YukonUserGroup yug");
        sql.append(  "JOIN YukonGroup yg ON yg.GroupId = yug.GroupId");
        sql.append("WHERE UserId").eq(userId);
        if (excludeYukonGroup) {
            sql.append("AND yg.GroupId").neq_k(YukonGroup.YUKON_GROUP_ID);
        }
        
        List<LiteYukonGroup> groupList = yukonJdbcTemplate.query(sql, liteYukonGroupRowMapper);
        
        return groupList;
    }

    @Override
    public LiteYukonGroup getLiteYukonGroup(int groupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder(); 
        sql.append("SELECT GroupId, GroupName, GroupDescription");
        sql.append("FROM YukonGroup");
        sql.append("WHERE GroupId").eq(groupId);
        
        LiteYukonGroup group = yukonJdbcTemplate.queryForObject(sql, liteYukonGroupRowMapper);
        
        return group;
    }

    @Override
    public Map<Integer, LiteYukonGroup> getLiteYukonGroups(Iterable<Integer> groupIds) {
        ChunkingMappedSqlTemplate template =
            new ChunkingMappedSqlTemplate(yukonJdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT groupId, groupName FROM yukonGroup");
                sql.append("WHERE groupId IN (").appendList(subList).append(")");
                return sql;
            }
        };

        Function<Integer, Integer> typeMapper = Functions.identity();

        Map<Integer, LiteYukonGroup> retVal =
            template.mappedQuery(sqlGenerator, groupIds, mapEntryRowMapper, typeMapper);

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
            group = yukonJdbcTemplate.queryForObject(sql, liteYukonGroupRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Login group name: " + groupName + " not found.", e);
        }        
        return group;
    }
    
    @Override
    public void save(LiteYukonGroup group) {
        boolean update = simpleTableTemplate.saveWillUpdate(group);
        simpleTableTemplate.save(group);
        
        DbChangeType changeType = update ? DbChangeType.UPDATE : DbChangeType.ADD;
        sendGroupDbChangeMsg(group.getGroupID(), changeType);
    }
    
    @Override
    @Transactional
    public void delete(int groupId) {
        /* YukonGroupRole */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM YukonGroupRole");
        sql.append("WHERE GroupId").eq(groupId);
        yukonJdbcTemplate.update(sql);
        
        /* YukonUserGroup */
        sql = new SqlStatementBuilder();
        sql.append("DELETE FROM YukonUserGroup");
        sql.append("WHERE GroupId").eq(groupId);
        yukonJdbcTemplate.update(sql);
        
        /* PaoPermissions */
        groupPaoPermissionDao.removeAllPermissions(groupId);
        
        /* YukonGroup */
        sql = new SqlStatementBuilder();
        sql.append("DELETE FROM YukonGroup");
        sql.append("WHERE GroupId").eq(groupId);
        yukonJdbcTemplate.update(sql);
        
        sendGroupDbChangeMsg(groupId, DbChangeType.DELETE);
    }
    
    private void sendGroupDbChangeMsg(Integer groupId, DbChangeType dbChangeType) {
        DBChangeMsg changeMsg = new DBChangeMsg(groupId,
                                                DBChangeMsg.CHANGE_YUKON_USER_DB,
                                                DBChangeMsg.CAT_YUKON_USER_GROUP,
                                                DBChangeMsg.CAT_YUKON_USER,
                                                dbChangeType);
        dbPersistantDao.processDBChange(changeMsg);
    }
    
    /* Depenencies */
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    public void setGroupPaoPermissionDao(PaoPermissionDao<LiteYukonGroup> groupPaoPermissionDao) {
        this.groupPaoPermissionDao = groupPaoPermissionDao;
    }
    
    @Autowired
    public void setDbPersistantDao(DBPersistentDao dbPersistantDao) {
        this.dbPersistantDao = dbPersistantDao;
    }
    
}