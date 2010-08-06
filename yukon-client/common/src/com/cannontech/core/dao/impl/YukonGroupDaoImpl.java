package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;

public class YukonGroupDaoImpl implements YukonGroupDao {

    private JdbcOperations jdbcTemplate = null;
    private YukonJdbcTemplate yukonJdbcTemplate = null;

    public void setJdbcOps(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.yukonJdbcTemplate = new YukonJdbcTemplate(jdbcTemplate);
    }

    public List<LiteYukonGroup> getGroupsForUser(LiteYukonUser user) {

    	return getGroupsForUser(user.getUserID());
    }
    
    public List<LiteYukonGroup> getGroupsForUser(int userID) {

        String sql = "select yug.userid, yug.groupid, yg.groupname from YukonUserGroup yug, YukonGroup yg where userid = ? " +
                " and yug.groupid = yg.groupid";
        List<LiteYukonGroup> groupList = jdbcTemplate.query(sql,
                                                            new Object[] { userID },
                                                            new LiteYukonGroupMapper());
        return groupList;
    }

    /**
     * Mapping class to process a result set row into a LiteYukonGroup
     */
    private class LiteYukonGroupMapper implements ParameterizedRowMapper<LiteYukonGroup>{

        public LiteYukonGroup mapRow(ResultSet rs, int rowNum) throws SQLException {

            LiteYukonGroup group = new LiteYukonGroup();
            group.setGroupID(rs.getInt("groupid"));
            String groupName = rs.getString("groupname");
            if(groupName != null)
                group.setGroupName(groupName);
            return group;
        }

    }

    private ParameterizedRowMapper<Map.Entry<Integer, LiteYukonGroup>> mappingRowMapper =
        new ParameterizedRowMapper<Map.Entry<Integer, LiteYukonGroup>>() {
            public Map.Entry<Integer, LiteYukonGroup> mapRow(ResultSet rs, int rowNum) throws SQLException {
                int groupId = rs.getInt("groupId");
                String groupName = rs.getString("groupName");

                LiteYukonGroup group = new LiteYukonGroup();
                group.setGroupID(groupId);
                if (groupName != null)
                    group.setGroupName(groupName);
                return Maps.immutableEntry(groupId, group);
            }
        };

    public LiteYukonGroup getLiteYukonGroup( int groupID )
    {
        String sql = "select groupid, groupname from YukonGroup where groupid = ? ";
        List<LiteYukonGroup> groupList = jdbcTemplate.query(sql,
                                                    new Object[] { groupID },
                                                    new LiteYukonGroupMapper());
        return groupList.get(0);
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
            template.mappedQuery(sqlGenerator, groupIds, mappingRowMapper, typeMapper);

        return retVal;
    }

    @Override
    public LiteYukonGroup getLiteYukonGroupByName(String groupName) {
        String sql = "SELECT GroupId, GroupName FROM YukonGroup WHERE GroupName = ?";
        LiteYukonGroup group;        
        try {
            group = yukonJdbcTemplate.queryForObject(sql, new LiteYukonGroupMapper(), groupName);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Login group name: " + groupName + " not found.", e);
        }        
        return group;
    }
}
