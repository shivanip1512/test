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
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.roles.yukon.SystemRole;
import java.sql.Connection;
import com.cannontech.database.Transaction;
import java.util.Date;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.porter.message.Request;
import com.cannontech.database.db.NestedDBPersistent;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.point.PointTypes;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class BulkImporter410 
{
	private Thread starter = null;
	
	private com.cannontech.message.dispatch.ClientConnection dispatchConn = null;
	private static com.cannontech.message.porter.ClientConnection connToPorter = null;
	public Request porterRequest = null;

	private GregorianCalendar nextImportTime = null;
	private static GregorianCalendar lastImportTime = null;

	public static boolean isService = true;
	public static Thread sleepThread = null;
	private static LogWriter logger = null;
	
	//5 minute interval for import attempts
	public static final int IMPORT_INTERVAL = 300;
	//sleeper thread interval for service
	public static final long SLEEP = 10000;
	
	private final int PORTER_PRIORITY = 6;
	private final String INTERVAL_COMMAND = "putconfig emetcon intervals";
	private final long PORTER_WAIT = 900000;
	private Vector porterPaos = new Vector();
	
	
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
	else
	{
		GregorianCalendar tempImp = new GregorianCalendar();
		long nowInMilliSeconds = tempImp.getTime().getTime();
		long aggIntInMilliSeconds = IMPORT_INTERVAL * 1000;
		long tempSeconds = (nowInMilliSeconds-(nowInMilliSeconds%aggIntInMilliSeconds))+aggIntInMilliSeconds;

		/* if it hasn't been at least one full import interval since we last did an
			 import, wait until next scheduled import time */
		if( tempSeconds < (this.nextImportTime.getTime().getTime()+aggIntInMilliSeconds) )
		{
			tempSeconds += aggIntInMilliSeconds;
		}
	
		this.nextImportTime = new GregorianCalendar();
		this.nextImportTime.setTime(new java.util.Date(tempSeconds));
		//System.out.println("Calculated: " + nextImportTime.getTime().toString());
	}
	/*
	logger = ImportFuncs.writeToImportLog(logger, 'N', " ... Next Import Data Event to occur at: " + nextImportTime.getTime(), "", "");
	CTILogger.info(" ... Import Data Event to occur at: " + nextImportTime.getTime());
	*/
	DBFuncs.writeNextImportTime(this.nextImportTime.getTime(), false);
}

