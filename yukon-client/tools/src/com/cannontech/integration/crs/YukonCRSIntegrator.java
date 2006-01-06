package com.cannontech.integration.crs;

import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.HashMap;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.LogWriter;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.PoolManager;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.roles.yukon.SystemRole;
import java.sql.Connection;
import com.cannontech.database.Transaction;
import java.util.Date;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.porter.message.Request;
import com.cannontech.database.db.NestedDBPersistent;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.db.stars.integration.CRSToSAM_PremiseMeterChange;
import com.cannontech.database.db.stars.integration.FailureCRSToSAM_PremMeterChg;
import com.cannontech.database.db.customer.Address;

public final class YukonCRSIntegrator 
{
	private Thread starter = null;
	
	private Thread worker = null;
	
	private com.cannontech.message.dispatch.ClientConnection dispatchConn = null;

	private GregorianCalendar nextImportTime = null;
	private static GregorianCalendar lastImportTime = null;

	public static boolean isService = true;
	private static LogWriter logger = null;
	
	/**
     * 30 minute interval for import attempts
	 */
	public static final int INTEGRATION_INTERVAL = 1800;
	//sleeper thread interval for service
	public static final long SLEEP = 10000;
    
	public YukonCRSIntegrator() 
    {
	    super();
	}

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
    		long aggIntInMilliSeconds = INTEGRATION_INTERVAL * 1000;
    		long tempSeconds = (nowInMilliSeconds-(nowInMilliSeconds%aggIntInMilliSeconds))+aggIntInMilliSeconds;
    
    		/**
             *  if it hasn't been at least one full import interval since we last did an
             *	import, wait until next scheduled import time 
             */
    		if( tempSeconds < (this.nextImportTime.getTime().getTime()+aggIntInMilliSeconds) )
    		{
    			tempSeconds += aggIntInMilliSeconds;
    		}
    	
