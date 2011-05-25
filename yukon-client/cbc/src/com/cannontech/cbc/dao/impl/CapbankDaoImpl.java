package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.CapBankOperationalState;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.cbc.dao.CapbankDao;
import com.cannontech.cbc.model.Capbank;
import com.cannontech.cbc.model.CapbankAdditional;
import com.cannontech.cbc.model.Feeder;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.util.Validator;

//TODO: This does not add to cap bank additional yet.
public class CapbankDaoImpl implements CapbankDao {
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    private static final String selectByControlDeviceIdSql;
    private static final String insertAdditionalSql;
    private static final String removeAdditionalSql;
    private static final String updateAdditionalSql;
    
    private static final ParameterizedRowMapper<Capbank> rowMapper;
    private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private PaoDao paoDao;
    private PaoDefinitionService paoDefinitionService;
    
    static {
        insertSql = "INSERT INTO CAPBANK (DEVICEID,OPERATIONALSTATE,ControllerType," + 
        "CONTROLDEVICEID,CONTROLPOINTID,BANKSIZE,TypeOfSwitch,SwitchManufacture,MapLocationID," + 
        "RecloseDelay,MaxDailyOps,MaxOpDisable) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        
        removeSql = "DELETE FROM CAPBANK WHERE DEVICEID = ?";
        
        updateSql = "UPDATE CAPBANK SET OPERATIONALSTATE = ?," + 
        "ControllerType = ?,CONTROLDEVICEID = ?,CONTROLPOINTID = ?, " + 
        "BANKSIZE = ?, TypeOfSwitch = ?, SwitchManufacture = ?,MapLocationID = ?, RecloseDelay = ?," + 
        " MaxDailyOps = ?, MaxOpDisable = ? WHERE DEVICEID = ?";
        
        selectAllSql = "SELECT yp.PAOName, DEVICEID, OPERATIONALSTATE, ControllerType, CONTROLDEVICEID,"
				+ "CONTROLPOINTID, BANKSIZE, TypeOfSwitch, SwitchManufacture, MapLocationID, RecloseDelay,"
				+ "MaxDailyOps,MaxOpDisable FROM CAPBANK, YukonPAObject yp ";
        
        selectByIdSql = selectAllSql + " WHERE DEVICEID = yp.PAObjectID AND DEVICEID = ?";
        
        selectByControlDeviceIdSql = selectAllSql + " WHERE CONTROLDEVICEID = ?";
        
        insertAdditionalSql = "INSERT INTO CAPBANKADDITIONAL (DeviceID,MaintenanceAreaID,PoleNumber," 
        	                + "DriveDirections,Latitude,Longitude,CapBankConfig,CommMedium,CommStrength," 
        	                + "ExtAntenna,AntennaType,LastMaintVisit,LastInspVisit,OpCountResetDate," 
        	                + "PotentialTransformer,MaintenanceReqPend,OtherComments,OpTeamComments,CBCBattInstallDate) " 
        	                + "VALUES (?,?,?,?,? ,?,?,?,?,? ,?,?,?,?,? ,?,?,?,?)";
        
        updateAdditionalSql = "UPDATE CAPBANKADDITIONAL SET MaintenanceAreaID=?,PoleNumber=?," 
            + "DriveDirections=?,Latitude=?,Longitude=?,CapBankConfig=?,CommMedium=?,CommStrength=?," 
            + "ExtAntenna=?,AntennaType=?,LastMaintVisit=?,LastInspVisit=?,OpCountResetDate=?," 
            + "PotentialTransformer=?,MaintenanceReqPend=?,OtherComments=?,OpTeamComments=?,CBCBattInstallDate=? " 
            + "WHERE DeviceID = ?";
        
        removeAdditionalSql = "DELETE FROM CAPBANKADDITIONAL WHERE DeviceID = ?";
        
        rowMapper = CapbankDaoImpl.createRowMapper();
        liteCapControlObjectRowMapper = CapbankControllerDaoImpl.createLiteCapControlObjectRowMapper();
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
    
    @Autowired
    public void setPaoDefinitionService(PaoDefinitionService paoDefinitionService) {
        this.paoDefinitionService = paoDefinitionService;
    }

    @Override
    public void add(Capbank bank) {
		DeviceBase device = DeviceFactory.createDevice(PAOGroups.CAPBANK);
		
		//Set what the factory didn't
		int newId = paoDao.getNextPaoId();
		
		device.setDeviceID(newId);
		device.setPAOCategory(PAOGroups.STRING_CAT_DEVICE);
		device.setPAOName(bank.getName());		
		device.setPAODescription(bank.getDescription());
		device.setDisabled(bank.getDisabled());

		SmartMultiDBPersistent smartDB = new SmartMultiDBPersistent();
		smartDB.addOwnerDBPersistent(device);
		List<PointBase> points = paoDefinitionService.createAllPointsForPao(new SimpleDevice(newId, PaoType.CAPBANK));
		smartDB.addAllDBPersistent(points);
		try { 
		    Transaction.createTransaction(com.cannontech.database.Transaction.INSERT, smartDB).execute();
		} catch (TransactionException e ) { 
		    throw new DataIntegrityViolationException("Insert of CapBank, " + bank.getName() + ", failed.", e);
		}
		
		//Added to YukonPAObject table, now add to CAPBANK
		bank.setId(newId);
		int rowsAffected = simpleJdbcTemplate.update(updateSql, 
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
	                                                bank.getMaxOpDisable(),
	                                                bank.getId());
		addCapbankAdditional(bank);
		
		boolean result = (rowsAffected == 1);
        
		if (result == false) {
			CTILogger.error("Update of bank information in CapBank table failed for bank with name: " + bank.getName());
		}
		
    }
	
    @Override
    public boolean remove(Capbank bank) {
    	
    	simpleJdbcTemplate.update(removeSql,bank.getId());
    	removeCapbankAdditional(bank);
    	
    	DeviceBase device = DeviceFactory.createDevice(PAOGroups.CAPBANK);
    	device.setDeviceID(bank.getId());
		
    	//TODO: delete points
		try {
			Transaction.createTransaction(com.cannontech.database.Transaction.DELETE, device).execute();
		} catch (TransactionException e) {
			CTILogger.error("Removal of CapBank Failed: " + bank.getName());
			return false;
		}
    	
		return true;
    }
    
    @Override
    public boolean update(Capbank bank) {
		DeviceBase device = DeviceFactory.createDevice(PAOGroups.CAPBANK);

		//Set what the factory didn't
		device.setPAOCategory(PAOGroups.STRING_CAT_DEVICE);
		device.setPAOName(bank.getName());
		device.setDeviceID(bank.getId());
		device.setPAODescription(bank.getDescription());
		
		try {
			Transaction.createTransaction(com.cannontech.database.Transaction.UPDATE, device).execute();
		} catch (TransactionException e) {
			CTILogger.error("Update of Capbank in YukonPAObject table failed for bank: " + bank.getName());
			return false;
		}

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
        updateCapbankAdditional(bank);
        
    	boolean result = (rowsAffected == 1);
        
		if (result == false) {
			CTILogger.debug("Update of bank in CapBank table failed for bank name: " + bank.getName());
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
    
	@SuppressWarnings("unchecked")
    @Override
	public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count) {
	    /* Get the unordered total count */
        int orphanCount = simpleJdbcTemplate.queryForInt("SELECT COUNT(*) FROM CapBank where DeviceId not in (SELECT DeviceId FROM CCFeederBankList)");
        
        /* Get the paged subset of cc objects */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Type").eq(PaoType.CAPBANK);
        sql.append("    AND PAObjectID not in (SELECT DeviceId FROM CCFeederBankList)");
        sql.append("ORDER BY PAOName");
        
        PagingResultSetExtractor orphanExtractor = new PagingResultSetExtractor(start, count, liteCapControlObjectRowMapper);
        simpleJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), orphanExtractor);
        
        List<LiteCapControlObject> unassignedBanks = (List<LiteCapControlObject>) orphanExtractor.getResultList();
        
        SearchResult<LiteCapControlObject> searchResult = new SearchResult<LiteCapControlObject>();
        searchResult.setResultList(unassignedBanks);
        searchResult.setBounds(start, count, orphanCount);
        
        return searchResult;
	}
    
