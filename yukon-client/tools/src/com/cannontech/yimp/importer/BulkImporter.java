/*
 * Created on Feb 2, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yimp.importer;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.MCT400SeriesBase;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.db.device.DeviceRoutes;
import com.cannontech.database.db.importer.ImportData;
import com.cannontech.database.db.importer.ImportFail;
import com.cannontech.database.db.importer.ImportPendingComm;
import com.cannontech.device.range.DlcAddressRangeService;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSetting;
import com.cannontech.system.dao.GlobalSettingsDao;
import com.cannontech.yimp.util.DBFuncs;
import com.cannontech.yimp.util.ImportFuncs;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

public final class BulkImporter extends Observable implements MessageListener
{
	private Thread starter = null;
	
	private Thread worker = null;
	private IServerConnection dispatchConn = null;
    private Logger log = YukonLogManager.getLogger(BulkImporter.class);
    
	private static IServerConnection connToPorter = null;
    private Set<Long> porterMessageIDs = new HashSet<Long>();
    private Map<Long, Integer> messageIDToRouteIDMap = new HashMap<Long, Integer>();
    /* Singleton incrementor for messageIDs to send to porter connection */
    private static volatile long currentMessageID = 1;
    private List<Request> cmdMultipleRouteList = new ArrayList<Request>();
    
    /* flag indicating more looping required*/
    //public volatile int loopRouteCounter = 0;

	private GregorianCalendar nextImportTime = null;
	private static GregorianCalendar lastImportTime = null;

	public static boolean isService = true;
	
	//5 minute interval for import attempts
	public static final int IMPORT_INTERVAL = 300;
	//sleeper thread interval for service
	public static final long SLEEP = 10000;
	
	private final int PORTER_PRIORITY = 6;
	private final String INTERVAL_COMMAND = "putconfig emetcon intervals";
    private final String LOOP_COMMAND = "loop";
    private final String LOOP_LOCATE_COMMAND = "loop locate";
    //15 min wait for porter safety
    private final long PORTER_WAIT = 900000;
	private final int SAVETHEAMPCARDS_AMOUNT = 50;
	
	private boolean singleGroup = false;	//false supports legacy functionality
	
public BulkImporter() {
	super();
}

/**
 * Insert the method's description here.
 * Creation date: (02/2/2005 7:27:20 PM)
 */
