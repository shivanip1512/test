package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cbc.dao.FeederDao;
import com.cannontech.cbc.model.Feeder;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.util.Validator;

public class FeederDaoImpl implements FeederDao {

    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    
    private static final ParameterizedRowMapper<Feeder> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static {
            insertSql = "INSERT INTO CapControlFeeder (FeederID," + 
            "CurrentVarLoadPointID,CurrentWattLoadPointID,MapLocationID,CurrentVoltLoadPointID," + 
            "MultiMonitorControl,usephasedata,phaseb,phasec) VALUES (?,?,?,?,?,?,?,?,?)";
            
            removeSql = "DELETE FROM CapControlFeeder WHERE FeederID = ?";
            
            updateSql = "UPDATE CapControlFeeder SET CurrentVarLoadPointID = ?," + 
            "CurrentWattLoadPointID = ?,MapLocationID = ?,CurrentVoltLoadPointID = ?, " + 
            "MultiMonitorControl = ?, usephasedata = ?,phaseb = ?, phasec = ? WHERE FeederID = ?";
            
            selectAllSql = "SELECT FeederID,CurrentVarLoadPointID,CurrentWattLoadPointID," + 
            "MapLocationID,CurrentVoltLoadPointID,MultiMonitorControl,usephasedata," + 
            "phaseb,phasec FROM CapControlFeeder";
            
            selectByIdSql = selectAllSql + " WHERE FeederID = ?";
            
            rowMapper = FeederDaoImpl.createRowMapper();
        }

    private static final ParameterizedRowMapper<Feeder> createRowMapper() {
        ParameterizedRowMapper<Feeder> rowMapper = new ParameterizedRowMapper<Feeder>() {
            public Feeder mapRow(ResultSet rs, int rowNum) throws SQLException {
                Feeder feeder = new Feeder();
                feeder.setId(rs.getInt("FeederID"));
                feeder.setCurrentVarLoadPointId(rs.getInt("CurrentVarLoadPointID"));
                feeder.setCurrentWattLoadPointId(rs.getInt("CurrentWattLoadPointID"));
                feeder.setMapLocationId(rs.getString("MapLocationID"));
                feeder.setCurrentVoltLoadPointId(rs.getInt("CurrentVoltLoadPointID"));
                String data = rs.getString("MultiMonitorControl");
                Validator.isNotNull(data);
                feeder.setMultiMonitorControl(data.charAt(0));
                data = rs.getString("usephasedata");
                Validator.isNotNull(data);
                feeder.setUsePhaseData(data.charAt(0));
                feeder.setPhaseb(rs.getInt("phaseb"));
                feeder.setPhasec(rs.getInt("phasec"));
                return feeder;
            }
        };
        return rowMapper;
    }
    
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(Feeder feeder) {
        int rowsAffected = simpleJdbcTemplate.update(insertSql, feeder.getId(),
                                                         feeder.getCurrentVarLoadPointId(),
                                                         feeder.getCurrentWattLoadPointId(),
                                                         feeder.getMapLocationId(),
                                                         feeder.getCurrentVoltLoadPointId(),
                                                         feeder.getMultiMonitorControl(),
                                                         feeder.getUsePhaseData(),
                                                         feeder.getPhaseb(),
                                                         feeder.getPhasec());
        boolean result = (rowsAffected == 1);
        return result;
    }
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Feeder getById(int id) throws DataAccessException {
        try {
            List<Feeder> list = simpleJdbcTemplate.query(selectByIdSql, rowMapper, id);
            return list.get(0);
        } catch (DataAccessException e) {
            return null;
        }
    }
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(Feeder feeder) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql,feeder.getId() );
        boolean result = (rowsAffected == 1);
        return result;
    }
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(Feeder feeder) {
        int rowsAffected = simpleJdbcTemplate.update(updateSql, feeder.getCurrentVarLoadPointId(),
                                                     feeder.getCurrentWattLoadPointId(),
                                                     feeder.getMapLocationId(),
                                                     feeder.getCurrentVoltLoadPointId(),
                                                     feeder.getMultiMonitorControl(),
                                                     feeder.getUsePhaseData(),
                                                     feeder.getPhaseb(),
                                                     feeder.getPhasec(),
                                                     feeder.getId());
        boolean result = (rowsAffected == 1);
        return result;
    }

    /**
     * This method returns all the Feeder IDs that are not assgined
     *  to a SubBus.
     */
    public List<Integer> getUnassignedFeederIds() {
        String sql = "SELECT FeederID FROM  CapControlFeeder where " +
        " FeederID not in (select FeederID from CCFeederSubAssignment) ORDER BY FeederID";

        ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
                Integer i = new Integer ( rs.getInt("FeederID") );
                return i;
            }
        };
        
        List<Integer> listmap = simpleJdbcTemplate.query(sql, mapper);
        return listmap;
    }

    /**
     * This method returns the SubBus ID that owns the given feeder ID.
     * If no parent is found, CtiUtilities.NONE_ZERO_ID is returned.
     */
    public int getParentSubBusID( int feederID ) {
    
        String sql = "SELECT SubStationBusID FROM CCFeederSubAssignment where FeederID = ?"; 
        Integer i = CtiUtilities.NONE_ZERO_ID;
        try{
            i = simpleJdbcTemplate.queryForInt(sql,feederID);
        }catch( EmptyResultDataAccessException e ){
            CTILogger.warn("Orphaned Feeder",e);
        }
        return i;
    }

}
