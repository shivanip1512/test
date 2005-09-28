/*
 * Created on Jan 31, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yimp.util;

import java.util.Vector;
import java.sql.Connection;
import java.util.Date;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.importer.ImportData;
import com.cannontech.database.db.importer.ImportFail;
import com.cannontech.database.db.NestedDBPersistent;
import com.cannontech.common.util.LogWriter;
import com.cannontech.database.Transaction;
import com.cannontech.database.SqlStatement;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ImportFuncs 
{
	/*
	 * This method will bring in the contents of the ImportData
	 * table in the form of ImportData objects
	 */
	public static Vector summonImps()
	{
		Vector hordeOfImps = new Vector();
		
		SqlStatement stmt = new SqlStatement("SELECT * FROM " + ImportData.TABLE_NAME, "yukon");
		
		try
		{
			stmt.execute();
			
			if( stmt.getRowCount() > 0 )
			{
				for( int i = 0; i < stmt.getRowCount(); i++ )
				{
					String address = stmt.getRow(i)[0].toString();
					String name = stmt.getRow(i)[1].toString();
					String routeName = stmt.getRow(i)[2].toString();
					String meterNumber = stmt.getRow(i)[3].toString();
					String collectionGrp = stmt.getRow(i)[4].toString();
					String altGrp = stmt.getRow(i)[5].toString();
					String templateName = stmt.getRow(i)[6].toString();

					ImportData imp = new ImportData( address, name,
										routeName, meterNumber, collectionGrp, altGrp, templateName );

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
		
		SqlStatement stmt = new SqlStatement("SELECT * FROM " + ImportFail.TABLE_NAME + " ORDER BY DATETIME DESC", "yukon");
		
		try
		{
			stmt.execute();
			
			if( stmt.getRowCount() > 0 )
			{
				for( int i = 0; i < stmt.getRowCount(); i++ )
				{
					String address = stmt.getRow(i)[0].toString();
					String name = stmt.getRow(i)[1].toString();
					String routeName = stmt.getRow(i)[2].toString();
					String meterNumber = stmt.getRow(i)[3].toString();
					String collectionGrp = stmt.getRow(i)[4].toString();
					String altGrp = stmt.getRow(i)[5].toString();
					String templateName = stmt.getRow(i)[6].toString();
					String errorMsg = stmt.getRow(i)[7].toString();
					Date dateTime = java.sql.Date.valueOf(stmt.getRow(i)[8].toString());

					ImportFail fail = new ImportFail( address, name,
										routeName, meterNumber, collectionGrp, altGrp,  
										templateName, errorMsg, dateTime );

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
	 * This method is called to log every event; it dutifully logs each entry to an 
	 * external log file location.  The parameter importStatus is a char that indicates
	 * a 'F' for failure, 'S' for success, or 'N' for non-import status event.
	 * It then returns the logger for continued use.
	 */
	public static LogWriter writeToImportLog(LogWriter logger, char importStatus, String event, String sqlEvent, String exception)
	{
		if (logger == null)
		{
			logger = changeLog(logger);
		}
		
		if(importStatus == 'F')
		{
			String output = "Failed import entry -- " +
						event + " -- " + sqlEvent 
						+ " -- " + exception; 
			logger.log( output, LogWriter.ERROR);
		}
		else if(importStatus == 'S')
		{
			String output = "Successful import entry: " +
						event + "----" + sqlEvent; 
			logger.log( output, LogWriter.INFO);
		}
		else
		{
			logger.log( event, LogWriter.INFO);
		}
			
		return logger;
	}
	
	public static LogWriter changeLog(LogWriter logger)
	{
		Date now = new Date();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(now);
		int day = cal.get(GregorianCalendar.DAY_OF_MONTH);
		
		try
		{
			String dataDir = "../log/";
			String opName = "import" + day;
			String filename = dataDir + opName  + ".log";
			java.io.File file = new java.io.File( filename );
				
			//if this log file hasn't been modified today, assume it is a month old and start over.
			if(file.exists() && file.lastModified() < (now.getTime() - 86400000))
			{
				file.delete();
			}
				
			if(! file.exists() || logger == null)
			{
				java.io.FileOutputStream out = new java.io.FileOutputStream(filename, true);
				java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
				logger = new LogWriter(opName, LogWriter.DEBUG, writer);
				logger.log("NEW DAY OF THE MONTH, NEW LOG", LogWriter.INFO );
				logger.log("Initializing " + opName, LogWriter.INFO );
				logger.log("Version: " + "(TEMPORARY RELEASE)" + ".", LogWriter.INFO );
			}
		}
		catch( java.io.FileNotFoundException e )
		{
			e.printStackTrace();
		}
		
		return logger;
	}
	
	
}
