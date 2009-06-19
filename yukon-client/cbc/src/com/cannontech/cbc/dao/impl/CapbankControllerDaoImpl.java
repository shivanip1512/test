package com.cannontech.cbc.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cbc.dao.CapbankControllerDao;
import com.cannontech.cbc.model.Capbank;
import com.cannontech.cbc.model.CapbankAdditional;
import com.cannontech.cbc.model.CapbankController;
import com.cannontech.cbc.model.LiteCapControlObject;
import com.cannontech.cbc.point.CBCPointFactory;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.DeviceType;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.definition.service.DeviceDefinitionService;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CapBankController6510;
import com.cannontech.database.data.capcontrol.CapBankController701x;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.CapBankControllerDNP;
import com.cannontech.database.data.capcontrol.CapBankControllerExpresscom;
import com.cannontech.database.data.capcontrol.CapBankControllerVersacom;
import com.cannontech.database.data.capcontrol.CapBankController_FP_2800;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointUtil;


public class CapbankControllerDaoImpl implements CapbankControllerDao {

    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
	
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private static final ParameterizedRowMapper<CapbankController> rowMapper;
	private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;
	
	private PaoDao paoDao;
	private DeviceDefinitionService deviceDefinitionService;
	private DeviceDao deviceDao;
	
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
	@Transactional
	public boolean add(CapbankController capbankController) {
		Connection connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
		int newId = paoDao.getNextPaoId();
		DeviceType type = DeviceType.getForId(capbankController.getType());
		
		DeviceBase controller = DeviceFactory.createDevice(type.getDeviceTypeId());
		controller.setDbConnection(connection);
		controller.setPAOName(capbankController.getName());
		controller.setDeviceID(newId);
		controller.setPAOCategory(PAOGroups.STRING_CAT_DEVICE);
		setTypeSpecificCbcFields(type,controller,capbankController);

		try {
			controller.add();
		} catch (SQLException e) {
			CTILogger.error("Insert of CapBankController, " + capbankController.getName() + ", failed.");
			return false;
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
		
		MultiDBPersistent pointVector = CBCPointFactory.createPointsForCBCDevice(controller);
		try {
			PointUtil.insertIntoDB(pointVector);
		} catch (TransactionException e) {
			CTILogger.error("Failed on Inserting Points for CapBankController, " + capbankController.getName() +".");
			return false;
		}
		
		return result;
	}

	@Override
	public boolean remove(CapbankController capbankController) {
    	DeviceBase device = DeviceFactory.createDevice(capbankController.getType());
    	device.setDeviceID(capbankController.getId());
		
    	//TODO Delete Points on object.
    	try {
    		device.delete();
    	} catch (SQLException e) {
    		CTILogger.error("Removal of CBC Failed: " + capbankController.getName());
    		return false;
    	}
    	
		return true;  
	}

	@Override
	public boolean update(CapbankController capbankController) {
		Connection connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
		DeviceType type = DeviceType.getForId(capbankController.getType());
		
		DeviceBase controller = DeviceFactory.createDevice(type.getDeviceTypeId());
		controller.setDbConnection(connection);
		controller.setPAOName(capbankController.getName());
		controller.setDeviceID(capbankController.getId());
		controller.setPAOCategory(PAOGroups.STRING_CAT_DEVICE);
		
		setTypeSpecificCbcFields(type,controller,capbankController);
		
		try {
			controller.update();
		} catch (SQLException e) {
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
	
	@Override
	public boolean assignController(Capbank capbank, CapbankController controller) {
		return assignController(capbank.getId(),controller.getId());
	}

	@Override
	public boolean assignController(int capbankId, int controllerId) {
    	String assignedController = "UPDATE CAPBANK SET CONTROLDEVICEID=? WHERE DEVICEID = ?";
    	
		int rowsAffected = simpleJdbcTemplate.update(assignedController,controllerId,capbankId);
		
		boolean result = (rowsAffected == 1);
		return result;
	}

	@Override
	public boolean unassignController(Capbank capbank, CapbankController controller) {
		return unassignController(capbank.getId(),controller.getId());
	}

	@Override
	public boolean unassignController(int capbankId, int controllerId) {
    	String removeAssignmentSql = "UPDATE CAPBANK SET CONTROLDEVICEID=? WHERE DEVICEID = ?";
    	
		int rowsAffected = simpleJdbcTemplate.update(removeAssignmentSql,controllerId,capbankId);
		
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
	
	@Override
	public List<LiteCapControlObject> getOrphans() {
		List<Integer> cbcIds = getUnassignedControllerIds();
		
		ChunkingSqlTemplate<Integer> template = new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);
		final List<LiteCapControlObject> unassignedCbcs = template.query(new SqlGenerator<Integer>() {
            @Override
            public String generate(List<Integer> subList) {
                SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
                sqlBuilder.append("SELECT PAObjectID,PAOName,TYPE,Description FROM YukonPAObject WHERE PAObjectID IN (");
                sqlBuilder.append(subList);
                sqlBuilder.append(")");
                String sql = sqlBuilder.toString();
                return sql;
            }
        }, cbcIds, liteCapControlObjectRowMapper);
		
		return unassignedCbcs;
	}
	
	/**
	 * Handles the special cases each type of CBC needs.
	 * 
	 * @param type
	 * @param device
	 * @param controller
	 */
	private void setTypeSpecificCbcFields(DeviceType type, DeviceBase device, CapbankController controller ) {
		
		switch(type) {
			case CBC_7022:
			case CBC_7024:
			case CBC_7023:
			case CBC_7020: {
				CapBankController702x cbc = (CapBankController702x)device;
				cbc.setCommID(controller.getPortId());
				break;	
			}
			case CBC_7012:
			case CBC_7011:
			case CBC_7010: {
				//CapBankController701x cbc = (CapBankController701x)device;
				break;
			}
			case DNP_CBC_6510: {
				//CapBankController6510 cbc = (CapBankController6510)device;
				break;
			}
			case CBC_DNP: {
				//CapBankControllerDNP cbc = (CapBankControllerDNP)device;
				break;
			}
			case CBC_FP_2800: {
				//CapBankController_FP_2800 cbc = (CapBankController_FP_2800)device;
				break;
			}
			case CAPBANKCONTROLLER: {
				//CapBankControllerVersacom cbc = (CapBankControllerVersacom)device;
				break;
			}
			case CBC_EXPRESSCOM: {
				//CapBankControllerExpresscom cbc = (CapBankControllerExpresscom)device;
				break;
			}
			default: {
				throw new UnsupportedOperationException("Device Type not supported: " + type.getPaoTypeName());
			}
		}
	}
	
	@Override
	public void changeSerialNumber(YukonDevice device, int newSerialNumber) {
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
	public void setDeviceDefinitionService(
			DeviceDefinitionService deviceDefinitionService) {
		this.deviceDefinitionService = deviceDefinitionService;
	}
    
    @Autowired
	public void setDeviceDao(DeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}

}
