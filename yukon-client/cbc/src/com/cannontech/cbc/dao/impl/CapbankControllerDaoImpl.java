package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.cbc.dao.CapbankControllerDao;
import com.cannontech.cbc.model.Capbank;
import com.cannontech.cbc.model.CapbankController;
import com.cannontech.cbc.model.LiteCapControlObject;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController6510;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.CapBankControllerDNP;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceScanRate;


public class CapbankControllerDaoImpl implements CapbankControllerDao {

    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
	
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private static final ParameterizedRowMapper<CapbankController> rowMapper;
	private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;
	
	private PaoDao paoDao;
	private PointDao pointDao;
	private PaoDefinitionService paoDefinitionService;
	
    static {
        insertSql = "INSERT INTO DeviceCBC (DEVICEID,SERIALNUMBER,ROUTEID) VALUES (?,?,?)";
        
        removeSql = "DELETE FROM DeviceCBC WHERE DEVICEID = ?";
        
        updateSql = "UPDATE DeviceCBC SET SERIALNUMBER=?, ROUTEID=? WHERE DEVICEID = ?";
        
        selectAllSql = "SELECT DEVICEID,SERIALNUMBER,ROUTEID FROM DeviceCBC";
        
        rowMapper = CapbankControllerDaoImpl.createRowMapper();
        liteCapControlObjectRowMapper = CapbankControllerDaoImpl.createLiteCapControlObjectRowMapper();
    }
    
    private static final ParameterizedRowMapper<CapbankController> createRowMapper() {
        ParameterizedRowMapper<CapbankController> rowMapper = new ParameterizedRowMapper<CapbankController>() {
            public CapbankController mapRow(ResultSet rs, int rowNum) throws SQLException {
            	CapbankController bankController = new CapbankController();
            	bankController.setId(rs.getInt("DEVICEID"));
            	bankController.setSerialNumber(rs.getInt("SERIALNUMBER"));
            	bankController.setRouteId(rs.getInt("ROUTEID"));

                return bankController;
            }
        };
        return rowMapper;
    }
	
    public static final ParameterizedRowMapper<LiteCapControlObject> createLiteCapControlObjectRowMapper() {
        ParameterizedRowMapper<LiteCapControlObject> rowMapper = new ParameterizedRowMapper<LiteCapControlObject>() {
            public LiteCapControlObject mapRow(ResultSet rs, int rowNum) throws SQLException {
            	
            	LiteCapControlObject lco = new LiteCapControlObject();
            	lco.setId(rs.getInt("PAObjectID"));
            	lco.setType(rs.getString("TYPE"));
            	lco.setDescription(rs.getString("Description"));
            	lco.setName(rs.getString("PAOName"));
            	//This is used for orphans. We will need to adjust the SQL if we intend to use this
            	//for anything other than orphaned cbcs.
            	lco.setParentId(0);
                return lco;
            }
        };
        return rowMapper;
    }
    
    @Override
    public void add(CapbankController capbankController) {
    	add(capbankController,true);
    }
    
	@Override
	public void add(CapbankController capbankController, boolean addPoints) {
		
		int newId = paoDao.getNextPaoId();
		capbankController.setId(newId);
		PaoType type = capbankController.getType();
		
		DeviceBase controller = DeviceFactory.createDevice(type.getDeviceTypeId());
		controller.setPAOName(capbankController.getName());
		controller.setDeviceID(newId);
		controller.setPAOCategory(PAOGroups.STRING_CAT_DEVICE);
		setTypeSpecificCbcFields(type,controller,capbankController);
		try {
		    Transaction.createTransaction(com.cannontech.database.Transaction.INSERT, controller).execute();
		} catch (TransactionException e ) {
		    throw new DataIntegrityViolationException("Insert of CapBankController, " + capbankController.getName() + ", failed.", e);
		}
		
		capbankController.setId(controller.getPAObjectID());
		
		int rowsAffected = simpleJdbcTemplate.update(updateSql, 
				capbankController.getSerialNumber(),
				capbankController.getRouteId(),
				capbankController.getId());
		boolean result = (rowsAffected == 1);
		
		if (result == false) {
			CTILogger.error("Update of controller information in DeviceCBC table failed for cbc with name: " + capbankController.getName());
		}
		if (addPoints) {
			List<PointBase> points = paoDefinitionService.createAllPointsForPao(new SimpleDevice(controller.getPAObjectID(), PAOGroups.getDeviceType(controller.getPAOType())));
			MultiDBPersistent pointMulti = new MultiDBPersistent();
	        pointMulti.getDBPersistentVector().addAll(points);
	        try {
	            PointUtil.insertIntoDB(pointMulti);
	        } catch (TransactionException e ) {
	            CTILogger.error("Failed on Inserting Points for CapBankController, " + capbankController.getName() +".");
	        }
		}
	}

