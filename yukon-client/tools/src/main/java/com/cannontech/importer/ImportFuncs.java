/*
 * Created on Jan 31, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.importer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.TransactionType;
import com.cannontech.database.db.importer.ImportData;
import com.cannontech.database.db.importer.ImportFail;
import com.cannontech.database.db.importer.ImportPendingComm;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ImportFuncs 
{
	public static final String FAIL_INVALID_DATA = "Invalid Data";
    public static final String FAIL_COMMUNICATION = "Communication Error";
    public static final String FAIL_DATABASE = "Database Problem";
    
    private static final RowMapper importDataRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            String address = rs.getString("Address").trim();
            String name = rs.getString("Name").trim();
            String routeName = rs.getString("RouteName").trim();
            String meterNumber = rs.getString("MeterNumber").trim();
            String collectionGrp = rs.getString("CollectionGrp").trim();
            String altGrp = rs.getString("AltGrp").trim();
            String templateName = rs.getString("TemplateName").trim();
            String billGrp = rs.getString("BillGrp").trim();
            String substationName = rs.getString("SubstationName").trim();

            return new ImportData( address, name,
                                routeName, meterNumber, collectionGrp, altGrp, 
                                templateName, billGrp, substationName );
        };
    };
    
    private static final RowMapper importFailRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            String address = rs.getString("Address").trim();
            String name = rs.getString("Name").trim();
            String routeName = rs.getString("RouteName").trim();
            String meterNumber = rs.getString("MeterNumber").trim();
            String collectionGrp = rs.getString("CollectionGrp").trim();
            String altGrp = rs.getString("AltGrp").trim();
            String templateName = rs.getString("TemplateName").trim();
            String errorMsg = rs.getString("ErrorMsg").trim();
            Date dateTime = rs.getTimestamp("DateTime");
            String billGrp = rs.getString("BillGrp").trim();
            String substationName = rs.getString("SubstationName").trim();
            String failType = rs.getString("FailType").trim();

            return new ImportFail( address, name, routeName, meterNumber, 
                                              collectionGrp, altGrp, templateName, 
                                              errorMsg, dateTime, billGrp, 
                                              substationName, failType);
        };
    };

    private static final RowMapper importPendingCommRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            Integer deviceID = rs.getInt("DeviceID");
            String address = rs.getString("Address").trim();
            String name = rs.getString("Name").trim();
            String routeName = rs.getString("RouteName").trim();
            String meterNumber = rs.getString("MeterNumber").trim();
            String collectionGrp = rs.getString("CollectionGrp").trim();
            String altGrp = rs.getString("AltGrp").trim();
            String templateName = rs.getString("TemplateName").trim();
            String billGrp = rs.getString("BillGrp").trim();
            String substationName = rs.getString("SubstationName").trim();

            return new ImportPendingComm( deviceID, address, name, routeName, 
                                                          meterNumber, collectionGrp, altGrp, 
                                                          templateName, billGrp, substationName);
        };
    };
    /*
	 * This method will bring in the contents of the ImportData
	 * table in the form of ImportData objects
	 */
	public static List<ImportData> summonImps()
	{
		String stmt = "SELECT ADDRESS, NAME, ROUTENAME, " + 
                " METERNUMBER, COLLECTIONGRP, ALTGRP, TEMPLATENAME, " + 
                " BILLGRP, SUBSTATIONNAME " + " FROM " + 
                ImportData.TABLE_NAME;
        
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        List<ImportData> hordeOfImps = 
            jdbcOps.query(stmt, importDataRowMapper);
        
		return hordeOfImps;
	}
	
	/*
	 * This method will bring in the contents of the ImportFail
	 * table in the form of ImportFail objects
	 */
	public static List<ImportFail> getAllFailed()
	{
		String stmt = "SELECT ADDRESS, NAME, ROUTENAME, " + 
                " METERNUMBER, COLLECTIONGRP, ALTGRP, TEMPLATENAME, " + 
                "ERRORMSG, DATETIME, BILLGRP, SUBSTATIONNAME, FAILTYPE " +
                " FROM " + ImportFail.TABLE_NAME + 
                " ORDER BY DATETIME DESC";
		
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        List<ImportFail> failures = 
            jdbcOps.query(stmt, importFailRowMapper);
        
        return failures;
	}
	
	/*
	 * This method is necessary to clean out the ImportData
	 * table since this table only holds unexecuted import entries.
	 * It then returns a boolean value indicating success or failure.
	 */
	public static boolean flushImportTable()
	{
		String stmt = "DELETE FROM " + ImportData.TABLE_NAME;
        
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
	/*
	* This method is necessary to clean out the ImportData
	* table since this table only holds unexecuted import entries.
	* This flush method takes in a vector of NestedDbPersistent ImportData
	* objects set to delete so only executed entries get removed.  This was 
	* done to protect the user from losing entries they had just entered.
	*/
   	public static void flushImportTable(List<ImportData> imps, Connection conn) throws java.sql.SQLException
   	{
		for( int i = 0; i < imps.size(); i++ ) {
			imps.get(i).setDbConnection(conn);
			imps.get(i).executeNestedOp();
		}
		
		return;
   	}
	
	/*
	 * This method takes in a vector of ImportData objects and
	 * a vector of ImportFail objects to be stored in the ImportFail table.
	 */
	public static void storeFailures(List<ImportData> impsSuccess, List<ImportFail> impsFailed, Connection conn) throws java.sql.SQLException
	{
		List<ImportFail> previousFailures = getAllFailed();
		
		//this is a little nasty looking, on account of 
		//how the ImportFail table is supposed to work.
		List<ImportFail> failVector = new ArrayList<ImportFail>();
		for(int j = 0; j < previousFailures.size(); j++) {
			String addy = previousFailures.get(j).getAddress();
			for(int x = 0; x < impsSuccess.size(); x++) {
				//this entry finally worked...remove from ImportFail table
				if(addy.compareTo(impsSuccess.get(x).getAddress()) == 0) {
					previousFailures.get(j).setOpCode(TransactionType.DELETE);
					failVector.add(previousFailures.get(j));
					continue;
				}
			}
			
			for(int y = 0; y < impsFailed.size(); y++) {
				//this entry failed again...set the current ImportFail to be updated
				if(addy.compareTo(impsFailed.get(y).getAddress()) == 0) {
					impsFailed.get(y).setOpCode(TransactionType.UPDATE);	
				}
			}
		}

		//throw the ImportFails into the Db
		for( int i = 0; i < failVector.size(); i++ ) {
			failVector.get(i).setDbConnection(conn);
			failVector.get(i).executeNestedOp();
		}
		for( int k = 0; k < impsFailed.size(); k++ ) {
			impsFailed.get(k).setDbConnection(conn);
			impsFailed.get(k).executeNestedOp();
		}
		
		return;
	}
	
    /*
     * This method will bring in the contents of the ImportPendingComm
     * table in the form of ImportPendingComm objects
     */
    public static List<ImportPendingComm> getAllPending() {
        String stmt = "SELECT DEVICEID, ADDRESS, NAME, ROUTENAME, " +
                " METERNUMBER, COLLECTIONGRP, ALTGRP, TEMPLATENAME, " +
                " BILLGRP, SUBSTATIONNAME " +
                " FROM " + ImportPendingComm.TABLE_NAME;
                
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        List<ImportPendingComm> pending = 
            jdbcOps.query(stmt, importPendingCommRowMapper);
        
        return pending;    
    }
    
    /*
     * This method will bring in the communication related contents of the ImportFail
     * table in the form of ImportFail objects
     */
    public static List<ImportFail> getAllCommunicationFailures() {
        String stmt = "SELECT ADDRESS, NAME, ROUTENAME, " + 
                " METERNUMBER, COLLECTIONGRP, ALTGRP, TEMPLATENAME, " + 
                "ERRORMSG, DATETIME, BILLGRP, SUBSTATIONNAME, FAILTYPE " +
                " FROM " + ImportFail.TABLE_NAME + " WHERE FAILTYPE = '" +
                ImportFuncs.FAIL_COMMUNICATION + "' ORDER BY DATETIME DESC";
                
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        List<ImportFail> failures = 
            jdbcOps.query(stmt, importFailRowMapper);
        
        return failures;
    }
    
    /*
     * This method will bring in the invalid data related contents of the ImportFail
     * table in the form of ImportFail objects
     */
    public static List<ImportFail> getAllDataFailures() {
        String stmt = "SELECT ADDRESS, NAME, ROUTENAME, " + 
                " METERNUMBER, COLLECTIONGRP, ALTGRP, TEMPLATENAME, " + 
                "ERRORMSG, DATETIME, BILLGRP, SUBSTATIONNAME, FAILTYPE " +
                " FROM " + ImportFail.TABLE_NAME + " WHERE NOT FAILTYPE = '" +
                ImportFuncs.FAIL_COMMUNICATION + "'" +
                " ORDER BY DATETIME DESC";
        
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        List<ImportFail> failures = 
            jdbcOps.query(stmt, importFailRowMapper);
        
        return failures;
    }
}
