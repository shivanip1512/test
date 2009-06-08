package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.CapBankOperationalState;
import com.cannontech.cbc.dao.CapbankDao;
import com.cannontech.cbc.model.Capbank;
import com.cannontech.cbc.model.Feeder;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.util.Validator;

//TODO: This does not add to cap bank additional yet.
public class CapbankDaoImpl implements CapbankDao {
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    private static final String selectByControlDeviceIdSql;
    
    private static final ParameterizedRowMapper<Capbank> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private PaoDao paoDao;
    
	private class ControlOrder {
		public int controlOrder;
		public int closeOrder;
		public int tripOrder;
	}
    
    static {
        insertSql = "INSERT INTO CAPBANK (DEVICEID,OPERATIONALSTATE,ControllerType," + 
        "CONTROLDEVICEID,CONTROLPOINTID,BANKSIZE,TypeOfSwitch,SwitchManufacture,MapLocationID," + 
        "RecloseDelay,MaxDailyOps,MaxOpDisable) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        
        removeSql = "DELETE FROM CAPBANK WHERE DEVICEID = ?";
        
        updateSql = "UPDATE CAPBANK SET OPERATIONALSTATE = ?," + 
        "ControllerType = ?,CONTROLDEVICEID = ?,CONTROLPOINTID = ?, " + 
        "BANKSIZE = ?, TypeOfSwitch = ?,TypeOfSwitch = ?, MapLocationID = ?, RecloseDelay = ?," + 
        " MaxDailyOps = ?, MaxOpDisable = ?, WHERE DEVICEID = ?";
        
        selectAllSql = "SELECT yp.PAOName, DEVICEID, OPERATIONALSTATE, ControllerType, CONTROLDEVICEID,"
				+ "CONTROLPOINTID, BANKSIZE, TypeOfSwitch, SwitchManufacture, MapLocationID, RecloseDelay,"
				+ "MaxDailyOps,MaxOpDisable FROM CAPBANK, YukonPAObject yp ";
        
        selectByIdSql = selectAllSql + " WHERE DEVICEID = yp.PAObjectID AND DEVICEID = ?";
        
        selectByControlDeviceIdSql = selectAllSql + " WHERE CONTROLDEVICEID = ?";
        
        rowMapper = CapbankDaoImpl.createRowMapper();
    }

