 /*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.client;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.FixedDeviceGroupingHack;
import com.cannontech.common.device.groups.service.FixedDeviceGroups;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.db.importer.ImportData;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.message.dispatch.message.SystemLogHelper;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.multispeak.block.YukonFormattedBlock;
import com.cannontech.multispeak.block.impl.LoadFormattedBlockImpl;
import com.cannontech.multispeak.block.impl.OutageFormattedBlockImpl;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.event.BlockMeterReadEvent;
import com.cannontech.multispeak.event.CDEvent;
import com.cannontech.multispeak.event.CDStatusEvent;
import com.cannontech.multispeak.event.MeterReadEvent;
import com.cannontech.multispeak.event.MultispeakEvent;
import com.cannontech.multispeak.event.ODEvent;
import com.cannontech.multispeak.service.ArrayOfMeter;
import com.cannontech.multispeak.service.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.service.ConnectDisconnectEvent;
import com.cannontech.multispeak.service.ErrorObject;
import com.cannontech.multispeak.service.ExtensionsItem;
import com.cannontech.multispeak.service.LoadActionCode;
import com.cannontech.multispeak.service.Meter;
import com.cannontech.multispeak.service.MeterRead;
import com.cannontech.multispeak.service.ServiceLocation;
import com.cannontech.multispeak.service.impl.MultispeakPortFactory;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yimp.util.DBFuncs;
import com.cannontech.yukon.BasicServerConnection;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Multispeak implements MessageListener {
	
    private MultispeakFuncs multispeakFuncs;
    private BasicServerConnection porterConnection;
    private MspMeterDao mspMeterDao;
    private DBPersistentDao dbPersistentDao;
    private PaoDao paoDao;
    private DeviceDao deviceDao;
    private MspObjectDao mspObjectDao;
    private DeviceGroupService deviceGroupService;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private MeterDao meterDao;
    private SystemLogHelper _systemLogHelper = null;
        
    /** Static Flags names */
    private static String INVENTORY_FLAG = "/Meters/Flags/Inventory";
    private static String DISCONNECTED_STATUS_FLAG = "/Meters/Flags/DisconnectedStatus";
    private static String USAGE_MONITORING_FLAG = "/Meters/Flags/UsageMonitoring";

    public static final String REMOVED_METER_SUFFIX = "_REM";
    
	/** Singleton incrementor for messageIDs to send to porter connection */
	private static long messageID = 1;

	/** A map of Long(userMessageID) to MultispeakEvent values */
	private static Map<Long,MultispeakEvent> eventsMap = new HashMap<Long,MultispeakEvent>();
	 
	public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }

    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }

    public void setMspMeterDao(MspMeterDao mspMeterDao) {
        this.mspMeterDao = mspMeterDao;
    }
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    public void setMspObjectDao(MspObjectDao mspObjectDao) {
        this.mspObjectDao = mspObjectDao;
    }
    
    public void setDeviceGroupMemberEditorDao(
            DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    /**
     * Get the static instance of Multispeak (this) object.
     * Adds a message listener to the pil connection instance. 
     * @return
     */
    public void initialize() throws Exception {
        CTILogger.info("New MSP instance created");
        porterConnection.addMessageListener(this);
    }
    
    /**
	 * generate a unique mesageid, don't let it be negative
	 * @return
	 */
	private synchronized long generateMessageID() {
		if(++messageID == Long.MAX_VALUE) {
			messageID = 1;
		}
		return messageID;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.message.util.MessageListener#messageReceived(com.cannontech.message.util.MessageEvent)
	 */
	public void messageReceived(MessageEvent e)
	{
		Message in = e.getMessage();		
		if(in instanceof Return)
		{
			Return returnMsg = (Return) in;
            MultispeakEvent event = getEventsMap().get(new Long (returnMsg.getUserMessageID()) );

            if( event != null) {    // This message is one that Multispeak is waiting for...

    			synchronized(this)
    			{
                    CTILogger.info("Message Received [ID:"+ returnMsg.getUserMessageID() + 
                                    " DevID:" + returnMsg.getDeviceID() + 
                                    " Command:" + returnMsg.getCommandString() +
                                    " Result:" + returnMsg.getResultString() + 
                                    " Status:" + returnMsg.getStatus() +
                                    " More:" + returnMsg.getExpectMore()+"]");
    
                    if( returnMsg.getExpectMore() == 0) {
                        
    					CTILogger.info("Received Message From ID:" + returnMsg.getDeviceID() + " - " + returnMsg.getResultString());
    
                        boolean doneProcessing = event.messageReceived(returnMsg);
                        if (doneProcessing)
                            getEventsMap().remove(new Long(event.getPilMessageID()));
    				}
    			}
            }
		}
	}
    
    public LoadActionCode CDMeterState(MultispeakVendor mspVendor, com.cannontech.amr.meter.model.Meter meter) throws RemoteException
    {
        if ( ! porterConnection.isValid() ) {
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }

        long id = generateMessageID();
        CDStatusEvent event = new CDStatusEvent(mspVendor, id);
        
        final String meterNumber = meter.getMeterNumber();
        if (meter != null) {
            event.setMeterNumber(meterNumber);
            getEventsMap().put(new Long(id), event);
            
            CTILogger.info("Received " + meterNumber + " for CDMeterState from " + mspVendor.getCompanyName());

            String commandStr = "getstatus disconnect";
            writePilRequest(meter, commandStr, id, 15);
            logMSPActivity("getCDMeterState",
            				"(ID:" + meter.getDeviceId() + ") MeterNumber (" + meterNumber + ") - " + commandStr,
            				mspVendor.getCompanyName());    

            synchronized (event) {
                boolean timeout = !waitOnEvent(event);
                if( timeout ) {
                    
                    event.setResultMessage("Reading Timed out after " + (MultispeakDefines.REQUEST_MESSAGE_TIMEOUT/1000)+ " seconds.");
                    logMSPActivity("getCDMeterState", "Reading Timed out after 2 minutes.  No reading collected.", mspVendor.getCompanyName());
                    if(event.getLoadActionCode() == null)
                        event.setLoadActionCode(LoadActionCode.Unknown);
                }

            }
      }

        return event.getLoadActionCode();
    }
    
    /**
     * This is a workaround method for SEDC.  This method is used to perform an actual meter interrogation and then return
     * the collected reading if message recieved within 2 minutes.
     * @param mspVendor
     * @param meterNumber
     * @return
     * @throws RemoteException
     */
    public MeterRead getLatestReadingInterrogate(MultispeakVendor mspVendor, com.cannontech.amr.meter.model.Meter meter)
    {
    	long id = generateMessageID();      
        MeterReadEvent event = new MeterReadEvent(mspVendor, id, meter);
        
        getEventsMap().put(new Long(id), event);
        String commandStr = "getvalue kwh";
        if( DeviceTypesFuncs.isMCT4XX(meter.getType()) )
            commandStr = "getvalue peak";    // getvalue peak returns the peak kW and the total kWh
        
        final String meterNumber = meter.getMeterNumber();
        CTILogger.info("Received " + meterNumber + " for LatestReadingInterrogate from " + mspVendor.getCompanyName());
        writePilRequest(meter, commandStr, id, 15);
        logMSPActivity("getLatestReadingByMeterNo",
						"(ID:" + meter.getDeviceId() + ") MeterNumber (" + meterNumber + ") - " + commandStr,
						mspVendor.getCompanyName());

        synchronized (event) {
            boolean timeout = !waitOnEvent(event);
            if( timeout ) {
                logMSPActivity("getLatestReadingByMeterNo", "MeterNumber (" + meterNumber + ") - Reading Timed out after " + 
                               (MultispeakDefines.REQUEST_MESSAGE_TIMEOUT/1000) + 
                               " seconds.  No reading collected.", mspVendor.getCompanyName());
            }
        }

        return event.getDevice().getMeterRead();
    }
    
	/**
	 * Send a ping command to pil connection for each meter in meterNumbers.
	 * @param meterNumbers
	 * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
	 */
	public synchronized ErrorObject[] ODEvent(MultispeakVendor vendor, String[] meterNumbers) throws RemoteException
	{
        if( vendor.getUrl() == null || vendor.getUrl().equalsIgnoreCase(CtiUtilities.STRING_NONE))
            throw new RemoteException("OMS vendor unknown.  Please contact Yukon administrator to set the Multispeak Vendor Role Property value in Yukon.");
        
        else if ( ! porterConnection.isValid() )
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
		CTILogger.info("Received " + meterNumbers.length + " Meter(s) for Outage Verification Testing from " + vendor.getCompanyName());
		
        for (String meterNumber : meterNumbers) {
            
			com.cannontech.amr.meter.model.Meter meter;
			try {
				meter = multispeakFuncs.getMeter(vendor.getUniqueKey(), meterNumber);
				long id = generateMessageID();		
				ODEvent event = new ODEvent(vendor, id);
				getEventsMap().put(new Long(id), event);
				writePilRequest(meter, "ping", id, 13); 

			} catch (NotFoundException e) {

				ErrorObject err = multispeakFuncs.getErrorObject(meterNumber, 
                       "MeterNumber: " + meterNumber + " - Was NOT found in Yukon.",
                       "Meter");
        	   errorObjects.add(err);              
           }
		}
    		
		return toErrorObject(errorObjects);
	}
	
    /**
     * Send meter read commands to pil connection for each meter in meterNumbers.
     * @param meterNumbers
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public synchronized ErrorObject[] MeterReadEvent(MultispeakVendor vendor, String[] meterNumbers)
    {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        CTILogger.info("Received " + meterNumbers.length + " Meter(s) for MeterReading from " + vendor.getCompanyName());
        
        for (String meterNumber : meterNumbers) {
        	
            com.cannontech.amr.meter.model.Meter meter;
            try {
            	meter = multispeakFuncs.getMeter(vendor.getUniqueKey(), meterNumber);
 	        	
            	long id = generateMessageID();      
 	            MeterReadEvent event = new MeterReadEvent(vendor, id, meter);
 	            getEventsMap().put(new Long(id), event);
 	                
 	            String commandStr = "getvalue kwh";
 	            if( DeviceTypesFuncs.isMCT4XX(meter.getType()) )
 	            	commandStr = "getvalue peak"; // getvalue peak returns the peak kW and the total kWh

 	            writePilRequest(meter, commandStr, id, 13);

 	            //Second message (legacy but kept here for reminder.
// 	            MeterReadEvent event = new MeterReadEvent(vendor, id, 2);
// 	            pilRequest.setCommandString("getvalue demand");
// 	            porterConnection.write(pilRequest);
 	        } 
            catch (NotFoundException e) {
 	               
            	ErrorObject err = multispeakFuncs.getErrorObject(meterNumber, 
                        "MeterNumber: " + meterNumber + " - Was NOT found in Yukon.",
                        "Meter");
            	errorObjects.add(err);            
            }
        }
        
        return toErrorObject(errorObjects);
    }
    
    /**
     * Send meter read commands to pil connection for each meter in meterNumbers.
     * @param meterNumbers
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public synchronized ErrorObject[] BlockMeterReadEvent(MultispeakVendor vendor, String[] meterNumbers, YukonFormattedBlock block)
    {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        CTILogger.info("Received " + meterNumbers.length + " Meter(s) for BlockMeterReading from " + vendor.getCompanyName());
        
        for (String meterNumber : meterNumbers) {
            com.cannontech.amr.meter.model.Meter meter;
            
            try {
            	meter = multispeakFuncs.getMeter(vendor.getUniqueKey(), meterNumber);
                
            	long id = generateMessageID();
                
            	if (block instanceof LoadFormattedBlockImpl){
            		int returnMessages = 0;
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                    String lpCommandStr = "getvalue lp channel 1 " + format.format(new Date());
                    returnMessages++;
                    
                    
                    String commandStr = null;
                    if( DeviceTypesFuncs.isMCT410(meter.getType())) {
                        commandStr = "getvalue demand";
                        returnMessages++;
                    }
                    else if (DeviceTypesFuncs.isMCT430(meter.getType()) ||
                            DeviceTypesFuncs.isMCT470(meter.getType())) {
                        commandStr = "getvalue ied kvar";
                        returnMessages++;
                    }
                    
                    BlockMeterReadEvent event = new BlockMeterReadEvent(vendor, id, meter, block, returnMessages);
                    getEventsMap().put(new Long(id), event);

                    writePilRequest(meter, lpCommandStr, id, 13);
                    if( commandStr != null)
                        writePilRequest(meter, commandStr, id, 13);
                }
                else if ( block instanceof OutageFormattedBlockImpl) {
                    
                    BlockMeterReadEvent event = new BlockMeterReadEvent(vendor, id, meter, block);
                    getEventsMap().put(new Long(id), event);
                    String commandStr = "getvalue demand";
                    writePilRequest(meter, commandStr, id, 13);
                }
            } 
            catch (NotFoundException e) {
 	               
                ErrorObject err = multispeakFuncs.getErrorObject(meterNumber, 
                        "MeterNumber: " + meterNumber + " - Was NOT found in Yukon.",
                        "Meter");
                errorObjects.add(err);
            }
        }
        
        return toErrorObject(errorObjects);
    }
    
    /**
     * Send a ping command to pil connection for each meter in meterNumbers.
     * @param meterNumbers
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public synchronized ErrorObject[] CDEvent(MultispeakVendor vendor, ConnectDisconnectEvent [] cdEvents) throws RemoteException
    {
        if ( ! porterConnection.isValid() )
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        CTILogger.info("Received " + cdEvents.length + " Meter(s) for Connect/Disconnect from " + vendor.getCompanyName());
        
        for (int i = 0; i < cdEvents.length; i++) {
            ConnectDisconnectEvent cdEvent = cdEvents[i];
            String meterNumber = cdEvent.getObjectID();
            com.cannontech.amr.meter.model.Meter meter;
            try {
 	        	meter = multispeakFuncs.getMeter(vendor.getUniqueKey(), meterNumber);

        		if (mspMeterDao.isCDSupportedMeter(meterNumber, vendor.getUniqueKey())){
 	                long id = generateMessageID();      
 	                CDEvent event = new CDEvent(vendor, id);
 	                getEventsMap().put(new Long(id), event);
 	                
 	                String commandStr = "control "; //connect|disconnect
 	                String loadActionCode = cdEvent.getLoadActionCode().getValue();
 	                if( loadActionCode.equalsIgnoreCase(LoadActionCode._Connect))
 	                    commandStr += "connect";
 	                else if( loadActionCode.equalsIgnoreCase(LoadActionCode._Disconnect))
 	                    commandStr += "disconnect";
 	                
 	                writePilRequest(meter, commandStr, id, 13);
 	                logMSPActivity("initiateConnectDisconnect",
 	        						"(ID:" + meter.getDeviceId() + ") MeterNumber (" + meterNumber + ") - " + commandStr + " sent for ReasonCode: " + cdEvent.getReasonCode().getValue(),
 	        						vendor.getCompanyName());
 	            } else {
 	                ErrorObject err = multispeakFuncs.getErrorObject(meterNumber, 
 	                												"MeterNumber (" + meterNumber + ") - Invalid Yukon Connect/Disconnect Meter.",
 	                												"Meter");
 	                errorObjects.add(err);
 	            }
            } 
            catch (NotFoundException e) {
 	               
                ErrorObject err = multispeakFuncs.getErrorObject(meterNumber,
						 "MeterNumber (" + meterNumber + ") - Invalid Yukon MeterNumber.",
                        "Meter");
                errorObjects.add(err);
            }
        }
            
        return toErrorObject(errorObjects);
    }
    
    /*
     * Writes a request to pil for the meter and commandStr using the id for mspVendor.
     * CTILogger a message for the method name.
     */
    public void writePilRequest(com.cannontech.amr.meter.model.Meter meter, String commandStr, long id, int priority) {
        Request pilRequest = null;
        commandStr += " update";
        commandStr += " noqueue";
        pilRequest = new Request(meter.getDeviceId(), commandStr, id);
        pilRequest.setPriority(priority);
        porterConnection.write(pilRequest);
    }
    
    /**
     * Returns true if event processes without timeing out, false if event times out.
     * @param event
     * @return
     */
    public boolean waitOnEvent(MultispeakEvent event) {
        
        long millisTimeOut = 0; //
        while (!event.isPopulated() && millisTimeOut < MultispeakDefines.REQUEST_MESSAGE_TIMEOUT)  //quit after timeout
        {
            try {
                Thread.sleep(1000);
                millisTimeOut += 1000;
            } catch (InterruptedException e) {
                CTILogger.error(e);
            }
        }
        if( millisTimeOut >= MultispeakDefines.REQUEST_MESSAGE_TIMEOUT) {// this broke the loop, more than likely, have to kill it sometime
            return false;
        }
        return true;
    }

	/**
	 * @return
	 */
	public static Map<Long,MultispeakEvent> getEventsMap() {
		return eventsMap;
	}
    
    public ErrorObject[] initiateDisconnectedStatus(MultispeakVendor mspVendor, String[] meterNos) {
        
        return addFlag(mspVendor, meterNos, DISCONNECTED_STATUS_FLAG, "initiateDisconnectedStatus");
    }
    
    public ErrorObject[] initiateUsageMonitoringStatus(MultispeakVendor mspVendor, String[] meterNos) {
        
        return addFlag(mspVendor, meterNos, USAGE_MONITORING_FLAG, "initiateUsageMonitoring");
    }

    public ErrorObject[] cancelDisconnectedStatus(MultispeakVendor mspVendor, String[] meterNos) {

        return removeFlag(mspVendor, meterNos, DISCONNECTED_STATUS_FLAG, "cancelDisconnectedStatus");
    }

    public ErrorObject[] cancelUsageMonitoringStatus(MultispeakVendor mspVendor, String[] meterNos) {

        return removeFlag(mspVendor, meterNos, USAGE_MONITORING_FLAG, "cancelUsageMonitoring");
    }
    
    public ErrorObject[] addMeterObject(MultispeakVendor mspVendor, Meter[] addMeters) throws RemoteException{
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        for  (int i = 0; i < addMeters.length; i++){
            Meter mspMeter = addMeters[i];
            String meterNo = mspMeter.getMeterNo().trim();
            String mspAddress = mspMeter.getNameplate().getTransponderID().trim();
            ServiceLocation mspServiceLocation = null;
            
            //Find Meter by MeterNumber in Yukon
            com.cannontech.amr.meter.model.Meter meter = null;
            try {
 	           meter = multispeakFuncs.getMeter(mspVendor.getUniqueKey(), meterNo);
            } 
            catch (NotFoundException e) {
            	//Do nothing, its okay (almost better) if one doesn't exist
            }
            
            if( meter != null) {    //Meter exists in Yukon
                LiteYukonPAObject liteYukonPaobject = paoDao.getLiteYukonPAO(meter.getDeviceId());
                YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
                if (yukonPaobject instanceof MCTBase){
                    DeviceMeterGroup deviceMeterGroup = ((MCTBase)yukonPaobject).getDeviceMeterGroup();
                    DeviceCarrierSettings deviceCarrierSettings = ((MCTBase)yukonPaobject).getDeviceCarrierSettings();
                    
                    boolean disabled = yukonPaobject.isDisabled();
                    String address = deviceCarrierSettings.getAddress().toString();
                    if( address.equalsIgnoreCase(mspMeter.getNameplate().getTransponderID())) { //Same MeterNumber and Address
                        if( disabled ) {        //Disabled Meter found
                        	//Load the CIS serviceLocation.
                        	mspServiceLocation = mspObjectDao.getMspServiceLocation(meterNo, mspVendor);
                        	String billingCycle = mspServiceLocation.getBillingCycle();
                            
                            //Enable Meter and update applicable fields.
                            removeFlag(meter, INVENTORY_FLAG, "MeterAddNotification", mspVendor);

                            //update the billing group from CIS billingCyle
                            updateBillingCyle(billingCycle, meter, "MeterAddNotification", mspVendor);
                            
                            String oldPaoName = yukonPaobject.getPAOName();
                            yukonPaobject.setPAOName(yukonPaobject.getPAOName().replaceAll(REMOVED_METER_SUFFIX, ""));
                            
                            yukonPaobject.setDisabled(false);
                            String logTemp = "";
                            
                            String newPaoName = yukonPaobject.getPAOName();
                            final int paoAlias = multispeakFuncs.getPaoNameAlias();
                            final String paoAliasStr = MultispeakVendor.paoNameAliasStrings[paoAlias];
                            newPaoName = getPaoNameFromMspMeter(mspMeter, paoAlias, newPaoName);
                            
                            if( newPaoName == null)
                            	logTemp = "; PAOName(" + paoAliasStr + ") NO CHANGE - MSP " + paoAliasStr + " IS EMPTY.";
                            else
                                logTemp = "; PAOName(" + paoAliasStr + ")(OLD:" + oldPaoName + " NEW:" + newPaoName + ").";

                            yukonPaobject.setPAOName(newPaoName);
	                                
                            dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                            logMSPActivity("MeterAddNotification",
                                           "MeterNumber(" + meterNo + ") - Meter Enabled" + logTemp, 
                                           mspVendor.getCompanyName());
                            //TODO Read the Meter.
                        }
                        else {  //Meter is currently enabled in Yukon
                            ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                             "Warning: MeterNumber(" + meterNo + ") - Meter is already active.  No updates were made.",
                                                                             "Meter");
                            errorObjects.add(err);
                            logMSPActivity("MeterAddNotification",
                                           "MeterNumber(" + meterNo + ") - Meter is already active.  No updates were made.",
                                           mspVendor.getCompanyName());
                        }
                        
                    } else {    //Address is different!
                        if( disabled ) {        //Disabled Meter found

                            //Lookup meter by mspAddress
                            List liteYukonPaoByAddressList = paoDao.getLiteYukonPaobjectsByAddress(new Integer(mspAddress).intValue());
                            
                            if (liteYukonPaoByAddressList.isEmpty()) {  //New Hardware
                                //Need to "remove" the existing (disabled) Meter Number
                                //deviceMeterGroup.setMeterNumber(meterNo + REMOVED_METER_NUMBER_SUFFIX); DO NOT DO ANYMORE, IT'S A PAIN!
                                yukonPaobject.setPAOName(yukonPaobject.getPAOName() + REMOVED_METER_SUFFIX);
                                
//                              Add meter to Inventory
                                addFlag(meter, INVENTORY_FLAG, "MeterAddNotification", mspVendor);
                                
                                dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                                logMSPActivity("MeterAddNotification",
                                               "MeterNumber(" + meterNo + ") - _REM disabled MeterNo with conflicting Address; ", 
                                               mspVendor.getCompanyName());
                                
//                                  Find a valid Template!
                                String templateName = getMeterTemplate(mspMeter);
                                
                                //Find template object in Yukon
                                LiteYukonPAObject liteYukonPaobjectTemplate = deviceDao.getLiteYukonPaobjectByDeviceName(templateName);
                                if( liteYukonPaobjectTemplate == null){ 
                                    ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                                     "Error: MeterNumber(" + meterNo + ")- AMRMeterType(" + templateName + ") not found in Yukon. Meter was NOT added.",
                                                                                     "Meter");
                                    errorObjects.add(err);
                                    logMSPActivity("MeterAddNotification",
                                                   "MeterNumber(" + meterNo + ") - AMRMeterType(" + templateName + ") not found in Yukon. Meter was NOT added.",
                                                   mspVendor.getCompanyName());
                                }
                                else {    //Valid template found
                                	//Load the CIS serviceLocation.
                                	mspServiceLocation = mspObjectDao.getMspServiceLocation(meterNo, mspVendor);
                                    //Find a valid substation
                                    if( mspMeter.getUtilityInfo() == null || mspMeter.getUtilityInfo().getSubstationName()== null){
                                        addImportData(mspMeter, mspServiceLocation, templateName, "");
                                        logMSPActivity("MeterAddNotification",
                                                       "MeterNumber(" + meterNo + ") - Meter inserted into ImportData for processing.", 
                                                       mspVendor.getCompanyName());
                                    } else {
                                        String substationName = mspMeter.getUtilityInfo().getSubstationName();
                                        List routeNames = DBFuncs.getRouteNamesFromSubstationName(substationName);
                                        if( routeNames.isEmpty()){
                                            ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                                             "Error: MeterNumber(" + meterNo + ") - SubstationName(" + substationName + ") - no RouteMappings found in Yukon.  Meter was NOT added",
                                                                                             "Meter");
                                            errorObjects.add(err);
                                            logMSPActivity("MeterAddNotification",
                                                           "MeterNumber(" + meterNo + ") - SubstationName(" + substationName + ") not found in Yukon. Meter was NOT added.", 
                                                           mspVendor.getCompanyName());
                                        }
                                        else {
                                            addImportData(mspMeter, mspServiceLocation, templateName, substationName);
                                            logMSPActivity("MeterAddNotification",
                                                           "MeterNumber(" + meterNo + ") - Meter inserted into ImportData for processing.", 
                                                           mspVendor.getCompanyName());
                                        }
                                    }
                                }                                    
                                
                            } else {    //Meter Number and Address both exist...on different objects! 
                                LiteYukonPAObject liteYukonPaoByAddress = (LiteYukonPAObject)liteYukonPaoByAddressList.get(0);
                                YukonPAObject yukonPaobjectByAddress = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaoByAddress);
                                
                                if (yukonPaobjectByAddress.isDisabled()) {  //Address object is disabled, so we can update and activate the Meter Number object
                                	//Load the CIS serviceLocation.
                                	mspServiceLocation = mspObjectDao.getMspServiceLocation(meterNo, mspVendor);
                                	String billingCycle = mspServiceLocation.getBillingCycle();
                                    //TODO deleteRawPointHistory(yukonPaobject);

//                                  Remove meter from Inventory
                                    removeFlag(meter, INVENTORY_FLAG, "MeterAddNotification", mspVendor);

//                                  update the billing group from CIS billingCyle
                                    updateBillingCyle(billingCycle, meter, "MeterAddNotification", mspVendor);
                                    
                                    String oldPaoName = yukonPaobject.getPAOName();
                                    yukonPaobject.setPAOName(yukonPaobject.getPAOName().replaceAll(REMOVED_METER_SUFFIX, ""));
                                    
                                    String oldAddress = String.valueOf(deviceCarrierSettings.getAddress());
                                    deviceCarrierSettings.setAddress(Integer.valueOf(mspAddress));
                                    
                                    yukonPaobject.setDisabled(false);
                                    String logTemp = "";

                                    String newPaoName = yukonPaobject.getPAOName();
                                    final int paoAlias = multispeakFuncs.getPaoNameAlias();
                                    final String paoAliasStr = MultispeakVendor.paoNameAliasStrings[paoAlias];
                                    newPaoName = getPaoNameFromMspMeter(mspMeter, paoAlias, newPaoName);
                                    
                                    if( newPaoName == null)
                                    	logTemp = "; PAOName(" + paoAliasStr + ") NO CHANGE - MSP " + paoAliasStr + " IS EMPTY.";
                                    else
                                        logTemp = "; PAOName(" + paoAliasStr + ")(OLD:" + oldPaoName + " NEW:" + newPaoName + ").";

                                    yukonPaobject.setPAOName(newPaoName);
                                    
                                    dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                                    logMSPActivity("MeterAddNotification",
                                                   "MeterNumber(" + meterNo + ") - Address(OLD:" + oldAddress + " NEW:" + deviceCarrierSettings.getAddress().toString() + ").", 
                                                   mspVendor.getCompanyName());
                                    logMSPActivity("MeterAddNotification",
                                                   "MeterNumber(" + meterNo + ") - Meter Enabled." + logTemp, 
                                                   mspVendor.getCompanyName());

                                    //TODO Read the Meter.
                                                                        
                                } else {    //Address is already active
                                    ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                                     "Error:  MeterNumber(" + meterNo + ") with Address(" + mspAddress + 
                                                                                     ") is already active on another enabled meter with MeterNumber(" + 
                                                                                     ((MCTBase)yukonPaobjectByAddress).getDeviceMeterGroup().getMeterNumber() + "). No updates were made.",
                                                                                     "Meter");
                                    errorObjects.add(err);
                                    logMSPActivity("MeterAddNotification",
                                                   "Error:  MeterNumber(" + meterNo + ") with Address(" + mspAddress +
                                                   ") is already active on another enabled meter with MeterNumber (" +
                                                   ((MCTBase)yukonPaobjectByAddress).getDeviceMeterGroup().getMeterNumber() + "). No updates were made.",
                                                   mspVendor.getCompanyName());
                                }
                            }
                        }
                        else {  //Meter is currently enabled in Yukon
                            ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                             "Error: MeterNumber(" + meterNo + ") - Meter is already active with a different " +
                                                                             "Address(" + deviceCarrierSettings.getAddress().toString() +"). No updates were made.",
                                                                             "Meter");
                            errorObjects.add(err);
                            logMSPActivity("MeterAddNotification",
                                           "Error: MeterNumber(" + meterNo + ") - Meter is already active with a different " +
                                           "Address(" + deviceCarrierSettings.getAddress().toString() +"). No updates were made.", 
                                           mspVendor.getCompanyName());                                
                        }
                    }
                }
            }else { //Meter Number not currently found in Yukon
                //Lookup meter by mspAddress
                List liteYukonPaoByAddressList = paoDao.getLiteYukonPaobjectsByAddress(new Integer(mspAddress).intValue());
                if (liteYukonPaoByAddressList.isEmpty()) {  //New Hardware
                    //Find a valid Template!
                	String templateName = getMeterTemplate(mspMeter);

                    LiteYukonPAObject liteYukonPaobjectTemplate = deviceDao.getLiteYukonPaobjectByDeviceName(templateName);
                    if( liteYukonPaobjectTemplate == null){ 
                        ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                         "Error: MeterNumber(" + meterNo + ")- AMRMeterType(" + templateName + ") not found in Yukon. Meter was NOT added.",
                                                                         "Meter");
                        errorObjects.add(err);
                        logMSPActivity("MeterAddNotification",
                                       "MeterNumber(" + meterNo + ") - AMRMeterType(" + templateName + ") not found in Yukon. Meter was NOT added.", 
                                       mspVendor.getCompanyName());
                    }
                    else {    //Valid template found
                    	//Load the CIS serviceLocation.
                    	mspServiceLocation = mspObjectDao.getMspServiceLocation(meterNo, mspVendor);
                        //Find a valid substation
                        if( mspMeter.getUtilityInfo() == null || mspMeter.getUtilityInfo().getSubstationName()== null){
                            addImportData(mspMeter, mspServiceLocation, templateName, "");
                            logMSPActivity("MeterAddNotification",
                                           "MeterNumber(" + meterNo + ") - Meter inserted into ImportData for processing.",
                                           mspVendor.getCompanyName());
                        } else {
                            String substationName = mspMeter.getUtilityInfo().getSubstationName();
                            List routeNames = DBFuncs.getRouteNamesFromSubstationName(substationName);
                            if( routeNames.isEmpty()){
                                ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                                 "Error: MeterNumber(" + meterNo + ") - SubstationName(" + substationName + ") - no RouteMappings found in Yukon.  Meter was NOT added",
                                                                                 "Meter");
                                errorObjects.add(err);
                                logMSPActivity("MeterAddNotification",
                                               "MeterNumber(" + meterNo + ") - SubstationName(" + substationName + ") not found in Yukon. Meter was NOT added.", 
                                               mspVendor.getCompanyName());
                            }
                            else {
                                addImportData(mspMeter, mspServiceLocation, templateName, substationName);
                                logMSPActivity("MeterAddNotification",
                                               "MeterNumber(" + meterNo + ") - Meter inserted into ImportData for processing.", 
                                               mspVendor.getCompanyName());
                            }
                        }
                    }
                } else { // mspAddress already exists in Yukon 
                    LiteYukonPAObject liteYukonPaoByAddress = (LiteYukonPAObject)liteYukonPaoByAddressList.get(0);
                    YukonPAObject yukonPaobjectByAddress = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaoByAddress);
                    if (yukonPaobjectByAddress.isDisabled()) {  //Address object is disabled, so we can update and activate the Address object
                        //TODO deleteRawPointHistory(yukonPaobject);
                    	//Load the CIS serviceLocation.
                    	mspServiceLocation = mspObjectDao.getMspServiceLocation(meterNo, mspVendor);
                    	String billingCycle = mspServiceLocation.getBillingCycle();
                    	String oldPaoName = yukonPaobjectByAddress.getPAOName();
                    	
                        yukonPaobjectByAddress.setDisabled(false);
                        String logTemp = "";

                        String newPaoName = yukonPaobjectByAddress.getPAOName();
                        final int paoAlias = multispeakFuncs.getPaoNameAlias();
                        final String paoAliasStr = MultispeakVendor.paoNameAliasStrings[paoAlias];
                        newPaoName = getPaoNameFromMspMeter(mspMeter, paoAlias, newPaoName);
                        
                        if( newPaoName == null)
                        	logTemp = "; PAOName(" + paoAliasStr + ") NO CHANGE - MSP " + paoAliasStr + " IS EMPTY.";
                        else
                            logTemp = "; PAOName(" + paoAliasStr + ")(OLD:" + oldPaoName + " NEW:" + newPaoName + ").";

                        yukonPaobjectByAddress.setPAOName(newPaoName);
                        
                        DeviceMeterGroup deviceMeterGroup = ((MCTBase)yukonPaobjectByAddress).getDeviceMeterGroup();
                        
//                      Remove meter from Inventory
                        removeFlag(meter, INVENTORY_FLAG, "MeterAddNotification", mspVendor);

//                      update the billing group from CIS billingCyle
                        updateBillingCyle(billingCycle, meter, "MeterAddNotification", mspVendor);

                        String oldMeterNo = deviceMeterGroup.getMeterNumber();
                        deviceMeterGroup.setMeterNumber(meterNo);

                        yukonPaobjectByAddress.setPAOName(yukonPaobjectByAddress.getPAOName().replaceAll(REMOVED_METER_SUFFIX, ""));
                        
                        dbPersistentDao.performDBChange(yukonPaobjectByAddress, Transaction.UPDATE);
                        logMSPActivity("MeterAddNotification",
                                       "MeterNumber(" + meterNo + ") - MeterNumber(OLD:" +oldMeterNo + "); Meter Enabled" + logTemp,
                                       mspVendor.getCompanyName());
                        //TODO Read the Meter.
                                                            
                    } else {    //Address is already active
                        ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                         "Error: Address(" + mspAddress + ") "+ 
                                                                         " - is already active on another enabled meter with MeterNumber(" +
                                                                         ((MCTBase)yukonPaobjectByAddress).getDeviceMeterGroup().getMeterNumber() +"). Meter was NOT added.",
                                                                         "Meter");
                        errorObjects.add(err);
                        
                        logMSPActivity("MeterAddNotification",
                                       "Error:  Address(" + mspAddress + ") - is already active on another enabled meter with MeterNumber(" +
                                       ((MCTBase)yukonPaobjectByAddress).getDeviceMeterGroup().getMeterNumber() + "). Meter was NOT added.",
                                       mspVendor.getCompanyName());                            
                    }
                }
            }
        }//end for

        return toErrorObject(errorObjects);
    }
    
    /**
     * Removes (disables) a list of meters in Yukon.
     * @param mspVendor
     * @param removeMeters
     * @return
     */
    public ErrorObject[] removeMeterObject(MultispeakVendor mspVendor, Meter[] removeMeters) {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        for  (int i = 0; i < removeMeters.length; i++){
            Meter mspMeter = removeMeters[i];
            String meterNo = mspMeter.getMeterNo().trim();
            //Lookup meter in Yukon by msp meter number
            com.cannontech.amr.meter.model.Meter meter;
            try {
            	meter = multispeakFuncs.getMeter(mspVendor.getUniqueKey(), meterNo);
            	
            	//Meter exists if no exception was thrown
                LiteYukonPAObject liteYukonPaobject = paoDao.getLiteYukonPAO(meter.getDeviceId());
                YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
                if (yukonPaobject instanceof MCTBase){
                    boolean disabled = yukonPaobject.isDisabled();
                    if( !disabled ) {//enabled
                        yukonPaobject.setDisabled(true);
                        DeviceMeterGroup deviceMeterGroup = ((MCTBase)yukonPaobject).getDeviceMeterGroup();
                        
                        String oldMeterNo = deviceMeterGroup.getMeterNumber();
                        String oldPaoName = yukonPaobject.getPAOName();
//                        if( oldMeterNo.indexOf(REMOVED_METER_NUMBER_SUFFIX) < 0)	//DON"T DO ANYMORE!!!
//                            deviceMeterGroup.setMeterNumber(oldMeterNo + REMOVED_METER_NUMBER_SUFFIX);

//                      Added meter to Inventory
                        addFlag(meter, INVENTORY_FLAG, "MeterRemovedNotification", mspVendor);

                        if( oldPaoName.indexOf(REMOVED_METER_SUFFIX) < 0)
                            yukonPaobject.setPAOName(oldPaoName + REMOVED_METER_SUFFIX);
                        
                        dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                        logMSPActivity("MeterRemoveNotification",
                                       "MeterNumber(" + oldMeterNo + ") - MeterNumber(NEW:" +deviceMeterGroup.getMeterNumber() +");",
                                       mspVendor.getCompanyName());
                        logMSPActivity("MeterRemoveNotification",
                                       "MeterNumber(" + meterNo + ") - Meter Disabled;" +
                                       "PaoName(OLD:" + oldPaoName + " NEW:" + yukonPaobject.getPAOName() + ").",
                                       mspVendor.getCompanyName());
                    }
                    else {
                        ErrorObject err = multispeakFuncs.getErrorObject(meterNo,
                                                                         "Warning: MeterNumber(" + meterNo + ") - Meter is already disabled. No updates were made.",
                                                                         "Meter");
                        errorObjects.add(err);
                        logMSPActivity("MeterRemoveNotification",
                                       "Warning: MeterNumber(" + meterNo + ") - Meter is already disabled. No updates were made.",
                                       mspVendor.getCompanyName());
                    }
                }
            } 
            catch (NotFoundException e) {
 	               
            	ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                        "Error: MeterNumber(" + meterNo + ") - Meter was NOT found in Yukon. No updates were made.",
                        "Meter");
				errorObjects.add(err);
				logMSPActivity("MeterRemoveNotification",
								"Error: MeterNumber(" + meterNo + ") - Meter was NOT found in Yukon. No updates were made.",
								mspVendor.getCompanyName());              
            } 
        }
        
        return toErrorObject(errorObjects);
    }
    
    /**
     * 
     * @param mspVendor
     * @param serviceLocations
     * @return
     */
    public ErrorObject[] updateServiceLocation(MultispeakVendor mspVendor, ServiceLocation[] serviceLocations) {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        for  (int i = 0; i < serviceLocations.length; i++){
            ServiceLocation mspServiceLocation = serviceLocations[i];
            String billingCycle = mspServiceLocation.getBillingCycle();
            String serviceLocationStr = mspServiceLocation.getObjectID();
            String paoName = null;
            LiteYukonPAObject liteYukonPaobject = null;
            String logString = MultispeakVendor.paoNameAliasStrings[multispeakFuncs.getPaoNameAlias()];
            
            liteYukonPaobject = getLiteYukonPaobjectFromMsp(mspServiceLocation, mspVendor);

            String logTemp = "";
            if( liteYukonPaobject != null) {
                YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
                if (yukonPaobject instanceof MCTBase) {
                    DeviceMeterGroup deviceMeterGroup = ((MCTBase)yukonPaobject).getDeviceMeterGroup();
                    boolean update = false;
                    
                    String oldPaoName = yukonPaobject.getPAOName();
                    final int paoAlias = multispeakFuncs.getPaoNameAlias();
                    final String paoAliasStr = MultispeakVendor.paoNameAliasStrings[paoAlias];
                    String newPaoName = getPaoNameFromMspServiceLocation(mspServiceLocation, paoAlias, oldPaoName);
                    
                    if( newPaoName == null)
                    	logTemp = "; PAOName(" + paoAliasStr + ") NO CHANGE - MSP " + paoAliasStr + " IS EMPTY.";
                    else{
                        logTemp = "; PAOName(" + paoAliasStr + ")(OLD:" + oldPaoName + " NEW:" + newPaoName + ").";
                        update=true;
                    }

                    //update the billing group from CIS billingCyle
                    com.cannontech.amr.meter.model.Meter meter = meterDao.getForId(liteYukonPaobject.getYukonID());
                    updateBillingCyle(billingCycle, meter, "ServiceLocationChangedNotificatoin", mspVendor);
                    
                    if (update) {
                        dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                        logMSPActivity("ServiceLocationChangedNotification",
                                       "MeterNumber(" + deviceMeterGroup.getMeterNumber()+ ") - " + logTemp,
                                       mspVendor.getCompanyName());
                    }


                }
            } else {
                ErrorObject err = multispeakFuncs.getErrorObject(serviceLocationStr, 
                                                                 "ServiceLocation(" + serviceLocationStr + ") - ServiceLocation was NOT found in Yukon.", 
                                                                 "ServiceLocation");
                errorObjects.add(err);
                logMSPActivity("ServiceLocationChangedNotification",
                               logString + "(" + paoName + ") - was NOT found in Yukon",
                               mspVendor.getCompanyName());
            }
        }

        return toErrorObject(errorObjects);
    }

    public SystemLogHelper getSystemLogHelper() {
        if (_systemLogHelper == null)
            _systemLogHelper = new SystemLogHelper(PointTypes.SYS_PID_MULTISPEAK);
        return _systemLogHelper;
    }
    
    private void addImportData(Meter mspMeter, ServiceLocation mspServiceLocation, String templateName, String substationName){
        String address = mspMeter.getNameplate().getTransponderID();
        String meterNumber = mspMeter.getMeterNo();
        String billingGroup = StringUtils.isBlank(mspServiceLocation.getBillingCycle())? "":mspServiceLocation.getBillingCycle();

        final int paoAlias = multispeakFuncs.getPaoNameAlias();
        String paoName = getPaoNameFromMspMeter(mspMeter, paoAlias, meterNumber);

        ImportData importData = new ImportData(address, paoName, "", meterNumber, 
                                               "Default", "Default", templateName, billingGroup, substationName);
        try {
            Transaction.createTransaction(Transaction.INSERT, importData).execute();
        } catch (TransactionException e) {
            CTILogger.error(e);
        }
    }
    
    private void logMSPActivity(String method, String description, String userName) {
        getSystemLogHelper().log(PointTypes.SYS_PID_MULTISPEAK, method, description, userName, SystemLog.TYPE_MULTISPEAK); 
    }
    
  
   private String getMeterTemplate(Meter mspMeter) {
       String templateName = MultispeakDefines.TEMPLATE_NAME_DEFAULT;
       boolean loaded = false;
       
       if( mspMeter.getAMRDeviceType() != null) {
           templateName = mspMeter.getAMRDeviceType();
           loaded = true;
       }
       
       if (!loaded) {
           if( mspMeter.getExtensionsList() != null) {
               ExtensionsItem [] eItems = mspMeter.getExtensionsList().getExtensionsItem();
               for (int j = 0; j < eItems.length; j++) {
                   ExtensionsItem eItem = eItems[j];
                   String extName = eItem.getExtName();
                   if ( extName.equalsIgnoreCase("AMRMeterType")) {
                       templateName = eItem.getExtValue();
                       loaded = true;
                   }
               }
           }
       }

       return templateName;
   }
   
   private void updateBillingCyle(String newBilling, com.cannontech.amr.meter.model.Meter meter, String logActionStr, MultispeakVendor mspVendor) {
       
       // update the billing group from CIS billingCyle
       FixedDeviceGroupingHack hacker = (FixedDeviceGroupingHack) YukonSpringHook.getBean("fixedDeviceGroupingHack"); 
       String oldBilling = hacker.getGroupForDevice(FixedDeviceGroups.BILLINGGROUP, meter);
       
       if (!StringUtils.isBlank(newBilling) && !oldBilling.equalsIgnoreCase(newBilling)) {
       
           //Remove from old billing
           DeviceGroup billingGroup = deviceGroupService.resolveGroupName(oldBilling);
           deviceGroupMemberEditorDao.removeDevices((StoredDeviceGroup)billingGroup, Collections.singletonList(meter));
           
           //Add to new billing group
           hacker.setGroup(FixedDeviceGroups.BILLINGGROUP, meter, newBilling);
           logMSPActivity(logActionStr,
                          "MeterNumber(" + meter.getMeterNumber()+ ") - BillingGroup(OLD:" + oldBilling + " NEW:" + newBilling +").",
                          mspVendor.getCompanyName());
       }
   }
   
   private void removeFlag(com.cannontech.amr.meter.model.Meter meter, String flag, String logActionStr, MultispeakVendor mspVendor){

       DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(flag);
       deviceGroupMemberEditorDao.removeDevices((StoredDeviceGroup)deviceGroup, Collections.singletonList(meter));
       
       logMSPActivity(logActionStr,
                      "MeterNumber(" + meter.getMeterNumber() + ") - Removed from Inventory flag; ",
                      mspVendor.getCompanyName());
   }
   
   private void addFlag(com.cannontech.amr.meter.model.Meter meter, String flag, String logActionStr, MultispeakVendor mspVendor){

       DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(flag);
       deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup)deviceGroup, Collections.singletonList(meter));
       
       logMSPActivity(logActionStr,
                      "MeterNumber(" + meter.getMeterNumber() + ") - Added to " + flag, 
                      mspVendor.getCompanyName());

   }
   
   private ErrorObject[] addFlag(MultispeakVendor mspVendor, String[] meterNos, String flag, String logActionStr) {
       Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
       
       DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(flag);
       
       for  (int i = 0; i < meterNos.length; i++){
           String meterNo = meterNos[i];
           com.cannontech.amr.meter.model.Meter meter;

           try {
	           meter = multispeakFuncs.getMeter(mspVendor.getUniqueKey(), meterNo);

	           try {
                   deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup)deviceGroup, Collections.singletonList(meter) );

                   logMSPActivity(logActionStr,
                                  "MeterNumber(" + meterNo + ") - Added to " + flag, 
                                  mspVendor.getCompanyName());
               }catch (DataAccessException e) {
                   ErrorObject err = multispeakFuncs.getErrorObject(meterNo,
                                                                    "MeterNumber: " + meterNo + " - Is already in " + flag + ".  Cannot initiate change.",
                                                                    "Meter");
                   errorObjects.add(err);
               } 
           } 
           catch (NotFoundException e) {
	               
               ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                       "MeterNumber: " + meterNo + " - Was NOT found in Yukon.",
                       "Meter");
               errorObjects.add(err);
           }
       }
       
       return toErrorObject(errorObjects);
   }
   
   public ErrorObject[] removeFlag(MultispeakVendor mspVendor, String[] meterNos, String flag, String logActionStr) {

       Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
       List<com.cannontech.amr.meter.model.Meter> meters =
           new ArrayList<com.cannontech.amr.meter.model.Meter>(meterNos.length); 
       
       DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(flag);
       
       for  (int i = 0; i < meterNos.length; i++){
           String meterNo = meterNos[i];
           com.cannontech.amr.meter.model.Meter meter;
           try {
	           meter = multispeakFuncs.getMeter(mspVendor.getUniqueKey(), meterNo);
               meters.add(meter);
           } 
           catch (NotFoundException e) {
	               
               ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                "MeterNumber: " + meterNo + " - Was NOT found in Yukon.",
                                                                "Meter");
               errorObjects.add(err);              
           }
       }
       
       deviceGroupMemberEditorDao.removeDevices((StoredDeviceGroup)deviceGroup, meters);

       logMSPActivity(logActionStr,
                      "Removed " + meters.size() + " meters to from " + flag, 
                      mspVendor.getCompanyName());
       
       return toErrorObject(errorObjects);
   }

   public ErrorObject[] toErrorObject(Vector<ErrorObject> errorObjects) {
       
       if( !errorObjects.isEmpty())
       {
           ErrorObject[] errors = new ErrorObject[errorObjects.size()];
           errorObjects.toArray(errors);
           return errors;
       }
       return new ErrorObject[0];
   }
       /**
    * Returns a serviceLocation for the meterNo.
    * If the interface/method is not supported by mspVendor, or if no object is found, an empty ServiceLocation object is returned.
    * @param mspVendor
    * @param meterNo
    * @return
    */
   private ServiceLocation getServiceLocation(MultispeakVendor mspVendor, String meterNo) {
	   // Load the CIS serviceLocation.
   		ServiceLocation mspServiceLocation = new ServiceLocation();
   		String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_MR_STR);
    	try {
    		CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(mspVendor);
   			mspServiceLocation =  port.getServiceLocationByMeterNo(meterNo);
    	} catch (RemoteException e) {
    		CTILogger.error("TargetService: " + endpointURL + " - getServiceLocationByMeterNo (" + mspVendor.getCompanyName() + ")");
			CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
			CTILogger.info("A default(empty) is being used for ServiceLocation");
       }
       return mspServiceLocation;
   }
   
	private static String getPaoNameFromMspMeter(Meter mspMeter, int paoNameAlias, String defaultValue) {
			
		switch (paoNameAlias) {
			case MultispeakVendor.ACCOUNT_NUMBER_PAONAME:
			{
			   if( mspMeter.getUtilityInfo() == null ||
					   StringUtils.isBlank(mspMeter.getUtilityInfo().getAccountNumber()))
                   return null;
               return mspMeter.getUtilityInfo().getAccountNumber();
			}	
			case MultispeakVendor.SERVICE_LOCATION_PAONAME:
			{
				if( mspMeter.getUtilityInfo() == null ||
						StringUtils.isBlank(mspMeter.getUtilityInfo().getServLoc()))
                   return null;
               return mspMeter.getUtilityInfo().getServLoc();
			}	
			case MultispeakVendor.CUSTOMER_PAONAME:
			{
				if( mspMeter.getUtilityInfo() == null ||
						StringUtils.isBlank(mspMeter.getUtilityInfo().getCustID()))
                   return null;
               return mspMeter.getUtilityInfo().getCustID();
			}
			case MultispeakVendor.EA_LOCATION_PAONAME:
			{
				if( mspMeter.getUtilityInfo() == null || mspMeter.getUtilityInfo().getEaLoc() == null ||
						StringUtils.isBlank(mspMeter.getUtilityInfo().getEaLoc().getName()))
                   return null;
               return mspMeter.getUtilityInfo().getEaLoc().getName();
			}				
			default:
				return defaultValue;
		}
	}
	
	private String getPaoNameFromMspServiceLocation(ServiceLocation mspServiceLocation, int paoNameAlias, String defaultValue) {
		
		switch (paoNameAlias) {
			case MultispeakVendor.ACCOUNT_NUMBER_PAONAME:
			{
			   if( StringUtils.isBlank(mspServiceLocation.getAccountNumber()))
                   return null;
               return mspServiceLocation.getAccountNumber();
			}	
			case MultispeakVendor.SERVICE_LOCATION_PAONAME:
			{
				if( StringUtils.isBlank(mspServiceLocation.getObjectID()))
                   return null;
               return mspServiceLocation.getObjectID();
			}	
			case MultispeakVendor.CUSTOMER_PAONAME:
			{
				if( StringUtils.isBlank(mspServiceLocation.getCustID()))
                   return null;
               return mspServiceLocation.getCustID();
			}
			case MultispeakVendor.EA_LOCATION_PAONAME:
			{
				if( mspServiceLocation.getNetwork() == null || mspServiceLocation.getNetwork().getEaLoc() == null ||
						StringUtils.isBlank(mspServiceLocation.getNetwork().getEaLoc().getName()))
                   return null;
               return mspServiceLocation.getNetwork().getEaLoc().getName();
			}				
			default:
				return defaultValue;
		}
	}
	
	public LiteYukonPAObject getLiteYukonPaobjectFromMsp(ServiceLocation mspServiceLocation, MultispeakVendor mspVendor) {
		String paoName;
		final int paoAlias = multispeakFuncs.getPaoNameAlias();
	    //Find a liteYukonPaobject 
		switch (paoAlias) {
			case MultispeakVendor.SERVICE_LOCATION_PAONAME:
		        paoName = mspServiceLocation.getObjectID();
		        CTILogger.info("Get YukonPaobject by ServiceLocation.ObjectID (" + paoName + ").");
		        return deviceDao.getLiteYukonPaobjectByDeviceName(paoName);
		        
			case MultispeakVendor.ACCOUNT_NUMBER_PAONAME:
		        paoName = mspServiceLocation.getAccountNumber();
		        CTILogger.info("Get YukonPaobject by ServiceLocation.AccountNumber (" + paoName + ").");
		        return deviceDao.getLiteYukonPaobjectByDeviceName(paoName);
				
			case MultispeakVendor.CUSTOMER_PAONAME:
		        paoName = mspServiceLocation.getCustID();
		        CTILogger.info("Get YukonPaobject by ServiceLocation.CustID (" + paoName + ").");
		        return deviceDao.getLiteYukonPaobjectByDeviceName(paoName);
				
			case MultispeakVendor.EA_LOCATION_PAONAME:
		        if( mspServiceLocation.getNetwork() != null && mspServiceLocation.getNetwork().getEaLoc() != null) {
		        	paoName = mspServiceLocation.getNetwork().getEaLoc().getName();
		        	CTILogger.info("Get YukonPaobject by ServiceLocation.Network.EALoc.Name (" + paoName + ").");
		        	return deviceDao.getLiteYukonPaobjectByDeviceName(paoName);
		        }
				return null;

			default:
			{ // lookup by meter number
		        //lookup meter by servicelocation
		        String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_MR_STR);
		
		        try {
		        	String serviceLocationStr = mspServiceLocation.getObjectID();
		        	CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(mspVendor);
		        	ArrayOfMeter mspMeters = port.getMeterByServLoc(serviceLocationStr);
		            if( mspMeters != null && mspMeters.getMeter() != null) {
		                for ( int j = 0; j < mspMeters.getMeter().length; j++){
		                	paoName = mspMeters.getMeter(j).getMeterNo();
		                    LiteYukonPAObject tempPao = deviceDao.getLiteYukonPaobjectByMeterNumber(paoName);
		                    if( tempPao != null) {
		                        return tempPao;
		                    }
		                }
		            }
		        } catch (RemoteException e) {
		        	CTILogger.error("TargetService: " + endpointURL + " - updateServiceLocation (" + mspVendor.getCompanyName() + ")");
					CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
		        } 
		    }
	    }
	    return null;
	}
}