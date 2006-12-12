/*
 * Created on Jan 31, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yimp.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.LogWriter;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.db.NestedDBPersistent;
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
    
    /*
	 * This method will bring in the contents of the ImportData
	 * table in the form of ImportData objects
	 */
	public static Vector summonImps()
	{
		Vector hordeOfImps = new Vector();
		
		SqlStatement stmt = new SqlStatement("SELECT ADDRESS, NAME, ROUTENAME, " +
                                             " METERNUMBER, COLLECTIONGRP, ALTGRP, TEMPLATENAME, " +
                                             " BILLGRP, SUBSTATIONNAME " +
                                             " FROM " + ImportData.TABLE_NAME, CtiUtilities.getDatabaseAlias());
		
		try
		{
			stmt.execute();
			
			if( stmt.getRowCount() > 0 )
			{
				for( int i = 0; i < stmt.getRowCount(); i++ )
				{
					String address = stmt.getRow(i)[0].toString().trim();
					String name = stmt.getRow(i)[1].toString().trim();
					String routeName = stmt.getRow(i)[2].toString().trim();
					String meterNumber = stmt.getRow(i)[3].toString().trim();
					String collectionGrp = stmt.getRow(i)[4].toString().trim();
					String altGrp = stmt.getRow(i)[5].toString().trim();
					String templateName = stmt.getRow(i)[6].toString().trim();
                    String billGrp = stmt.getRow(i)[7].toString().trim();
                    String substationName = stmt.getRow(i)[8].toString().trim();

					ImportData imp = new ImportData( address, name,
										routeName, meterNumber, collectionGrp, altGrp, 
                                        templateName, billGrp, substationName );

					hordeOfImps.add(imp);
				}
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return hordeOfImps;
	}
	
	/*
	 * This method will bring in the contents of the ImportFail
	 * table in the form of ImportFail objects
	 */
	public static Vector getAllFailed()
	{
		Vector failures = new Vector();
		
		SqlStatement stmt = new SqlStatement("SELECT ADDRESS, NAME, ROUTENAME, " + 
                                             " METERNUMBER, COLLECTIONGRP, ALTGRP, TEMPLATENAME, " + 
                                             "ERRORMSG, DATETIME, BILLGRP, SUBSTATIONNAME, FAILTYPE " +
                                             " FROM " + ImportFail.TABLE_NAME + 
                                             " ORDER BY DATETIME DESC", CtiUtilities.getDatabaseAlias());
		
		try
		{
			stmt.execute();
			
			if( stmt.getRowCount() > 0 )
			{
				for( int i = 0; i < stmt.getRowCount(); i++ )
				{
					String address = stmt.getRow(i)[0].toString().trim();
					String name = stmt.getRow(i)[1].toString().trim();
					String routeName = stmt.getRow(i)[2].toString().trim();
					String meterNumber = stmt.getRow(i)[3].toString().trim();
					String collectionGrp = stmt.getRow(i)[4].toString().trim();
					String altGrp = stmt.getRow(i)[5].toString().trim();
					String templateName = stmt.getRow(i)[6].toString().trim();
					String errorMsg = stmt.getRow(i)[7].toString().trim();
					Date dateTime = new Date(((java.sql.Timestamp)stmt.getRow(i)[8]).getTime());
                    String billGrp = stmt.getRow(i)[9].toString().trim();
                    String substationName = stmt.getRow(i)[10].toString().trim();
                    String failType = stmt.getRow(i)[11].toString().trim();

					ImportFail fail = new ImportFail( address, name, routeName, meterNumber, 
                                                      collectionGrp, altGrp, templateName, 
                                                      errorMsg, dateTime, billGrp, 
                                                      substationName, failType);

					failures.add(fail);
				}
			}
		}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		
		return failures;
	}
	
	/*
	 * This method is necessary to clean out the ImportData
	 * table since this table only holds unexecuted import entries.
	 * It then returns a boolean value indicating success or failure.
	 */
	public static boolean flushImportTable()
	{
		SqlStatement stmt = new SqlStatement("DELETE FROM " + ImportData.TABLE_NAME, "yukon");
		
		try
		{
			stmt.execute();
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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
   	public static void flushImportTable(Vector imps, Connection conn) throws java.sql.SQLException
   	{
		for( int i = 0; i < imps.size(); i++ )
		{
			((NestedDBPersistent)imps.elementAt(i)).setDbConnection(conn);
			((NestedDBPersistent)imps.elementAt(i)).executeNestedOp();
		}
		
		return;
   	}
	
	/*
	 * This performs the actual import, taking in a vector
	 * filled with ImportData objects and converting them to
	 * objects of the specified classtype, which it returns.
	 * This method also will call logging methods for those
	 * that failed.
	 * 
	 * Holding off on the classtype...that functionality can be
	 * added later.  For now, default to MCT410.
	 */
	/*public void runImport(Vector imps)
	{
		//This is done in the actual importer class
	}*/
	
	/*
	 * This method takes in a vector of ImportData objects and
	 * a vector of ImportFail objects to be stored in the ImportFail table.
	 */
	public static void storeFailures(Vector impsSuccess, Vector impsFailed, Connection conn) throws java.sql.SQLException
	{
		Vector previousFailures = getAllFailed();
		
		//this is a little nasty looking, on account of 
		//how the ImportFail table is supposed to work.
		Vector failVector = new Vector();
		for(int j = 0; j < previousFailures.size(); j++)
		{
			String addy = ((ImportFail)previousFailures.elementAt(j)).getAddress();
			for(int x = 0; x < impsSuccess.size(); x++)
			{
				//this entry finally worked...remove from ImportFail table
				if(addy.compareTo(((ImportData)impsSuccess.elementAt(x)).getAddress()) == 0)
				{
					((NestedDBPersistent)previousFailures.elementAt(j)).setOpCode(Transaction.DELETE);
					failVector.addElement(previousFailures.elementAt(j));
					continue;
				}
			}
			
			for(int y = 0; y < impsFailed.size(); y++)
			{
				//this entry failed again...set the current ImportFail to be updated
				if(addy.compareTo(((ImportFail)impsFailed.elementAt(y)).getAddress()) == 0)
				{
					((NestedDBPersistent)impsFailed.elementAt(y)).setOpCode(Transaction.UPDATE);	
				}
			}
			
		}

		//throw the ImportFails into the Db
		for( int i = 0; i < failVector.size(); i++ )
		{
			((NestedDBPersistent)failVector.elementAt(i)).setDbConnection(conn);
			((NestedDBPersistent)failVector.elementAt(i)).executeNestedOp();
		}
		for( int k = 0; k < impsFailed.size(); k++ )
		{
			((NestedDBPersistent)impsFailed.elementAt(k)).setDbConnection(conn);
			((NestedDBPersistent)impsFailed.elementAt(k)).executeNestedOp();
		}
		
		return;
	}
	
    /*
     * This method will bring in the contents of the ImportPendingComm
     * table in the form of ImportPendingComm objects
     */
    public static List getAllPending() {
        List pending = new ArrayList();
        
        SqlStatement stmt = new SqlStatement("SELECT DEVICEID, ADDRESS, NAME, ROUTENAME, " +
                                             " METERNUMBER, COLLECTIONGRP, ALTGRP, TEMPLATENAME, " +
                                             " BILLGRP, SUBSTATIONNAME " +
                                             " FROM " + ImportPendingComm.TABLE_NAME, CtiUtilities.getDatabaseAlias());
        try {
            stmt.execute();
            
            if( stmt.getRowCount() > 0 ) {
                for( int i = 0; i < stmt.getRowCount(); i++ ) {
                    Integer deviceID = new Integer(((java.math.BigDecimal) stmt.getRow(i)[0]).intValue());
                    String address = stmt.getRow(i)[1].toString().trim();
                    String name = stmt.getRow(i)[2].toString().trim();
                    String routeName = stmt.getRow(i)[3].toString().trim();
                    String meterNumber = stmt.getRow(i)[4].toString().trim();
                    String collectionGrp = stmt.getRow(i)[5].toString().trim();
                    String altGrp = stmt.getRow(i)[6].toString().trim();
                    String templateName = stmt.getRow(i)[7].toString().trim();
                    String billGrp = stmt.getRow(i)[8].toString().trim();
                    String substationName = stmt.getRow(i)[9].toString().trim();

                    ImportPendingComm pc = new ImportPendingComm( deviceID, address, name, routeName, 
                                                      meterNumber, collectionGrp, altGrp, 
                                                      templateName, billGrp, substationName);
                    pending.add(pc);
                }
            }
        }
            catch( Exception e ) {
                e.printStackTrace();
            }
        
        return pending;
    }
    
    /*
     * This method will bring in the communication related contents of the ImportFail
     * table in the form of ImportFail objects
     */
    public static List getAllCommunicationFailures() {
        List failures = new ArrayList();
        
        SqlStatement stmt = new SqlStatement("SELECT ADDRESS, NAME, ROUTENAME, " + 
                                             " METERNUMBER, COLLECTIONGRP, ALTGRP, TEMPLATENAME, " + 
                                             "ERRORMSG, DATETIME, BILLGRP, SUBSTATIONNAME, FAILTYPE " +
                                             " FROM " + ImportFail.TABLE_NAME + " WHERE FAILTYPE = '" +
                                             ImportFuncs.FAIL_COMMUNICATION + "' ORDER BY DATETIME DESC", CtiUtilities.getDatabaseAlias());
        
        try {
            stmt.execute();
            
            if( stmt.getRowCount() > 0 ) {
                for( int i = 0; i < stmt.getRowCount(); i++ ) {
                    String address = stmt.getRow(i)[0].toString().trim();
                    String name = stmt.getRow(i)[1].toString().trim();
                    String routeName = stmt.getRow(i)[2].toString().trim();
                    String meterNumber = stmt.getRow(i)[3].toString().trim();
                    String collectionGrp = stmt.getRow(i)[4].toString().trim();
                    String altGrp = stmt.getRow(i)[5].toString().trim();
                    String templateName = stmt.getRow(i)[6].toString().trim();
                    String errorMsg = stmt.getRow(i)[7].toString().trim();
                    Date dateTime = new Date(((java.sql.Timestamp)stmt.getRow(i)[8]).getTime());
                    String billGrp = stmt.getRow(i)[9].toString().trim();
                    String substationName = stmt.getRow(i)[10].toString().trim();
                    String failType = stmt.getRow(i)[11].toString().trim();

                    ImportFail fail = new ImportFail( address, name, routeName, meterNumber, 
                                                      collectionGrp, altGrp, templateName, 
                                                      errorMsg, dateTime, billGrp, 
                                                      substationName, failType);

                    failures.add(fail);
                }
            }
        }
            catch( Exception e ) {
                e.printStackTrace();
            }
        
        return failures;
    }
    
    public static List getAllDataFailures() {
        List failures = new ArrayList();
        
        SqlStatement stmt = new SqlStatement("SELECT ADDRESS, NAME, ROUTENAME, " + 
                                             " METERNUMBER, COLLECTIONGRP, ALTGRP, TEMPLATENAME, " + 
                                             "ERRORMSG, DATETIME, BILLGRP, SUBSTATIONNAME, FAILTYPE " +
                                             " FROM " + ImportFail.TABLE_NAME + " WHERE NOT FAILTYPE = '" +
                                             ImportFuncs.FAIL_COMMUNICATION + "'" +
                                             " ORDER BY DATETIME DESC", CtiUtilities.getDatabaseAlias());
        
        try {
            stmt.execute();
            
            if( stmt.getRowCount() > 0 ) {
                for( int i = 0; i < stmt.getRowCount(); i++ ) {
                    String address = stmt.getRow(i)[0].toString().trim();
                    String name = stmt.getRow(i)[1].toString().trim();
                    String routeName = stmt.getRow(i)[2].toString().trim();
                    String meterNumber = stmt.getRow(i)[3].toString().trim();
                    String collectionGrp = stmt.getRow(i)[4].toString().trim();
                    String altGrp = stmt.getRow(i)[5].toString().trim();
                    String templateName = stmt.getRow(i)[6].toString().trim();
                    String errorMsg = stmt.getRow(i)[7].toString().trim();
                    Date dateTime = new Date(((java.sql.Timestamp)stmt.getRow(i)[8]).getTime());
                    String billGrp = stmt.getRow(i)[9].toString().trim();
                    String substationName = stmt.getRow(i)[10].toString().trim();
                    String failType = stmt.getRow(i)[11].toString().trim();

                    ImportFail fail = new ImportFail( address, name, routeName, meterNumber, 
                                                      collectionGrp, altGrp, templateName, 
                                                      errorMsg, dateTime, billGrp, 
                                                      substationName, failType);

                    failures.add(fail);
                }
            }
        }
            catch( Exception e ) {
                e.printStackTrace();
            }
        
        return failures;
    }
}
