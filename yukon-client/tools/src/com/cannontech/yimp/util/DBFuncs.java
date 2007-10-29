/*
 * Created on Feb 4, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yimp.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.device.MCT400SeriesBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.importer.ImportData;
import com.cannontech.database.db.importer.ImportFail;
import com.cannontech.database.db.importer.ImportPendingComm;
import com.cannontech.database.db.point.Point;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yimp.importer.BulkImporter;
import com.cannontech.yukon.IServerConnection;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DBFuncs {
    
    private static final RowMapper routeNameRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            String name = rs.getString("Name").trim();
            return name;
        };
    };
    
    private static final RowMapper routeIDRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            Integer routeID = rs.getInt("RouteID");
            return routeID;
        };
    };
	/*
	 * Grab a Route using a PAOName, returns one with an ID of -12 if unsuccessful
	 * This should be changed to use cache at a later time
	 */
	public static Integer getRouteFromName(String name) {
		Integer routeID = -12;
		
        try {
            String stmt = "SELECT PAOBJECTID FROM YUKONPAOBJECT WHERE PAONAME = '" 
                + name + "' AND PAOCLASS = 'ROUTE'";
            
            JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
            routeID = jdbcOps.queryForInt(stmt);
            return routeID;
        } catch (IncorrectResultSizeDataAccessException e) {
            return routeID;
        }
	}
	
	/*
	 * Grab an MCT410 using a PAOName, returns one with an ID of -12 if unsuccessful
	 * This should be changed to use cache at a later time
	 */
	public static MCT400SeriesBase get410FromTemplateName(String name) {
		MCT400SeriesBase template410 = new MCT400SeriesBase();
		template410.setDeviceID(-12);
        Integer id = null;
        
		String stmt = "SELECT PAOBJECTID FROM YUKONPAOBJECT WHERE PAONAME = '" 
			+ name + "' AND TYPE LIKE 'MCT-4%'";
        
        try {
            JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
            id = jdbcOps.queryForInt(stmt);
            if(id != null && id > 0) {
                template410.setDeviceID(id);
                template410 = (MCT400SeriesBase) Transaction.createTransaction(Transaction.RETRIEVE, template410).execute();
            }
        }
        catch( Exception e ) {
            template410.setDeviceID(new Integer(-12));
        }
    
		return template410;
	}
	
	public static boolean IsDuplicateName(String name) {
		String stmt = "SELECT PAOBJECTID FROM YUKONPAOBJECT WHERE PAONAME = '" 
			+ name + "' AND TYPE LIKE 'MCT-4%'";
		
		try {
		    Integer paoID = 0;
		    JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
		    paoID = jdbcOps.queryForInt(stmt);
            return paoID > 0;
        } 
        catch (IncorrectResultSizeDataAccessException e) {
            return false;
        }
	}
	/**
	 * returns the paobjectid of the device with address.  Else null
	 * @param address
	 * @return
	 */
	public static Integer getDeviceIDByAddress(String address) {
		Integer returnDeviceID = null;
		
		String stmt = "SELECT DEVICEID FROM " + DeviceCarrierSettings.TABLE_NAME + " WHERE ADDRESS = '" + address + "'";
		    
        try {
            JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
            returnDeviceID = jdbcOps.queryForInt(stmt);
        } 
        catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
        return returnDeviceID;
    }
	
	public static Vector getPointsForPAO( Integer paoID ) {
        List<LitePoint> points = DaoFactory.getPointDao().getLitePointsByPaObjectId(paoID);

		Vector daPoints = new Vector(points.size());
		
		PointBase pointBase = null;
		for (LitePoint point: points) {
			pointBase = (PointBase) LiteFactory.createDBPersistent(point);
			
			try {
				com.cannontech.database.Transaction t =
					com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, pointBase);
				t.execute();
				daPoints.addElement(pointBase);
			}
			catch (com.cannontech.database.TransactionException e) {
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
		}
			
		return daPoints;
	}
	
	public static boolean writeLastImportTime(Date lastImport) {
		String stmt = "UPDATE DYNAMICIMPORTSTATUS SET LASTIMPORTTIME = '" + lastImport.toString() + "' WHERE ENTRY = 'SYSTEMVALUE'";
		
		return executeSimpleImportStatusSQL(stmt);
	}
	
	public static boolean writeNextImportTime(Date nextImport, boolean currentlyRunning) {
		String next;
		if(currentlyRunning)
			next = "CURRENTLY RUNNING..";
		else
			next = nextImport.toString();

		String stmt = "UPDATE DYNAMICIMPORTSTATUS SET NEXTIMPORTTIME = '" + next + "' WHERE ENTRY = 'SYSTEMVALUE'";
        return executeSimpleImportStatusSQL(stmt);
	}
	
	public static boolean writeTotalSuccess(int success) {
		String stmt = "UPDATE DYNAMICIMPORTSTATUS SET TOTALSUCCESSES = " + success + " WHERE ENTRY = 'SYSTEMVALUE'";

        return executeSimpleImportStatusSQL(stmt);
	}
	
	public static boolean writeTotalAttempted(int attempts) {
		String stmt = "UPDATE DYNAMICIMPORTSTATUS SET TOTALATTEMPTS = " + attempts + " WHERE ENTRY = 'SYSTEMVALUE'";

        return executeSimpleImportStatusSQL(stmt);
	}
	
	public static void alreadyForcedImport() {
		String stmt = "UPDATE DYNAMICIMPORTSTATUS SET FORCEIMPORT = 'N' WHERE ENTRY = 'SYSTEMVALUE'";
	
        executeSimpleImportStatusSQL(stmt);
	}
	
	public static boolean forceImport() {
		String stmt = "UPDATE DYNAMICIMPORTSTATUS SET FORCEIMPORT = 'Y' WHERE ENTRY = 'SYSTEMVALUE'";
			
        return executeSimpleImportStatusSQL(stmt);
	}
	
	public static boolean isForcedImport() {
        String forceImport = "N";
		String stmt = "SELECT FORCEIMPORT FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'";
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        
        try {
            forceImport = (String) jdbcOps.queryForObject(stmt, String.class);
            return forceImport.compareTo("Y") == 0;
        }
        catch (DataAccessException e) {
            YukonLogManager.getLogger(BulkImporter.class).error(e);
            return false;
        }
	}
	
	/*
	 * If the servers receive a device or point DBChangeMsg with an id of zero, then
	 * they do a full reload of the database.
	 * This is a poor man's RELOAD_ALL DBChangeMsg.
	 */
	public static void generateBulkDBChangeMsg(int dbField, String objCategory, String objType, IServerConnection dispatchConnection  ) {
		DBChangeMsg chumpChange = new com.cannontech.message.dispatch.message.DBChangeMsg(
			0,
			dbField,
			objCategory,
			objType,
			DBChangeMsg.CHANGE_TYPE_ADD );
		
		dispatchConnection.write(chumpChange);
	}
	
	public static List<Integer> getRouteIDsFromSubstationName(String name) {
        String stmt = "SELECT ROUTEID FROM SUBSTATIONTOROUTEMAPPING WHERE SUBSTATIONID IN " +
                "(SELECT SUBSTATIONID FROM SUBSTATION WHERE SUBSTATIONNAME  = '" 
            + name + "') ORDER BY ORDERING";
        
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        List<Integer> routeIDs = 
            jdbcOps.query(stmt, routeIDRowMapper);
        
        return routeIDs;
    }
    
    public static List<String> getRouteNamesFromSubstationName(String subName) {
        String stmt = "SELECT PAONAME " + 
                                " FROM YUKONPAOBJECT PAO, SUBSTATION SUB, SUBSTATIONTOROUTEMAPPING MAP " + 
                                " WHERE SUB.SUBSTATIONID = MAP.SUBSTATIONID " + 
                                " AND SUB.SUBSTATIONNAME = '" + subName + "' " + 
                                " AND PAO.PAOBJECTID = MAP.ROUTEID ORDER BY MAP.ORDERING";
        
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        List<String> routeNames = 
            jdbcOps.query(stmt, routeNameRowMapper);
        
        return routeNames;
    }    
    
    public static boolean writePendingCommToFail(String failType, String errorMsg, Integer deviceID) {
        ImportPendingComm pc = new ImportPendingComm();
        pc.setPendingID(deviceID);
        try {
            pc = (ImportPendingComm)Transaction.createTransaction(Transaction.RETRIEVE, pc).execute();
            ImportFail failure = new ImportFail();
            failure.setAddress(pc.getAddress());
            failure.setName( pc.getName() );
            failure.setRouteName( pc.getRouteName() );
            failure.setMeterNumber( pc.getMeterNumber() );
            failure.setCollectionGrp( pc.getCollectionGrp() );
            failure.setAltGrp( pc.getAltGrp() );
            failure.setTemplateName( pc.getTemplateName() );
            failure.setErrorMsg( errorMsg );
            failure.setSubstationName( pc.getSubstationName() );
            failure.setDateTime(new Date());
            failure.setBillGrp(pc.getBillGrp());
            failure.setFailType( failType );
            Transaction.createTransaction(Transaction.INSERT, failure).execute();
            Transaction.createTransaction(Transaction.DELETE, pc).execute();
            return true;
        }
        catch( TransactionException e ) {
            return false;
        }
    }
    
    public static boolean removeFromPendingAndFailed(int devID) {
        ImportPendingComm pc = new ImportPendingComm();
        pc.setPendingID(devID);
        
        try {
            pc = (ImportPendingComm)Transaction.createTransaction(Transaction.RETRIEVE, pc).execute();
            ImportFail failure = new ImportFail();
            failure.setAddress(pc.getAddress());
            Transaction.createTransaction(Transaction.DELETE, pc).execute();
            Transaction.createTransaction(Transaction.DELETE, failure).execute();
            return true;
        }
        catch( TransactionException e ) {
            return false;
        }
    }
    
    public static boolean executeSimpleImportStatusSQL(String stmt) {
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        
        try {
            jdbcOps.execute(stmt);
        }
        catch (DataAccessException e) {
            YukonLogManager.getLogger(BulkImporter.class).error(e);
            return false;
        }

        return true;
    }
}