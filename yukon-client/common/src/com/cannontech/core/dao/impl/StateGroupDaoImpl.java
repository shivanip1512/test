package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.Lists;

public class StateGroupDaoImpl implements StateGroupDao {
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private PointDao pointDao;
    
    private static final Logger log = YukonLogManager.getLogger(StateGroupDaoImpl.class);
    private final Map<Integer, LiteStateGroup> stateGroupCache = new ConcurrentHashMap<>();
    
    private void createDatabaseChangeListener() {
        asyncDynamicDataSource.addDBChangeListener(new DBChangeListener() {
            
            @Override
            public void dbChangeReceived(DBChangeMsg dbChange) {
                if (dbChange.getDatabase() == DBChangeMsg.CHANGE_STATE_GROUP_DB) {
                    stateGroupCache.remove(dbChange.getId());
                    
                    if (dbChange.getDbChangeType() != DbChangeType.DELETE) {
                        LiteStateGroup liteStateGroup = getStateGroup(dbChange.getId());
                        stateGroupCache.put(liteStateGroup.getLiteID(), liteStateGroup);
                    }
                }
            }
        });
    }
    
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
        List<LiteStateGroup> liteStateGroups = Lists.newArrayList(stateGroupCache.values());
        Collections.sort(liteStateGroups, LiteComparators.liteStringComparator);
        return liteStateGroups;
    }
    
    @Override
    public LiteStateGroup getStateGroup(int stateGroupId) {
        
        LiteStateGroup liteStateGroup = stateGroupCache.get(stateGroupId);
        if (liteStateGroup != null) {
            return liteStateGroup;
        } else {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT StateGroupId, Name, GroupType");
            sql.append("FROM StateGroup");
            sql.append("WHERE StateGroupId").eq(stateGroupId);
            
            liteStateGroup = jdbcTemplate.queryForObject(sql, groupMapper);
            
            sql = new SqlStatementBuilder();
            sql.append("SELECT StateGroupId, RawState, Text, ForegroundColor, BackgroundColor, ImageId");
            sql.append("FROM State");
            sql.append("WHERE StateGroupId").eq(liteStateGroup.getStateGroupID());
            
            List<LiteState> states = jdbcTemplate.query(sql, stateMapper);
            liteStateGroup.setStatesList(states);
            stateGroupCache.put(liteStateGroup.getLiteID(), liteStateGroup);
        }
        return liteStateGroup;
    }
    
    @Override
    public List<LiteState> getLiteStates(int stateGroupId) {
        LiteStateGroup liteStateGroup = getStateGroup(stateGroupId);
        return liteStateGroup.getStatesList();
    }

    @Override
    public LiteStateGroup getStateGroup(String stateGroupName) {
        for(LiteStateGroup group : getAllStateGroups()){
            if(stateGroupName.equals(group.getStateGroupName())){
                return group;
            }
        }
        throw new NotFoundException("State group '" + stateGroupName + "' doesn't exist");
    }
    
    @Override
    public LiteState findLiteState(int stateGroupId, int rawState) {
        
        LiteStateGroup stateGroup = getStateGroup(stateGroupId);
        List<LiteState> stateList = stateGroup.getStatesList();
        for (final LiteState state : stateList) {
            if (rawState == state.getStateRawState()) return state;
        }
        
        //this is a internal error
        log.error("Unable to find the state for StateGroupID = " + stateGroupId + " and rawState = " + rawState );
        return null;
    }
 
    @PostConstruct
    public void init() throws Exception {
        createDatabaseChangeListener();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT StateGroupId, Name, GroupType");
        sql.append("FROM StateGroup");
        
        List<LiteStateGroup> liteStateGroups = jdbcTemplate.query(sql, groupMapper);
        
        for (LiteStateGroup liteStateGroup : liteStateGroups) {
            sql = new SqlStatementBuilder();
            sql.append("SELECT StateGroupId, RawState, Text, ForegroundColor, BackgroundColor, ImageId");
            sql.append("FROM State");
            sql.append("WHERE StateGroupId").eq(liteStateGroup.getStateGroupID());
        
            List<LiteState> states = jdbcTemplate.query(sql, stateMapper);
            liteStateGroup.setStatesList(states);
            
            stateGroupCache.put(liteStateGroup.getStateGroupID(),  liteStateGroup);
        }
 
        log.info("Initialized state group cache.");
    }
    
   @Override
    public List<LiteState> getStateList(Integer pointId) {

        // Getting state list corresponding to pointId its stateGroupId
        LitePoint litePoint = pointDao.getLitePoint(pointId);
        if (litePoint == null) {
            throw new NotFoundException("Invalid point Id" + pointId);
        }
        LiteStateGroup stateGroup = getStateGroup(litePoint.getStateGroupID());
        return stateGroup.getStatesList();
    }

    @Override
    public String getRawStateName(Integer pointId, Integer rawState) {
        List<LiteState> stateList = getStateList(pointId);
        return stateList.stream()
                        .filter(state -> state.getStateRawState() == rawState)
                        .findFirst()
                        .get()
                        .getStateText();
    }
}