public boolean isForcedImport()
{
	if(DBFuncs.isForcedImport())
	{
		DBFuncs.alreadyForcedImport();
		return true;
	}
	
	return false;
}
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
			
			figureNextImportTime();
			
			do
			{
				logger = ImportFuncs.changeLog(logger);
				
				/*send requests to porter to update the real world intervals for these inserted meters.
				 * We don't do this right after an import because porter takes so long to process
				 * DBChanges, the device may not show up yet if we don't give it a chance.
				 */
				submitToPorter();	
				/*
				CTILogger.info("410 Importer holding until next import.");
				logger = ImportFuncs.writeToImportLog(logger, 'N', "410 Importer holding until next import.", "", "");
				*/
				
				//System.out.println("Next import time: " + getNextImportTime().getTime().toString());
				
				sleepThread = new Thread();
		
				java.util.Date now = null;
				now = new java.util.Date();
				
				//System.out.println("The current time is: " + now.toString());
				
				if( getNextImportTime().getTime().compareTo(now) <= 0 || isForcedImport())
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
					}
					else
					{
						//go go go, import away!
						runImport(importEntries, conn);
					}
					
					figureNextImportTime();
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
					sleepThread.sleep(SLEEP);

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
	DBFuncs.writeNextImportTime(this.nextImportTime.getTime(), true);
	
	ImportData currentEntry = null;
	ImportFail currentFailure = null;
	MCT410IL current410 = null;
	MCT410IL template410 = null;
	Vector failures = new Vector();
	Vector successVector = new Vector();
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
		else if(name.indexOf('/') != -1 || name.indexOf(',') != -1 || name.indexOf('/') != -1 || name.indexOf('/') != -1)
		{
			CTILogger.info("Import entry with address " + address + " has a name that uses invalid characters.");
			logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with address " + address + " has a name that uses invalid characters.", "", "");
			badEntry = true;
			errorMsg.append("invalid name chars; ");			
		}
		else
		{
			boolean isDuplicate = DBFuncs.IsDuplicateName(name, conn);
			if(isDuplicate)
			{
				CTILogger.info("Name " + name + " is already used by an MCT-410 in the Yukon database.");
				logger = ImportFuncs.writeToImportLog(logger, 'F', "Name " + name + " is already used by an MCT-410 in the Yukon database.", "", "");
				badEntry = true;
				errorMsg.append("is using an existing MCT-410 name; ");
			}
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
				
				successVector.addElement(imps.elementAt(j));
				logger = ImportFuncs.writeToImportLog(logger, 'S', "MCT-410 " + name + " with address " + address + ".", "", "");
				porterPaos.addElement(current410);
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
		//having trouble with fail adds...want to make sure these work
		for(int m = 0; m < failures.size(); m++)
		{
			((NestedDBPersistent)failures.elementAt(m)).setOpCode(Transaction.INSERT);
		}		
		ImportFuncs.storeFailures(successVector, failures, conn);
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
		logger = ImportFuncs.writeToImportLog(logger, 'F', "FAILURES NOT RECORDED: THEY WOULD NOT INSERT!!!", e.toString(), e.toString());
	}
	
	//send off a big DBChangeMsg so all Yukon entities know what's goin' on...
	DBFuncs.generateBulkDBChangeMsg(DBChangeMsg.CHANGE_PAO_DB, "DEVICE", DeviceTypes.STRING_MCT_410IL[1], getDispatchConnection());
	DBFuncs.generateBulkDBChangeMsg(DBChangeMsg.CHANGE_POINT_DB, DBChangeMsg.CAT_POINT, PointTypes.getType(PointTypes.SYSTEM_POINT), getDispatchConnection());
	
	DBFuncs.writeTotalSuccess(successCounter);
	DBFuncs.writeTotalAttempted(imps.size());
	Date now = new Date();
	DBFuncs.writeLastImportTime(now);
	
	try
	{
		getDispatchConnection().disconnect();
		dispatchConn = null;
	}
	catch(java.io.IOException ioe)
	{
		logger = ImportFuncs.writeToImportLog(logger, 'N', "Error disconnecting from dispatch: " + ioe.toString(), "", "");
		CTILogger.info("An exception occured disconnecting from dispatch");
	}
	
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

private synchronized com.cannontech.message.dispatch.ClientConnection getDispatchConnection()
{
	if( dispatchConn == null || !dispatchConn.isValid() )
	{
		String host = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE );
		String portStr = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_PORT );
		int port = 1510;
		
		try 
		{
			port = Integer.parseInt(portStr);
		} 
		catch(NumberFormatException nfe) 
		{
			CTILogger.warn("Bad value for DISPATCH-PORT");		
		}
		
		CTILogger.debug("attempting to connect to dispatch @" + host + ":" + port);	
		dispatchConn = new com.cannontech.message.dispatch.ClientConnection();

		Registration reg = new Registration();
		reg.setAppName("410 Bulk Importer");
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay( 3600 );  // 1 hour should be OK

		dispatchConn.setHost(host);
		dispatchConn.setPort(port);
		dispatchConn.setAutoReconnect(true);
		dispatchConn.setRegistrationMsg(reg);
		
		try
		{
			dispatchConn.connect();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	return dispatchConn;
}

