/*
 * Created on Feb 2, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yimp.importer;


import java.sql.Connection;
import java.util.ArrayList;
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
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.device.MCT400SeriesBase;
import com.cannontech.database.data.device.MCT410CL;
import com.cannontech.database.data.device.MCT410FL;
import com.cannontech.database.data.device.MCT410GL;
import com.cannontech.database.data.device.MCT410IL;
import com.cannontech.database.data.device.MCT430A;
import com.cannontech.database.data.device.MCT430S4;
import com.cannontech.database.data.device.MCT430SL;
import com.cannontech.database.data.device.MCT470;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.db.device.DeviceRoutes;
import com.cannontech.database.db.importer.ImportData;
import com.cannontech.database.db.importer.ImportFail;
import com.cannontech.database.db.importer.ImportPendingComm;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.device.range.DeviceAddressRange;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yimp.util.DBFuncs;
import com.cannontech.yimp.util.ImportFuncs;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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
            
			do {
				java.util.Date now = null;
				now = new java.util.Date();
				
				if( getNextImportTime().getTime().compareTo(now) <= 0 || isForcedImport()) {
					log.info("Starting import process.");
					
					List<ImportData> importEntries = ImportFuncs.summonImps();
					
					//if no importEntries, report this and go back to waiting
					if(importEntries.size() < 1) {
						log.info("ImportData table is empty.  No new 410s to import.");
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

    DeviceGroupService deviceGroupService = (DeviceGroupService) YukonSpringHook.getBean("deviceGroupService");
    DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = (DeviceGroupMemberEditorDao) YukonSpringHook.getBean("deviceGroupMemberEditorDao");
    DeviceGroupEditorDao deviceGroupEditorDao = (DeviceGroupEditorDao) YukonSpringHook.getBean("deviceGroupEditorDao");
    RoleDao roleDao = (RoleDao) YukonSpringHook.getBean("roleDao");
    
    StoredDeviceGroup alternateGroupBase = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.ALTERNATE);
    StoredDeviceGroup billingGroupBase = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.BILLING);
    StoredDeviceGroup collectionGroupBase = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.COLLECTION);

    StoredDeviceGroup alternateGroup = alternateGroupBase;
    StoredDeviceGroup billingGroup = billingGroupBase;
    StoredDeviceGroup collectionGroup = collectionGroupBase;
    
	for(int j = 0; j < imps.size(); j++) {
		updateDeviceID = null;
        currentEntry = imps.get(j);
	
		//mark entry for deletion
		imps.get(j).setOpCode(Transaction.DELETE);
		
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
        String errorMsgBase = "Import entry with name "+name+" ";
        List<String> errorMsg = new ArrayList<String>();
		
		if (StringUtils.isBlank(address)) {
            String error = errorMsgBase+"has a a blank address.  ";
			log.error(error);
			errorMsg.add(error);
		} else {
			try{
				// Does a parseCheck to make sure its a numical value
				Double doubleAddress = Double.parseDouble(address);				
				updateDeviceID = DBFuncs.getDeviceIDByAddress(doubleAddress.toString());
			} catch (NumberFormatException nfe) {
                String error = errorMsgBase+"has an incorrect address ("+address+").  ";
				log.error(error);
                errorMsg.add(error);
            }
		}
		
        if (StringUtils.isBlank(templateName)) {
            if (updateDeviceID == null) {
                String error = errorMsgBase+"has no 410 template specified.  ";
                log.error(error);
                errorMsg.add(error);
            }
        } else {
            template400SeriesBase = DBFuncs.get410FromTemplateName(templateName);
            if(template400SeriesBase.getDevice().getDeviceID().intValue() == -12) {
                String error = errorMsgBase+"specifies a template MCT ("+templateName+") not in the Yukon database.  ";
                log.error(error);
                errorMsg.add(error);
            }
        }
        
        if(StringUtils.isBlank(name)){
            String error = "Import entry doesn't have a name.  ";
            log.error(error);
            errorMsg.add(error);
        } else {
            if(name.length() > 60) {
                String error = errorMsgBase+"has a name with an improper length.  ";
                log.error(error);
                errorMsg.add(error);
            } else {
                if(name.indexOf(',') != -1) {
                    String error = errorMsgBase+"has a name that uses invalid characters.  ";
                    log.error(error);
                    errorMsg.add(error);
                } else {
                    if( updateDeviceID != null) {
                        notUpdate = false;
                        log.info("Address " + address + " is already used by a 400 series MCT in the Yukon database.  Attempting to modify device.");
                    }
                
                    if (DBFuncs.IsDuplicateName(name) && notUpdate) {
                        String error = errorMsgBase+"is already used by a 400 series MCT in the Yukon database.  ";
                        log.error(error);
                        errorMsg.add(error);
                    }
                }
            }
        }
        
        /*Address range check for 400 series*/
		if(template400SeriesBase instanceof MCT410IL && !DeviceAddressRange.isValidRange(DeviceTypes.MCT410IL, Long.parseLong(address))) {
            String error = errorMsgBase+"has an incorrect MCT410IL address ("+address+").  ";
            log.error(error);
            errorMsg.add(error);
		}
        else if(template400SeriesBase instanceof MCT410CL && !DeviceAddressRange.isValidRange(DeviceTypes.MCT410CL, Long.parseLong(address))) {
            String error = errorMsgBase+"has an incorrect MCT410CL address ("+address+").  ";
            log.error(error);
            errorMsg.add(error);
        }
        else if(template400SeriesBase instanceof MCT410FL && !DeviceAddressRange.isValidRange(DeviceTypes.MCT410FL, Long.parseLong(address))) {
            String error = errorMsgBase+"has an incorrect MCT410FL address ("+address+").  ";
            log.error(error);
            errorMsg.add(error);
        }
        else if(template400SeriesBase instanceof MCT410GL && !DeviceAddressRange.isValidRange(DeviceTypes.MCT410GL, Long.parseLong(address))) {
            String error = errorMsgBase+"has an incorrect MCT410GL address ("+address+").  ";
            log.error(error);
            errorMsg.add(error);
        }
        else if(template400SeriesBase instanceof MCT430S4 && !DeviceAddressRange.isValidRange(DeviceTypes.MCT430S4, Long.parseLong(address))) {
            String error = errorMsgBase+"has an incorrect MCT430LG address ("+address+").  ";
            log.error(error);
            errorMsg.add(error);
        }
        else if(template400SeriesBase instanceof MCT430A && !DeviceAddressRange.isValidRange(DeviceTypes.MCT430A, Long.parseLong(address))) {
            String error = errorMsgBase+"has an incorrect MCT430EL address ("+address+").  ";
            log.error(error);
            errorMsg.add(error);
        }
        else if(template400SeriesBase instanceof MCT430SL && !DeviceAddressRange.isValidRange(DeviceTypes.MCT430SL, Long.parseLong(address))) {
            String error = errorMsgBase+"has an incorrect MCT430IN address ("+address+").  ";
            log.error(error);
            errorMsg.add(error);
        }
        else if(template400SeriesBase instanceof MCT470 && !DeviceAddressRange.isValidRange(DeviceTypes.MCT470, Long.parseLong(address))) {
            String error = errorMsgBase+"has an incorrect MCT470 address ("+address+").  ";
            log.error(error);
            errorMsg.add(error);
        }
        
        /*New 400 series MCTs will each need a clause added above if address range
         * validation is desired
         */
        if (notUpdate) {
            if(StringUtils.isBlank(meterNumber)) {
                String error = errorMsgBase+"has no meter number.  ";
                log.error(error);
                errorMsg.add(error);
            }
            if(StringUtils.isBlank(collectionGrp)) {
                String error = errorMsgBase+"has no collection group.  ";
                log.warn(error);
                errorMsg.add(error);
            } else {
                try {
                    collectionGroup = (StoredDeviceGroup) deviceGroupService.resolveGroupName(collectionGroupBase.getFullName()+"/"+currentEntry.getCollectionGrp());
                } catch (NotFoundException nfe) {
                    String error = errorMsgBase+"has a collection group that does not exist.  Creating device group.  ";
                    log.warn(error);
                    deviceGroupEditorDao.addGroup(collectionGroupBase, DeviceGroupType.STATIC, currentEntry.getCollectionGrp());
                    collectionGroup = (StoredDeviceGroup) deviceGroupService.resolveGroupName(collectionGroupBase.getFullName()+"/"+currentEntry.getCollectionGrp());
                }
            }
            if(StringUtils.isBlank(altGrp)) {
                String error = errorMsgBase+"has no alternate group.  ";
                log.warn(error);
                errorMsg.add(error);
            } else {
                try {
                    alternateGroup = (StoredDeviceGroup) deviceGroupService.resolveGroupName(alternateGroupBase.getFullName()+"/"+currentEntry.getAltGrp());
                } catch (NotFoundException nfe) {
                    String error = errorMsgBase+"has an alternate group that does not exist.  Creating device group.  ";
                    log.warn(error);
                    deviceGroupEditorDao.addGroup(alternateGroupBase, DeviceGroupType.STATIC, currentEntry.getAltGrp());
                    alternateGroup = (StoredDeviceGroup) deviceGroupService.resolveGroupName(alternateGroupBase.getFullName()+"/"+currentEntry.getAltGrp());
                }
                
            }
            if(StringUtils.isBlank(billGrp)) {
                String warning = errorMsgBase+"has no billing group.  ";
                log.warn(warning);
                //This is not an error.  Otherwise we could not be backwards compatible, but we should note it anyways in the log file.
            } else {
                try {
                    billingGroup = (StoredDeviceGroup) deviceGroupService.resolveGroupName(billingGroupBase.getFullName()+"/"+currentEntry.getBillingGroup());
                } catch (NotFoundException nfe) {
                    String error = errorMsgBase+"has a billing group that does not exist.  Creating device group.  ";
                    log.warn(error);
                    deviceGroupEditorDao.addGroup(billingGroupBase, DeviceGroupType.STATIC, currentEntry.getBillingGroup());
                    billingGroup = (StoredDeviceGroup) deviceGroupService.resolveGroupName(billingGroupBase.getFullName()+"/"+currentEntry.getBillingGroup());
                }
            }
        }
        
        if(StringUtils.isBlank(routeName)) {
            if(StringUtils.isBlank(substationName)) {
                if(notUpdate) {
                    String error = errorMsgBase+"has no specified substation or route.  ";
                    log.error(error);
                    errorMsg.add(error);
                }
            }
        else if(!StringUtils.isBlank(substationName)) {
            routeIDsFromSub = DBFuncs.getRouteIDsFromSubstationName(substationName);
            if(routeIDsFromSub.size() < 1) {
                String error = errorMsgBase+"specifies a substation ("+substationName+") with routes not in the Yukon database.  ";
                log.error(error);
                errorMsg.add(error);
            }
        } else if(!notUpdate) {
                String error = errorMsgBase+"has no specified route ("+routeName+").  ";
                log.error(error);
                errorMsg.add(error);
            } else {
                routeID = new Integer(-12);
            }
		}
		else {
			routeID = DBFuncs.getRouteFromName(routeName);
			if(routeID.intValue() == -12) {
                String error = errorMsgBase+"specifies a route ("+routeName+") not in the Yukon database.";
				log.error(error);
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
			YukonPAObject pao = new YukonPAObject();
			pao.setPaObjectID(updateDeviceID);
    
			try {
				//update the paobject if the name has changed
				Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, pao);			    
				pao = (YukonPAObject)t.execute();

                if( !pao.getPaoName().equals(name) && !StringUtils.isBlank(name)) {
                    pao.setPaoName(name);
                    t = Transaction.createTransaction(Transaction.UPDATE, pao);
                    pao = (YukonPAObject)t.execute();
                }
        
                //update the deviceMeterGroup table if meternumber 
                DeviceMeterGroup dmg = new DeviceMeterGroup();
                dmg.setDeviceID(updateDeviceID);
                t = Transaction.createTransaction(Transaction.RETRIEVE, dmg);
                dmg = (DeviceMeterGroup)t.execute();
                if( !dmg.getMeterNumber().equals(meterNumber)) {
                    dmg.setMeterNumber(meterNumber);
                    t = Transaction.createTransaction( Transaction.UPDATE, dmg);
                    dmg = (DeviceMeterGroup)t.execute();
                }
                
                //update device groups if they changed
                YukonDevice yukonDevice = DaoFactory.getDeviceDao().getYukonDevice(pao.getPaObjectID());
                
                deviceGroupMemberEditorDao.addDevices(alternateGroup, yukonDevice);
                deviceGroupMemberEditorDao.addDevices(billingGroup, yukonDevice);
                deviceGroupMemberEditorDao.addDevices(collectionGroup, yukonDevice);
                
				//update the deviceRoutes table if the routeID has changed.
				if(routeID.intValue() != -12) {
                    DeviceRoutes dr = new DeviceRoutes();
    				dr.setDeviceID(updateDeviceID);
    				t = Transaction.createTransaction(Transaction.RETRIEVE, dr);
    				dr = (DeviceRoutes)t.execute();
    				if( dr.getRouteID().intValue() != routeID.intValue()) {
    					dr.setRouteID(routeID);
    					t = Transaction.createTransaction(Transaction.UPDATE, dr);
    					dr = (DeviceRoutes)t.execute();
    				}
                }
                /*else if(routeIDsFromSub.size() > 0) {
                    for(int i = 0; i < routeIDsFromSub.size(); i++) {
                        TODO: do we want to change the route based on substation
                         * on an update since there will be no porter communication to verify?
                         * Or will there be porter communication to verify even on an update?
                         
                    }*/
                log.info("Updated " + pao.getType() + " with name " + name + " with address " + address + ".");  
                successCounter++;
            } catch (TransactionException e) {
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
                    current400Series.getDeviceRoutes().setRouteID((Integer)routeIDsFromSub.get(0));
                }
            }
            else
                current400Series.getDeviceRoutes().setRouteID(routeID);
            
			MultiDBPersistent objectsToAdd = new MultiDBPersistent();
			objectsToAdd.getDBPersistentVector().add(current400Series);