public void figureNextImportTime() {
	if( this.nextImportTime == null ) {
		this.nextImportTime = new GregorianCalendar();
	}
	else {
		GregorianCalendar tempImp = new GregorianCalendar();
		long nowInMilliSeconds = tempImp.getTime().getTime();
		long aggIntInMilliSeconds = IMPORT_INTERVAL * 1000;
		long tempSeconds = (nowInMilliSeconds-(nowInMilliSeconds%aggIntInMilliSeconds))+aggIntInMilliSeconds;

		/* if it hasn't been at least one full import interval since we last did an
			 import, wait until next scheduled import time */
		if( tempSeconds < (this.nextImportTime.getTime().getTime()+aggIntInMilliSeconds) ) {
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

public boolean isForcedImport() {
	if(DBFuncs.isForcedImport()) {
		DBFuncs.alreadyForcedImport();
		return true;
	}
	
	return false;
}

public GregorianCalendar getNextImportTime() {
	return this.nextImportTime;
}

public void start() {
	Runnable runner = new Runnable() {
		public void run() {
            log.info("Bulk MCT Importer Version " + VersionTools.getYUKON_VERSION() + " starting.");
			
			figureNextImportTime();
			//start the worker bee to handle porter communication
            porterWorker();
            
            // Load possible cParm for controlling single vs multi child group membership in parent group
            ConfigurationSource configurationSource = YukonSpringHook.getBean("configurationSource", ConfigurationSource.class);
            singleGroup = configurationSource.getBoolean(MasterConfigBooleanKeysEnum.BULK_IMPORTER_SINGLE_GROUP);
            
			do {
				java.util.Date now = null;
				now = new java.util.Date();
				
				if( getNextImportTime().getTime().compareTo(now) <= 0 || isForcedImport()) {
					log.debug("Starting import process.");
					
					List<ImportData> importEntries = ImportFuncs.summonImps();
					
					//if no importEntries, report this and go back to waiting
					if(importEntries.size() < 1) {
						log.debug("ImportData table is empty.  No new 410s to import.");
					}
					else {
						//go go go, import away!
						runImport(importEntries);
					}
					
					figureNextImportTime();
				}
				
				try {
					Thread.sleep(SLEEP);
				}
				catch (InterruptedException ie) {
					log.info("Exiting the bulk importer unexpectedly...sleep failed!!!");
					break;
				}
			} while (isService);

			log.info("Import operation complete.");
			
			//be sure the runner thread is NULL
			starter = null;		
		}
	};

	if( starter == null ) {
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
@SuppressWarnings("deprecation")
public void runImport(List<ImportData> imps) {
	DBFuncs.writeNextImportTime(this.nextImportTime.getTime(), true);
	
	ImportData currentEntry = null;
	ImportFail currentFailure = null;
    MCT400SeriesBase template400SeriesBase = null;
	List<ImportFail> failures = new ArrayList<ImportFail>();
	List<ImportData> successVector = new ArrayList<ImportData>();

	Integer updateDeviceID = null;
	int successCounter = 0;
	Connection conn = null;
    boolean notUpdate = true;

    DBPersistentDao dbPersistentDao = YukonSpringHook.getBean("dbPersistentDao", DBPersistentDao.class);
    
    DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = (DeviceGroupMemberEditorDao) YukonSpringHook.getBean("deviceGroupMemberEditorDao");
    DeviceGroupEditorDao deviceGroupEditorDao = (DeviceGroupEditorDao) YukonSpringHook.getBean("deviceGroupEditorDao");
    RoleDao roleDao = (RoleDao) YukonSpringHook.getBean("roleDao");
	DlcAddressRangeService dlcAddressRangeService = YukonSpringHook.getBean("dlcAddressRangeService", DlcAddressRangeService.class);
	
    StoredDeviceGroup alternateGroupBase = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.ALTERNATE);
    StoredDeviceGroup billingGroupBase = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.BILLING);
    StoredDeviceGroup collectionGroupBase = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.COLLECTION);

	for(int j = 0; j < imps.size(); j++) {
		updateDeviceID = null;
        currentEntry = imps.get(j);
	
		//mark entry for deletion
		imps.get(j).setOpCode(TransactionType.DELETE);
		
		String name = currentEntry.getName();
		String address = currentEntry.getAddress();
		String routeName = currentEntry.getRouteName();
		Integer routeID = new Integer(-12);
		String meterNumber = currentEntry.getMeterNumber();
		/*
         * TODO We will want to support any number of custom device groups in the future
         * For now we will restrict to the original 3 columns
		 */
        String collectionGrp = currentEntry.getCollectionGrp();
		String altGrp = currentEntry.getAltGrp();
		String templateName = currentEntry.getTemplateName();
        String billGrp = currentEntry.getBillingGroup();
        String substationName = currentEntry.getSubstationName();
        List<Integer> routeIDsFromSub = new ArrayList<Integer>();
		
		//validation
        String logMsgPrefix = "Import entry with name "+name+" ";
        List<String> errorMsg = new ArrayList<String>();
		
		if (StringUtils.isBlank(address)) {
            String error = "Has a a blank address.  ";
			log.error(logMsgPrefix + error);
			errorMsg.add(error);
		} else {
			try{
				// Does a parseCheck to make sure its a numerical value
				Double doubleAddress = Double.parseDouble(address);				
				updateDeviceID = DBFuncs.getDeviceIDByAddress(doubleAddress.toString());
			} catch (NumberFormatException nfe) {
                String error = "Has an incorrect address ("+address+").  ";
				log.error(logMsgPrefix + error);
                errorMsg.add(error);
            }
		}
		
        if (StringUtils.isBlank(templateName)) {
            if (updateDeviceID == null) {
                String error = "Has no 410 template specified.  ";
                log.error(logMsgPrefix + error);
                errorMsg.add(error);
            }
        } else {
            template400SeriesBase = DBFuncs.get410FromTemplateName(templateName);
            if(template400SeriesBase.getDevice().getDeviceID().intValue() == -12) {
                String error = "Specifies a template MCT ("+templateName+") not in the Yukon database.  ";
                log.error(logMsgPrefix + error);
                errorMsg.add(error);
            } else {
                /*Address range check for 400 series*/
            	PaoType paoType = PaoType.getForDbString(template400SeriesBase.getPAOType());
            	if (!dlcAddressRangeService.isEnforcedAddress(paoType, Integer.parseInt(address))) {
            		String error = "Has an incorrect " + template400SeriesBase.getPAOType() + " address ("+address+").  ";
            		log.error(logMsgPrefix + error);
            		errorMsg.add(error);
            	}
            }
        }
        
        if(StringUtils.isBlank(name)){
            String error = "Import entry doesn't have a name.  ";
            log.error(error);
            errorMsg.add(error);
        } else {
            if(name.length() > 60) {
                String error = "Has a name with an improper length.  ";
                log.error(logMsgPrefix + error);
                errorMsg.add(error);
            } else {
                if(CtiUtilities.isContainsInvalidPaoNameCharacters(name)) {
                    String error = "Has a name that uses invalid characters.  ";
                    log.error(logMsgPrefix + error);
                    errorMsg.add(error);
                } else {
                    if( updateDeviceID != null) {
                        notUpdate = false;
                        log.info(logMsgPrefix + "Address " + address + " is already used by a 400 series MCT in the Yukon database.  Attempting to modify device.");
                    }
                
                    if (DBFuncs.IsDuplicateName(name) && notUpdate) {
                        String error = "Already used by a 400 series MCT in the Yukon database.  ";
                        log.error(logMsgPrefix + error);
                        errorMsg.add(error);
                    }
                }
            }
        }

        /*New 400 series MCTs will each need a clause added above if address range
         * validation is desired
         */
        if(StringUtils.isBlank(meterNumber) && notUpdate) {
            String error = "Has no meter number.  ";
            log.error(logMsgPrefix + error);
            errorMsg.add(error);
        }
        
        // COLLECTION GROUP
        // updatePrefixGroup will check for isBlank and ignore group field updates/inserts when collectionGroup is blank. 
        if (CtiUtilities.isContainsInvalidDeviceGroupNameCharacters(collectionGrp)) {
            String error = "Collection group name has invalid characters " + Arrays.toString(TextFieldDocument.INVALID_CHARS_DEVICEGROUPNAME) + ".  ";
            log.warn(logMsgPrefix + error);
            errorMsg.add(error);
        } 
       
        // ALTERNATE GROUP
        // updatePrefixGroup will check for isBlank and ignore group field updates/inserts when alternateGroup is blank.
        if (CtiUtilities.isContainsInvalidDeviceGroupNameCharacters(altGrp)) {
            String error = "Alternate group name has invalid characters " + Arrays.toString(TextFieldDocument.INVALID_CHARS_DEVICEGROUPNAME) + ".  ";
            log.warn(logMsgPrefix + error);
            errorMsg.add(error);
        }
        
        // BILLING GROUP
        // updatePrefixGroup will check for isBlank and ignore group field updates/inserts when billingGroup is blank.
        if (CtiUtilities.isContainsInvalidDeviceGroupNameCharacters(billGrp)) {
            String error = "Billing group name has invalid characters " + Arrays.toString(TextFieldDocument.INVALID_CHARS_DEVICEGROUPNAME) + ".  ";
            log.warn(logMsgPrefix + error);
            errorMsg.add(error);
        }
        
        if(StringUtils.isBlank(routeName)) {
            if(StringUtils.isBlank(substationName)) {
                if(notUpdate) {
                    String error = "Has no specified substation or route.  ";
                    log.error(logMsgPrefix + error);
                    errorMsg.add(error);
                }
            }
        else if(!StringUtils.isBlank(substationName)) {
            routeIDsFromSub = DBFuncs.getRouteIDsFromSubstationName(substationName);
            if(routeIDsFromSub.size() < 1) {
                String error = "Specifies a substation ("+substationName+") with routes not in the Yukon database.  ";
                log.error(logMsgPrefix + error);
                errorMsg.add(error);
            }
        } else if(!notUpdate) {
                String error = "Has no specified route ("+routeName+").  ";
                log.error(logMsgPrefix + error);
                errorMsg.add(error);
            } else {
                routeID = new Integer(-12);
            }
		}
		else {
			routeID = DBFuncs.getRouteFromName(routeName);
			if(routeID.intValue() == -12) {
                String error = "Specifies a route ("+routeName+") not in the Yukon database.";
				log.error(logMsgPrefix + error);
                errorMsg.add(error);
			}
		}
		
		//failure handling
		if(!errorMsg.isEmpty()) {
			GregorianCalendar now = new GregorianCalendar();
            String errors = errorMsg.remove(0);
			for (String error : errorMsg) {
                errors += "<br>" + error;
            }
			currentFailure = new ImportFail(address, name, routeName, 
			                                meterNumber, collectionGrp, altGrp, templateName, 
			                                errors, now.getTime(), billGrp, 
			                                substationName, ImportFuncs.FAIL_INVALID_DATA);
			failures.add(currentFailure);
            
            log.error("Unable to import or update device with address " + address + " and name " + name + ".");
		}
		else if( updateDeviceID != null) {
            LiteYukonPAObject liteYukonPaobject = DaoFactory.getPaoDao().getLiteYukonPAO(updateDeviceID);
            YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
			boolean updateTransaction = false;
			
			try {
				//update the paobject if the name has changed
                if( !yukonPaobject.getPAOName().equals(name) && !StringUtils.isBlank(name)) {
                    yukonPaobject.setPAOName(name);
                    updateTransaction = true;
                }
        
                //update the deviceMeterGroup table if meternumber 
                DeviceMeterGroup dmg = ((MCTBase)yukonPaobject).getDeviceMeterGroup();
                if( !dmg.getMeterNumber().equals(meterNumber) && StringUtils.isNotBlank(meterNumber)) {
                    dmg.setMeterNumber(meterNumber);
                    updateTransaction = true;
                }
                
                //update device groups if they changed
                SimpleDevice yukonDevice = new SimpleDevice(yukonPaobject.getPAObjectID(), PaoType.getForDbString(yukonPaobject.getPAOType()));
                updateGroup(collectionGroupBase, collectionGrp, yukonDevice, deviceGroupMemberEditorDao, deviceGroupEditorDao);
                updateGroup(alternateGroupBase, altGrp, yukonDevice, deviceGroupMemberEditorDao, deviceGroupEditorDao);
                updateGroup(billingGroupBase, billGrp, yukonDevice, deviceGroupMemberEditorDao, deviceGroupEditorDao);
                
				//update the deviceRoutes table if the routeID has changed.
				if(routeID.intValue() != -12) {
                    DeviceRoutes dr = ((MCTBase)yukonPaobject).getDeviceRoutes();
    				if( dr.getRouteID().intValue() != routeID.intValue()) {
    					dr.setRouteID(routeID);
    					updateTransaction = true;
    				}
                }
                /*else if(routeIDsFromSub.size() > 0) {
                    for(int i = 0; i < routeIDsFromSub.size(); i++) {
                        TODO: do we want to change the route based on substation
                         * on an update since there will be no porter communication to verify?
                         * Or will there be porter communication to verify even on an update?
                         
                    }*/
				if( updateTransaction) {
                    dbPersistentDao.performDBChange(yukonPaobject, TransactionType.UPDATE); //update transaction and DBChange write
				}
                log.info("Updated " + yukonPaobject.getPAOType() + " with name " + name + " with address " + address + ".");
                successCounter++;
            } catch (PersistenceException e) {
                log.error(e);
                GregorianCalendar now = new GregorianCalendar();
                String errors = "Database update for DeviceId " + updateDeviceID + " failed. (PersistenceException)";
                if(!errorMsg.isEmpty()) {
                    errors = errorMsg.remove(0);
                    for (String error : errorMsg) {
                        errors += "<br>" + error;
                    }
                }
                currentFailure = new ImportFail(address, name, routeName, 
                                                meterNumber, collectionGrp, altGrp, templateName, 
                                                errors, now.getTime(), billGrp, 
                                                substationName, ImportFuncs.FAIL_INVALID_DATA);
                failures.add(currentFailure);
            }
		}
		//actual 410 creation
		else {
			Integer deviceID = DaoFactory.getPaoDao().getNextPaoId();
			GregorianCalendar now = new GregorianCalendar();
			lastImportTime = now;
			Integer templateID = template400SeriesBase.getPAObjectID();
			
            MCT400SeriesBase current400Series = template400SeriesBase;
			current400Series.setPAOName(name);
			current400Series.setDeviceID(deviceID);
			current400Series.setAddress(new Integer(address));
			current400Series.getDeviceMeterGroup().setMeterNumber(meterNumber);
			
            ImportPendingComm pc = new ImportPendingComm(deviceID, address, name, routeName, 
                                                         meterNumber, collectionGrp, altGrp, 
                                                         templateName, billGrp, substationName);
            
            /*
             * single route vs. substation specified: multiple possible routes
             * if sub specified, a porter thread will attempt to find the right one; for now
             * just use the first in the list.
             */
            if(routeID.intValue() == -12) {
                if (routeIDsFromSub.size() > 0) {
                    current400Series.getDeviceRoutes().setRouteID(routeIDsFromSub.get(0));
                }
            }
            else
                current400Series.getDeviceRoutes().setRouteID(routeID);
            
			MultiDBPersistent pointsToAdd = new MultiDBPersistent();

			//grab the points we need off the template
			Vector<PointBase> points = DBFuncs.getPointsForPAO(templateID);
			for (int i = 0; i < points.size(); i++) {
				points.get(i).setPointID(DaoFactory.getPointDao().getNextPointId());
				points.get(i).getPoint().setPaoID(deviceID);
				pointsToAdd.getDBPersistentVector().add(points.get(i));
				log.debug("Added object to Add: Device(" + current400Series.getPAObjectID() + ") Point(" + points.get(i).getPoint().getPointID()+").");
			}
			
			try {
			    //Add Pao (Database insert AND DbChange Message
			    dbPersistentDao.performDBChange(current400Series, TransactionType.INSERT);
			    log.debug("Insert into DB with DBChangeMessage: Device(" + current400Series.getPAObjectID() + ").");
			
                //Add Points (Database insert but NO dbChange Message
                dbPersistentDao.performDBChangeWithNoMsg(pointsToAdd, TransactionType.INSERT);
                log.debug("Insert into DB with NO DBChangeMessage: " + points.size() + " Points for Device(" + current400Series.getPAObjectID() + ").");

                SimpleDevice yukonDevice = new SimpleDevice(current400Series.getPAObjectID(), PaoType.getForDbString(current400Series.getPAOType()));
                updateGroup(collectionGroupBase, collectionGrp, yukonDevice, deviceGroupMemberEditorDao, deviceGroupEditorDao);
                updateGroup(alternateGroupBase, altGrp, yukonDevice, deviceGroupMemberEditorDao, deviceGroupEditorDao);
                updateGroup(billingGroupBase, billGrp, yukonDevice, deviceGroupMemberEditorDao, deviceGroupEditorDao);

                //write pending communication entry for porter thread to pick up
                boolean importerCommunications =  YukonSpringHook.getBean(GlobalSettingsDao.class).getBoolean(GlobalSetting.BULK_IMPORTER_COMMUNICATIONS_ENABLED);
                if (importerCommunications){
                    Transaction.createTransaction(TransactionType.INSERT, pc).execute();
                }
                
				successVector.add(imps.get(j));
				log.info(current400Series.getPAOType() + " with name " + name + " with address " + address + " successfully imported.");
				successCounter++;
			}
			catch( TransactionException e ) {
				log.error(e);
				StringBuffer tempErrorMsg = new StringBuffer(e.toString());
				currentFailure = new ImportFail(address, name, routeName, 
                                                meterNumber, collectionGrp, altGrp, 
                                                templateName, tempErrorMsg.toString(), now.getTime(), 
                                                billGrp, substationName, ImportFuncs.FAIL_DATABASE);
				failures.add(currentFailure);
				log.error(current400Series.getPAOType() + " with name " + name + " failed on INSERT into database (ImportPendingComm). ");
			} catch( PersistenceException e ) {
			    log.error(e);
                StringBuffer tempErrorMsg = new StringBuffer(e.toString());
                currentFailure = new ImportFail(address, name, routeName, 
                                                meterNumber, collectionGrp, altGrp, 
                                                templateName, tempErrorMsg.toString(), now.getTime(), 
                                                billGrp, substationName, ImportFuncs.FAIL_DATABASE);
                failures.add(currentFailure);
                log.error(current400Series.getPAOType() + " with name " + name + " failed on INSERT into database. (objectsToAdd)");
            }
			finally {
				try {
					if( conn != null ) {
						conn.close();
					}
				}
				catch( java.sql.SQLException e ) {
					log.error(e);
				}
			}
		}
	}
	conn = null;	
	//remove executed ImportData entries
	try {
		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
		ImportFuncs.flushImportTable(imps, conn);
	}
	catch( java.sql.SQLException e ) {
		log.error(e);
		log.error("PREVIOUSLY USED IMPORT ENTRIES NOT REMOVED: THEY WOULD NOT DELETE!!!");
	}
	finally {
		try {
			if( conn != null ) {
				conn.close();
			}
		}
		catch( java.sql.SQLException e ) {
			log.error(e);
		}
	}
	
	conn = null;
	//store failures
	try {
		//having trouble with fail adds...want to make sure these work
		for(int m = 0; m < failures.size(); m++) {
			failures.get(m).setOpCode(TransactionType.INSERT);
		}		
		
		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
		ImportFuncs.storeFailures(successVector, failures, conn);
	}
	catch( java.sql.SQLException e ) {
		log.error(e);
		log.error("FAILURES NOT RECORDED: THEY WOULD NOT INSERT!!!");
	}
	finally {
		try {
			if( conn != null ) {
				conn.close();
			}
		}
		catch( java.sql.SQLException e ) {
		    log.error(e);
		}
	}
	DBFuncs.writeTotalSuccess(successCounter);
	DBFuncs.writeTotalAttempted(imps.size());
	Date now = new Date();
	DBFuncs.writeLastImportTime(now);
}

/** 
 * Stop us
 */
public void stop() {
	try {
        generateMessageID();
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

public boolean isRunning() {
	return starter != null;
}

/**
 * Starts the application.
 */
public static void main(java.lang.String[] args) {
	ClientSession session = ClientSession.getInstance(); 
	if(!session.establishSession()){
		System.exit(-1);			
	}
	  	
	if(session == null) 		
		System.exit(-1);
				
	BulkImporter bulkImporter = new BulkImporter();
	bulkImporter.start();	
}

public void stopApplication() {
	log.error("Forced stop on import application.");
	isService = false;

	//System.exit(0);
}

private synchronized IServerConnection getDispatchConnection() {
    if(dispatchConn == null) {
        dispatchConn = ConnPool.getInstance().getDefDispatchConn();
        
        log.info("Dispatch connection created.");
    }
    
    return dispatchConn;
}

private synchronized IServerConnection getPorterConnection() {
	if(connToPorter == null) {
        connToPorter = ConnPool.getInstance().getDefPorterConn();
        connToPorter.addMessageListener(this);
		log.info("Porter connection created.");
	}
    
	return connToPorter;	
}

/*
 * With big databases, porter needs a lot of time to reload.  Therefore, if we want the submits
 * to work, we need to give porter its grace period.  This worker thread will grab the ids of 
 * all successfully imported devices and then wait fifteen minutes before it attempts to locate 
 * them and submit the intervals for them.
 * 
 * Also, we will now do only fifty at a time.  Otherwise, we could dump a thousand or more meters out at
 * at time and burn out some poor CCU amp card.
 */
private void porterWorker() {
	if(worker == null) {
		
		//get the porter connection so that is created when we need it.  isValid is causing u
		getPorterConnection();
		
		Runnable runner = new Runnable() {
			public void run() {
                log.info("Porter submission thread created.");
                
                while(true) {
                    try {
                        Thread.sleep(PORTER_WAIT);
                    }
                    catch (InterruptedException ie) {
                        log.info("Exiting the worker bee unexpectedly...sleep failed!!!");
                        break;
                    }
                    
                    List<ImportPendingComm> pending = ImportFuncs.getAllPending();
                    
					if(pending.size() > 0) {
                        if(pending.size() > SAVETHEAMPCARDS_AMOUNT) {
    						pending = pending.subList(0, SAVETHEAMPCARDS_AMOUNT);
    					}
    					
                        log.info("Porter worker thread has obtained " + pending.size() + " MCT IDs.  Communication attempt.");
                        Request porterRequest = null;
						for(int j = 0; j < pending.size(); j++) {
							//locate attempt (only if given multiple routes via substation)
                            ImportPendingComm pc = pending.get(j);
                            List<Integer> routeIDsFromSub = DBFuncs.getRouteIDsFromSubstationName(pc.getSubstationName());
                            String routeName = pc.getRouteName();
                            if(routeName.length() > 1) {
                                /*
                                 * Don't actually need to set a routeID; loop will use what is attached to the device
                                 * Proper route has already been attached by the bulk importer.  Why use an extra db connection finding
                                 * out what we already know?
                                 */
                                porterRequest = new Request( pc.getPendingID().intValue(), LOOP_COMMAND, currentMessageID );
                                Integer routeID = DBFuncs.getRouteFromName(routeName);
                                log.info("Locate attempt written to porter: device " + porterRequest.getDeviceID() + " on route " + routeName +" ("+ routeID+").");
                                writeToPorter(porterRequest);
                                messageIDToRouteIDMap.put(new Long(porterRequest.getUserMessageID()), routeID);
                            }
                            else if(routeIDsFromSub.size() > 0) {
                                //send first one right off
                                int routeID = routeIDsFromSub.get(0).intValue();
                                routeName = DaoFactory.getPaoDao().getYukonPAOName(routeID);
                                
                                porterRequest = new Request( pc.getPendingID().intValue(), LOOP_COMMAND, currentMessageID );
                                porterRequest.setRouteID(routeIDsFromSub.get(0).intValue());
                                cmdMultipleRouteList.add(porterRequest);
                                log.info("Locate attempt written to porter: device " + porterRequest.getDeviceID() + " on route " + routeName +" ("+ routeID+").");
                                writeToPorter(porterRequest);
                                messageIDToRouteIDMap.put(new Long(porterRequest.getUserMessageID()), routeID);
                                for(int i = 1; i < routeIDsFromSub.size(); i++) {
                                    routeID = routeIDsFromSub.get(i).intValue();
                                    routeName = DaoFactory.getPaoDao().getYukonPAOName(routeID);
                                
                                    porterRequest = new Request( pc.getPendingID().intValue(), LOOP_COMMAND, currentMessageID );
                                    generateMessageID();
                                    porterRequest.setRouteID(routeID);
                                    cmdMultipleRouteList.add(porterRequest);
                                    messageIDToRouteIDMap.put(new Long(porterRequest.getUserMessageID()), routeID);
                                    log.info("Locate attempt queued: device " + porterRequest.getDeviceID() + " on route " + routeName +" ("+ routeID+").");
                                }
                            }
                            //going to have to do a general loop locate to find it; we apparently don't know a possible route
                            else {
                                porterRequest = new Request( pc.getPendingID().intValue(), LOOP_LOCATE_COMMAND, currentMessageID );
                                log.info("General location loop attempt written to porter: device " + porterRequest.getDeviceID() + " on route " + routeName);
                                writeToPorter(porterRequest);
                            }
						}
					}
                }
			}
		};
		
		worker = new Thread(runner, "WrkrBee");
		worker.start();
	}
}

public void update(Observable o, Object arg) 
{}

/*
 * Uses the messageReceived from the MessageListener class to listen to messages coming back
 * from Porter.  This will be used when trying to locate meters on some or all routes, as well as
 * to determine whether meter interval sends are successful.
 */
public void messageReceived(MessageEvent e) {
    Message in = e.getMessage();        
    
    if(in instanceof Return) {
        Return returnMsg = (Return) in;
        synchronized(this) {
            CTILogger.debug("Message Received [ID:"+ returnMsg.getUserMessageID() + 
                            " DevID:" + returnMsg.getDeviceID() + 
                            " Command:" + returnMsg.getCommandString() +
                            " Result:" + returnMsg.getResultString() + 
                            " Status:" + returnMsg.getStatus() +
                            " More:" + returnMsg.getExpectMore()+"]");
            
            if( !porterMessageIDs.contains( new Long(returnMsg.getUserMessageID()))) {
                log.info("Unknown Message: "+ returnMsg.getUserMessageID() +" Command [" + returnMsg.getCommandString()+"]");
                log.info("Unknown Message: "+ returnMsg.getUserMessageID() +" Result [" + returnMsg.getResultString()+"]");
                return;
            }
            else {
                //Remove the messageID from the set of stored ids.
                if(returnMsg.getExpectMore() == 0) { //nothing more is coming, remove from list.
                    getPorterMessageIDs().remove( new Long(returnMsg.getUserMessageID()));
                    Request nextRouteLoop = null;
                    boolean removed = false;
                    for(int j = 0; j < cmdMultipleRouteList.size(); j++) {
                        if(removed && returnMsg.getDeviceID() == cmdMultipleRouteList.get(j).getDeviceID()) {
                            nextRouteLoop = cmdMultipleRouteList.get(j);
                            log.info("NextRoute("+ nextRouteLoop.getRouteID() +") to process for device(" + nextRouteLoop.getDeviceID() + ").");
                            break;
                        }
                        if(returnMsg.getUserMessageID() == cmdMultipleRouteList.get(j).getUserMessageID()) {
                            cmdMultipleRouteList.remove(j);
                            removed = true;
                            j--;
                        }
                    }

                    boolean commSuccessful = returnMsg.getStatus() == 0;
                    
                    //message was not successful and it was the only one for this device
                    if(!commSuccessful && nextRouteLoop == null) {
                        if(DBFuncs.writePendingCommToFail(ImportFuncs.FAIL_COMMUNICATION, "Unable to communicate with device.", new Integer(returnMsg.getDeviceID())))
                            log.info("Communication failure with device " + returnMsg.getDeviceID() + ".");
                        else
                            log.error("Could not move pending communication to fail table, but communication failure occurred with device " + returnMsg.getDeviceID() + ".");
                        return;
                    }
                    //it was a loop locate (checking all available routes) and succeeded, save the route and then write the interval out
                    else if(returnMsg.getCommandString().lastIndexOf(LOOP_LOCATE_COMMAND) > -1) {
                        handleSuccessfulLocate(returnMsg);
                    }
                    //check to see if it was a route-specific or substation specific loop
                    else if(returnMsg.getCommandString().lastIndexOf(LOOP_COMMAND) > -1) {
                        //there is more than one loop; substation must have been supplied instead of route
                        if(commSuccessful) {  
                            handleSuccessfulLocate(returnMsg);
                            //there were multiple routes specified; remove later attempts
                            if(nextRouteLoop != null) {
                                for(int j = 0; j < cmdMultipleRouteList.size(); j++) {
                                    if(removed && returnMsg.getDeviceID() == cmdMultipleRouteList.get(j).getDeviceID()) {
                                        cmdMultipleRouteList.remove(j);
                                        j--;
                                    }
                                }
                            } 
                        }
                        //failed, on to the next one
                        else if (nextRouteLoop!= null ) {
                            log.info("Locate attempt written to porter: device " + nextRouteLoop.getDeviceID() + " on route with ID " + nextRouteLoop.getRouteID());
                            writeToPorter(nextRouteLoop);
                        }
                    }
                    //must be an interval command if it has made it this far
                    else if(returnMsg.getCommandString().lastIndexOf(INTERVAL_COMMAND) > -1) {
                        log.info("Intervals successfully written to device " + returnMsg.getDeviceID());
                    }
                    
                    /*it made it this far, communicationg succeeded, we can remove it from the pending table 
                     *and from the failure table if it still happens to be there
                     */
                    DBFuncs.removeFromPendingAndFailed(returnMsg.getDeviceID());
                }
            }
        }
    }
}

public Set<Long> getPorterMessageIDs() {
    return porterMessageIDs;
}

public void setPorterMessageIDs(Set<Long> porterMessageIDs) {
    this.porterMessageIDs = porterMessageIDs;
}

public List<Request> getCmdMultipleRouteList() {
    return cmdMultipleRouteList;
}

public void setCmdMultipleRouteList(List<Request> cmdList) {
    this.cmdMultipleRouteList = cmdList;
}
						
private synchronized long generateMessageID() {
    if(++currentMessageID == Integer.MAX_VALUE) {
        currentMessageID = 1;
    }
    return currentMessageID;
}

public void writeToPorter(Request porterRequest) {
    porterRequest.setPriority(PORTER_PRIORITY);
    if( getPorterConnection().isValid() ) {
        getPorterConnection().write( porterRequest );
        porterMessageIDs.add(new Long(porterRequest.getUserMessageID()));
        log.info("Locate sent to porter: device (" + porterRequest.getDeviceID() + "); route ("+ porterRequest.getRouteID()+").");
    }
    else {
        log.info(porterRequest.getUserMessageID() + " REQUEST NOT SENT: CONNECTION TO PORTER IS NULL");
        log.error("("+ porterRequest.getUserMessageID() + ")REQUEST NOT SENT: CONNECTION TO PORTER IS NULL");
    }
    generateMessageID();
}

private void handleSuccessfulLocate(Return returnMsg) {
    Integer routeID = messageIDToRouteIDMap.get(new Long(returnMsg.getUserMessageID()));
    String routeName = DaoFactory.getPaoDao().getYukonPAOName(routeID.intValue());
    
    MCT400SeriesBase retMCT = new MCT400SeriesBase();
    retMCT.setDeviceID(new Integer(returnMsg.getDeviceID()));
    Request porterRequest = null;
    
    try {
        porterRequest = new Request( retMCT.getPAObjectID().intValue(), INTERVAL_COMMAND, currentMessageID );
        log.info("Successful location of device " + returnMsg.getDeviceID() + " on route " + routeName +" ("+ routeID+").");
        retMCT = (MCT400SeriesBase) Transaction.createTransaction(TransactionType.RETRIEVE, retMCT).execute();
        if(routeID != null)
            retMCT.getDeviceRoutes().setRouteID(routeID);
        else
            throw new TransactionException("Route not found for a message ID of " + returnMsg.getUserMessageID());

        log.info(retMCT.getPAOType() + "with name " + retMCT.getPAOName() + " was successfully located on route " + routeName +" ("+ routeID+").");
        Transaction.createTransaction(TransactionType.UPDATE, retMCT).execute();
        DBChangeMsg retMCTChange = new DBChangeMsg(retMCT.getPAObjectID().intValue(), DBChangeMsg.CHANGE_PAO_DB, 
        		retMCT.getPAOCategory(), retMCT.getPAOType(), DbChangeType.UPDATE);
			
		getDispatchConnection().write(retMCTChange);
		
        //interval write
        log.info("Interval write attempted: device " + returnMsg.getDeviceID() + " on route with ID " + routeID + ".");
        writeToPorter(porterRequest);
        log.info("Interval write sent to porter: device " + returnMsg.getDeviceID() + " on route " + routeName +" ("+ routeID+").");
    }
    catch( TransactionException e ) {
        if(DBFuncs.writePendingCommToFail(ImportFuncs.FAIL_DATABASE, e.toString(), retMCT.getPAObjectID()))
            log.info("Could not assign device " + retMCT.getPAObjectID() + " the route " + routeName +" ("+ routeID+").");
        else
            log.error("Could not move pending communication to fail table, but failure occurred assigning device " + porterRequest.getDeviceID() + " the route " + routeName +" ("+ routeID+").");
    }
}

/**
 * Removes meter from all immediate descendants of deviceGroupParent.
 * Adds meter to a subgroup of deviceGroupParent called groupName.
 * If groupName does not exist, a new group will be created.
 * @param deviceGroupParent - base group
 * @param groupName - child group name
 * @param yukonDevice - device to add to groupName
 * @param deviceGroupMemberEditorDao
 * @param deviceGroupEditorDao
 */
private void updateGroup(StoredDeviceGroup deviceGroupParent, String groupName, 
		YukonDevice yukonDevice, DeviceGroupMemberEditorDao deviceGroupMemberEditorDao, DeviceGroupEditorDao deviceGroupEditorDao) {
	
	if (StringUtils.isBlank(groupName)) {
		log.debug("Device(" + yukonDevice.getPaoIdentifier().toString() + ") - Group not updated - No child group name provided for: " + deviceGroupParent.getFullName());
		return;
	}
	
    boolean alreadyInGroup = false;
    
    Set<StoredDeviceGroup> deviceGroups = deviceGroupMemberEditorDao.getGroupMembership(deviceGroupParent, yukonDevice);
    for (StoredDeviceGroup deviceGroup : deviceGroups) {
        if( deviceGroup.getName().equalsIgnoreCase(groupName) ) {
            alreadyInGroup = true;
            log.debug("Device(" + yukonDevice.getPaoIdentifier().toString() + ") - Already in group:  " + deviceGroup.getFullName());
        } else {
        	if (singleGroup) {
        		// remove from any other groups so only membership in one child group remains.
	            deviceGroupMemberEditorDao.removeDevices(deviceGroup, yukonDevice);
	            log.debug("Device(" + yukonDevice.getPaoIdentifier().toString() + ") - Removed from Group: " + deviceGroup.getFullName() + ".");
        	}
        }
    }

    if (!alreadyInGroup) {
        StoredDeviceGroup deviceGroup = deviceGroupEditorDao.getGroupByName(deviceGroupParent, groupName, true);
        deviceGroupMemberEditorDao.addDevices(deviceGroup, yukonDevice);
        log.debug("Device(" + yukonDevice.getPaoIdentifier().toString() + ") - Added to Group: " + deviceGroup.getFullName() + ".");
    }
}
}