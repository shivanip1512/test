package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cbc.dao.SubstationBusDao;
import com.cannontech.cbc.model.SubstationBus;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.util.Validator;

public class SubstationBusDaoImpl implements SubstationBusDao {
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    
    private static final ParameterizedRowMapper<SubstationBus> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static {
            insertSql = "INSERT INTO CapControlSubstationBus (SubstationBusID,CurrentVarLoadPointID," +
            " CurrentWattLoadPointID,MapLocationID,CurrentVoltLoadPointID,AltSubID,SwitchPointID," + 
            "DualBusEnabled,MultiMonitorControl,usephasedata,phaseb,phasec) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            
            removeSql = "DELETE FROM CapControlSubstationBus WHERE SubstationBusID = ?";
            
            updateSql = "UPDATE CapControlSubstationBus SET CurrentVarLoadPointID = ?," + 
            "CurrentWattLoadPointID = ?,MapLocationID = ?,CurrentVoltLoadPointID = ?,AltSubID = ?, " + 
            "SwitchPointID = ?,DualBusEnabled = ?,MultiMonitorControl = ?,usephasedata = ?,phaseb = ?, " + 
            "phasec = ? WHERE SubstationBusID = ?";
            
            selectAllSql = "SELECT SubstationBusID,CurrentVarLoadPointID,CurrentWattLoadPointID," + 
            "MapLocationID,CurrentVoltLoadPointID,AltSubID,SwitchPointID,DualBusEnabled," + 
            "MultiMonitorControl,usephasedata,phaseb,phasec FROM CapControlSubstationBus";
            
            selectByIdSql = selectAllSql + " WHERE SubstationBusID = ?";
                        
            rowMapper = SubstationBusDaoImpl.createRowMapper();
        }
    private static ParameterizedRowMapper<Integer> SubstationIdMapper = new ParameterizedRowMapper<Integer>() {
        public Integer mapRow(ResultSet rs, int num) throws SQLException{
            Integer i = new Integer ( rs.getInt("substationBusID") );
            return i;
        }
    };
    
    private static final ParameterizedRowMapper<SubstationBus> createRowMapper() {
        ParameterizedRowMapper<SubstationBus> rowMapper = new ParameterizedRowMapper<SubstationBus>() {
            public SubstationBus mapRow(ResultSet rs, int rowNum) throws SQLException {
                SubstationBus bus = new SubstationBus();
                bus.setId(rs.getInt("SubstationBusID"));
                bus.setCurrentVarLoadPointId(rs.getInt("CurrentVarLoadPointID"));
                bus.setCurrentWattLoadPointId(rs.getInt("CurrentWattLoadPointID"));
                bus.setMapLocationId(rs.getString("MapLocationID"));
                bus.setCurrentVoltLoadPointId(rs.getInt("CurrentVoltLoadPointID"));
                bus.setAltSubId(rs.getInt("AltSubID"));
                bus.setSwitchPointId(rs.getInt("SwitchPointID"));
                String data = rs.getString("DualBusEnabled");
                Validator.isNotNull(data);
                bus.setDualBusEnabled(data.charAt(0));
                data = rs.getString("MultiMonitorControl");
                Validator.isNotNull(data);
                bus.setMultiMonitorControl(data.charAt(0));
                data = rs.getString("usephasedata");
                Validator.isNotNull(data);
                bus.setUsephasedata(data.charAt(0));
                bus.setPhaseb(rs.getInt("phaseb"));
                bus.setPhasec(rs.getInt("phasec"));
                return bus;
            }
        };
        return rowMapper;
    }
    
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(SubstationBus bus) {
        int rowsAffected = simpleJdbcTemplate.update(insertSql, bus.getId(),
                                                     bus.getCurrentVarLoadPointId(),
                                                     bus.getCurrentWattLoadPointId(),
                                                     bus.getMapLocationId(),
                                                     bus.getCurrentVoltLoadPointId(),
                                                     bus.getAltSubId(),
                                                     bus.getSwitchPointId(),
                                                     bus.getDualBusEnabled(),
                                                     bus.getMultiMonitorControl(),
                                                     bus.getUsephasedata(),
                                                     bus.getPhaseb(),
                                                     bus.getPhasec());
        boolean result = (rowsAffected == 1);
        return result;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(SubstationBus bus) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql,bus.getId() );
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(SubstationBus bus) {
        int rowsAffected = simpleJdbcTemplate.update(updateSql, bus.getCurrentVarLoadPointId(),
                                                     bus.getCurrentWattLoadPointId(),
                                                     bus.getMapLocationId(),
                                                     bus.getCurrentVoltLoadPointId(),
                                                     bus.getAltSubId(),
                                                     bus.getSwitchPointId(),
                                                     bus.getDualBusEnabled(),
                                                     bus.getMultiMonitorControl(),
                                                     bus.getUsephasedata(),
                                                     bus.getPhaseb(),
                                                     bus.getPhasec(),
                                                     bus.getId());
        boolean result = (rowsAffected == 1);
        return result;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public SubstationBus getById(int id){
        SubstationBus s = simpleJdbcTemplate.queryForObject(selectByIdSql, rowMapper, id);
        return s;
    }
    
    public List<Integer> getAllUnassignedBuses () {
    
        ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
                Integer i = new Integer ( rs.getInt("substationBusID") );
                return i;
            }
        };
        
        String orphanedSubs = "SELECT substationBusID FROM CapControlSubstationBus WHERE ";
        orphanedSubs += "substationBusID NOT IN (SELECT substationBusId FROM CCSubstationSubBusList) ";
        orphanedSubs += "ORDER BY substationBusID";
        List<Integer> listmap = simpleJdbcTemplate.query(orphanedSubs, SubstationIdMapper);
        
        return listmap;
        
    }

}
