/*
 * Created on Feb 2, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yimp.importer;


import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.HashMap;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.LogWriter;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.yimp.util.ImportFuncs;
import com.cannontech.yimp.util.DBFuncs;
import com.cannontech.database.db.importer.ImportFail;
import com.cannontech.database.db.importer.ImportData;
import com.cannontech.database.data.device.MCT410IL;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.roles.yukon.SystemRole;
import java.sql.Connection;
import com.cannontech.database.Transaction;
import java.util.Date;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class BulkImporter410 
{
	private Thread starter = null;
	
	//is this necessary?
	private HashMap all410Imports;
	private HashMap allFailures;

	private GregorianCalendar nextImportTime = null;
	private static GregorianCalendar lastImportTime = null;

	public static boolean isService = true;
	public static Thread sleepThread = null;
	private static LogWriter logger = null;
	
	//5 minute interval for import attempts
	public static final int IMPORT_INTERVAL = 300;
	

public BulkImporter410() {
	super();
}

/**
 * Insert the method's description here.
 * Creation date: (02/2/2005 7:27:20 PM)
 */
public void figureNextImportTime()
{
	if( this.nextImportTime == null )
	{
		this.nextImportTime = new GregorianCalendar();
	}
	else if(DBFuncs.isForcedImport())
	{
		this.nextImportTime.setTime(new Date());
		DBFuncs.alreadyForcedImport();
	}
	else
	{
		GregorianCalendar tempImp = new GregorianCalendar();
		long nowInMilliSeconds = tempImp.getTime().getTime();
		long aggIntInMilliSeconds = getImportInterval().longValue() * 1000;
		long tempSeconds = (nowInMilliSeconds-(nowInMilliSeconds%aggIntInMilliSeconds))+aggIntInMilliSeconds;

		/* if it hasn't been at least one full import interval since we last did an
			 import, wait until next scheduled import time */
		if( tempSeconds < (this.nextImportTime.getTime().getTime()+aggIntInMilliSeconds) )
		{
			tempSeconds += aggIntInMilliSeconds;
		}
		else
		{
			this.nextImportTime = new GregorianCalendar();
			this.nextImportTime.setTime(new java.util.Date(tempSeconds));
		}
	}
	logger = ImportFuncs.writeToImportLog(logger, 'N', " ... Next Import Data Event to occur at: " + nextImportTime.getTime(), "", "");
	CTILogger.info(" ... Import Data Event to occur at: " + nextImportTime.getTime());
	DBFuncs.writeNextImportTime(this.nextImportTime.getTime());
}

/**
 * Insert the method's description here.
 * Creation date: (02/2/2005 7:27:20 PM)
 */
public java.lang.Integer getImportInterval()
{
	logger = ImportFuncs.writeToImportLog(logger, 'N', " Import attempt interval = " + IMPORT_INTERVAL + " seconds.", "", "");
	CTILogger.info("Import attempt interval is " + IMPORT_INTERVAL + " seconds.");

	return new Integer(IMPORT_INTERVAL);
}
/**
 * Insert the method's description here.
 * Creation date: (02/2/2005 7:27:20 PM)
 */
