package com.cannontech.dr.controlarea.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.controlarea.model.ControlArea;
import com.cannontech.dr.controlarea.model.ControlAreaTrigger;
import com.google.common.collect.Sets;

public class ControlAreaDaoImpl implements ControlAreaDao {
    private YukonJdbcOperations yukonJdbcOperations;

    private static class ControlAreaRowMapper implements ParameterizedRowMapper<ControlArea>  {
        private Map<Integer, List<ControlAreaTrigger>> triggerMap;        
        ControlAreaRowMapper(Map<Integer, List<ControlAreaTrigger>> triggerMap){
            this.triggerMap = triggerMap;
        }
        
        @Override
        public ControlArea mapRow(ResultSet rs, int rowNum) throws SQLException {
            int controlAreaId = rs.getInt("paObjectId");
            PaoIdentifier paoId = new PaoIdentifier(controlAreaId,
                                                    PaoType.LM_CONTROL_AREA);
            ControlArea retVal = new ControlArea(paoId, rs.getString("paoName"));
            retVal.setTriggers(triggerMap.get(controlAreaId));
            
            return retVal;
        }
    }

    private static class TriggerRowCallbackHandler implements RowCallbackHandler {
        private Map<Integer, List<ControlAreaTrigger>> triggerMap =
            new HashMap<Integer, List<ControlAreaTrigger>>();

        public Map<Integer, List<ControlAreaTrigger>> getTriggerMap() {
            return triggerMap;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            ControlAreaTrigger trigger = new ControlAreaTrigger(rs.getInt("deviceId"),
                                                                rs.getInt("triggerNumber"),
                                                                rs.getString("triggerType"));
            List<ControlAreaTrigger> triggerList = triggerMap.get(trigger.getControlAreaId());
            if (triggerList == null) {
                triggerList = new ArrayList<ControlAreaTrigger>();
                triggerMap.put(trigger.getControlAreaId(), triggerList);
            }
            triggerList.add(trigger);
        }
    };

    @Override
    public ControlArea getControlArea(int controlAreaId) {
        // Retrieve the triggers first
        Map<Integer, List<ControlAreaTrigger>> triggerMap = getControlAreaTriggers(controlAreaId);        
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT paObjectId, paoName FROM yukonPAObject");
        sql.append("WHERE type = 'LM CONTROL AREA' AND paObjectId =").appendArgument(controlAreaId);
        ControlArea controlArea = yukonJdbcOperations.queryForObject(sql, new ControlAreaRowMapper(triggerMap));

        return controlArea;
    }
    
    @Override
    public Set<Integer> getProgramIdsForControlArea(int controlAreaId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT paObjectId");
        sql.append("FROM yukonPAObject");
        sql.append("WHERE");
        sql.append("paObjectId IN ");
        sql.append("    (SELECT lmProgramDeviceId FROM lmControlAreaProgram ");
        sql.append("        WHERE deviceId").eq(controlAreaId);
        sql.append("     )");
        
        List<Integer> programIdList = yukonJdbcOperations.query(sql, new IntegerRowMapper());
        return Sets.newHashSet(programIdList);
    }

    private Map<Integer, List<ControlAreaTrigger>> getControlAreaTriggers(int controlAreaId) {
        TriggerRowCallbackHandler triggerRowCallbackHandler = new TriggerRowCallbackHandler();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        if (controlAreaId < 0) {
            sql.append("SELECT deviceId, triggerNumber, triggerType FROM lmControlAreaTrigger");
            sql.append("ORDER BY deviceId, triggerNumber");
            yukonJdbcOperations.query(sql, triggerRowCallbackHandler);
        } else {
            sql.append("SELECT deviceId, triggerNumber, triggerType FROM lmControlAreaTrigger");
            sql.append("WHERE deviceId =").appendArgument(controlAreaId);
            sql.append("ORDER BY triggerNumber");
            yukonJdbcOperations.query(sql, triggerRowCallbackHandler);
        }
        return triggerRowCallbackHandler.getTriggerMap();
    }

    @Autowired
    public void setYukonJdbcOperations(YukonJdbcOperations yukonJdbcOperations) {
        this.yukonJdbcOperations = yukonJdbcOperations;
    }
}
