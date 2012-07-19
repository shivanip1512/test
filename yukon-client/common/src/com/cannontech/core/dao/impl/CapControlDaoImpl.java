package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.capcontrol.CBCPointGroup;
import com.cannontech.capcontrol.LiteCapBankAdditional;
import com.cannontech.capcontrol.OrphanCBC;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.MapQueue;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.CapControlDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.capcontrol.CCEventLog;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.google.common.collect.Lists;

public class CapControlDaoImpl  implements CapControlDao{

    @Autowired private PointDao pointDao;
    @Autowired private PaoDao paoDao;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    
    private static final Logger log = YukonLogManager.getLogger(CapControlDaoImpl.class);

    private static MapQueue<CBCPointGroup, String> cbcPointGroupConfig = new MapQueue<CBCPointGroup, String>();
    
    // Initialize point group config
    static {
    	
		// Analog Group
    	cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "Average Line Voltage");
        cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "Last Control Reason");
    	cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "High Voltage");
    	cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "Low Voltage");
    	cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "Delta Voltage");
        cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "Line Voltage THD");
    	cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "Temperature");
    	cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "Neutral Current Sensor");
    	cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "Analog Input 1");
    	cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "RSSI");
    	cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "Control Ignored Reason");
        cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "Ignored Control Reason");
        cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "Serial Number");
    	cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "IP Address");
    	cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "UDP Port");
    	cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "Firmware Version");
        cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "Comms Loss Time");
        cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "Comms Retry Delay Time");
        cbcPointGroupConfig.add(CBCPointGroup.ANALOG, "Yukon Poll Time");
        
    	// Accumulator Group
    	cbcPointGroupConfig.add(CBCPointGroup.ACCUMULATOR, "Total Op Count");
    	cbcPointGroupConfig.add(CBCPointGroup.ACCUMULATOR, "UV Op Count");
    	cbcPointGroupConfig.add(CBCPointGroup.ACCUMULATOR, "OV Op Count");
    	
    	// Status Group
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Capacitor Bank State");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Control Mode");
        cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Auto Control Mode");
        cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Manual Control Mode");
        cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Remote Control Mode");
        cbcPointGroupConfig.add(CBCPointGroup.STATUS, "CVR Mode");
        cbcPointGroupConfig.add(CBCPointGroup.STATUS, "SCADA Override");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Re-Close Blocked");
        cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Reclose Block");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Last Control - Local");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Last Control - Remote");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Last Control - OVUV");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Last Control - Neutral Fault");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Last Control - Scheduled");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Last Control - Digital");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Last Control - Analog");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Last Control - Temperature");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Auto Volt Control");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Line Voltage High");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Line Voltage Low");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "OV Condition");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "UV Condition");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Voltage Delta Abnormal");
        cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Abnormal Delta Voltage");
        cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Operation Failed");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Op Failed - Neutral Current");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Neutral Current Fault");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Neutral Lockout");
        cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Relay Sense Failed");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Bad Relay");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Bad Active Close Relay");
        cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Bad Active Trip Relay");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Daily Max Ops");
        cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Max Operation Count");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Temp Alarm");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Temperature High");
        cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Temperature Low");       
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "DST Active");
    	cbcPointGroupConfig.add(CBCPointGroup.STATUS, "Control Ignored Indicator");
    	
    	// Configurable Parameters
    	cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Daily Control Limit");
        cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Close Op Count");
        cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Open Op Count");
    	cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "UV Threshold");
    	cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "OV Threshold");
    	cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Control OVUV Track Time");
    	cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Emergency OV Threshold");
    	cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Emergency UV Threshold");
    	cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Emergency OVUV Track Time");
        cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "CVR UV Threshold");
        cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "CVR OV Threshold");
        cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Comms Loss OV Threshold");
        cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Comms Loss UV Threshold");
    	cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Neutral Current Alarm Threshold");
    	cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Close Delay Time");
    	cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Auto Close Delay Time");
    	cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Manual Close Delay Time");
    	cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Open Delay Time");
    	cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Re-Close Delay Time");
    	cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Bank Control Time");
        cbcPointGroupConfig.add(CBCPointGroup.CONFIGURABLE_PARAMETERS, "Average Line Voltage Time");
    	
    }
    
    public Map<String, List<LitePoint>> getSortedCBCPointTimeStamps (Integer cbcId) {

        List<LitePoint> allPoints = pointDao.getLitePointsByPaObjectId(cbcId);
        
        Map<String,LitePoint> pointNameMap = new HashMap<String,LitePoint>();
        for (LitePoint point: allPoints) {
        	pointNameMap.put(point.getPointName(), point);
        }
        
        Map<String, List<LitePoint>> returnMap = new HashMap<String, List<LitePoint>>();

        // Add Analog group points
        List<LitePoint> analogList = new ArrayList<LitePoint>();
        for (String pointName : cbcPointGroupConfig.get(CBCPointGroup.ANALOG)) {
            LitePoint point = pointNameMap.get(pointName);
            if(point != null) {
            	pointNameMap.remove(pointName);
            	analogList.add(point);
            }
        }
        returnMap.put(CBCPointGroup.ANALOG.toString(), analogList);
        
        // Add Accumulator group points
        List<LitePoint> accumulatorList = new ArrayList<LitePoint>();
        for (String pointName : cbcPointGroupConfig.get(CBCPointGroup.ACCUMULATOR)) {
        	LitePoint point = pointNameMap.get(pointName);
        	if(point != null) {
        		pointNameMap.remove(pointName);
        		accumulatorList.add(point);
        	}
        }
        returnMap.put(CBCPointGroup.ACCUMULATOR.toString(), accumulatorList);

        // Add Status group points
        List<LitePoint> statusList = new ArrayList<LitePoint>();
        for (String pointName : cbcPointGroupConfig.get(CBCPointGroup.STATUS)) {
        	LitePoint point = pointNameMap.get(pointName);
        	if(point != null) {
        		pointNameMap.remove(pointName);
            	statusList.add(point);
        	}
        }
        returnMap.put(CBCPointGroup.STATUS.toString(), statusList);

        // Add Configurable Parameters group points
        List<LitePoint> configList = new ArrayList<LitePoint>();
        for (String pointName : cbcPointGroupConfig.get(CBCPointGroup.CONFIGURABLE_PARAMETERS)) {
        	LitePoint point = pointNameMap.get(pointName);
        	if(point != null) {
        		pointNameMap.remove(pointName);
            	configList.add(point);
        	}
        }
        returnMap.put(CBCPointGroup.CONFIGURABLE_PARAMETERS.toString(), configList);
        
        // Add all other points
        List<LitePoint> miscList = new ArrayList<LitePoint>();
        miscList.addAll(pointNameMap.values());
        returnMap.put(CBCPointGroup.MISC.toString(), miscList);

        return returnMap;
    }
    
    public List<LiteYukonPAObject> getAllSubsForUser(LiteYukonUser user) {
        List<LiteYukonPAObject> subList = new ArrayList<LiteYukonPAObject>(10);
        
        List<LiteYukonPAObject> temp = paoDao.getAllCapControlSubBuses();
        for (Iterator<LiteYukonPAObject> iter = temp.iterator(); iter.hasNext();) {
            LiteYukonPAObject element = iter.next();
            
            if(paoAuthorizationService.isAuthorized(user, Permission.PAO_VISIBLE, element )){
                subList.add(element);
            }
        }
        return subList;
    }

    public List<LitePoint> getPaoPoints(YukonPAObject pao) {
        return  pointDao.getLitePointsByPaObjectId(pao.getPAObjectID());
    }

    public Integer getParentForController(int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceId FROM Capbank WHERE ControlDeviceId").eq(id);
        Integer parentId = 0;
        try{
            parentId = yukonJdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException dae) {
            log.debug("Could not find parent for cbc:" + id);
        }
        return parentId;
    }

    public Integer getParentForPoint(int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectId FROM Point WHERE PointId").eq(id);
        Integer parentId = 0;
        try{
            parentId = yukonJdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException dae) {
            log.debug("Could not find parent for cbc:" + id);
        }
        return parentId;
    }
    
    public CapControlType getCapControlType(int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT type FROM YukonPAObject WHERE PAObjectID").eq(id);
        
    	String typeStr = yukonJdbcTemplate.queryForString(sql);
    	
    	return CapControlType.getCapControlType(typeStr);
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
        query.append("and p.pointtype = 'Status' "); 
        query.append("order by y.paoname ");
        
        List<OrphanCBC> cbcList = yukonJdbcTemplate.query(query, new YukonRowMapper<OrphanCBC>() {
            public OrphanCBC mapRow(YukonResultSet rs) throws SQLException {
                String deviceName = rs.getString("devicename");
                Integer pointId = rs.getInt("pointid");
                String pointName = rs.getString("pointname");
                OrphanCBC cbc = new OrphanCBC(deviceName, pointId, pointName);
                return cbc;
            }
        });
        return cbcList;
    }
    
    public List<LiteCapBankAdditional> getCapBankAdditional(List<Integer> deviceIds) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT ca.DeviceID, ca.DriveDirections, cbc.SERIALNUMBER"); 
    	sql.append("FROM CAPBANKADDITIONAL ca");
    	sql.append("JOIN CAPBANK bank on ca.DeviceID = bank.DEVICEID"); 
    	sql.append("left outer join DeviceCBC cbc on bank.CONTROLDEVICEID = cbc.deviceid");
    	
    	List<LiteCapBankAdditional> capbanks = Lists.newArrayList();
    	
    	if (deviceIds.size() > 0) {
    		sql.append("WHERE bank.DeviceID").in(deviceIds);
    	 	
    		capbanks = yukonJdbcTemplate.query(sql, new YukonRowMapper<LiteCapBankAdditional>() {
	            public LiteCapBankAdditional mapRow(YukonResultSet rs) throws SQLException {
	                int deviceId = rs.getInt("DeviceID");
	                String drivingDirections = rs.getString("DriveDirections");
	                Integer serialNumber = rs.getInt("SERIALNUMBER");
	                
	                LiteCapBankAdditional capBank = new LiteCapBankAdditional();
	                
	                capBank.setDeviceId(deviceId);
	                capBank.setDrivingDirections(drivingDirections);
	                capBank.setSerialNumber(serialNumber);
	                
	                return capBank;
	            }
	        });
    	
    	}
    	return capbanks;
    }
    
    @Override
    public List<CCEventLog> getEventsForPao (StreamableCapObject streamable, int prevDaysCount) {
        PaoType type = PaoType.getForDbString(streamable.getCcType());
        int id = streamable.getCcId();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT LogId, PointId, DateTime, SubId, FeederId, EventType, SeqId, Value, Text, Username");
        sql.append("FROM").append(CCEventLog.TABLE_NAME).append("WHERE");
        
        DateMidnight date = new DateMidnight(Instant.now().minus(Duration.standardDays(prevDaysCount)));
        
        if (type == PaoType.CAP_CONTROL_FEEDER) {
            sql.append("FeederId").eq(id);
        } else if (type == PaoType.CAP_CONTROL_SUBBUS) {
            sql.append("SubId").eq(id);
        } else if (type == PaoType.CAP_CONTROL_SUBSTATION) {
            sql.append("StationId").eq(id);
        } else if (type == PaoType.CAP_CONTROL_AREA) {
            sql.append("AreaId").eq(id);
        } else if (type == PaoType.CAP_CONTROL_SPECIAL_AREA) {
            sql.append("SPAreaId").eq(id);
        } else if (type == PaoType.CAPBANK) {
            sql.append("PointId").eq(id);
        }
        sql.append("AND DateTime").gte(date.toDate());
        
        sql.append("ORDER BY " + CCEventLog.COLUMNS [CCEventLog.COL_DATETIME] + " DESC");
        
        return yukonJdbcTemplate.query(sql, new YukonRowMapper<CCEventLog>() {
            public CCEventLog mapRow(YukonResultSet rs) throws SQLException {
                CCEventLog row = new CCEventLog(); 
                row.setLogId (rs.getLong("LogId"));
                row.setPointId(rs.getLong("PointId"));
                row.setDateTime( rs.getDate("DateTime"));
                row.setSubId(rs.getLong("SubId"));
                row.setFeederId(rs.getLong("FeederId"));
                row.setEventType(rs.getInt("EventType"));
                row.setSeqId(rs.getLong("SeqId"));
                row.setValue(rs.getLong("Value"));
                row.setText( rs.getString("Text"));
                row.setUserName( rs.getString("Username"));
                return row;                     
            }
        });
    }
    
}