/*
//this is a problem...I am going to need last import time
//instead of last failure, and I am not keeping track of it
public static GregorianCalendar getLastImportTimeStamp()
{
	if(lastImportTime == null)
	{
		java.sql.PreparedStatement preparedStatement = null;
		java.sql.Connection conn = null;
		java.sql.ResultSet rset = null;
		
		try
		{
			
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			//at least get the timestamp of the last failure...not great, but better than just current time
			preparedStatement = conn.prepareStatement("SELECT MAX(DATETIME) FROM " + ImportFail.TABLE_NAME);
			rset = preparedStatement.executeQuery();
		
			if( rset == null)
			{
				lastImportTime = new GregorianCalendar();

				logger = ImportFuncs.writeToImportLog(logger, 'N', "UNABLE TO DETERMINE TIMESTAMP OF LAST IMPORT, USING CURRENT TIME", "", "");
				CTILogger.info("UNABLE TO DETERMINE TIMESTAMP OF LAST IMPORT, USING CURRENT TIME");
			}
		
			java.sql.Timestamp tempTimestamp = null;
			while (rset.next())
			{
				tempTimestamp = rset.getTimestamp(1);
				if( tempTimestamp != null )
				{
					lastImportTime.setTime(tempTimestamp);
					logger = ImportFuncs.writeToImportLog(logger, 'N', "UNABLE TO DETERMINE TIMESTAMP OF LAST IMPORT, USING LAST RECORDED FAILURE TIME", "", "");
					CTILogger.info("UNABLE TO DETERMINE TIMESTAMP OF LAST IMPORT, USING LAST RECORDED FAILURE TIME");
				
				}
			}
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
			logger = ImportFuncs.writeToImportLog(logger, 'N', "DATABASE PROBLEMS: " + e.toString(), "", "");
		}
		finally
		{
			try
			{
				if( preparedStatement != null )
					preparedStatement.close();			
				if (rset != null)
					rset.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				logger = ImportFuncs.writeToImportLog(logger, 'N', "UNABLE TO DETERMINE TIMESTAMP OF LAST IMPORT", "", "");
				CTILogger.info("UNABLE TO DETERMINE TIMESTAMP OF LAST IMPORT");
				e.printStackTrace();
			}
		}
	}
	
	return lastImportTime;
}
*/
/**
 * Insert the method's description here.
 * Creation date: (02/2/2005 7:27:20 PM)
 */
public GregorianCalendar getNextImportTime()
{
	return this.nextImportTime;
}

public void start()
{
	Runnable runner = new Runnable()
	{
		public void run()
		{
		
			CTILogger.info("410 Importer Version: " + "TEMPORARY RELEASE" + " starting.");
			logger = ImportFuncs.writeToImportLog(logger, 'N', "410 Importer (Version: " + "TEMPORARY RELEASE" + ") starting.", "", "");
			
			do
			{
				logger = ImportFuncs.changeLog(logger);
				figureNextImportTime();
			
				CTILogger.info("410 Importer holding until next import.");
				logger = ImportFuncs.writeToImportLog(logger, 'N', "410 Importer holding until next import.", "", "");
		
				sleepThread = new Thread();
		
				java.util.Date now = null;
				now = new java.util.Date();
				
				if( getNextImportTime().getTime().compareTo(now) <= 0 )
				{
					CTILogger.info("Starting import process.");
					logger = ImportFuncs.writeToImportLog(logger, 'N', "Starting import process.", "", "");
					
					Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
					Vector importEntries = ImportFuncs.summonImps(conn);
					
					//if no importEntries, report this and go back to waiting
					if(importEntries.size() < 1)
					{
						CTILogger.info("ImportData table is empty.  No new 410s to import.");
						logger = ImportFuncs.writeToImportLog(logger, 'N', "ImportData table is empty.  No new 410s to import.", "", "");
						figureNextImportTime();
					}
					else
						//go go go, import away!
						runImport(importEntries, conn);
					
					/*
					 * This was dangerous...users could easily
					 * lose data if they wrote new entries to 
					 * the ImportData table DURING an import run.
					//scrub out the ImportData table
					ImportFuncs.flushImportTable(conn);
					*/
										
					// Clear out the lists???
					
					try
					{
						if( conn != null )
							conn.close();
					}
					catch( java.sql.SQLException e )
					{
						e.printStackTrace();
					}
				}
				
				try
				{
					System.gc();
					sleepThread.sleep(30000);
				}
				catch (InterruptedException ie)
				{
					CTILogger.info("Exiting the yimp unexpectedly...sleep failed!!!");
					logger = ImportFuncs.writeToImportLog(logger, 'N', "Exiting the Imp unexpectedly...sleep failed!!!" + ie.toString(), "", "");
						
					break;
				}
			
			} while (isService);

			CTILogger.info("Import operation complete.");
			logger = ImportFuncs.writeToImportLog(logger, 'N', "Import service stopping completely.", "", "");
			
			logger.getPrintWriter().close();
			logger = null;

			//be sure the runner thread is NULL
			starter = null;		
		}
	};

	if( starter == null )
	{
		starter = new Thread( runner, "Importer" );
		starter.start();
	}

}

/*
	 * This performs the actual import, taking in a vector
	 * filled with ImportData objects and converting them to 410s 
	 * which it writes to the database.
	 * This method also will call logging methods for those
	 * that failed.
	 */
