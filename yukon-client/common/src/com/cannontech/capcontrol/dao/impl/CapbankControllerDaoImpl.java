package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.providers.fields.DeviceAddressFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceCbcFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceDirectCommSettingsFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceScanRateFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceWindowFields;
import com.cannontech.capcontrol.model.Capbank;
import com.cannontech.capcontrol.model.CapbankController;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.DeviceScanTypesEnum;
import com.cannontech.common.device.DeviceWindowTypesEnum;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.db.DBPersistent;

public class CapbankControllerDaoImpl implements CapbankControllerDao {
	
	private YukonJdbcTemplate yukonJdbcTemplate;
	private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;

	private PointDao pointDao;
	
    static {
        liteCapControlObjectRowMapper = CapbankControllerDaoImpl.createLiteCapControlObjectRowMapper();
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
    	throw new UnsupportedOperationException("CBC creation should be handled by the PaoCreationService!");
    }
    
	@Override
	public void add(CapbankController capbankController, boolean addPoints) {
		throw new UnsupportedOperationException("CBC creation should be handled by the PaoCreationService!");
	}

	@Override
	public boolean remove(CapbankController capbankController) {
    	throw new UnsupportedOperationException("CBC deletion should be handled by the PaoCreationService!");
	}

	@Override
	public boolean update(CapbankController capbankController) {
		throw new UnsupportedOperationException("CBC updates should be handled by the PaoCreationService!");
	}
	