//			log.info("Added object to Add: Device(" + current400Series.getPAObjectID() + ").");
			
			//grab the points we need off the template
			Vector points = DBFuncs.getPointsForPAO(templateID);
			boolean hasPoints = false;
			for (int i = 0; i < points.size(); i++) {
				((PointBase) points.get(i)).setPointID(DaoFactory.getPointDao().getNextPointId());
				((PointBase) points.get(i)).getPoint().setPaoID(deviceID);
				objectsToAdd.getDBPersistentVector().add((DBPersistent) points.get(i));
//				log.info("Added object to Add: Device(" + current400Series.getPAObjectID() + ") Point(" + ((PointBase)points.get(i)).getPoint().getPointID()+").");
				hasPoints = true;
			}
			
			try {
				if(hasPoints) {
                    Transaction.createTransaction(Transaction.INSERT, objectsToAdd).execute();
                }
				else {
                    Transaction.createTransaction(Transaction.INSERT, current400Series).execute();
                }
				
                YukonDevice yukonDevice = DaoFactory.getDeviceDao().getYukonDevice(deviceID);
                
                deviceGroupMemberEditorDao.addDevices(alternateGroup, yukonDevice);
                deviceGroupMemberEditorDao.addDevices(billingGroup, yukonDevice);
                deviceGroupMemberEditorDao.addDevices(collectionGroup, yukonDevice);

                //write pending communication entry for porter thread to pick up
                boolean importerCommunications = Boolean.parseBoolean(roleDao.getGlobalPropertyValue(SystemRole.BULK_IMPORTER_COMMUNICATIONS_ENABLED));
                if (importerCommunications){
                    Transaction.createTransaction(Transaction.INSERT, pc).execute();
                }
                
				successVector.add(imps.get(j));
				log.info(current400Series.getPAOType() + " with name " + name + " with address " + address + " successfully imported.");
				
				successCounter++;
			}
			catch( TransactionException e ) {
				e.printStackTrace();
				StringBuffer tempErrorMsg = new StringBuffer(e.toString());
				currentFailure = new ImportFail(address, name, routeName, 
                                                meterNumber, collectionGrp, altGrp, 
                                                templateName, tempErrorMsg.toString(), now.getTime(), 
                                                billGrp, substationName, ImportFuncs.FAIL_DATABASE);
				failures.add(currentFailure);
				log.error(current400Series.getPAOType() + " with name " + name + "failed on INSERT into database.");
			}
			finally {
				try {
					if( conn != null ) {
						conn.commit();
						conn.close();
					}
				}
				catch( java.sql.SQLException e ) {
					e.printStackTrace();
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
		e.printStackTrace();
		log.error("PREVIOUSLY USED IMPORT ENTRIES NOT REMOVED: THEY WOULD NOT DELETE!!!");
	}
	finally {
		try {
			if( conn != null ) {
				conn.commit();
				conn.close();
			}
		}
		catch( java.sql.SQLException e ) {
			e.printStackTrace();
		}
	}
	
	conn = null;
	//store failures
	try {
		//having trouble with fail adds...want to make sure these work
		for(int m = 0; m < failures.size(); m++) {
			failures.get(m).setOpCode(Transaction.INSERT);
		}		
		
		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
		ImportFuncs.storeFailures(successVector, failures, conn);
	}
	catch( java.sql.SQLException e ) {
		e.printStackTrace();
		log.error("FAILURES NOT RECORDED: THEY WOULD NOT INSERT!!!");
	}
	finally {
		try {
			if( conn != null ) {
				conn.commit();
				conn.close();
			}
		}
		catch( java.sql.SQLException e ) {
			e.printStackTrace();
		}
	}
	
	//send off a big DBChangeMsg so all Yukon entities know what's goin' on...
    //SN - Changed to only send one PAO db change message.  One for each type is not required since a 0 deviceID reloads everything.
    //SN - Still send a deviceType that is valid to represent being part of the DeviceMeterGroup table. 
    DBFuncs.generateBulkDBChangeMsg(DBChangeMsg.CHANGE_PAO_DB, "DEVICE", DeviceTypes.STRING_MCT_410IL[0], getDispatchConnection());
	DBFuncs.generateBulkDBChangeMsg(DBChangeMsg.CHANGE_POINT_DB, DBChangeMsg.CAT_POINT, PointTypes.getType(PointTypes.SYSTEM_POINT), getDispatchConnection());
	
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

public Set getPorterMessageIDs() {
    return porterMessageIDs;
}

public void setPorterMessageIDs(Set<Long> porterMessageIDs) {
    this.porterMessageIDs = porterMessageIDs;
}

public List getCmdMultipleRouteList() {
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
        retMCT = (MCT400SeriesBase) Transaction.createTransaction(Transaction.RETRIEVE, retMCT).execute();
        if(routeID != null)
            retMCT.getDeviceRoutes().setRouteID(routeID);
        else
            throw new TransactionException("Route not found for a message ID of " + returnMsg.getUserMessageID());

        log.info(retMCT.getPAOType() + "with name " + retMCT.getPAOName() + " was successfully located on route " + routeName +" ("+ routeID+").");
        Transaction.createTransaction(Transaction.UPDATE, retMCT).execute();
        DBChangeMsg retMCTChange = new DBChangeMsg(retMCT.getPAObjectID().intValue(), DBChangeMsg.CHANGE_PAO_DB, 
        		retMCT.getPAOCategory(), retMCT.getPAOType(), DBChangeMsg.CHANGE_TYPE_UPDATE);
			
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
}