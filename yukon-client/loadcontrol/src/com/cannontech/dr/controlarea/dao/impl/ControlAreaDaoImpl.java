package com.cannontech.dr.controlarea.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.MappingRowCallbackHandler;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.controlarea.model.ControlArea;
import com.cannontech.dr.controlarea.model.ControlAreaTrigger;

public class ControlAreaDaoImpl implements ControlAreaDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;

    private final static String baseControlAreaQuery =
        "SELECT paObjectId, paoName FROM yukonPAObject "
            + "WHERE type = 'LM CONTROL AREA'";
    private final static String singleControlAreaByIdQuery = 
        "SELECT paObjectId, paoName FROM yukonPAObject " 
        + "WHERE type = 'LM CONTROL AREA' AND paObjectId = ?";
    private final static String singleControlAreaByProgramIdQuery = 
        "SELECT paObjectId, paoName FROM yukonPAObject " 
        + "WHERE type = 'LM CONTROL AREA' AND paObjectId IN (SELECT deviceId " 
        + "FROM lmControlAreaProgram WHERE lmProgramDeviceId = ?)";

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

    private final static ParameterizedRowMapper<ControlAreaTrigger> controlAreaTriggerRowMapper = 
            new ParameterizedRowMapper<ControlAreaTrigger>() {
            @Override
            public ControlAreaTrigger mapRow(ResultSet rs, int rowNum) throws SQLException {
                ControlAreaTrigger retVal = new ControlAreaTrigger(rs.getInt("deviceId"),
                                                                   rs.getInt("triggerNumber"),
                                                                   rs.getString("triggerType"));
                return retVal;
            }
        };  

    @Override
    public List<ControlArea> getControlAreas() {
        //get all control area triggers, and init callback to associate control areas/triggers
        Map<Integer, List<ControlAreaTrigger>> triggerMap = getControlAreaTriggers(-1);
        ControlAreaCallback controlAreaCallback = new ControlAreaCallback(triggerMap);
        simpleJdbcTemplate.getJdbcOperations()
                          .query(baseControlAreaQuery,
                                 new MappingRowCallbackHandler<ControlArea>(controlAreaRowMapper,
                                                                            controlAreaCallback));
        return controlAreaCallback.getControlAreaList();
    }

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

    @Override
    public ControlArea findControlAreaForProgram(int programId) {
        try {
            ControlArea controlArea = simpleJdbcTemplate.queryForObject(singleControlAreaByProgramIdQuery,
                                                                        controlAreaRowMapper,
                                                                        programId);
            // get control area triggers, to associate control area/triggers
            int controlAreaId = controlArea.getPaoIdentifier().getPaoId();
            Map<Integer, List<ControlAreaTrigger>> triggerMap = getControlAreaTriggers(controlAreaId);
            controlArea.setTriggers(triggerMap.get(controlAreaId));
            return controlArea;
        } catch (EmptyResultDataAccessException e) {
            // in case the program doesn't have a parent control area yet
            return null;
        }
    }

    private static class ControlAreaCallback implements SimpleCallback<ControlArea> {
        private Map<Integer, List<ControlAreaTrigger>> triggerMap;
        private List<ControlArea> controlAreaList = new ArrayList<ControlArea>();
        
        private ControlAreaCallback(Map<Integer, List<ControlAreaTrigger>> triggerMap) {
            this.triggerMap = triggerMap;
        }
        
        @Override
        public void handle(ControlArea controlArea) throws Exception {
            List<ControlAreaTrigger> triggerList = triggerMap.get(controlArea.getPaoIdentifier().getPaoId());
            if (triggerList == null) {
                triggerList = Collections.emptyList();
            }
            controlArea.setTriggers(triggerList);
            controlAreaList.add(controlArea);
        }

        public List<ControlArea> getControlAreaList() {
            return controlAreaList;
        }
    }
    
    private Map<Integer, List<ControlAreaTrigger>> getControlAreaTriggers(int controlAreaId) {
        ControlAreaTriggerCallback triggerCallback = new ControlAreaTriggerCallback();
        if (controlAreaId < 0) {
            simpleJdbcTemplate.getJdbcOperations()
                              .query(selectAllTriggersQuery,
                                     new MappingRowCallbackHandler<ControlAreaTrigger>(controlAreaTriggerRowMapper,
                                                                                       triggerCallback));
        } else {
            simpleJdbcTemplate.getJdbcOperations()
                              .query(selectTriggersByControlAreaIdQuery,
                                     new Object[] { controlAreaId },
                                     new MappingRowCallbackHandler<ControlAreaTrigger>(controlAreaTriggerRowMapper,
                                                                                       triggerCallback));
        }
        return triggerCallback.getTriggerMap();
    }

    private static class ControlAreaTriggerCallback implements SimpleCallback<ControlAreaTrigger> {
        private Map<Integer, List<ControlAreaTrigger>> triggerMap = new HashMap<Integer, List<ControlAreaTrigger>>();

        @Override
        public void handle(ControlAreaTrigger trigger) throws Exception {
            List<ControlAreaTrigger> triggerList = triggerMap.get(trigger.getControlAreaId());
            if (triggerList == null) {
                triggerList = new ArrayList<ControlAreaTrigger>();
                triggerMap.put(trigger.getControlAreaId(), triggerList);
            }
            triggerList.add(trigger);
        }

        public Map<Integer, List<ControlAreaTrigger>> getTriggerMap() {
            return triggerMap;
        }
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
