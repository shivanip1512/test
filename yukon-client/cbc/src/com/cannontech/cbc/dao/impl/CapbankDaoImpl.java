package com.cannontech.cbc.dao.impl;

import java.sql.Connection;
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
import com.cannontech.cbc.model.CapbankAdditional;
import com.cannontech.cbc.model.Feeder;
import com.cannontech.cbc.model.LiteCapControlObject;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
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
    private static final String insertAdditionalSql;
    private static final String removeAdditionalSql;
    private static final String updateAdditionalSql;
    
    private static final ParameterizedRowMapper<Capbank> rowMapper;
    private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;
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

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(Capbank bank) {
		Connection connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
		
		DeviceBase device = DeviceFactory.createDevice(PAOGroups.CAPBANK);
		device.setDbConnection(connection);
		
		//Set what the factory didn't
		int newId = paoDao.getNextPaoId();
		
		device.setDeviceID(newId);
		device.setPAOCategory(PAOGroups.STRING_CAT_DEVICE);
		device.setPAOName(bank.getName());		
		device.setPAODescription(bank.getDescription());
		
		SmartMultiDBPersistent smartDB = new SmartMultiDBPersistent();
		smartDB.addOwnerDBPersistent(device);
		
		//create the Status Point for this CapBank
		PointBase statusPoint = PointFactory.createBankStatusPt(newId);
		statusPoint.setDbConnection(connection);
		smartDB.addDBPersistent(statusPoint);
		
		//create the Analog Point for this CapBank used to track Op Counts
		PointBase analogPoint = PointFactory.createBankOpCntPoint(newId);
		analogPoint.setDbConnection(connection);
		smartDB.addDBPersistent(analogPoint);
		
		try {
			smartDB.add();
		} catch(SQLException e) {
			CTILogger.error("Insert of CapBank, " + bank.getName() + ", failed.");
			return false;
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
		
        return result;
    }
	
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(Capbank bank) {
    	
    	simpleJdbcTemplate.update(removeSql,bank.getId());
    	removeCapbankAdditional(bank);
    	
    	DeviceBase device = DeviceFactory.createDevice(PAOGroups.CAPBANK);
    	device.setDeviceID(bank.getId());
		
    	//delete points
    	
    	try {
    		device.delete();
    	} catch (SQLException e) {
    		CTILogger.error("Removal of CapBank Failed: " + bank.getName());
    		return false;
    	}
    	
		return true;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(Capbank bank) {
    	Connection connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
		
		DeviceBase device = DeviceFactory.createDevice(PAOGroups.CAPBANK);
		device.setDbConnection(connection);

		//Set what the factory didn't
		device.setPAOCategory(PAOGroups.STRING_CAT_DEVICE);
		device.setPAOName(bank.getName());
		device.setDeviceID(bank.getId());
		device.setPAODescription(bank.getDescription());
		
		try {
			device.update();
		} catch (SQLException e) {
			CTILogger.debug("Update of Capbank in YukonPAObject table failed for bank: " + bank.getName());
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
    
	@Override
	public List<LiteCapControlObject> getOrphans() {
        List<Integer> ids = getUnassignedCapBankIds();
		
		ChunkingSqlTemplate<Integer> template = new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);
		final List<LiteCapControlObject> unassignedObjects = template.query(new SqlGenerator<Integer>() {
            @Override
            public String generate(List<Integer> subList) {
                SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
                sqlBuilder.append("SELECT PAObjectID,PAOName,TYPE,Description FROM YukonPAObject WHERE PAObjectID IN (");
                sqlBuilder.append(subList);
                sqlBuilder.append(")");
                String sql = sqlBuilder.toString();
                return sql;
            }
        }, ids, liteCapControlObjectRowMapper);
		
		return unassignedObjects;
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
		ControlOrder controlOrder = generateControlOrder(feederId);
    	String insertAssignmentSql = "INSERT INTO CCFeederBankList " 
    		 + "(FeederID,DeviceID,ControlOrder,CloseOrder,TripOrder) VALUES (?,?,?,?,?)";
    	
		//remove any existing assignment
    	unassignCapbank(capbankId);
    	
		int rowsAffected = simpleJdbcTemplate.update(insertAssignmentSql,
				feederId, capbankId, controlOrder.controlOrder,
				controlOrder.closeOrder, controlOrder.tripOrder);
		
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
	
	private ControlOrder generateControlOrder(int feederId) {
		ControlOrder order = new ControlOrder();
		
		order.controlOrder = 0;
		order.closeOrder = 0;
		order.tripOrder = 0;
		
		return order;
	}
}
