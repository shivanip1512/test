package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;

public class YukonGroupDaoImpl implements YukonGroupDao {

    private JdbcOperations jdbcTemplate = null;
    private SimpleJdbcTemplate simpleJdbcTemplate = null;

    public void setJdbcOps(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcTemplate = new SimpleJdbcTemplate(jdbcTemplate);
    }

    @SuppressWarnings("unchecked")
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

    public LiteYukonGroup getLiteYukonGroup( int groupID )
    {
        String sql = "select groupid, groupname from YukonGroup where groupid = ? ";
        List<LiteYukonGroup> groupList = jdbcTemplate.query(sql,
                                                    new Object[] { groupID },
                                                    new LiteYukonGroupMapper());
        return groupList.get(0);
    }
    
    @Override
    public LiteYukonGroup getLiteYukonGroupByName(String groupName) {
        String sql = "SELECT GroupId, GroupName FROM ? WHERE GroupName = ?";
        LiteYukonGroup group = simpleJdbcTemplate.queryForObject(sql, new LiteYukonGroupMapper(), groupName);
        return group;
    }
}