	@Override
	public boolean remove(CapbankController capbankController) {
    	DeviceBase device = DeviceFactory.createDevice(capbankController.getType().getDeviceTypeId());
    	device.setDeviceID(capbankController.getId());
		
    	//TODO: Delete Points on object.
		try {
			Transaction.createTransaction(com.cannontech.database.Transaction.DELETE, device).execute();
		} catch (TransactionException e) {
			CTILogger.error("Removal of CBC Failed: " + capbankController.getName());
			return false;
		}
    	
		return true;  
	}

	@Override
	public boolean update(CapbankController capbankController) {
		PaoType type = capbankController.getType();
		
		DeviceBase controller = DeviceFactory.createDevice(type.getDeviceTypeId());
		controller.setPAOName(capbankController.getName());
		controller.setDeviceID(capbankController.getId());
		controller.setPAOCategory(PAOGroups.STRING_CAT_DEVICE);
		
		setTypeSpecificCbcFields(type,controller,capbankController);
		
		try {
			Transaction.createTransaction(com.cannontech.database.Transaction.UPDATE, controller).execute();
		} catch (TransactionException e) {
			CTILogger.error("Update of CapBankController, " + capbankController.getName() + ", failed.");
			return false;
		}
		
		int rowsAffected = simpleJdbcTemplate.update(updateSql, 
				capbankController.getSerialNumber(),
				capbankController.getRouteId(),
				capbankController.getId());
		boolean result = (rowsAffected == 1);
		
		if (result == false) {
			CTILogger.error("Update of controller information in DeviceCBC table failed for cbc with name: " + capbankController.getName());
		}
		
		return result;
	}
	
	/**
	 * This function will copy an existing controller and it's points. 
	 * Then set the values stored in CapbankController into the newly created controller
	 */
	@Override
	public boolean createControllerFromTemplate(String templateName, CapbankController controller) {
		List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(templateName, false);
		if (paos.size() != 1) {
			CTILogger.error("Template not found to copy.");
			throw new UnsupportedOperationException("Template not found to copy. " + templateName);
		}
		
		LiteYukonPAObject pao = paos.get(0);
		PaoType deviceType = pao.getPaoType();
		
		controller.setType(deviceType);
		
		int templateDeviceId = pao.getLiteID();
		DeviceBase base = DeviceFactory.createDevice(deviceType);
		base.setDeviceID(templateDeviceId);

		try {
			Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, base).execute();
		} catch (TransactionException e) {
			throw new UnsupportedOperationException("Error Retrieving Template from the database. " + templateName);
		}
		
		int newId = paoDao.getNextPaoId();
		controller.setId(newId);
		base.setDeviceID(newId);
		base.setPAOName(controller.getName());
		setTypeSpecificCbcFields(deviceType, base, controller);
		
		try {
			Transaction.createTransaction(com.cannontech.database.Transaction.INSERT, base).execute();
		} catch (TransactionException e) {
			throw new UnsupportedOperationException("Error inserting copy of template into the database. " + controller.getName());
		}
		
		//Copy points and add them to the DB
        List<PointBase> points = getPointsForPao(templateDeviceId);
        this.applyPoints(newId, points);
		