    private static final ParameterizedRowMapper<Capbank> createRowMapper() {
        ParameterizedRowMapper<Capbank> rowMapper = new ParameterizedRowMapper<Capbank>() {
            public Capbank mapRow(ResultSet rs, int rowNum) throws SQLException {
                Capbank bank = new Capbank();
                bank.setId(rs.getInt("DEVICEID"));
                bank.setOperationalState(CapBankOperationalState.valueOf(rs.getString("OPERATIONALSTATE")));
                bank.setControllerType(rs.getString("ControllerType"));
                bank.setControlDeviceId(rs.getInt("CONTROLDEVICEID"));
                bank.setControlPointId(rs.getInt("CONTROLPOINTID"));
                bank.setBankSize(rs.getInt("BANKSIZE"));
                bank.setTypeOfSwitch(rs.getString("TypeOfSwitch"));
                bank.setSwitchManufacturer(rs.getString("SwitchManufacture"));
                bank.setMapLocationId(rs.getString("MapLocationID"));
                bank.setRecloseDelay(rs.getInt("RecloseDelay"));
                bank.setMaxDailyOps(rs.getInt("MaxDailyOps"));
                String data = rs.getString("MaxOpDisable");
                Validator.isNotNull(data);
                bank.setMaxOpDisable(data);

                return bank;
            }
        };
        return rowMapper;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Autowired
	public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(Capbank bank) {
		int rowsAffected = 0;
		YukonPAObject pao = new YukonPAObject();
		
		pao.setCategory(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoClass(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoName(bank.getName());
		pao.setType(CapControlTypes.STRING_CAPCONTROL_CAPBANK);
		pao.setDescription("(none)");
		
		boolean ret = paoDao.add(pao);
		
		if (ret == false) {
			CTILogger.debug("Insert of CapBank, " + bank.getName() + ", in YukonPAObject table failed.");
			return false;
		}
		
		//Added to YukonPAObject table, now add to CapControlFeeder
		bank.setId(pao.getPaObjectID());
		rowsAffected = simpleJdbcTemplate.update(insertSql, bank.getId(),
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
        
		if (result == false) {
			CTILogger.debug("Insert of CapBank, " + bank.getName() + ", in CapBank table failed.");
		}
		
        return result;
    }
	
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(Capbank bank) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql,bank.getId());
        boolean result = (rowsAffected == 1);
        
        if (result) {
    		YukonPAObject pao = new YukonPAObject();
    		pao.setPaObjectID(bank.getId());
    		
    		return paoDao.remove(pao);       	
        }
		
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(Capbank bank) {
		int rowsAffected = 0;
		YukonPAObject pao = new YukonPAObject();
		
		pao.setCategory(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoClass(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoName(bank.getName());
		pao.setType(CapControlTypes.STRING_CAPCONTROL_CAPBANK);
		pao.setDescription("(none)");
		
		List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(bank.getName(), false);
		
		if (paos.size() == 0) {
			CTILogger.debug("No results returned for Feeder, " + bank.getName() + ", cannot update.");
			return false;
		}
		if (paos.size() > 1) {
			CTILogger.debug("Multiple paos with Feeder name: " + bank.getName() + " cannot update.");
			return false;
		}
		
		//Added to YukonPAObject table, now add to CapControlFeeder
		LiteYukonPAObject litePao = paos.get(0);
		pao.setPaObjectID(litePao.getYukonID());
		
		boolean ret = paoDao.update(pao);
		
		if (ret == false) {
			CTILogger.debug("Update of CapBank, " + bank.getName() + ", in YukonPAObject table failed.");
			return false;
		}
		
		//Added to YukonPAObject table, now add to CapControlFeeder
		bank.setId(pao.getPaObjectID());
    	rowsAffected = simpleJdbcTemplate.update(updateSql,bank.getOperationalState().name(),
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
        
		if (result == false) {
			CTILogger.debug("Update of CapBank, " + bank.getName() + ", in CapBank table failed.");
		}
        
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
        String sql = "select DEVICEID from CAPBANK where CONTROLDEVICEID = ? ";
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
        
        String sql = "SELECT DEVICEID FROM CAPBANK where DEVICEID not in " + 
        "(select DEVICEID from CCFeederBankList) ORDER BY DEVICEID";
        
        ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
                Integer i = new Integer ( rs.getInt("DEVICEID") );
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
        String sql = "SELECT OPERATIONALSTATE FROM CAPBANK WHERE DEVICEID = ?";
        
        String result = simpleJdbcTemplate.queryForObject(sql, String.class, paoID);
    
        return result.compareTo("Switched") == 0;
    }

    public int getParentId(Capbank capbank) {
		String getParentSql = "SELECT FeederID FROM CCFeederSubAssignment WHERE DeviceID= ?";
		
		int id = simpleJdbcTemplate.queryForInt(getParentSql, capbank.getId());
		return id;
    }
    
	@Override
	public boolean assignCapbank(Feeder feeder, Capbank capbank) {
		return assignCapbank(feeder.getId(),capbank.getId());
	}

	@Override
	public boolean assignCapbank(int feederId, int capbankId) {
		ControlOrder controlOrder = generateControlOrder(feederId);
    	String insertAssignmentSql = "INSERT INTO CCFeederBankList " 
    		 + "(FeederID,DeviceID,ControlOrder,CloseOrder,TripOrder) VALUES (?,?,?,?,?)";
    	
		int rowsAffected = simpleJdbcTemplate.update(insertAssignmentSql,
				feederId, capbankId, controlOrder.controlOrder,
				controlOrder.closeOrder, controlOrder.tripOrder);
		
		boolean result = (rowsAffected == 1);
		return result;
	}

	@Override
	public boolean unassignCapbank(Feeder feeder, Capbank capbank) {
		return unassignCapbank(feeder.getId(),capbank.getId());
	}

	@Override
	public boolean unassignCapbank(int feederId, int capbankId) {
    	String deleteAssignmentSql = "DELETE FROM CCFeederBankList WHERE FeederID = ? AND DeviceId = ?";
    	
		int rowsAffected = simpleJdbcTemplate.update(deleteAssignmentSql,feederId,capbankId);
		
		boolean result = (rowsAffected == 1);
		return result;
	}
	
	private ControlOrder generateControlOrder(int feederId) {
		ControlOrder order = new ControlOrder();
		
		/*
		 * Figure out the order for a newly assigned bank here.
		 */
		
		order.controlOrder = 0;
		order.closeOrder = 0;
		order.tripOrder = 0;
		
		return order;
	}
}