	private void addCapbankAdditional(Capbank bank) {
    	CapbankAdditional capbankAdditional = bank.getCapbankAdditional();
    	
		simpleJdbcTemplate.update(insertAdditionalSql,
		    			bank.getId(),
		    			capbankAdditional.getMaintenanceAreaId(),
		    			capbankAdditional.getPoleNumber(),
		    			capbankAdditional.getDriveDirections(),
		    			capbankAdditional.getLatitude(),
		    			capbankAdditional.getLongitude(),
		    			capbankAdditional.getCapbankConfig(),
		    			capbankAdditional.getCommMedium(),
		    			capbankAdditional.getCommStrength(),
		    			capbankAdditional.getExtAntenna(),
		    			capbankAdditional.getAntennaType(),
		    			capbankAdditional.getLastMaintenanceVisit(),
		    			capbankAdditional.getLastInspection(),
		    			capbankAdditional.getOpCountResetDate(),
		    			capbankAdditional.getPotentialTransformer(),
		    			capbankAdditional.getMaintenanceRequired(),
		    			capbankAdditional.getOtherComments(),
		    			capbankAdditional.getOpTeamComments(),
		    			capbankAdditional.getCbcInstallDate());    	
	}
	
	private void updateCapbankAdditional(Capbank bank) {
    	CapbankAdditional capbankAdditional = bank.getCapbankAdditional();
    	
		simpleJdbcTemplate.update(updateAdditionalSql,
		    			capbankAdditional.getMaintenanceAreaId(),
		    			capbankAdditional.getPoleNumber(),
		    			capbankAdditional.getDriveDirections(),
		    			capbankAdditional.getLatitude(),
		    			capbankAdditional.getLongitude(),
		    			capbankAdditional.getCapbankConfig(),
		    			capbankAdditional.getCommMedium(),
		    			capbankAdditional.getCommStrength(),
		    			capbankAdditional.getExtAntenna(),
		    			capbankAdditional.getAntennaType(),
		    			capbankAdditional.getLastMaintenanceVisit(),
		    			capbankAdditional.getLastInspection(),
		    			capbankAdditional.getOpCountResetDate(),
		    			capbankAdditional.getPotentialTransformer(),
		    			capbankAdditional.getMaintenanceRequired(),
		    			capbankAdditional.getOtherComments(),
		    			capbankAdditional.getOpTeamComments(),
		    			capbankAdditional.getCbcInstallDate(),
		    			bank.getId());    			
	}
	