private synchronized com.cannontech.message.porter.ClientConnection getPorterConnection()
{	
	if(connToPorter == null)
	{
		String host = "127.0.0.1";
		int port = 1540;
		try
		{
			host = RoleFuncs.getGlobalPropertyValue( SystemRole.PORTER_MACHINE );
			port = Integer.parseInt( RoleFuncs.getGlobalPropertyValue( SystemRole.PORTER_PORT ) ); 
		}
		catch( Exception e)
		{
			CTILogger.error( e.getMessage(), e );
		}

		connToPorter = new com.cannontech.message.porter.ClientConnection();
		connToPorter.setQueueMessages(false);	//don't keep messages, toss once read.
		connToPorter.setHost(host);
		connToPorter.setPort(port);
		connToPorter.setAutoReconnect(true);
					
		try 
		{
			connToPorter.connect(30000);
		}
		catch( Exception e ) 
		{
			CTILogger.error( e.getMessage(), e );
		}
		CTILogger.info(" ************ CONNECTION TO PORTER ESTABLISHED ********************");
	}
	return connToPorter;	
}

private void submitToPorter()
{	
	if(porterPaos.size() > 0)
	{
		final Vector mctIDs = new Vector();
		mctIDs.addAll(porterPaos);
		porterPaos.removeAllElements();
		
		CTILogger.info("Porter thread spawned.  Will write intervals after " + PORTER_WAIT + " ms.");
		logger = ImportFuncs.writeToImportLog(logger, 'N', "Porter thread spawned.  Will write intervals after " + PORTER_WAIT + " ms.", "", "");
	
		//since reflection could take some time, lets do this in its own Thread
		new Thread( new Runnable()
		{
			public void run()
			{
				Thread threadOfDeath = new Thread();
	
				try
				{
					System.gc();
					//wait XX minutes to make sure porter has reloaded the database
					threadOfDeath.sleep(PORTER_WAIT);
				}
				catch (InterruptedException ie)
				{
					CTILogger.info("Exiting the porter wait period without warning...sleep failed!!!");
					logger = ImportFuncs.writeToImportLog(logger, 'N', "Porter wait period failed...no longer waiting!!!" + ie.toString(), "", "");
				}
		
				for(int j = 0; j < mctIDs.size(); j++)
				{
					MCT410IL temp410 = (MCT410IL)mctIDs.elementAt(j);
					String paoName = temp410.getPAOName();
					porterRequest = new Request( temp410.getPAObjectID().intValue(), INTERVAL_COMMAND, temp410.getPAObjectID().longValue() );
					porterRequest.setPriority(PORTER_PRIORITY);
					if( getPorterConnection() != null )
					{
						if( getPorterConnection().isValid())
						{		
							getPorterConnection().write( porterRequest );
						}
						else	
						{
							CTILogger.info(paoName + " REQUEST NOT SENT: CONNECTION TO PORTER IS NOT VALID");
							logger = ImportFuncs.writeToImportLog(logger, 'N', "("+ paoName + ")REQUEST NOT SENT: CONNECTION TO PORTER IS NOT VALID", "", "");
						}
					}
					else
					{
						CTILogger.info(paoName + " REQUEST NOT SENT: CONNECTION TO PORTER IS NULL");
						logger = ImportFuncs.writeToImportLog(logger, 'N', "("+ paoName + ")REQUEST NOT SENT: CONNECTION TO PORTER IS NULL", "", "");
					}
				}
				
				mctIDs.removeAllElements();
				
				CTILogger.info("Intervals written to porter.  Submission thread is at an end.");
				logger = ImportFuncs.writeToImportLog(logger, 'N', "Intervals written to porter.  Submission thread is at an end.", "", "");
				
				try
				{
					getPorterConnection().disconnect();
					connToPorter = null;
				}
				catch(java.io.IOException ioe)
				{
					logger = ImportFuncs.writeToImportLog(logger, 'N', "Error disconnecting from porter: " + ioe.toString(), "", "");
					CTILogger.info("An exception occured disconnecting from porter");
				}
			}
		}).start();
	}
}
}