public void runImport(Vector imps, Connection conn)
{
	ImportData currentEntry = null;
	ImportFail currentFailure = null;
	MCT410IL current410 = null;
	MCT410IL template410 = null;
	Vector failures = new Vector();
	boolean badEntry = false;
	int successCounter = 0;
	
	int ids[] = DBFuncs.getNextPAObjectID(imps.size());
	
	for(int j = 0; j < imps.size(); j++)
	{
		currentEntry = (ImportData)imps.elementAt(j);
	
		//mark entry for deletion
		((ImportData)imps.elementAt(j)).setOpCode(Transaction.DELETE);
		
		Integer deviceID = new Integer(ids[j]);
		String name = currentEntry.getName();
		String address = currentEntry.getAddress();
		String routeName = currentEntry.getRouteName();
		Integer routeID = new Integer(-12);
		String meterNumber = currentEntry.getMeterNumber();
		String collectionGrp = currentEntry.getCollectionGrp();
		String altGrp = currentEntry.getAltGrp();
		String templateName = currentEntry.getTemplateName();
		
		//validation
		StringBuffer errorMsg = new StringBuffer("Failed due to: ");
		badEntry = false;
		
		if(name.length() < 1 || name.length() > 60)
		{
			CTILogger.info("Import entry with address " + address + " has a name with an improper length.");
			logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with address " + address + " has a name with an improper length.", "", "");
			badEntry = true;
			errorMsg.append("improper name length; ");			
		}
		if(name.indexOf('/') != -1 || name.indexOf(',') != -1 || name.indexOf('/') != -1 || name.indexOf('/') != -1)
		{
			CTILogger.info("Import entry with address " + address + " has a name that uses invalid characters.");
			logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with address " + address + " has a name that uses invalid characters.", "", "");
			badEntry = true;
			errorMsg.append("invalid name chars; ");			
		}
		if(new Integer(address).intValue() < 1000000 || new Integer(address).intValue() > 2796201)
		{
			CTILogger.info("Import entry with name " + name + " has an incorrect MCT410 address.");
			logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " has an incorrect MCT410 address.", "", "");
			badEntry = true;
			errorMsg.append("address out of MCT410 range; ");	
		}
		if(meterNumber.length() < 1)
		{
			CTILogger.info("Import entry with name " + name + " has no meter number.");
			logger = logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " has no meter number.", "", "");
			badEntry = true;
			errorMsg.append("has no meter number specified; ");	
		}
		if(collectionGrp.length() < 1)
		{
			CTILogger.info("Import entry with name " + name + " has no collection group.");
			logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " has no collection group.", "", "");
			badEntry = true;
			errorMsg.append("has no collection group specified; ");	
		}
		if(altGrp.length() < 1)
		{
			CTILogger.info("Import entry with name " + name + " has no alternate group.");
			logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " has no alternate group.", "", "");
			badEntry = true;
			errorMsg.append("has no alternate group specified; ");	
		}
		if(routeName.length() < 1)
		{
			CTILogger.info("Import entry with name " + name + " has no specified route.");
			logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " has no specified route.", "", "");
			badEntry = true;
			errorMsg.append("has no route specified; ");	
		}
		else
		{
			routeID = DBFuncs.getRouteFromName(routeName, conn);
			if(routeID.intValue() == -12)
			{
				CTILogger.info("Import entry with name " + name + " specifies a route not in the Yukon database.");
				logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " specifies a route not in the Yukon database.", "", "");
				badEntry = true;
				errorMsg.append("has an unknown route; ");
			}
		}
		if(templateName.length() < 1)
		{
			CTILogger.info("Import entry with name " + name + " has no specified 410 template.");
			logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " has no specified 410 template.", "", "");
			badEntry = true;
			errorMsg.append("has no 410 template specified; ");	
		}
		else
		{
			template410 = DBFuncs.get410FromTemplateName(templateName, conn);
			if(template410.getDevice().getDeviceID().intValue() == -12)
			{
				CTILogger.info("Import entry with name " + name + " specifies a template MCT410 not in the Yukon database.");
				logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " specifies a template MCT410 not in the Yukon database.", "", "");
				badEntry = true;
				errorMsg.append("has an unknown MCT410 template; ");
			}
		}
		
		//failure handling
		if(badEntry)
		{
			GregorianCalendar now = new GregorianCalendar();
			currentFailure = new ImportFail(address, name, routeName, meterNumber, collectionGrp, altGrp, templateName, errorMsg.toString(), now.getTime());
			failures.addElement(currentFailure);
		}
		
		//actual 410 creation
		else
		{
			GregorianCalendar now = new GregorianCalendar();
			lastImportTime = now;
			Integer templateID = template410.getPAObjectID();
			current410 = template410;
			current410.setPAOName(name);
			current410.setDeviceID(deviceID);
			current410.assignAddress(new Integer(address));
			current410.getDeviceMeterGroup().setMeterNumber(meterNumber);
			current410.getDeviceMeterGroup().setCollectionGroup(collectionGrp);
			current410.getDeviceMeterGroup().setTestCollectionGroup(altGrp);
			current410.getDeviceRoutes().setRouteID(routeID);
			com.cannontech.database.data.multi.MultiDBPersistent objectsToAdd = new com.cannontech.database.data.multi.MultiDBPersistent();
			objectsToAdd.getDBPersistentVector().add(current410);
			
			//grab the points we need off the template
			Vector points = DBFuncs.getPointsForPAO(templateID);
			boolean hasPoints = false;
			for (int i = 0; i < points.size(); i++)
			{
				((com.cannontech.database.data.point.PointBase) points.get(i)).setPointID(new Integer(DBFuncs.getNextPointID() + i));
				((com.cannontech.database.data.point.PointBase) points.get(i)).getPoint().setPaoID(deviceID);
				objectsToAdd.getDBPersistentVector().add(points.get(i));
				hasPoints = true;
			}
			
			try
			{
				if(hasPoints)
				{				
					objectsToAdd.setDbConnection(conn);
					objectsToAdd.add();
				}
				else
				{
					current410.setDbConnection(conn);
					current410.add();
				}
				
				successCounter++;
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
				StringBuffer tempErrorMsg = new StringBuffer(e.toString());
				currentFailure = new ImportFail(address, name, routeName, meterNumber, collectionGrp, altGrp, templateName, tempErrorMsg.toString(), now.getTime());
				failures.addElement(currentFailure);
				logger = ImportFuncs.writeToImportLog(logger, 'F', "MCT410 with name " + name + "failed on INSERT into database.", e.toString(), e.toString());
			}
		}
	}
	
	//remove executed ImportData entries
	try
	{
		ImportFuncs.flushImportTable(imps, conn);
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
		logger = ImportFuncs.writeToImportLog(logger, 'F', "PREVIOUSLY USED IMPORT ENTRIES NOT REMOVED: THEY WOULD NOT DELETE!!!", e.toString(), e.toString());
	}
	
	//store failures
	try
	{
		ImportFuncs.storeFailures(failures, conn);
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
		logger = ImportFuncs.writeToImportLog(logger, 'F', "FAILURES NOT RECORDED: THEY WOULD NOT INSERT!!!", e.toString(), e.toString());
	}
	
	DBFuncs.writeTotalSuccess(successCounter);
	DBFuncs.writeTotalAttempted(imps.size());
	Date now = new Date();
	DBFuncs.writeLastImportTime(now);
	
}

/** 
 * Stop us
 */
public void stop()
{
	try
	{
		Thread t = starter;
		starter = null;
		t.interrupt();
	}
	catch (Exception e)
	{}
}

public boolean isRunning()
{
	return starter != null;
}

/**
 * Starts the application.
 */
public static void main(java.lang.String[] args)
{
	ClientSession session = ClientSession.getInstance(); 
	if(!session.establishSession()){
		System.exit(-1);			
	}
	  	
	if(session == null) 		
		System.exit(-1);
				
	BulkImporter410 bulkImporter = new BulkImporter410();
	bulkImporter.start();	
}

public void stopApplication()
{
	logger = ImportFuncs.writeToImportLog(logger, 'N', "Forced stop on import application.", "", "");
	isService = false;
	sleepThread.interrupt();

	//System.exit(0);
}


}

