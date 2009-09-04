package com.cannontech.dr.controlarea.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.controlarea.model.ControlArea;
import com.cannontech.dr.controlarea.model.ControlAreaTrigger;

public class ControlAreaDaoImpl implements ControlAreaDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;

    private final static String singleControlAreaByIdQuery = 
        "SELECT paObjectId, paoName FROM yukonPAObject " 
        + "WHERE type = 'LM CONTROL AREA' AND paObjectId = ?";

    private final static String selectAllTriggersQuery =
        "SELECT deviceId, triggerNumber, triggerType FROM lmControlAreaTrigger "
        + "ORDER BY deviceId, triggerNumber ";
    private final static String selectTriggersByControlAreaIdQuery =
        "SELECT deviceId, triggerNumber, triggerType FROM lmControlAreaTrigger "
        + "WHERE deviceId = ? "
        + "ORDER BY triggerNumber ";
    
    private final static ParameterizedRowMapper<ControlArea> controlAreaRowMapper =
        new ParameterizedRowMapper<ControlArea>() {
        @Override
        public ControlArea mapRow(ResultSet rs, int rowNum) throws SQLException {
            PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                    PaoType.LM_CONTROL_AREA);
            ControlArea retVal = new ControlArea(paoId, rs.getString("paoName"));
            return retVal;
        }};

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
        ControlArea controlArea = simpleJdbcTemplate.queryForObject(singleControlAreaByIdQuery,
                                                             controlAreaRowMapper,
                                                             controlAreaId);
        //get control area triggers, to associate control area/triggers
        Map<Integer, List<ControlAreaTrigger>> triggerMap = getControlAreaTriggers(controlAreaId);        
        controlArea.setTriggers(triggerMap.get(controlAreaId));
        return controlArea;
    }

    private Map<Integer, List<ControlAreaTrigger>> getControlAreaTriggers(int controlAreaId) {
        TriggerRowCallbackHandler triggerRowCallbackHandler = new TriggerRowCallbackHandler();
        if (controlAreaId < 0) {
            simpleJdbcTemplate.getJdbcOperations()
                              .query(selectAllTriggersQuery, triggerRowCallbackHandler);
        } else {
            simpleJdbcTemplate.getJdbcOperations()
                              .query(selectTriggersByControlAreaIdQuery,
                                     new Object[] { controlAreaId },
                                     triggerRowCallbackHandler);
        }
        return triggerRowCallbackHandler.getTriggerMap();
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
