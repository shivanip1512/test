package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;

public class StateGroupDaoImpl implements StateGroupDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private static final YukonRowMapper<LiteStateGroup> groupMapper = new YukonRowMapper<LiteStateGroup>() {
        @Override
        public LiteStateGroup mapRow(YukonResultSet rs) throws SQLException {
            LiteStateGroup group = new LiteStateGroup(rs.getInt("StateGroupId"));
            group.setStateGroupName(rs.getString("Name"));
            group.setGroupType(rs.getString("GroupType"));
            return group;
        }
    };
    private static final YukonRowMapper<LiteState> stateMapper = new YukonRowMapper<LiteState>() {
        @Override
        public LiteState mapRow(YukonResultSet rs) throws SQLException {
            int rawState = rs.getInt("RawState");
            String text = rs.getString("Text");
            int fgColor = rs.getInt("ForegroundColor");
            int bgColor = rs.getInt("BackgroundColor");
            int imageId = rs.getInt("ImageId");
            LiteState state = new LiteState(rawState, text, fgColor, bgColor, imageId);
            return state;
        }
    };
        
    
    @Override
    public List<LiteStateGroup> getAllStateGroups() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select StateGroupId, Name, GroupType");
        sql.append("from StateGroup");
        
        List<LiteStateGroup> groups = jdbcTemplate.query(sql, groupMapper);
        
        for (LiteStateGroup group : groups) {
            sql = new SqlStatementBuilder();
            sql.append("select StateGroupId, RawState, Text, ForegroundColor, BackgroundColor, ImageId");
            sql.append("from State");
            sql.append("where StateGroupId").eq(group.getStateGroupID());
            
            List<LiteState> states = jdbcTemplate.query(sql, stateMapper);
            
            group.setStatesList(states);
        }
        
        return groups;
    }
    
    @Override
    public LiteStateGroup getStateGroup(int stateGroupId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select StateGroupId, Name, GroupType");
        sql.append("from StateGroup");
        sql.append("where StateGroupId").eq(stateGroupId);
        
        LiteStateGroup group = jdbcTemplate.queryForObject(sql, groupMapper);
        
        sql = new SqlStatementBuilder();
        sql.append("select StateGroupId, RawState, Text, ForegroundColor, BackgroundColor, ImageId");
        sql.append("from State");
        sql.append("where StateGroupId").eq(group.getStateGroupID());
        
        List<LiteState> states = jdbcTemplate.query(sql, stateMapper);
        
        group.setStatesList(states);
        
        return group;
    }
    
}