	private void removeCapbankAdditional(Capbank bank) {
		simpleJdbcTemplate.update(removeAdditionalSql,bank.getId());    
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
		String adjustTripOrders = "UPDATE CCFeederBankList SET TripOrder = TripOrder+1 WHERE FeederId = ?";
    	String insert = "INSERT INTO CCFeederBankList(FeederID,DeviceID,ControlOrder,CloseOrder,TripOrder) ";
		String assignmentsExist = " Select ?,?,MAX(ControlOrder)+1,MAX(CloseOrder)+1,1 " 
    		 + "From CCFeederBankList WHERE FeederId = ?";
		String firstAssignment = " VALUES (?,?,1,1,1)"; 
		
		//remove any existing assignment
    	unassignCapbank(capbankId);
    	
    	//Check if any assignments exist.
    	int rowsAffected = 0;
    	int count = simpleJdbcTemplate.queryForInt("SELECT count(FeederID) FROM CCFeederBankList WHERE FeederId=?", feederId);
    	
    	if (count > 0) {
	    	//Change trip orders to +1 so the new one can be 1
			simpleJdbcTemplate.update(adjustTripOrders,feederId);
			
			//Insert new assignment
			rowsAffected = simpleJdbcTemplate.update(insert + assignmentsExist,feederId,capbankId,feederId);
    	} else {
    		//This is the first assignment. Insert with defaults
    		rowsAffected = simpleJdbcTemplate.update(insert + firstAssignment,feederId,capbankId);
    	}
		boolean result = (rowsAffected == 1);
		return result;
	}

	@Override
	public boolean unassignCapbank(Capbank capbank) {
		return unassignCapbank(capbank.getId());
	}

	@Override
	public boolean unassignCapbank(int capbankId) {
    	String deleteAssignmentSql = "DELETE FROM CCFeederBankList WHERE DeviceId = ?";
    	
		int rowsAffected = simpleJdbcTemplate.update(deleteAssignmentSql,capbankId);
		
		boolean result = (rowsAffected == 1);
		return result;
	}
}
