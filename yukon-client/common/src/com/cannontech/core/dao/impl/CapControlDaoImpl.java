package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.capcontrol.CBCPointGroup;
import com.cannontech.capcontrol.OrphanCBC;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.CommonUtils;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.util.MapQueue;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.CapControlDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.CBCPointTimestampParams;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.ScalarPoint;
import com.cannontech.message.dispatch.message.PointData;

public class CapControlDaoImpl  implements CapControlDao{

    private PointDao pointDao;
    private PaoDao paoDao;
    private AuthDao authDao;
    private StateDao stateDao;
    private DynamicDataSource dynamicDataSource;
    private JdbcTemplate jdbcOps;
    private DeviceDefinitionDao deviceDefinitionDao = null;
    List<OrphanCBC> cbcList;
    
    private static MapQueue<CBCPointGroup, String> pointGroupConfig = new MapQueue<CBCPointGroup, String>();
    
    // Initialize point group config
    static {
    	
		// Analog Group
    	pointGroupConfig.add(CBCPointGroup.ANALOG, "Voltage");
    	pointGroupConfig.add(CBCPointGroup.ANALOG, "High Voltage");
    	pointGroupConfig.add(CBCPointGroup.ANALOG, "Low Voltage");
    	pointGroupConfig.add(CBCPointGroup.ANALOG, "Delta Voltage");
    	pointGroupConfig.add(CBCPointGroup.ANALOG, "Temperature");
    	pointGroupConfig.add(CBCPointGroup.ANALOG, "Neutral Current Sensor");
    	pointGroupConfig.add(CBCPointGroup.ANALOG, "Analog Input 1");
    	pointGroupConfig.add(CBCPointGroup.ANALOG, "RSSI");
    	pointGroupConfig.add(CBCPointGroup.ANALOG, "Control Ignored Reason");
    	pointGroupConfig.add(CBCPointGroup.ANALOG, "IP Address");
    	pointGroupConfig.add(CBCPointGroup.ANALOG, "UDP Port");
    	pointGroupConfig.add(CBCPointGroup.ANALOG, "Firmware Version");
        pointGroupConfig.add(CBCPointGroup.ANALOG, "Com Loss Time");
        pointGroupConfig.add(CBCPointGroup.ANALOG, "Com Retry Delay Time");
        pointGroupConfig.add(CBCPointGroup.ANALOG, "Yukon Poll Time");
        
    	// Accumulator Group
    	pointGroupConfig.add(CBCPointGroup.ACCUMULATOR, "Total op count");
    	pointGroupConfig.add(CBCPointGroup.ACCUMULATOR, "UV op count");
    	pointGroupConfig.add(CBCPointGroup.ACCUMULATOR, "OV op count");
    	
    	// Status Group
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Capacitor bank state");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Control Mode");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Re-close Blocked");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Last Control - Local");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Last Control - Remote");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Last Control - OVUV");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Last Control - Neutral Fault");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Last Control - Scheduled");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Last Control - Digital");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Last Control - Analog");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Last Control - Temperature");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Auto Volt Control");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "OV Condition");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "UV Condition");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Voltage Delta Abnormal");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Op Failed - Neutral Current");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Neutral Current Fault");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Neutral Lockout");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Bad Relay");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Daily Max Ops");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Temp Alarm");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "DST Active");
    	pointGroupConfig.add(CBCPointGroup.STATUS, "Control Ignored Indicator");
    	
    	// Configurable Parameters
    	pointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Daily Control Limit");
    	pointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Control UV Set Point");
    	pointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Control OV Set Point");
    	pointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Control OVUV Track Time");
    	pointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Emergency UV Set Point");
    	pointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Emergency OV Set Point");
    	pointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Emergency OVUV Track Time");
    	pointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Neutral Current Alarm Set Point");
    	pointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Trip Delay Time");
    	pointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Close Delay Time");
    	pointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Re-Close Delay Time");
    	pointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Bank Control Time");
    	
    }
    
    public CapControlDaoImpl() {
        super();
    }
    
    public Map<String, List<CBCPointTimestampParams>> getSortedCBCPointTimeStamps(
            Integer cbcId) {

        LiteYukonPAObject liteCbc = paoDao.getLiteYukonPAO(cbcId);
        YukonDevice device = new YukonDevice(cbcId, liteCbc.getType());

        Map<String, CBCPointTimestampParams> nameToParamsMap = new HashMap<String, CBCPointTimestampParams>();
        List<LitePoint> allPoints = pointDao.getLitePointsByPaObjectId(cbcId);
        for (LitePoint point : allPoints) {

            // Get default point name from device definition
            int offset = point.getPointOffset();
            int pointType = point.getPointType();

            String defaultName = null;
            try {
	            PointTemplate pointTemplate = deviceDefinitionDao.getPointTemplateByTypeAndOffset(device,
	                                                                                              offset,
	                                                                                              pointType);
				defaultName = pointTemplate.getName();
            } catch (NotFoundException e) {
            	// point not in deviceDefinition.xml
            	defaultName = point.getPointName();
            }
            
            // Convert point to CBCPointTimestampParams
            CBCPointTimestampParams convertedPoint = this.convertLitePointToCBCPointTimestampParams(point);

            nameToParamsMap.put(defaultName, convertedPoint);

        }

        // Create return MapQueue
        Map<String, List<CBCPointTimestampParams>> returnMap = new HashMap<String, List<CBCPointTimestampParams>>();

        // Add Analog group point timestamp params
        List<CBCPointTimestampParams> analogList = new ArrayList<CBCPointTimestampParams>();
        for (String pointName : pointGroupConfig.get(CBCPointGroup.ANALOG)) {
            CBCPointTimestampParams pointTimestampParams = nameToParamsMap.get(pointName);
            nameToParamsMap.remove(pointName);
            analogList.add(pointTimestampParams);
        }
        returnMap.put(CBCPointGroup.ANALOG.toString(), analogList);
        
        // Add Accumulator group point timestamp params
        List<CBCPointTimestampParams> accumulatorList = new ArrayList<CBCPointTimestampParams>();
        for (String pointName : pointGroupConfig.get(CBCPointGroup.ACCUMULATOR)) {
            CBCPointTimestampParams pointTimestampParams = nameToParamsMap.get(pointName);
            nameToParamsMap.remove(pointName);
            accumulatorList.add(pointTimestampParams);
        }
        returnMap.put(CBCPointGroup.ACCUMULATOR.toString(), accumulatorList);

        // Add Status group point timestamp params
        List<CBCPointTimestampParams> statusList = new ArrayList<CBCPointTimestampParams>();
        for (String pointName : pointGroupConfig.get(CBCPointGroup.STATUS)) {
            CBCPointTimestampParams pointTimestampParams = nameToParamsMap.get(pointName);
            nameToParamsMap.remove(pointName);
            statusList.add(pointTimestampParams);
        }
        returnMap.put(CBCPointGroup.STATUS.toString(), statusList);

        // Add Configurable Parameters group point timestamp params
        List<CBCPointTimestampParams> configList = new ArrayList<CBCPointTimestampParams>();
        for (String pointName : pointGroupConfig.get(CBCPointGroup.CONFIGURABLE_PARAMETERS)) {
            CBCPointTimestampParams pointTimestampParams = nameToParamsMap.get(pointName);
            nameToParamsMap.remove(pointName);
            configList.add(pointTimestampParams);
        }
        returnMap.put(CBCPointGroup.CONFIGURABLE_PARAMETERS.toString(), configList);
        
        // Add all other point timestamp params to misc group
        List<CBCPointTimestampParams> miscList = new ArrayList<CBCPointTimestampParams>();
        miscList.addAll(nameToParamsMap.values());
        returnMap.put(CBCPointGroup.MISC.toString(), miscList);

        return returnMap;
    }

    /**
	 * Helper method to create a CBCPointTimestampParams from a LitePoint
	 * 
	 * @param point - LitePoint to be converted
	 * @return Converted point
	 */
	private CBCPointTimestampParams convertLitePointToCBCPointTimestampParams(
			LitePoint point) {

		if (point == null) {
			return null;
		}

		CBCPointTimestampParams pointTimestamp = new CBCPointTimestampParams();
		pointTimestamp.setPointId(new Integer(point.getLiteID()));
		pointTimestamp.setPointName(point.getPointName());

		// wait for the point data to initialize
		PointValueHolder pointData = new PointData();
		try {
			pointData = dynamicDataSource.getPointValue(point.getLiteID());
		} catch (DynamicDataAccessException ddae) {
			// Should this code really just use a default pointdata if one
			// couldn't be found for this point id??
		}

		if (pointData.getType() == PointTypes.STATUS_POINT) {
			LiteState currentState = stateDao.getLiteState(point
					.getStateGroupID(), (int) pointData.getValue());
			String stateText = currentState.getStateText();
			pointTimestamp.setValue(stateText);
		} else {
			if (point.getPointOffset() != 20001) {
				Double analogVal = new Double(pointData.getValue());

				ScalarPoint persPoint = (ScalarPoint) LiteFactory
						.convertLiteToDBPers(point);
				Integer decimalPlaces = persPoint.getPointUnit()
						.getDecimalPlaces();

				DecimalFormat formater = new DecimalFormat();
				formater.setMaximumFractionDigits(decimalPlaces);
				String format = formater.format(analogVal.doubleValue());
				pointTimestamp.setValue(format);
			} else {
				// handle ip address differently
				Double pvalue = new Double(pointData.getValue());
				Long plong = 0l;
				plong = new Long(pvalue.longValue());
				String ipaddress = convertToOctalIp(plong);
				pointTimestamp.setValue(ipaddress);
			}
		}

		if (!pointData.getPointDataTimeStamp().equals(
				CommonUtils.getDefaultStartTime())) {
			pointTimestamp.setTimestamp(pointData.getPointDataTimeStamp());
		}

		return pointTimestamp;
	}
   
    public String convertToOctalIp(long ipvalue){
        
        StringBuilder sb = new StringBuilder();
        int temp = (int) ((ipvalue >> 24) & 0xFF);
        sb.append(Integer.toString(temp, 10) + ".");
        temp = (int) ((ipvalue >> 16) & 0xFF);
        sb.append(Integer.toString(temp, 10) + ".");
        temp = (int) ((ipvalue >> 8) & 0xFF);
        sb.append(Integer.toString(temp, 10) + ".");
        temp = (int) (ipvalue & 0xFF);
        sb.append(Integer.toString(temp, 10));
       
        return sb.toString();
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.cbc.daoimpl.CBCDao#getAllSubsForUser(com.cannontech.database.data.lite.LiteYukonUser)
     */
    public List<LiteYukonPAObject> getAllSubsForUser(LiteYukonUser user) {
        List<LiteYukonPAObject> subList = new ArrayList<LiteYukonPAObject>(10);
        
        List<LiteYukonPAObject> temp = paoDao.getAllCapControlSubBuses();
        for (Iterator<LiteYukonPAObject> iter = temp.iterator(); iter.hasNext();) {
            LiteYukonPAObject element = iter.next();
            
            if (authDao.userHasAccessPAO(user, element.getLiteID())) {
                subList.add(element);
            }
        }
        return subList;
    }

    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
    
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    public void setJdbcOps(JdbcTemplate jdbcOps) {
        this.jdbcOps = jdbcOps;
    }



    public List<LitePoint> getPaoPoints(YukonPAObject pao) {
        return  pointDao.getLitePointsByPaObjectId(pao.getPAObjectID());
    }

    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
		this.deviceDefinitionDao = deviceDefinitionDao;
	}

    public Integer getParentForController(int id) {
        String sql = "select deviceid from capbank where controldeviceid = ?";
        Integer parentID = 0;
        try{
            parentID = jdbcOps.queryForInt(sql, new Integer[] {id});
        }
        catch (DataAccessException dae)
        {
            CTILogger.debug("Could not find parent for cbc:" + id);
        }
        return parentID;
    }



    public Integer getParentForPoint(int id) {
        String sql = "select paobjectid from point where pointid = ?";
        Integer parentID = 0;
        try{
            parentID = jdbcOps.queryForInt(sql, new Integer[] {id});
        }
        catch (DataAccessException dae)
        {
            CTILogger.debug("Could not find parent for cbc:" + id);
        }
        return parentID;
    }
    
    public List<OrphanCBC> getOrphanedCBCs(){
        
        SqlStatementBuilder query = new SqlStatementBuilder();
        query.append("select y.paoname as devicename ");
        query.append(", p.pointid as pointid ");
        query.append(", p.pointname as pointname "); 
        query.append("from point p ");
        query.append(", yukonpaobject y ");
        query.append("where p.paobjectid = y.paobjectid ");
        query.append("and y.type like 'CBC%'  ");
        query.append("and y.paobjectid not in (select controldeviceid from capbank) "); 
        query.append("and p.pointoffset = 1  ");
        query.append("and p.pointtype = 'STATUS' "); 
        query.append("order by y.paoname ");
        cbcList = new ArrayList<OrphanCBC>();
        jdbcOps.query(query.toString(), new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                String deviceName = rs.getString("devicename");
                Integer pointId = rs.getInt("pointid");
                String pointName = rs.getString("pointname");
                OrphanCBC cbc = new OrphanCBC(deviceName, pointId, pointName);
                cbcList.add(cbc);
            }
        });
        return cbcList;
    }
    
}

