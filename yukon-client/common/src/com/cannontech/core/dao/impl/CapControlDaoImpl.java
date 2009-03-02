package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.capcontrol.CBCPointGroup;
import com.cannontech.capcontrol.LiteCapBankAdditional;
import com.cannontech.capcontrol.OrphanCBC;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.MapQueue;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.CapControlDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.data.pao.YukonPAObject;

public class CapControlDaoImpl  implements CapControlDao{

    private PointDao pointDao;
    private PaoDao paoDao;
    private AuthDao authDao;
    private JdbcTemplate jdbcOps;
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
    
    public Map<String, List<LitePoint>> getSortedCBCPointTimeStamps (Integer cbcId) {

        List<LitePoint> allPoints = pointDao.getLitePointsByPaObjectId(cbcId);
        
        Map<String,LitePoint> pointNameMap = new HashMap<String,LitePoint>();
        for (LitePoint point: allPoints) {
        	pointNameMap.put(point.getPointName(), point);
        }
        
        Map<String, List<LitePoint>> returnMap = new HashMap<String, List<LitePoint>>();

        // Add Analog group points
        List<LitePoint> analogList = new ArrayList<LitePoint>();
        for (String pointName : pointGroupConfig.get(CBCPointGroup.ANALOG)) {
            LitePoint point = pointNameMap.get(pointName);
            if(point != null) {
            	pointNameMap.remove(pointName);
            	analogList.add(point);
            }
        }
        returnMap.put(CBCPointGroup.ANALOG.toString(), analogList);
        
        // Add Accumulator group points
        List<LitePoint> accumulatorList = new ArrayList<LitePoint>();
        for (String pointName : pointGroupConfig.get(CBCPointGroup.ACCUMULATOR)) {
        	LitePoint point = pointNameMap.get(pointName);
        	if(point != null) {
        		pointNameMap.remove(pointName);
        		accumulatorList.add(point);
        	}
        }
        returnMap.put(CBCPointGroup.ACCUMULATOR.toString(), accumulatorList);

        // Add Status group points
        List<LitePoint> statusList = new ArrayList<LitePoint>();
        for (String pointName : pointGroupConfig.get(CBCPointGroup.STATUS)) {
        	LitePoint point = pointNameMap.get(pointName);
        	if(point != null) {
        		pointNameMap.remove(pointName);
            	statusList.add(point);
        	}
        }
        returnMap.put(CBCPointGroup.STATUS.toString(), statusList);

        // Add Configurable Parameters group points
        List<LitePoint> configList = new ArrayList<LitePoint>();
        for (String pointName : pointGroupConfig.get(CBCPointGroup.CONFIGURABLE_PARAMETERS)) {
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
	
	static public String convertNeutralCurrent(Double value) {        
		Integer pvalue = value.intValue();
        String neutralCurrent = "No";
        
        if ((pvalue & 0x08) == 0x08){
            neutralCurrent = "Yes";
        }
		
        return neutralCurrent;
	}
	
    static public String convertToOctalIp(Double value) {
    	
    	Long ipvalue = new Long(value.longValue());

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

    public void setJdbcOps(JdbcTemplate jdbcOps) {
        this.jdbcOps = jdbcOps;
    }

    public List<LitePoint> getPaoPoints(YukonPAObject pao) {
        return  pointDao.getLitePointsByPaObjectId(pao.getPAObjectID());
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
    
    public CapControlType getCapControlType(int id) {
        String sql = "SELECT type FROM YukonPAObject WHERE PAObjectID = ?";
        String typeStr = null;
        
    	typeStr = (String) jdbcOps.queryForObject(sql, new Integer[] {id}, String.class);
    	
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
    
    public List<LiteCapBankAdditional> getCapBankAdditional(List<Integer> deviceIds) {
  
    	final List<LiteCapBankAdditional> capbanks = new ArrayList<LiteCapBankAdditional>();
    	SqlStatementBuilder query = new SqlStatementBuilder("SELECT ca.DeviceID,ca.DriveDirections, " 
    			+ "cbc.SERIALNUMBER FROM CAPBANKADDITIONAL ca join CAPBANK bank on ca.DeviceID " 
    			+ "= bank.DEVICEID left outer join DeviceCBC cbc on bank.CONTROLDEVICEID = cbc.deviceid ");
    	
    	if( deviceIds.size() > 0) {
    		query.append(" WHERE bank.DeviceID IN ( ");
    		query.appendList(deviceIds);
    	 	query.append(")");
    	 	
	        jdbcOps.query(query.getSql(), new RowCallbackHandler() {
	            public void processRow(ResultSet rs) throws SQLException {
	                int deviceId = rs.getInt("DeviceID");
	                String drivingDirections = rs.getString("DriveDirections");
	                Integer serialNumber = rs.getInt("SERIALNUMBER");
	                
	                LiteCapBankAdditional capBank = new LiteCapBankAdditional();
	                
	                capBank.setDeviceId(deviceId);
	                capBank.setDrivingDirections(drivingDirections);
	                capBank.setSerialNumber(serialNumber);
	                
	                capbanks.add(capBank);
	            }
	        });
    	
    	}
    	return capbanks;
    }
}

