package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.CapBankOperationalState;
import com.cannontech.cbc.dao.CapbankDao;
import com.cannontech.cbc.model.Capbank;
import com.cannontech.util.Validator;

public class CapbankDaoImpl implements CapbankDao {
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    private static final String selectByControlDeviceIdSql;
    
    private static final ParameterizedRowMapper<Capbank> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static {
            insertSql = "INSERT INTO capbank (DeviceID,OperationalState,ControllerType," + 
            "ControlDeviceID,ControlPointID,BankSize,TypeOfSwitch,SwitchManufacture,MapLocationID," + 
            "RecloseDelay,MaxDailyOps,MaxOpDisable) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            
            removeSql = "DELETE FROM capbank WHERE DeviceID = ?";
            
            updateSql = "UPDATE capbank SET OperationalState = ?," + 
            "ControllerType = ?,ControlDeviceID = ?,ControlPointID = ?, " + 
            "BankSize = ?, TypeOfSwitch = ?,TypeOfSwitch = ?, MapLocationID = ?, RecloseDelay = ?," + 
            " MaxDailyOps = ?, MaxOpDisable = ?, WHERE DeviceID = ?";
            
            selectAllSql = "SELECT DeviceID,OperationalState,ControllerType,ControlDeviceID," + 
            "ControlPointID,BankSize,TypeOfSwitch,SwitchManufacture,MapLocationID,RecloseDelay," + 
            "MaxDailyOps,MaxOpDisable FROM capbank";
            
            selectByIdSql = selectAllSql + " WHERE DeviceID = ?";
            
            selectByControlDeviceIdSql = selectAllSql + " WHERE CONTROLDEVICEID = ?";
            
            rowMapper = CapbankDaoImpl.createRowMapper();
        }

    private static final ParameterizedRowMapper<Capbank> createRowMapper() {
        ParameterizedRowMapper<Capbank> rowMapper = new ParameterizedRowMapper<Capbank>() {
            public Capbank mapRow(ResultSet rs, int rowNum) throws SQLException {
                Capbank bank = new Capbank();
                bank.setId(rs.getInt("DeviceID"));
                bank.setOperationalState(CapBankOperationalState.valueOf(rs.getString("OperationalState")));
                bank.setControllerType(rs.getString("ControllerType"));
                bank.setControlDeviceId(rs.getInt("ControlDeviceID"));
                bank.setControlPointId(rs.getInt("ControlPointID"));
                bank.setBankSize(rs.getInt("BankSize"));
                bank.setTypeOfSwitch(rs.getString("TypeOfSwitch"));
                bank.setSwitchManufacturer(rs.getString("SwitchManufacture"));
                bank.setMapLocationId(rs.getString("MapLocationID"));
                bank.setRecloseDelay(rs.getInt("RecloseDelay"));
                bank.setMaxDailyOps(rs.getInt("MaxDailyOps"));
                String data = rs.getString("MaxOpDisable");
                Validator.isNotNull(data);
                bank.setMaxOpDisable(data.charAt(0));

                return bank;
            }
        };
        return rowMapper;
    }
    
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(Capbank bank) {
        int rowsAffected = simpleJdbcTemplate.update(insertSql, bank.getId(),
                                                                bank.getOperationalState().name(),
                                                                bank.getControllerType(),
                                                                bank.getControlDeviceId(),
                                                                bank.getControlPointId(),
                                                                bank.getBankSize(),
                                                                bank.getTypeOfSwitch(),
                                                                bank.getSwitchManufacturer(),
                                                                bank.getMapLocationId(),
                                                                bank.getRecloseDelay(),
                                                                bank.getMaxDailyOps(),
                                                                bank.getMaxOpDisable());
        boolean result = (rowsAffected == 1);
        return result;
    }
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(Capbank bank) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql,bank.getId() );
        boolean result = (rowsAffected == 1);
        return result;
    }
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(Capbank bank) {
        int rowsAffected = simpleJdbcTemplate.update(updateSql,bank.getOperationalState().name(),
                                                     bank.getControllerType(),
                                                     bank.getControlDeviceId(),
                                                     bank.getControlPointId(),
                                                     bank.getBankSize(),
                                                     bank.getTypeOfSwitch(),
                                                     bank.getSwitchManufacturer(),
                                                     bank.getMapLocationId(),
                                                     bank.getRecloseDelay(),
                                                     bank.getMaxDailyOps(),
                                                     bank.getMaxOpDisable(),
                                                     bank.getId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Capbank getById(int id) {
        Capbank c = simpleJdbcTemplate.queryForObject(selectByIdSql, rowMapper, id);
        return c;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Capbank getByControlDeviceId(final int controlDeviceId) throws DataRetrievalFailureException {
        Capbank capBank = simpleJdbcTemplate.queryForObject(selectByControlDeviceIdSql, rowMapper, controlDeviceId);
        return capBank;
    }
    
    public int getCapBankIdByCBC(Integer paoId) {
        String sql = "select deviceId from capbank where controlDeviceId = ? ";
        Integer capBankId = -1;
        try {
            capBankId = simpleJdbcTemplate.queryForInt(sql, paoId);
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
        return capBankId;
    }
    
    /**
     * This method returns all the CapBank IDs that are not assigned
     *  to a Feeder.
     */
    public List<Integer> getUnassignedCapBankIds(){
        
        String sql = "SELECT DeviceID FROM CapBank where deviceid not in " + 
        "(select deviceid from CCFeederBankList) ORDER BY deviceid";
        
        ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
                Integer i = new Integer ( rs.getInt("DeviceID") );
                return i;
            }
        };
        
        List<Integer> listmap = simpleJdbcTemplate.query(sql, mapper);
        return listmap;
    }
    /**
     * This method returns the Feeder ID that owns the given cap bank ID.
     */
    public int getParentFeederId( int capBankID )  throws EmptyResultDataAccessException
    {
        String sql = "SELECT FeederID FROM CCFeederBankList where DeviceID = ?";
        return simpleJdbcTemplate.queryForInt(sql,capBankID);
    }   
    
    public boolean isSwitchedBank( Integer paoID ){
        //TODO untested
        String sql = "SELECT operationalstate FROM capbank WHERE deviceid = ?";
        
        String result = simpleJdbcTemplate.queryForObject(sql, String.class, paoID);
    
        return result.compareTo("Switched") == 0;
    }

}