	@Override
	public void insertDeviceData(PaoIdentifier paoIdentifier, DeviceFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto("Device");
		params.addValue("DeviceId", paoIdentifier.getPaoId());
		params.addValue("AlarmInhibit", fields.getAlarmInhibit());
		params.addValue("ControlInhibit", fields.getControlInhibit());
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void updateDeviceData(PaoIdentifier paoIdentifier, DeviceFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.update("Device");
		params.addValue("AlarmInhibit", fields.getAlarmInhibit());
		params.addValue("ControlInhibit", fields.getControlInhibit());
		
		sql.append("WHERE DeviceId").eq(paoIdentifier.getPaoId());
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void insertScanRateData(PaoIdentifier paoIdentifier, DeviceScanRateFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto("DeviceScanRate");
		params.addValue("DeviceId", paoIdentifier.getPaoId());
		params.addValue("ScanType", fields.getScanType());
		params.addValue("IntervalRate", fields.getIntervalRate());
		params.addValue("ScanGroup", fields.getScanGroup());
		params.addValue("AlternateRate", fields.getAlternateRate());
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void updateScanRateData(PaoIdentifier paoIdentifier, DeviceScanRateFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.update("DeviceScanRate");
		params.addValue("ScanType", fields.getScanType());
		params.addValue("IntervalRate", fields.getIntervalRate());
		params.addValue("ScanGroup", fields.getScanGroup());
		params.addValue("AlternateRate", fields.getAlternateRate());
		
		sql.append("WHERE DeviceId").eq(paoIdentifier.getPaoId());
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void insertDeviceWindowData(PaoIdentifier paoIdentifier, DeviceWindowFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto("DeviceWindow");
		params.addValue("DeviceId", paoIdentifier.getPaoId());
		params.addValue("Type", fields.getType());
		params.addValue("WinOpen", fields.getWindowOpen());
		params.addValue("WinClose", fields.getWindowClose());
		params.addValue("AlternateOpen", fields.getAlternateOpen());
		params.addValue("AlternateClose", fields.getAlternateClose());
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void updateDeviceWindowData(PaoIdentifier paoIdentifier, DeviceWindowFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.update("DeviceWindow");
		params.addValue("Type", fields.getType());
		params.addValue("WinOpen", fields.getWindowOpen());
		params.addValue("WinClose", fields.getWindowClose());
		params.addValue("AlternateOpen", fields.getAlternateOpen());
		params.addValue("AlternateClose", fields.getAlternateClose());
		
		sql.append("WHERE DeviceId").eq(paoIdentifier.getPaoId());
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void insertCommSettingsData(PaoIdentifier paoIdentifier, DeviceDirectCommSettingsFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto("DeviceDirectCommSettings");
		params.addValue("DeviceId", paoIdentifier.getPaoId());
		params.addValue("PortId", fields.getPortId());
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void updateCommSettingsData(PaoIdentifier paoIdentifier, DeviceDirectCommSettingsFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.update("DeviceDirectCommSettings");
		params.addValue("PortId", fields.getPortId());
		
		sql.append("WHERE DeviceId").eq(paoIdentifier.getPaoId());
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void insertDeviceCbcData(PaoIdentifier paoIdentifier, DeviceCbcFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto("DeviceCBC");
		params.addValue("DeviceId", paoIdentifier.getPaoId());
		params.addValue("SerialNumber", fields.getSerialNumber());
		params.addValue("RouteId", fields.getRouteId());
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void updateDeviceCbcData(PaoIdentifier paoIdentifier, DeviceCbcFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.update("DeviceCBC");
		params.addValue("SerialNumber", fields.getSerialNumber());
		params.addValue("RouteId", fields.getRouteId());
		
		sql.append("WHERE DeviceId").eq(paoIdentifier.getPaoId());
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void insertDeviceAddressData(PaoIdentifier paoIdentifier, DeviceAddressFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto("DeviceAddress");
		params.addValue("DeviceId", paoIdentifier.getPaoId());
		params.addValue("MasterAddress", fields.getMasterAddress());
		params.addValue("SlaveAddress", fields.getSlaveAddress());
		params.addValue("PostCommWait", fields.getPostCommWait());
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void updateDeviceAddressData(PaoIdentifier paoIdentifier, DeviceAddressFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.update("DeviceAddress");
		params.addValue("MasterAddress", fields.getMasterAddress());
		params.addValue("SlaveAddress", fields.getSlaveAddress());
		params.addValue("PostCommWait", fields.getPostCommWait());
		
		sql.append("WHERE DeviceId").eq(paoIdentifier.getPaoId());
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void deleteControllerData(PaoProviderTableEnum table, PaoIdentifier paoIdentifier) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("DELETE FROM " + table.name());
		sql.append("WHERE DeviceId").eq(paoIdentifier.getPaoId());
		
		yukonJdbcTemplate.update(sql);
	}
	
	/**
	 * This function will copy an existing controller and it's points. 
	 * Then set the values stored in CapbankController into the newly created controller
	 */
	@Override
	public boolean createControllerFromTemplate(String templateName, CapbankController controller) {
		
		return true;
		/*
		List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(templateName, false);
		if (paos.size() != 1) {
			CTILogger.error("Template not found to copy.");
			throw new UnsupportedOperationException("Template not found to copy. " + templateName);
		}
		
		LiteYukonPAObject pao = paos.get(0);
		PaoType deviceType = pao.getPaoType();
		
		int templateDeviceId = pao.getLiteID();
		DeviceBase base = DeviceFactory.createDevice(deviceType);
		base.setDeviceID(templateDeviceId);

		try {
			Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, base).execute();
		} catch (TransactionException e) {
			throw new UnsupportedOperationException("Error Retrieving Template from the database. " + templateName);
		}
		
		int newId = paoDao.getNextPaoId();
		//TODO jg fix!
		//controller.setId(newId);
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
		*/
	}
	
	@Override
	public boolean assignController(Capbank capbank, CapbankController controller) {
		return assignController(capbank.getPaoId(),controller.getId());
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
	    
	    if (controlPoint == null) {
	    	return false;
	    }
	    
	    unassignController(controllerId);
	    
	    SqlStatementBuilder sql = new SqlStatementBuilder();
	    
	    SqlParameterSink params = sql.update("CapBank");
	    params.addValue("ControlDeviceID", controllerId);
	    params.addValue("ControlPointID", controlPoint.getPoint().getPointID());
	    
	    sql.append("WHERE DeviceID").eq(capbankId);
	    
		int rowsAffected = yukonJdbcTemplate.update(sql);
		
		boolean result = (rowsAffected == 1);
		
		return result;
	}

	@Override
	public boolean unassignController(CapbankController controller) {
		return unassignController(controller.getId());
	}

	@Override
	public boolean unassignController(int controllerId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.update("CapBank");
		params.addValue("ControlDeviceID", 0);
		params.addValue("ControlPointID", 0);
		
		sql.append("WHERE ControlDeviceID").eq(controllerId);
    	
		int rowsAffected = yukonJdbcTemplate.update(sql);
		
		boolean result = (rowsAffected == 1);
		
		return result;
	}
	
	@Override
    public List<Integer> getUnassignedControllerIds() {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("SELECT PAObjectID");
		sql.append("FROM YukonPAObject");
		sql.append("WHERE Category").eq(PaoCategory.DEVICE);
		sql.append(   "AND PAOClass").eq(PaoClass.CAPCONTROL);
		sql.append(   "AND Type like 'CBC%' AND PAObject NOT IN");
		sql.append(      "(SELECT ControlDeviceID");
		sql.append(      " FROM CAPBANK)");
		sql.append("ORDER BY PAOName");
    
		ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
	        public Integer mapRow(ResultSet rs, int num) throws SQLException{
	            Integer i = new Integer ( rs.getInt("PAObjectID") );
	            return i;
	        }
	    };
	    
	    List<Integer> cbcIds = yukonJdbcTemplate.query(sql, mapper);
	    
	    return cbcIds;
    }
	
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
	    
	    int orphanCount = yukonJdbcTemplate.queryForInt(sql.getSql(), sql.getArguments());
	    
	    /* Get the paged subset of cc objects */
	    sql = new SqlStatementBuilder();
	    sql.append("SELECT PAObjectID, PAOName, Type, Description");
	    sql.append("FROM YukonPAObject");
	    sql.append("  WHERE Category").eq(PaoCategory.DEVICE);
	    sql.append("    AND PAOClass").eq(PaoClass.CAPCONTROL);
	    sql.append("    AND type like 'CBC%'");
	    sql.append("    AND PAObjectID not in (SELECT ControlDeviceID FROM CAPBANK)");
	    sql.append("ORDER BY PAOName");
	    
	    PagingResultSetExtractor<LiteCapControlObject> cbcOrphanExtractor = new PagingResultSetExtractor<LiteCapControlObject>(start, count, liteCapControlObjectRowMapper);
	    yukonJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), cbcOrphanExtractor);
	    
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
	 *
	private void setTypeSpecificCbcFields(PaoType type, DeviceBase device, CapbankController controller ) {
		
		switch(type) {
			case CBC_7022:
			case CBC_7024:
			case CBC_7023:
			case CBC_7020:
			case CBC_8020:
			case CBC_8024: {
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
	*/
	@Override
    public void applyPoints(int deviceId, List<PointBase> points) {
        
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
    
    @Override
    public List<PointBase> getPointsForPao(int paoId) {
        
        List<LitePoint> litePoints = pointDao.getLitePointsByPaObjectId(paoId);
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
		
		yukonJdbcTemplate.update(sql,newSerialNumber,device.getDeviceId());
	}
	
	@Autowired
	public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
    
    @Autowired
	public void setPointDao(PointDao pointDao) {
		this.pointDao = pointDao;
	}

	@Override
	public DeviceFields getDeviceData(PaoIdentifier paoIdentifier) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("SELECT AlarmInhibit, ControlInhibit");
		sql.append("FROM Device");
		sql.append("WHERE DeviceID").eq(paoIdentifier.getPaoId());
		
		YukonRowMapper<DeviceFields> deviceRowMapper = new YukonRowMapper<DeviceFields>() {
			public DeviceFields mapRow(YukonResultSet rs) throws SQLException {
				DeviceFields deviceFields = new DeviceFields();
				
				deviceFields.setAlarmInhibit(rs.getString("AlarmInhibit"));
				deviceFields.setControlInhibit(rs.getString("ControlInhibit"));
				
				return deviceFields;
			}
		};
		
		DeviceFields deviceFields = yukonJdbcTemplate.queryForObject(sql, deviceRowMapper);
		
		return deviceFields;
	}

	@Override
	public DeviceScanRateFields getDeviceScanRateData(PaoIdentifier paoIdentifier) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("SELECT ScanType, IntervalRate, ScanGroup, AlternateRate");
		sql.append("FROM DeviceScanRate");
		sql.append("WHERE DeviceID").eq(paoIdentifier.getPaoId());
		
		YukonRowMapper<DeviceScanRateFields> scanRateRowMapper = new YukonRowMapper<DeviceScanRateFields>() {
			public DeviceScanRateFields mapRow(YukonResultSet rs) throws SQLException {
				DeviceScanRateFields scanRateFields = new DeviceScanRateFields();
				
				scanRateFields.setAlternateRate(rs.getInt("AlternateRate"));
				scanRateFields.setIntervalRate(rs.getInt("IntervalRate"));
				scanRateFields.setScanGroup(rs.getInt("ScanGroup"));
				scanRateFields.setScanType(DeviceScanTypesEnum.getForDbString(rs.getString("ScanType")));
				
				return scanRateFields;
			}
		};
		
		DeviceScanRateFields scanRateFields = yukonJdbcTemplate.queryForObject(sql, scanRateRowMapper);
		
		return scanRateFields;
	}

	@Override
	public DeviceWindowFields getDeviceWindowData(PaoIdentifier paoIdentifier) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("SELECT Type, WinOpen, WinClose, AlternateOpen, AlternateClose");
		sql.append("FROM DeviceWindow");
		sql.append("WHERE DeviceID").eq(paoIdentifier.getPaoId());
		
		YukonRowMapper<DeviceWindowFields> windowFieldsRowMapper = new YukonRowMapper<DeviceWindowFields>() {
			@Override
			public DeviceWindowFields mapRow(YukonResultSet rs) throws SQLException {
				DeviceWindowFields windowFields = new DeviceWindowFields();
				
				windowFields.setAlternateClose(rs.getInt("AlternateClose"));
				windowFields.setAlternateOpen(rs.getInt("AlternateOpen"));
				windowFields.setType(DeviceWindowTypesEnum.getForDbString(rs.getString("Type")));
				windowFields.setWindowClose(rs.getInt("WinClose"));
				windowFields.setWindowOpen(rs.getInt("WinOpen"));
				
				return windowFields;
			}
		};
	
		DeviceWindowFields windowFields = yukonJdbcTemplate.queryForObject(sql, windowFieldsRowMapper);
		
		return windowFields;
	}

	@Override
	public DeviceDirectCommSettingsFields getDeviceDirectCommSettingsData(PaoIdentifier paoIdentifier) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("SELECT PortID");
		sql.append("FROM DeviceDirectCommSettings");
		sql.append("WHERE DeviceID").eq(paoIdentifier.getPaoId());
		
		YukonRowMapper<DeviceDirectCommSettingsFields> commSettingsRowMapper = new YukonRowMapper<DeviceDirectCommSettingsFields>() {
			@Override
			public DeviceDirectCommSettingsFields mapRow(YukonResultSet rs) throws SQLException {
				int portId = rs.getInt("PortID");
				
				DeviceDirectCommSettingsFields commSettingsFields = new DeviceDirectCommSettingsFields(portId);
			
				return commSettingsFields;
			}
		};
		
		DeviceDirectCommSettingsFields commSettingsFields = yukonJdbcTemplate.queryForObject(sql, commSettingsRowMapper);
		
		return commSettingsFields;
	}

	@Override
	public DeviceCbcFields getDeviceCbcData(PaoIdentifier paoIdentifier) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("SELECT SerialNumber, RouteID");
		sql.append("FROM DeviceCBC");
		sql.append("WHERE DeviceID").eq(paoIdentifier.getPaoId());
		
		YukonRowMapper<DeviceCbcFields> cbcRowMapper = new YukonRowMapper<DeviceCbcFields>() {
			@Override
			public DeviceCbcFields mapRow(YukonResultSet rs) throws SQLException {
				int serialNumber = rs.getInt("SerialNumber");
				
				DeviceCbcFields cbcFields = new DeviceCbcFields();
				cbcFields.setSerialNumber(serialNumber);
				
				cbcFields.setRouteId(rs.getInt("RouteID"));
				
				return cbcFields;
			}
		};
		
		DeviceCbcFields cbcFields = yukonJdbcTemplate.queryForObject(sql, cbcRowMapper);
		
		return cbcFields;
	}

	@Override
	public DeviceAddressFields getDeviceAddressData(PaoIdentifier paoIdentifier) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("SELECT MasterAddress, SlaveAddress, PostCommWait");
		sql.append("FROM DeviceAddress");
		sql.append("WHERE DeviceID").eq(paoIdentifier.getPaoId());
		
		YukonRowMapper<DeviceAddressFields> addressRowMapper = new YukonRowMapper<DeviceAddressFields>() {
			@Override
			public DeviceAddressFields mapRow(YukonResultSet rs) throws SQLException {
				DeviceAddressFields addressFields = new DeviceAddressFields();
				
				addressFields.setMasterAddress(rs.getInt("MasterAddress"));
				addressFields.setPostCommWait(rs.getInt("PostCommWait"));
				addressFields.setSlaveAddress(rs.getInt("SlaveAddress"));
				
				return addressFields;
			}
		};
		
		DeviceAddressFields addressFields = yukonJdbcTemplate.queryForObject(sql, addressRowMapper);
		
		return addressFields;
	}
}