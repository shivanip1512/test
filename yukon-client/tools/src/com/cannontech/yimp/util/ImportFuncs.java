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
import java.util.GregorianCalendar;
import java.util.Calendar;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.importer.ImportData;
import com.cannontech.database.db.importer.ImportFail;
import com.cannontech.database.db.NestedDBPersistent;
import com.cannontech.common.util.LogWriter;

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
	public static Vector summonImps(Connection conn)
	{
		Vector hordeOfImps = new Vector();
		
		java.sql.PreparedStatement preparedStatement = null;
		java.sql.ResultSet rset = null;
		
		if( conn == null )
			throw new IllegalArgumentException("Database connection should not be (null)");
		
		try
		{
			String statement = ("SELECT * FROM " + ImportData.TABLE_NAME);

			preparedStatement = conn.prepareStatement( statement );
			rset = preparedStatement.executeQuery();

			while (rset.next() && rset != null)
			{
				String address = rset.getString(1);
				String name = rset.getString(2);
				String routeName = rset.getString(3);
				String meterNumber = rset.getString(4);
				String collectionGrp = rset.getString(5);
				String altGrp = rset.getString(6);
				String templateName = rset.getString(7);

				ImportData imp = new ImportData( address, name,
									routeName, meterNumber, collectionGrp, altGrp, templateName );

				hordeOfImps.add(imp);
				}
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
		
		return hordeOfImps;
	}
	
	/*
	 * This method will bring in the contents of the ImportFail
	 * table in the form of ImportFail objects
	 */
	public static Vector getAllFailed(Connection conn)
	{
		Vector failures = new Vector();
		
		java.sql.PreparedStatement preparedStatement = null;
		java.sql.ResultSet rset = null;
		
		if( conn == null )
			throw new IllegalArgumentException("Database connection should not be (null)");
		
		try
		{
			String statement = ("SELECT * FROM " + ImportFail.TABLE_NAME);

			preparedStatement = conn.prepareStatement( statement );
			rset = preparedStatement.executeQuery();

			while (rset.next() && rset != null)
			{
				String address = rset.getString(1);
				String name = rset.getString(2);
				String routeName = rset.getString(3);
				String meterNumber = rset.getString(4);
				String collectionGrp = rset.getString(5);
				String altGrp = rset.getString(6);
				String templateName = rset.getString(7);
				String errorMsg = rset.getString(8);
				Date dateTime = rset.getDate(9);

				ImportFail fail = new ImportFail( address, name,
									routeName, meterNumber, collectionGrp, altGrp,  
									templateName, errorMsg, dateTime );

				failures.add(fail);
			}
		}
			catch( java.sql.SQLException e )
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
	public static boolean flushImportTable(Connection conn)
	{
		if( conn == null )
			throw new IllegalArgumentException("Database connection should not be (null)");

		try
		{
			java.sql.Statement stat = conn.createStatement();

			stat.execute("DELETE FROM " + ImportData.TABLE_NAME);
		
			if (stat != null)
				stat.close();
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
	 * This method takes in a vector of ImportFail objects
	 * to be stored in the ImportFail table.
	 */
	public static void storeFailures(Vector imps, Connection conn) throws java.sql.SQLException
	{
		Vector previousFailures = getAllFailed(conn);
		
		//unleash the power of the NestedDBPersistent
		Vector failVector = CtiUtilities.NestedDBPersistentComparator(previousFailures, imps);

		//throw the ImportFails into the Db
		for( int i = 0; i < failVector.size(); i++ )
		{
			((NestedDBPersistent)failVector.elementAt(i)).setDbConnection(conn);
			((NestedDBPersistent)failVector.elementAt(i)).executeNestedOp();
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
			try
			{
				String dataDir = "../log/";
				java.io.File file = new java.io.File( dataDir );
				Date now = new Date();
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(now);
				file.mkdirs();
			
				String opName = "import" + cal.get(Calendar.DAY_OF_MONTH);
				//+ GregorianCalendar.getInstance().getTime()
				String filename = dataDir + opName  + ".log";
				java.io.FileOutputStream out = new java.io.FileOutputStream(filename, true);
				java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
				logger = new LogWriter(opName, LogWriter.DEBUG, writer);
					
				logger.log("Initializing " + opName, LogWriter.INFO );
				logger.log("Version: " + "(TEMPORARY RELEASE)" + ".", LogWriter.INFO );

			}
			catch( java.io.FileNotFoundException e )
			{
				e.printStackTrace();
			}
		}
		
		if(importStatus == 'F')
		{
			String output = "Failed import entry -- " +
						event + " -- SQL output: " + sqlEvent 
						+ " (Exception: " + exception; 
			logger.log( event, LogWriter.ERROR);
		}
		else if(importStatus == 'S')
		{
			String output = "Successful import entry: " +
						event + ", using SQL statement: " + sqlEvent; 
			logger.log( event, LogWriter.INFO);
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
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		if (logger != null)
		{
			try
			{
				String dataDir = "../log/";
				java.io.File file = new java.io.File( dataDir );
				String opName = "import" + cal.get(day);
				java.io.File file2 = new java.io.File( dataDir + opName);
				if(! file2.exists())
				{
					String filename = dataDir + opName  + ".log";
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
		}
		
		return logger;
	}
	
	
}