    		this.nextImportTime = new GregorianCalendar();
    		this.nextImportTime.setTime(new java.util.Date(tempSeconds));
    	}
       
    }

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
    			CTILogger.info("Yukon to CRS Integration Task starting.");
    			logger = YukonToCRSFuncs.writeToImportLog(logger, 'N', "Yukon to CRS Integration Task starting.", "", "");
    			
    			figureNextImportTime();
    			
    			do
    			{
    				logger = YukonToCRSFuncs.changeLog(logger);
    				
    				java.util.Date now = null;
    				now = new java.util.Date();
    				
    				if( getNextImportTime().getTime().compareTo(now) <= 0)
    				{
    					CTILogger.info("Starting import process.");
    					logger = YukonToCRSFuncs.writeToImportLog(logger, 'N', "Starting import process.", "", "");
    					
    					ArrayList premMeterChanges = YukonToCRSFuncs.readCRSToSAM_PremiseMeterChange();
    					ArrayList incomingPTJs = YukonToCRSFuncs.readCRSToSAM_PTJ();
                                            
    					/**
                         * if no crs integration entries of any kind, report this and go back to waiting
                         */ 
                        if(premMeterChanges.size() < 1)
    					{
    						CTILogger.info("CRSToSAM_PremiseMeterChange table is empty.  No new premise or meter changes from CRS.");
    						logger = YukonToCRSFuncs.writeToImportLog(logger, 'N', "CRSToSAM_PremiseMeterChange table is empty.  No new premise or meter changes from CRS.", "", "");
    					}
    					else
    					{
                            runCRSToSAM_PremiseMeterChanger(premMeterChanges);
    					}
                        
                        if(incomingPTJs.size() < 1)
                        {
                            CTILogger.info("CRSToSAM_PTJ table is empty.  No new incoming PTJs from CRS.");
                            logger = YukonToCRSFuncs.writeToImportLog(logger, 'N', "CRSToSAM_PTJ table is empty.  No new incoming PTJs from CRS.", "", "");
                        }
                        else
                        {
                            //runCRSToSAM_PTJ(incomingPTJs);
                        }
                        
    					figureNextImportTime();
    				}
    				try
    				{
    					Thread.sleep(SLEEP);
    
    				}
    				catch (InterruptedException ie)
    				{
    					CTILogger.info("Exiting the CRS Integrator unexpectedly...sleep failed!!!");
    					logger = YukonToCRSFuncs.writeToImportLog(logger, 'N', "Exiting the CRS Integrator unexpectedly...sleep failed!!!" + ie.toString(), "", "");
    						
    					break;
    				}
    			} while (isService);
    
    			CTILogger.info("Integrator import operations are complete.");
    			logger = YukonToCRSFuncs.writeToImportLog(logger, 'N', "CRS Integration service stopping completely.", "", "");
    			
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

    public void runCRSToSAM_PremiseMeterChanger(ArrayList entries)
    {
        CRSToSAM_PremiseMeterChange currentEntry = null;
    	FailureCRSToSAM_PremMeterChg currentFailure = null;
    	
        ArrayList failures = new ArrayList();
    	ArrayList successArrayList = new ArrayList();
    	boolean badEntry = false;
    	int successCounter = 0;
    	Connection conn = null;
        
        for(int j = 0; j < entries.size(); j++)
    	{
    		badEntry = false;
            
            currentEntry = (CRSToSAM_PremiseMeterChange)entries.get(j);
            String accountNumber = currentEntry.getPremiseNumber().toString();
            
            if(accountNumber.length() > 0)
            {
                Contact currentContact = YukonToCRSFuncs.getContactFromPremiseNumber(currentEntry.getPremiseNumber());
                ContactNotification workNotify = null;
                ContactNotification homeNotify = null;
                
                String lastName = currentEntry.getLastName();
                String firstName = currentEntry.getFirstName();
                String homePhone = currentEntry.getHomePhone();
                String workPhone = currentEntry.getWorkPhone();
                String streetAddress = currentEntry.getStreetAddress();
                String cityName = currentEntry.getCityName();
                String state = currentEntry.getStateCode();
                String zipCode = currentEntry.getZipCode();
                String customerNumber = currentEntry.getNewDebtorNumber();
                String altTrackingNum = currentEntry.getTransID();
                String oldMeterNumber = currentEntry.getOldMeterNumber();
                String newMeterNumber = currentEntry.getNewMeterNumber();
                
                if(lastName.length() > 0)
                    currentContact.setContLastName(lastName);
                if(firstName.length() > 0)
                    currentContact.setContFirstName(firstName);
                
                if(homePhone.length() > 0)
                {
                    homeNotify = YukonToCRSFuncs.getHomePhoneFromContactID(currentContact.getContactID());
                    homeNotify.setNotification(homePhone);
                }
                if(workPhone.length() > 0)
                {
                    workNotify = YukonToCRSFuncs.getWorkPhoneFromContactID(currentContact.getContactID());
                    workNotify.setNotification(workPhone);
                }
                
                if(streetAddress.length() > 0 || cityName.length() > 0 || state.length() > 0
                        || zipCode.length() > 0)
                {
                    Address changingAddress = new Address();
                    changingAddress.setAddressID(currentContact.getAddressID());
                    //Transaction.createTransaction( Transaction.RETRIEVE, changingAddress).execute();
                }
                
               
                /*ONE TRANSACTION FAILURE SHOULD FAIL THE WHOLE THING
                com.cannontech.database.db.*/
                
            }
            else
            {
                badEntry = true;
            }
        }
    }
    		/*
    		//validation
    		StringBuffer errorMsg = new StringBuffer("Failed due to: ");
    		badEntry = false;
    		updateDeviceID = null;
    		       
            if(templateName.length() < 1)
            {
                CTILogger.info("Import entry with name " + name + " has no specified 410 template.");
                logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " has no specified 410 template.", "", "");
                badEntry = true;
                errorMsg.append("has no 410 template specified; "); 
            }
            else
            {
                template410 = DBFuncs.get410FromTemplateName(templateName);
                if(template410.getDevice().getDeviceID().intValue() == -12)
                {
                    CTILogger.info("Import entry with name " + name + " specifies a template MCT410 not in the Yukon database.");
                    logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " specifies a template MCT410 not in the Yukon database.", "", "");
                    badEntry = true;
                    errorMsg.append("has an unknown MCT410 template; ");
                }
            }
            
            if(template410 instanceof MCT410CL)
            {
                currentIL = null;
                currentCL = new MCT410CL();
            }
            else
            {
                currentCL = null;
                currentIL = new MCT410IL();
            }
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
    			updateDeviceID = DBFuncs.getDeviceIDByAddress(address);
    			if( updateDeviceID != null)
    		   	{
    				CTILogger.info("Address " + address + " is already used by an MCT-410 in the Yukon database.  Attempting to modify device.");
    			   	logger = ImportFuncs.writeToImportLog(logger, 'F', "Address " + address + " is already used by an MCT-410 in the Yukon database.  Attempting to modify device.", "", "");
    		   	}
    			boolean isDuplicate = DBFuncs.IsDuplicateName(name);
    			if(isDuplicate)
    			{
    				CTILogger.info("Name " + name + " is already used by an MCT-410 in the Yukon database.");
    				logger = ImportFuncs.writeToImportLog(logger, 'F', "Name " + name + " is already used by an MCT-410 in the Yukon database.", "", "");
    				badEntry = true;
    				errorMsg.append("is using an existing MCT-410 name; ");
    			}
    		}		
    		if(template410 instanceof MCT410IL && (new Integer(address).intValue() < 1000000 || new Integer(address).intValue() > 2796201))
    		{
    			CTILogger.info("Import entry with name " + name + " has an incorrect MCT410 address.");
    			logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " has an incorrect MCT410 address.", "", "");
    			badEntry = true;
    			errorMsg.append("address out of MCT410IL range; ");	
    		}
            else if(template410 instanceof MCT410CL && (new Integer(address).intValue() < 0 || new Integer(address).intValue() > 2796201))
            {
                CTILogger.info("Import entry with name " + name + " has an incorrect MCT410 address.");
                logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " has an incorrect MCT410 address.", "", "");
                badEntry = true;
                errorMsg.append("address out of MCT410CL range; "); 
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
    			routeID = DBFuncs.getRouteFromName(routeName);
    			if(routeID.intValue() == -12)
    			{
    				CTILogger.info("Import entry with name " + name + " specifies a route not in the Yukon database.");
    				logger = ImportFuncs.writeToImportLog(logger, 'F', "Import entry with name " + name + " specifies a route not in the Yukon database.", "", "");
    				badEntry = true;
    				errorMsg.append("has an unknown route; ");
    			}
    		}
    		
    		//failure handling
    		if(badEntry)
    		{
    			GregorianCalendar now = new GregorianCalendar();
    			currentFailure = new ImportFail(address, name, routeName, meterNumber, collectionGrp, altGrp, templateName, errorMsg.toString(), now.getTime());
    			failures.addElement(currentFailure);
    		}
    		else if( updateDeviceID != null)
    		{
    			YukonPAObject pao = new YukonPAObject();
    			pao.setPaObjectID(updateDeviceID);
        
    			try
    			{
    				//update the paobject if the name has changed
    				Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, pao);			    
    				pao = (YukonPAObject)t.execute();
    
    				if( !pao.getPaoName().equals(name))
    				{
    					pao.setPaoName(name);
    					t = Transaction.createTransaction(Transaction.UPDATE, pao);
    					pao = (YukonPAObject)t.execute();
    				}
            
    				//update the deviceMeterGroup table if meternumber, collectiongroup or alternate group changed 
    				DeviceMeterGroup dmg = new DeviceMeterGroup();
    				dmg.setDeviceID(updateDeviceID);
    				t = Transaction.createTransaction(Transaction.RETRIEVE, dmg);
    				dmg = (DeviceMeterGroup)t.execute();
            
    				if( !dmg.getMeterNumber().equals(meterNumber) || !dmg.getCollectionGroup().equals(collectionGrp)||
    						!dmg.getTestCollectionGroup().equals(altGrp))
    				{
    					dmg.setMeterNumber(meterNumber);
    					dmg.setCollectionGroup(collectionGrp);
    					dmg.setTestCollectionGroup(altGrp);
    					t = Transaction.createTransaction( Transaction.UPDATE, dmg);
    					dmg = (DeviceMeterGroup)t.execute();
    				}
            
    				//update teh deviceRotues table if hte routeID has changed.
    				DeviceRoutes dr = new DeviceRoutes();
    				dr.setDeviceID(updateDeviceID);
    				t = Transaction.createTransaction(Transaction.RETRIEVE, dr);
    				dr = (DeviceRoutes)t.execute();
    				if( dr.getRouteID().intValue() != routeID.intValue())
    				{
    					dr.setRouteID(routeID);
    					t = Transaction.createTransaction(Transaction.UPDATE, dr);
    					dr = (DeviceRoutes)t.execute();
    				}
            
    			} catch (TransactionException e)
    			{
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    		//actual 410 creation
    		else
    		{
    			Integer deviceID = DBFuncs.getNextMCTID();
    			GregorianCalendar now = new GregorianCalendar();
    			lastImportTime = now;
    			Integer templateID = template410.getPAObjectID();
    			MCT400SeriesBase current410;
    			if(currentIL != null)
    			{
    				currentIL = (MCT410IL)template410;
    				current410 = currentIL;
    			}
    			else if(currentCL != null)
    			{
    				currentCL = (MCT410CL)template410;
    				current410 = currentCL;
    			}
    			current410 = template410;
    			current410.setPAOName(name);
    			current410.setDeviceID(deviceID);
    			current410.setAddress(new Integer(address));
    			current410.getDeviceMeterGroup().setMeterNumber(meterNumber);
    			current410.getDeviceMeterGroup().setCollectionGroup(collectionGrp);
    			current410.getDeviceMeterGroup().setTestCollectionGroup(altGrp);
    			current410.getDeviceRoutes().setRouteID(routeID);
    			com.cannontech.database.data.multi.MultiDBPersistent objectsToAdd = new com.cannontech.database.data.multi.MultiDBPersistent();
    			objectsToAdd.getDBPersistentArrayList().add(current410);
    			
    			//grab the points we need off the template
    			ArrayList points = DBFuncs.getPointsForPAO(templateID);
    			boolean hasPoints = false;
    			for (int i = 0; i < points.size(); i++)
    			{
    				((com.cannontech.database.data.point.PointBase) points.get(i)).setPointID(new Integer(DBFuncs.getNextPointID() + i));
    				((com.cannontech.database.data.point.PointBase) points.get(i)).getPoint().setPaoID(deviceID);
    				objectsToAdd.getDBPersistentArrayList().add(points.get(i));
    				hasPoints = true;
    			}
    			
    			try
    			{
    				/*
    				 * Do we want to do this every iteration or just use one
    				 * connection for the whole import run??
    				 */
    				/*conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    				
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
    				
    				successArrayList.addElement(imps.elementAt(j));
    				logger = ImportFuncs.writeToImportLog(logger, 'S', "MCT-410 " + name + " with address " + address + ".", "", "");
    				synchronized(paoIDsForPorter)
    				{				
    					paoIDsForPorter.addElement(current410.getPAObjectID());
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
    			finally
    			{
    				try
    				{
    					if( conn != null )
    					{
    						conn.commit();
    						conn.close();
    					}
    				}
    				catch( java.sql.SQLException e )
    				{
    					e.printStackTrace();
    				}
    			}
    		}
    	}
    	conn = null;	
    	//remove executed ImportData entries
    	try
    	{
    		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    		ImportFuncs.flushImportTable(imps, conn);
    	}
    	catch( java.sql.SQLException e )
    	{
    		e.printStackTrace();
    		logger = ImportFuncs.writeToImportLog(logger, 'F', "PREVIOUSLY USED IMPORT ENTRIES NOT REMOVED: THEY WOULD NOT DELETE!!!", e.toString(), e.toString());
    	}
    	finally
    	{
    		try
    		{
    			if( conn != null )
    			{
    				conn.commit();
    				conn.close();
    			}
    		}
    		catch( java.sql.SQLException e )
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	conn = null;
    	//store failures
    	try
    	{
    		//having trouble with fail adds...want to make sure these work
    		for(int m = 0; m < failures.size(); m++)
    		{
    			((NestedDBPersistent)failures.elementAt(m)).setOpCode(Transaction.INSERT);
    		}		
    		
    		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    		ImportFuncs.storeFailures(successArrayList, failures, conn);
    	}
    	catch( java.sql.SQLException e )
    	{
    		e.printStackTrace();
    		logger = ImportFuncs.writeToImportLog(logger, 'F', "FAILURES NOT RECORDED: THEY WOULD NOT INSERT!!!", e.toString(), e.toString());
    	}
    	finally
    	{
    		try
    		{
    			if( conn != null )
    			{
    				conn.commit();
    				conn.close();
    			}
    		}
    		catch( java.sql.SQLException e )
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	//send off a big DBChangeMsg so all Yukon entities know what's goin' on...
    	DBFuncs.generateBulkDBChangeMsg(DBChangeMsg.CHANGE_PAO_DB, "DEVICE", DeviceTypes.STRING_MCT_410IL[1], getDispatchConnection());
        DBFuncs.generateBulkDBChangeMsg(DBChangeMsg.CHANGE_PAO_DB, "DEVICE", DeviceTypes.STRING_MCT_410CL[1], getDispatchConnection());
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
    
    public void runCRSToSAM_PTJ(ArrayList entries) { }
    
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
    		Thread w = worker;
    		worker = null;
    		w.interrupt();
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
    				
    	YukonCRSIntegrator bulkImporter = new YukonCRSIntegrator();
    	bulkImporter.start();	
    }

    public void stopApplication()
    {
    	logger = YukonToCRSFuncs.writeToImportLog(logger, 'N', "Forced stop on import application.", "", "");
    	isService = false;
    
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
    			dispatchConn.connectWithoutWait();
    		}
    		catch( Exception e )
    		{
    			e.printStackTrace();
    		}
    	}
    	return dispatchConn;
    }

}