		return true;
	}
	
	@Override
	public boolean assignController(Capbank capbank, CapbankController controller) {
		return assignController(capbank.getId(),controller.getId());
	}

	@Override
	public boolean assignController(int capbankId, int controllerId) {
	    List<PointBase> cbcPoints = getPointsForPao(controllerId);
	    PointBase controlPoint = null;
	    for(PointBase pointBase : cbcPoints){
	        if(pointBase.getPoint().getPointOffset() == 1 && PointType.getForString(pointBase.getPoint().getPointType()) == PointType.Status){
	            controlPoint = pointBase;
	            break;
	        }
	    }
	    if(controlPoint == null) return false;
	    
    	String assignedController = "UPDATE CAPBANK SET CONTROLDEVICEID = ?, CONTROLPOINTID = ? WHERE DEVICEID = ?";
    	
		int rowsAffected = simpleJdbcTemplate.update(assignedController,controllerId, controlPoint.getPoint().getPointID(), capbankId);
		
		boolean result = (rowsAffected == 1);
		return result;
	}

	@Override
	public boolean unassignController(CapbankController controller) {
		return unassignController(controller.getId());
	}

	@Override
	public boolean unassignController(int controllerId) {
    	String removeAssignmentSql = "UPDATE CAPBANK SET CONTROLDEVICEID=0 WHERE CONTROLDEVICEID = ?";
    	
		int rowsAffected = simpleJdbcTemplate.update(removeAssignmentSql,controllerId);
		
		boolean result = (rowsAffected == 1);
		return result;
	}
	
	@Override
    public List<Integer> getUnassignedControllerIds() {
		String sql = "SELECT PAObjectID FROM YukonPAObject"
			+ " WHERE Category = 'DEVICE' and PAOClass = 'CAPCONTROL'"
			+ " and type like 'CBC%' and PAObjectID not in"
			+ " (SELECT ControlDeviceID FROM CAPBANK) ORDER BY PAOName";
    
		ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
	        public Integer mapRow(ResultSet rs, int num) throws SQLException{
	            Integer i = new Integer ( rs.getInt("PAObjectID") );
	            return i;
	        }
	    };
	    
	    List<Integer> cbcIds = simpleJdbcTemplate.query(sql, mapper);
	    
	    return cbcIds;
    }
	
	@SuppressWarnings("unchecked")
    @Override
	public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count) {
	    /* Get the unordered total count */
	    SqlStatementBuilder sql = new SqlStatementBuilder();
	    sql.append("SELECT COUNT(*)");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Category").eq(PaoCategory.DEVICE);
        sql.append("    AND PAOClass").eq(PaoClass.CAPCONTROL);
        sql.append("    AND type like 'CBC%'");
        sql.append("    AND PAObjectID not in (SELECT ControlDeviceID FROM CAPBANK)");
	    
	    int orphanCount = simpleJdbcTemplate.queryForInt(sql.getSql(), sql.getArguments());
	    
	    /* Get the paged subset of cc objects */
	    sql = new SqlStatementBuilder();
	    sql.append("SELECT PAObjectID, PAOName, Type, Description");
	    sql.append("FROM YukonPAObject");
	    sql.append("  WHERE Category").eq(PaoCategory.DEVICE);
	    sql.append("    AND PAOClass").eq(PaoClass.CAPCONTROL);
	    sql.append("    AND type like 'CBC%'");
	    sql.append("    AND PAObjectID not in (SELECT ControlDeviceID FROM CAPBANK)");
	    sql.append("ORDER BY PAOName");
	    
	    PagingResultSetExtractor cbcOrphanExtractor = new PagingResultSetExtractor(start, count, liteCapControlObjectRowMapper);
	    simpleJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), cbcOrphanExtractor);
	    
	    List<LiteCapControlObject> unassignedCbcs = (List<LiteCapControlObject>) cbcOrphanExtractor.getResultList();
	    
		SearchResult<LiteCapControlObject> searchResult = new SearchResult<LiteCapControlObject>();
        searchResult.setResultList(unassignedCbcs);
        searchResult.setBounds(start, count, orphanCount);
		
        return searchResult;
	}
	
	/**
	 * Handles the special cases each type of CBC needs.
	 * 
	 * @param type
	 * @param device
	 * @param controller
	 */
	private void setTypeSpecificCbcFields(PaoType type, DeviceBase device, CapbankController controller ) {
		
		switch(type) {
			case CBC_7022:
			case CBC_7024:
			case CBC_7023:
			case CBC_7020: {
				CapBankController702x cbc = (CapBankController702x)device;
				cbc.setCommID(controller.getPortId());
				
				DeviceAddress devAddress = cbc.getDeviceAddress();
				DeviceCBC devCbc = cbc.getDeviceCBC();
				
				devAddress.setMasterAddress(controller.getMasterAddress());
				devAddress.setSlaveAddress(controller.getSlaveAddress());
				devAddress.setPostCommWait(controller.getPostCommWait());
				devCbc.setSerialNumber(controller.getSerialNumber());
				devCbc.setRouteID(controller.getRouteId());
				
				DeviceScanRate scanRate = new DeviceScanRate();
				scanRate.setDeviceID(controller.getId());
				
				scanRate.setScanType(controller.getScanType());
				scanRate.setScanGroup(controller.getScanGroup());
				scanRate.setIntervalRate(controller.getIntervalRate());
				scanRate.setAlternateRate(controller.getAlternateRate());
				
				cbc.getDeviceScanRateMap().clear();
				cbc.getDeviceScanRateMap().put(scanRate.getScanType(),scanRate);
				
				break;	
			}
			case CBC_DNP: {
				CapBankControllerDNP cbc = (CapBankControllerDNP)device;
				cbc.setCommID(controller.getPortId());
				
				DeviceAddress devAddress = cbc.getDeviceAddress();
				DeviceCBC devCbc = cbc.getDeviceCBC();
				
				devAddress.setMasterAddress(controller.getMasterAddress());
				devAddress.setSlaveAddress(controller.getSlaveAddress());
				devAddress.setPostCommWait(controller.getPostCommWait());
				devCbc.setSerialNumber(controller.getSerialNumber());
				devCbc.setRouteID(controller.getRouteId());
				
				DeviceScanRate scanRate = new DeviceScanRate();
				scanRate.setDeviceID(controller.getId());
				
				scanRate.setScanType(controller.getScanType());
				scanRate.setScanGroup(controller.getScanGroup());
				scanRate.setIntervalRate(controller.getIntervalRate());
				scanRate.setAlternateRate(controller.getAlternateRate());
				
				cbc.getDeviceScanRateMap().clear();
				cbc.getDeviceScanRateMap().put(scanRate.getScanType(), scanRate);
				
				break;
			}
			case DNP_CBC_6510: {
				CapBankController6510 cbc = (CapBankController6510)device;
				cbc.setCommID(controller.getPortId());
				
				DeviceAddress devAddress = cbc.getDeviceAddress();
				
				devAddress.setMasterAddress(controller.getMasterAddress());
				devAddress.setSlaveAddress(controller.getSlaveAddress());
				devAddress.setPostCommWait(controller.getPostCommWait());
				break;
			}
			case CBC_FP_2800:
			case CAPBANKCONTROLLER:
			case CBC_EXPRESSCOM:
			case CBC_7012:
			case CBC_7011:
			case CBC_7010: {
				CapBankController cbc = (CapBankController)device;
				
				DeviceCBC devCbc = cbc.getDeviceCBC();
				
				devCbc.setSerialNumber(controller.getSerialNumber());
				devCbc.setRouteID(controller.getRouteId());
				break;
			}
			default: {
				throw new UnsupportedOperationException("Device Type not supported: " + type.getPaoTypeName());
			}
		}
	}
	
    private void applyPoints(int deviceId, List<PointBase> points) {
        
        MultiDBPersistent pointsToAdd = new MultiDBPersistent();
        Vector<DBPersistent> newPoints = pointsToAdd.getDBPersistentVector();

        for (PointBase point : points) {
        
            int nextPointId = pointDao.getNextPointId();
            point.setPointID(nextPointId);
            point.getPoint().setPaoID(deviceId);
            
            newPoints.add(point);
        }
        
		try {
			PointUtil.insertIntoDB(pointsToAdd);
		} catch (TransactionException e) {
			String str = "Failed on Inserting Points for CapBankController with id " + deviceId +".";
			CTILogger.error(str);
			throw new UnsupportedOperationException(str);
		}
    }
    
    private List<PointBase> getPointsForPao(int id) {
        
        List<LitePoint> litePoints = pointDao.getLitePointsByPaObjectId(id);
        List<PointBase> points = new ArrayList<PointBase>(litePoints.size());
        
        for (LitePoint litePoint: litePoints) {
            
            PointBase pointBase = (PointBase)LiteFactory.createDBPersistent(litePoint);
            
            try {
                Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, pointBase).execute();
                points.add(pointBase);
            }
            catch (TransactionException e) {
                throw new DeviceCreationException("Could not retrieve points for new device.", e);
            }
        }

        return points;
    }
	
	@Override
	public void changeSerialNumber(SimpleDevice device, int newSerialNumber) {
		String sql = "UPDATE DeviceCBC SET SERIALNUMBER = ? WHERE DEVICEID = ?";
		
		simpleJdbcTemplate.update(sql,newSerialNumber,device.getDeviceId());
	}
	
	@Autowired
	public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
		this.simpleJdbcTemplate = simpleJdbcTemplate;
	}
	
    @Autowired
	public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}
    
    @Autowired
	public void setPointDao(PointDao pointDao) {
		this.pointDao = pointDao;
	}
    
    @Autowired
	public void setPaoDefinitionService(PaoDefinitionService paoDefinitionService) {
		this.paoDefinitionService = paoDefinitionService;
	}
    
}