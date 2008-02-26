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
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
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
import com.cannontech.multispeak.block.FormattedBlockService;
import com.cannontech.multispeak.block.impl.LoadFormattedBlockImpl;
import com.cannontech.multispeak.block.impl.OutageFormattedBlockImpl;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.deploy.service.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.ExtensionsItem;
import com.cannontech.multispeak.deploy.service.LoadActionCode;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.multispeak.deploy.service.impl.MultispeakPortFactory;
import com.cannontech.multispeak.event.BlockMeterReadEvent;
import com.cannontech.multispeak.event.CDEvent;
import com.cannontech.multispeak.event.CDStatusEvent;
import com.cannontech.multispeak.event.MeterReadEvent;
import com.cannontech.multispeak.event.MultispeakEvent;
import com.cannontech.multispeak.event.ODEvent;
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
    private MspObjectDao mspObjectDao;
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupProviderDao deviceGroupDao;
    private MeterDao meterDao;
    private SystemLogHelper _systemLogHelper = null;
    private TransactionTemplate transactionTemplate = null;
   
	/** Singleton incrementor for messageIDs to send to porter connection */
	private static long messageID = 1;

	/** A map of Long(userMessageID) to MultispeakEvent values */
	private static Map<Long,MultispeakEvent> eventsMap = new HashMap<Long,MultispeakEvent>();
	
    @Required
	public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }
    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }
    @Required
    public void setMspMeterDao(MspMeterDao mspMeterDao) {
        this.mspMeterDao = mspMeterDao;
    }
    @Required
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    @Required
    public void setMspObjectDao(MspObjectDao mspObjectDao) {
        this.mspObjectDao = mspObjectDao;
    }
    @Required
    public void setDeviceGroupMemberEditorDao(
            DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    @Required
    public void setDeviceGroupEditorDao(
            DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
    @Required
    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }
    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    @Required
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
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
    
    public LoadActionCode CDMeterState(MultispeakVendor mspVendor, 
            com.cannontech.amr.meter.model.Meter meter,
            String transactionID) throws RemoteException
    {
        if ( ! porterConnection.isValid() ) {
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }

        long id = generateMessageID();
        CDStatusEvent event = new CDStatusEvent(mspVendor, id, transactionID);
        
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
                boolean timeout = !waitOnEvent(event, mspVendor.getRequestMessageTimeout());
                if( timeout ) {
                    
                    event.setResultMessage("Reading Timed out after " + (mspVendor.getRequestMessageTimeout()/1000)+ " seconds.");
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
    public MeterRead getLatestReadingInterrogate(MultispeakVendor mspVendor, 
            com.cannontech.amr.meter.model.Meter meter,
            String transactionID)
    {
    	long id = generateMessageID();      
        MeterReadEvent event = new MeterReadEvent(mspVendor, id, meter, transactionID);
        
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
            boolean timeout = !waitOnEvent(event, mspVendor.getRequestMessageTimeout());
            if( timeout ) {
                logMSPActivity("getLatestReadingByMeterNo", "MeterNumber (" + meterNumber + ") - Reading Timed out after " + 
                               (mspVendor.getRequestMessageTimeout()/1000) + 
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
	public synchronized ErrorObject[] ODEvent(MultispeakVendor vendor, 
	        String[] meterNumbers,
	        String transactionID) throws RemoteException
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
				meter = meterDao.getForMeterNumber(meterNumber);
				long id = generateMessageID();		
				ODEvent event = new ODEvent(vendor, id, transactionID);
				getEventsMap().put(new Long(id), event);
				writePilRequest(meter, "ping", id, 13); 

			} catch (NotFoundException e) {

				ErrorObject err = mspObjectDao.getErrorObject(meterNumber, 
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
    public synchronized ErrorObject[] MeterReadEvent(MultispeakVendor vendor, 
            String[] meterNumbers,
            String transactionID)
    {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        CTILogger.info("Received " + meterNumbers.length + " Meter(s) for MeterReading from " + vendor.getCompanyName());
        
        for (String meterNumber : meterNumbers) {
        	
            com.cannontech.amr.meter.model.Meter meter;
            try {
            	meter = meterDao.getForMeterNumber(meterNumber);
 	        	
            	long id = generateMessageID();      
 	            MeterReadEvent event = new MeterReadEvent(vendor, id, meter, transactionID);
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
 	               
            	ErrorObject err = mspObjectDao.getErrorObject(meterNumber, 
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
    public synchronized ErrorObject[] BlockMeterReadEvent(MultispeakVendor vendor, 
            String meterNumber, FormattedBlockService block, String transactionID)
    {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        CTILogger.info("Received " + meterNumber+ " for BlockMeterReading from " + vendor.getCompanyName());
        
        com.cannontech.amr.meter.model.Meter meter;
        
        try {
        	meter = meterDao.getForMeterNumber(meterNumber);
            
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
                
                BlockMeterReadEvent event = 
                    new BlockMeterReadEvent(vendor, id, meter, block, returnMessages, transactionID);
                getEventsMap().put(new Long(id), event);

                writePilRequest(meter, lpCommandStr, id, 13);
                if( commandStr != null)
                    writePilRequest(meter, commandStr, id, 13);
            }
            else if ( block instanceof OutageFormattedBlockImpl) {
                
                BlockMeterReadEvent event = 
                    new BlockMeterReadEvent(vendor, id, meter, block, transactionID);
                getEventsMap().put(new Long(id), event);
                String commandStr = "getvalue demand";
                writePilRequest(meter, commandStr, id, 13);
            }
        } 
        catch (NotFoundException e) {
               
            ErrorObject err = mspObjectDao.getErrorObject(meterNumber, 
                    "MeterNumber: " + meterNumber + " - Was NOT found in Yukon.",
                    "Meter");
            errorObjects.add(err);
        }
        
        return toErrorObject(errorObjects);
    }
    
    /**
     * Send a ping command to pil connection for each meter in meterNumbers.
     * @param meterNumbers
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public synchronized ErrorObject[] CDEvent(MultispeakVendor vendor, 
            ConnectDisconnectEvent [] cdEvents,
            String transactionID) throws RemoteException
    {
        if ( ! porterConnection.isValid() )
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        CTILogger.info("Received " + cdEvents.length + " Meter(s) for Connect/Disconnect from " + vendor.getCompanyName());
        
        for (ConnectDisconnectEvent cdEvent : cdEvents) {
            String meterNumber = cdEvent.getObjectID();
            com.cannontech.amr.meter.model.Meter meter;
            try {
 	        	meter = meterDao.getForMeterNumber(meterNumber);

        		if (mspMeterDao.isCDSupportedMeter(meterNumber)){
 	                long id = generateMessageID();      
 	                CDEvent event = new CDEvent(vendor, id, transactionID);
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
 	                ErrorObject err = mspObjectDao.getErrorObject(meterNumber, 
 	                												"MeterNumber (" + meterNumber + ") - Invalid Yukon Connect/Disconnect Meter.",
 	                												"Meter");
 	                errorObjects.add(err);
 	            }
            } 
            catch (NotFoundException e) {
 	               
                ErrorObject err = mspObjectDao.getErrorObject(meterNumber,
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
     * Returns true if event processes without timing out, false if event times out.
     * @param event
     * @return
     */
    public boolean waitOnEvent(MultispeakEvent event, long timeout) {
        
        long millisTimeOut = 0; //
        while (!event.isPopulated() && millisTimeOut < timeout)  //quit after timeout
        {
            try {
                Thread.sleep(1000);
                millisTimeOut += 1000;
            } catch (InterruptedException e) {
                CTILogger.error(e);
            }
        }
        if( millisTimeOut >= timeout) {// this broke the loop, more than likely, have to kill it sometime
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
        
        return addToGroup(meterNos, SystemGroupEnum.DISCONNECTSTATUS, "initiateDisconnectedStatus", mspVendor);
    }
    
    public ErrorObject[] initiateUsageMonitoringStatus(MultispeakVendor mspVendor, String[] meterNos) {
        
        return addToGroup(meterNos, SystemGroupEnum.USAGEMONITORING, "initiateUsageMonitoring", mspVendor);
    }

    public ErrorObject[] cancelDisconnectedStatus(MultispeakVendor mspVendor, String[] meterNos) {

        return removeFromGroup(meterNos, SystemGroupEnum.DISCONNECTSTATUS, "cancelDisconnectedStatus", mspVendor);
    }

    public ErrorObject[] cancelUsageMonitoringStatus(MultispeakVendor mspVendor, String[] meterNos) {

        return removeFromGroup(meterNos, SystemGroupEnum.USAGEMONITORING, "cancelUsageMonitoring", mspVendor);
    }
    
    public ErrorObject[] addMeterObject(final MultispeakVendor mspVendor, Meter[] addMeters) throws RemoteException{
        final List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();
        final int paoAlias = multispeakFuncs.getPaoNameAlias();
        final String paoAliasStr = MultispeakVendor.paoNameAliasStrings[paoAlias];

        for (final Meter mspMeter : addMeters) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult(){
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status){
                        String meterNo;
                        String mspAddress;
                        
                        ErrorObject invalidMeterError = isValidMeter(mspMeter, mspVendor);
                        if( invalidMeterError == null) {
                            meterNo = mspMeter.getMeterNo().trim();
                            mspAddress = mspMeter.getNameplate().getTransponderID().trim();
                            try {
                                com.cannontech.amr.meter.model.Meter meter = meterDao.getForMeterNumber(meterNo);
                                if( meter != null) {    //Meter exists in Yukon
                                    
                                    String address = meter.getAddress();
                                    if( address.equalsIgnoreCase(mspAddress)) { //Same MeterNumber and Address
                                        ErrorObject err = isMeterDisabled(meter, mspVendor);
                                        if (err != null) {
                                            errorObjects.add(err);
                                        } else { //Disabled Meter found
                                            addExistingMeter(mspMeter, meter, mspVendor, paoAlias);
                                        }
                                    } else {    //Address is different!
                                        ErrorObject err = isMeterDisabled(meter, mspVendor);
                                        if (err != null) { //Meter is currently enabled in Yukon
                                            errorObjects.add(err);
                                        } else { //Disabled Meter found
                                            //Lookup meter by mspAddress
                                            List<LiteYukonPAObject> liteYukonPaoByAddressList = paoDao.getLiteYukonPaobjectsByAddress(new Integer(mspAddress).intValue());
                                            
                                            if (liteYukonPaoByAddressList.isEmpty()) {  //New Hardware
                                                // Add meter to Inventory, "remove" it
                                                addToGroup(meter, SystemGroupEnum.INVENTORY, "MeterAddNotification", mspVendor);
                                                
                                                List<ErrorObject> addMeterErrors = addNewMeter(mspMeter, mspVendor);
                                                errorObjects.addAll(addMeterErrors);
                                                
                                            } else {    //Meter Number and Address both exist...on different objects! 
                                                LiteYukonPAObject liteYukonPaoByAddress = liteYukonPaoByAddressList.get(0);
                                                com.cannontech.amr.meter.model.Meter meterByAddress = meterDao.getForId(liteYukonPaoByAddress.getYukonID());

                                                ErrorObject err2 = isMeterDisabled(meterByAddress, mspVendor);
                                                if (err2 != null) { //Address is already active
                                                    errorObjects.add(err2);
                                                } else {  //Address object is disabled, so we can update and activate the Meter Number object
                                                    //Load the CIS serviceLocation.
                                                    ServiceLocation mspServiceLocation = mspObjectDao.getMspServiceLocation(meterNo, mspVendor);
                                                    String billingCycle = mspServiceLocation.getBillingCycle();
                                                    //TODO deleteRawPointHistory(yukonPaobject);
                
                                                    // Remove meter from Inventory
                                                    removeFromGroup(meter, SystemGroupEnum.INVENTORY, "MeterAddNotification", mspVendor);
                
                                                    // update the billing group from CIS billingCyle
                                                    updateBillingCyle(billingCycle, meter, "MeterAddNotification", mspVendor);
                                                        
                                                    LiteYukonPAObject liteYukonPaobject = paoDao.getLiteYukonPAO(meter.getDeviceId());
                                                    YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
                                                    DeviceCarrierSettings deviceCarrierSettings = ((MCTBase)yukonPaobject).getDeviceCarrierSettings();
                 
                                                    String currentAddress = meter.getAddress();
                                                    deviceCarrierSettings.setAddress(Integer.valueOf(mspAddress));
                                                    
                                                    String currentPaoName = yukonPaobject.getPAOName();
                                                    String newPaoName = getPaoNameFromMspMeter(mspMeter, paoAlias, currentPaoName);
            
                                                    String logTemp;
                                                    if( newPaoName == null)
                                                        logTemp = "; PAOName(" + paoAliasStr + ") No Change - MSP " + paoAliasStr + " is empty.";
                                                    else if (newPaoName.equalsIgnoreCase(currentPaoName))
                                                        logTemp = "; PAOName(" + paoAliasStr + ") No change in value.";
                                                    else {
                                                        logTemp = "; PAOName(" + paoAliasStr + ") - Old:" + currentPaoName + " New:" + newPaoName;
                                                        yukonPaobject.setPAOName(newPaoName);
                                                    }
                
                                                    yukonPaobject.setDisabled(false);
                                                    dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                                                    logMSPActivity("MeterAddNotification",
                                                                   "MeterNumber(" + meterNo + ") - Address - Old:" + currentAddress + " New:" + deviceCarrierSettings.getAddress().toString() + ".", 
                                                                   mspVendor.getCompanyName());
                                                    logMSPActivity("MeterAddNotification",
                                                                   "MeterNumber(" + meterNo + ") - Meter Enabled" + logTemp, 
                                                                   mspVendor.getCompanyName());
                
                                                    //TODO Read the Meter.
                                                                                        
                                                }
                                            }
                                        }
                                    }
                                }
                               
                            } 
                            catch (NotFoundException e) {
                                //Meter Number not currently found in Yukon
                                //Lookup meter by mspAddress
                                List<LiteYukonPAObject> liteYukonPaoByAddressList = paoDao.getLiteYukonPaobjectsByAddress(new Integer(mspAddress).intValue());
                                if (liteYukonPaoByAddressList.isEmpty()) {  //New Hardware
                                    List<ErrorObject> addMeterErrors = addNewMeter(mspMeter, mspVendor);
                                    errorObjects.addAll(addMeterErrors);
                                    
                                } else { // mspAddress already exists in Yukon 
                                    LiteYukonPAObject liteYukonPaoByAddress = liteYukonPaoByAddressList.get(0);
                                    com.cannontech.amr.meter.model.Meter meterByAddress = meterDao.getForId(liteYukonPaoByAddress.getYukonID());
                                    ErrorObject err = isMeterDisabled(meterByAddress, mspVendor);
                                    if (err != null) { //Address is already active
                                        errorObjects.add(err);
                                    } else { //Address object is disabled, so we can update and activate the Address object
                                        //TODO deleteRawPointHistory(yukonPaobject);
                                        //Load the CIS serviceLocation.
                                        ServiceLocation mspServiceLocation = mspObjectDao.getMspServiceLocation(meterNo, mspVendor);
                                        String billingCycle = mspServiceLocation.getBillingCycle();
                                        
                                        YukonPAObject yukonPaobjectByAddress = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaoByAddress);
                                        yukonPaobjectByAddress.setDisabled(false);
                
                                        String currentPaoName = meterByAddress.getName();
                                        String newPaoName = getPaoNameFromMspMeter(mspMeter, paoAlias, currentPaoName);
                                        
                                        String logTemp;
                                        if( newPaoName == null)
                                            logTemp = "; PAOName(" + paoAliasStr + ") No Change - MSP " + paoAliasStr + " is empty.";
                                        else if (newPaoName.equalsIgnoreCase(currentPaoName))
                                            logTemp = "; PAOName(" + paoAliasStr + ") No change in value.";
                                        else {
                                            logTemp = "; PAOName(" + paoAliasStr + ") - Old:" + currentPaoName + " New:" + newPaoName;
                                            yukonPaobjectByAddress.setPAOName(newPaoName);
                                        }
                
                                        DeviceMeterGroup deviceMeterGroup = ((MCTBase)yukonPaobjectByAddress).getDeviceMeterGroup();
                                        
                                        // Remove meter from Inventory
                                        removeFromGroup(meterByAddress, SystemGroupEnum.INVENTORY, "MeterAddNotification", mspVendor);
                
                                        // update the billing group from CIS billingCyle
                                        updateBillingCyle(billingCycle, meterByAddress, "MeterAddNotification", mspVendor);
                
                                        String oldMeterNo = deviceMeterGroup.getMeterNumber();
                                        deviceMeterGroup.setMeterNumber(meterNo);
                
                                        dbPersistentDao.performDBChange(yukonPaobjectByAddress, Transaction.UPDATE);
                                        logMSPActivity("MeterAddNotification",
                                                       "MeterNumber(" + meterNo + " Old:" +oldMeterNo + "); Meter Enabled" + logTemp,
                                                       mspVendor.getCompanyName());
                                        //TODO Read the Meter.
                                                                            
                                    }
                                }
                            }
                        } else {
                            errorObjects.add(invalidMeterError);
                        }
                    };
                });
            }catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(), 
                                                                 "X Exception: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(), 
                                                                 "Meter");
                errorObjects.add(err);
                logMSPActivity("MeterAddNotification", err.getErrorString(), mspVendor.getCompanyName());
                ex.printStackTrace();
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(), 
                                                                 "X Error: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(), 
                                                                 "Meter");
                errorObjects.add(err);
                logMSPActivity("MeterAddNotification", err.getErrorString(), mspVendor.getCompanyName());
                ex.printStackTrace();
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
            	meter = meterDao.getForMeterNumber(meterNo);
            	//Meter exists if no exception was thrown
                if( !meter.isDisabled()) {//enabled
                    // Added meter to Inventory
                    addToGroup(meter, SystemGroupEnum.INVENTORY, "MeterRemovedNotification", mspVendor);

                    LiteYukonPAObject liteYukonPaobject = paoDao.getLiteYukonPAO(meter.getDeviceId());
                    YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
                    if (yukonPaobject instanceof MCTBase){
                            yukonPaobject.setDisabled(true);
                            dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                            logMSPActivity("MeterRemoveNotification",
                                           "MeterNumber(" + meterNo + ") - Meter Disabled.",
                                           mspVendor.getCompanyName());
                    }
                }
                else {
                    ErrorObject err = mspObjectDao.getErrorObject(meterNo,
                                                                     "Warning: MeterNumber(" + meterNo + ") - Meter is already disabled. No updates were made.",
                                                                     "Meter");
                    errorObjects.add(err);
                    logMSPActivity("MeterRemoveNotification", err.getErrorString(), mspVendor.getCompanyName());
                }
            } 
            catch (NotFoundException e) {
 	               
            	ErrorObject err = mspObjectDao.getErrorObject(meterNo, 
                        "Error: MeterNumber(" + meterNo + ") - Meter was NOT found in Yukon. No updates were made.",
                        "Meter");
				errorObjects.add(err);
				logMSPActivity("MeterRemoveNotification", err.getErrorString(), mspVendor.getCompanyName());              
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
    public ErrorObject[] updateServiceLocation(final MultispeakVendor mspVendor, ServiceLocation[] serviceLocations) {
        final Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        final int paoAlias = multispeakFuncs.getPaoNameAlias();
        final String paoAliasStr = MultispeakVendor.paoNameAliasStrings[paoAlias];
        
        for (final ServiceLocation mspServiceLocation : serviceLocations) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult(){
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status){
    
                        String newPaoName = getPaoNameFromMspServiceLocation(mspServiceLocation, mspVendor, paoAlias, null);
                        //Have a newPaoName to lookup in Yukon
                        if( newPaoName != null) {
                            try {
                                com.cannontech.amr.meter.model.Meter meter = meterDao.getForPaoName(newPaoName);
                                String currentPaoName = meter.getName();
                                //Meter exits in Yukon with newPaoName
                                if( newPaoName == null) {
                                    logMSPActivity("ServiceLocationChangedNotification",
                                                   "MeterNumber(" + meter.getMeterNumber()+ ") - PAOName(" + paoAliasStr + ") NO CHANGE - MSP " + paoAliasStr + " IS EMPTY.",
                                                   mspVendor.getCompanyName());
                                } else if (currentPaoName.equalsIgnoreCase(newPaoName)) {
                                    logMSPActivity("ServiceLocationChangedNotification",
                                                   "MeterNumber(" + meter.getMeterNumber()+ ") - PAOName(" + paoAliasStr + ") NO CHANGE - MSP " + paoAliasStr + " has no change.",
                                                   mspVendor.getCompanyName());
                                } else {
                                    LiteYukonPAObject liteYukonPaobject = paoDao.getLiteYukonPAO(meter.getDeviceId());
                                    YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
                                    if (yukonPaobject instanceof MCTBase) {
                                        yukonPaobject.setPAOName(newPaoName);
                                        dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                                        logMSPActivity("ServiceLocationChangedNotification",
                                                       "MeterNumber(" + meter.getMeterNumber()+ ") - PAOName(" + paoAliasStr + ")(OLD:" + currentPaoName + " NEW:" + newPaoName + ").",
                                                       mspVendor.getCompanyName());
                                    }
                                }
                                
                                //Update billing cycle information
                                String billingCycle = mspServiceLocation.getBillingCycle();
                                updateBillingCyle(billingCycle, meter, "ServiceLocationChangedNotification", mspVendor);
                               
                            } catch (NotFoundException e) {
                                CTILogger.debug(e);
                                ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(), 
                                                                                 "ServiceLocation(" + mspServiceLocation.getObjectID() + ") - ServiceLocation was NOT found in Yukon.", 
                                                                                 "ServiceLocation");
                                errorObjects.add(err);
                                logMSPActivity("ServiceLocationChangedNotification",
                                               paoAliasStr + "(" + newPaoName + ") - was NOT found for Device Name in Yukon",
                                               mspVendor.getCompanyName());
                            }
            
                        } else {
                            ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(), 
                                                                             "ServiceLocation(" + mspServiceLocation.getObjectID() + ") - valid ServiceLocation was NOT found in MSP Message.", 
                                                                             "ServiceLocation");
                            errorObjects.add(err);
                            logMSPActivity("ServiceLocationChangedNotification",
                                           paoAliasStr + "(" + newPaoName + ") - valid ServiceLocation was NOT found in MSP Message.",
                                           mspVendor.getCompanyName());
                        }
                    };
                });
            }catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(), 
                                                                 "X Exception: (ServLoc:" + mspServiceLocation.getObjectID() + ")-" + ex.getMessage(), 
                                                                 "ServiceLocation");
                errorObjects.add(err);
                logMSPActivity("ServiceLocationChangedNotification",
                               "X Exception: (ServLoc:" + mspServiceLocation.getObjectID() + ")-" + ex.getMessage(),
                               mspVendor.getCompanyName());
                ex.printStackTrace();
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(), 
                                                                 "X Error: (ServLoc:" + mspServiceLocation.getObjectID() + ")-" + ex.getMessage(), 
                                                                 "ServiceLocation");
                errorObjects.add(err);
                logMSPActivity("ServiceLocationChangedNotification",
                               "X error: (ServLoc:" + mspServiceLocation.getObjectID() + ")-" + ex.getMessage(),
                               mspVendor.getCompanyName());
                ex.printStackTrace();
            }
        }
        return toErrorObject(errorObjects);
    }

    public SystemLogHelper getSystemLogHelper() {
        if (_systemLogHelper == null)
            _systemLogHelper = new SystemLogHelper(PointTypes.SYS_PID_MULTISPEAK);
        return _systemLogHelper;
    }
    
    private void addImportData(Meter mspMeter, MultispeakVendor mspVendor, 
            String templatePaoName, String substationName){

        String address = mspMeter.getNameplate().getTransponderID().trim();
        String meterNumber = mspMeter.getMeterNo().trim();
        
        ServiceLocation mspServiceLocation = mspObjectDao.getMspServiceLocation(meterNumber, mspVendor);
        String billingGroup = StringUtils.isBlank(mspServiceLocation.getBillingCycle())? "":mspServiceLocation.getBillingCycle();
        
        // get the Collection Group value...returns a set...just pick one I guess.
        DeviceGroup collDeviceGroup = getSystemGroup(mspVendor, templatePaoName, SystemGroupEnum.COLLECTION);
        String collectionGroup = (collDeviceGroup != null ? collDeviceGroup.getName() : "Default");
        
        // get the Alternate Group value...returns a set...just pick one I guess.
        DeviceGroup altDeviceGroup = getSystemGroup(mspVendor, templatePaoName, SystemGroupEnum.ALTERNATE);
        String alternateGroup = (altDeviceGroup != null ? altDeviceGroup.getName() : "Default");

        
        final int paoAlias = multispeakFuncs.getPaoNameAlias();
        String paoName = getPaoNameFromMspMeter(mspMeter, paoAlias, meterNumber);

        ImportData importData = new ImportData(address, paoName, "", meterNumber, 
                                               (collectionGroup == null ? "" : collectionGroup),
                                               (alternateGroup == null ? "" : alternateGroup), 
                                               templatePaoName, billingGroup, substationName);
        try {
            Transaction.createTransaction(Transaction.INSERT, importData).execute();
            logMSPActivity("MeterAddNotification",
                           "MeterNumber(" + meterNumber + ") - Meter inserted into ImportData for processing.", 
                           mspVendor.getCompanyName());
        } catch (TransactionException e) {
            CTILogger.error(e);
        }
    }

    /**
     * Returns the deviceGroup membership for the SystemGroupEnum.  
     * If there is more than one group membership, then the first one found in the list 
     * of children is returned.  If no group memberships are found, then return null.
     */
    private DeviceGroup getSystemGroup(MultispeakVendor mspVendor, String templatePaoName, 
            SystemGroupEnum groupEnum) {
        
        com.cannontech.amr.meter.model.Meter meter = meterDao.getForPaoName(templatePaoName);;
        
        DeviceGroup deviceGroup = null;
        StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getSystemGroup(groupEnum);
        Set<DeviceGroup> deviceGroups = deviceGroupDao.getGroupMembership(deviceGroupParent, meter);
        if( deviceGroups.isEmpty())
            logMSPActivity("addImportData", "Template '" + meter.getName() + 
                           "' is not a member of " + groupEnum.getFullPath()+ 
                           ".  'Default' will be assigned", mspVendor.getUserName());
        else if( deviceGroups.size() > 1)
            logMSPActivity("addImportData", "Template '" + meter.getName() + 
                           "' is a member of more than one " + groupEnum.getFullPath()+ 
                           ".  '" + deviceGroups.iterator().next().getFullName() + "' will be assigned.", mspVendor.getUserName());
        else if( deviceGroups.size() == 1)
            logMSPActivity("addImportData", "Template '" + meter.getName() + 
                           "' is a member of " + groupEnum.getFullPath()+ 
                           ".  '" + deviceGroups.iterator().next().getFullName() + "' will be assigned.", mspVendor.getUserName());
        
        if( deviceGroups.size() > 0)
            deviceGroup = deviceGroups.iterator().next();
        
        return deviceGroup;
    }
    
    private void logMSPActivity(String method, String description, String userName) {
        getSystemLogHelper().log(PointTypes.SYS_PID_MULTISPEAK, method, description, userName, SystemLog.TYPE_MULTISPEAK);
        CTILogger.debug("MSP Activity (Method: " + method +  " - " + description + ")");
    }
    
    private String getMeterTemplate(Meter mspMeter, String defaultTemplateName) {
        String templateName = defaultTemplateName;
        boolean loaded = false;
       
        if( mspMeter.getAMRDeviceType() != null) {
            templateName = mspMeter.getAMRDeviceType();
            loaded = true;
        }
       
        if (!loaded) {
            if( mspMeter.getExtensionsList() != null) {
                ExtensionsItem [] eItems = mspMeter.getExtensionsList();
                for (ExtensionsItem eItem : eItems) {
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
   
    /**
     * Removes the Meter from all Billing group memberships (all children under Billing).
     * Adds the Meter to 'newBilling' Billing child group.  If the billing group does not already
     * exist, then a new Billing sub group is created. 
     */
    private void updateBillingCyle(String newBilling, com.cannontech.amr.meter.model.Meter meter,
            String logActionStr, MultispeakVendor mspVendor) {

        boolean alreadyInGroup = false;
        if (!StringUtils.isBlank(newBilling)) {

            //Remove from all billing membership groups
            StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.BILLING);
            Set<StoredDeviceGroup> deviceGroups = deviceGroupMemberEditorDao.getGroupMembership(deviceGroupParent, meter);
            for (StoredDeviceGroup deviceGroup : deviceGroups) {
                if( deviceGroup.getName().equalsIgnoreCase(newBilling) ) {
                    System.out.println("Already in billing group:  " + newBilling);
                    alreadyInGroup = true;
                } else {
                    deviceGroupMemberEditorDao.removeDevices(deviceGroup, meter);
                    logMSPActivity(logActionStr,
                                   "MeterNumber(" + meter.getMeterNumber()+ ") - Removed from Group: " + deviceGroup.getFullName() + ".",
                                   mspVendor.getCompanyName());
                }
            }

            if (!alreadyInGroup) {
                StoredDeviceGroup billingGroup = deviceGroupEditorDao.getGroupByName(deviceGroupParent, newBilling, true);
                deviceGroupMemberEditorDao.addDevices(billingGroup, meter);
                logMSPActivity(logActionStr,
                               "MeterNumber(" + meter.getMeterNumber()+ ") - Added to Group: " + billingGroup.getFullName() + ".",
                               mspVendor.getCompanyName());
            }
        }
    }

    private void removeFromGroup(com.cannontech.amr.meter.model.Meter meter, SystemGroupEnum systemGroup, 
            String logActionStr, MultispeakVendor mspVendor){

        DeviceGroup deviceGroup = deviceGroupEditorDao.getSystemGroup(systemGroup);
        deviceGroupMemberEditorDao.removeDevices((StoredDeviceGroup)deviceGroup, Collections.singletonList(meter));

        logMSPActivity(logActionStr,
                       "MeterNumber(" + meter.getMeterNumber() + ") - Removed from " + systemGroup.getFullPath(),
                       mspVendor.getCompanyName());
    }

    private void addToGroup(com.cannontech.amr.meter.model.Meter meter, SystemGroupEnum systemGroup, 
            String logActionStr, MultispeakVendor mspVendor){

        DeviceGroup deviceGroup = deviceGroupEditorDao.getSystemGroup(systemGroup);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup)deviceGroup, Collections.singletonList(meter));

        logMSPActivity(logActionStr,
                       "MeterNumber(" + meter.getMeterNumber() + ") - Added to " + systemGroup.getFullPath(), 
                       mspVendor.getCompanyName());
    }
   
    private ErrorObject[] addToGroup(String[] meterNos, SystemGroupEnum systemGroup, String logActionStr, MultispeakVendor mspVendor) {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();

        for (String meterNumber : meterNos) {
            com.cannontech.amr.meter.model.Meter meter;
            try {
                meter = meterDao.getForMeterNumber(meterNumber);
                addToGroup(meter, systemGroup, logActionStr, mspVendor);
            } 
            catch (NotFoundException e) {
                   
                ErrorObject err = mspObjectDao.getErrorObject(meterNumber, 
                                                              "MeterNumber: " + meterNumber + " - Was NOT found in Yukon.",
                                                              "Meter");
                errorObjects.add(err);              
            }
        }
        return toErrorObject(errorObjects);
    }

    private ErrorObject[] removeFromGroup(String[] meterNos, SystemGroupEnum systemGroup, String logActionStr, MultispeakVendor mspVendor) {

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();

        for (String meterNumber : meterNos) {
            com.cannontech.amr.meter.model.Meter meter;
            try {
                meter = meterDao.getForMeterNumber(meterNumber);
                removeFromGroup(meter, systemGroup, logActionStr, mspVendor);
            } 
            catch (NotFoundException e) {
                ErrorObject err = mspObjectDao.getErrorObject(meterNumber,
                                                              "MeterNumber: " + meterNumber + " - Was NOT found in Yukon.",
                                                                 "Meter");
                errorObjects.add(err);
            }
        }

        return toErrorObject(errorObjects);
    }

    public ErrorObject[] toErrorObject(List<ErrorObject> errorObjects) {

        if( !errorObjects.isEmpty()) {
            ErrorObject[] errors = new ErrorObject[errorObjects.size()];
            errorObjects.toArray(errors);
            return errors;
        }
        return new ErrorObject[0];
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
	
    private String getPaoNameFromMspServiceLocation(ServiceLocation mspServiceLocation, MultispeakVendor mspVendor, int paoNameAlias, String defaultValue) {
        
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
            case MultispeakVendor.DEFAULT_PAONAME:
            { // lookup by meter number
                //lookup meter by servicelocation
                String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_MR_STR);
        
                try {
                    String serviceLocationStr = mspServiceLocation.getObjectID();
                    CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(mspVendor);
                    long start = System.currentTimeMillis();
                    CTILogger.debug("Begin call to getMeterByServLoc for ServLoc:" + serviceLocationStr);
                    Meter[] mspMeters = port.getMeterByServLoc(serviceLocationStr);
                    CTILogger.debug("End call to getMeterByServLoc for ServLoc:" + serviceLocationStr + "  (took " + (System.currentTimeMillis() - start) + " millis)");
                    if( mspMeters != null ) {
                        for (Meter mspMeter : mspMeters) {
                            return mspMeter.getMeterNo();
                        }
                    }
                } catch (RemoteException e) {
                    CTILogger.error("TargetService: " + endpointURL + " - updateServiceLocation (" + mspVendor.getCompanyName() + ") for ServLoc: " + mspServiceLocation.getObjectID());
                    CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
                } 
            }
            default:
                return defaultValue;
        }
    }
	
	private List<ErrorObject> addNewMeter(Meter mspMeter, MultispeakVendor mspVendor) {
	    
	    List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();
	    
	    //Find a valid Template!
        String templateName = getMeterTemplate(mspMeter, mspVendor.getTemplateNameDefault());
        String meterNo = mspMeter.getMeterNo().trim();
        
        //Find template object in Yukon
        ErrorObject err = isValidTemplate(templateName, meterNo, mspVendor);
        if( err != null) {
            errorObjects.add(err);
        } else { //Valid template found
            //Find a valid substation
            if( mspMeter.getUtilityInfo() == null || mspMeter.getUtilityInfo().getSubstationName()== null){
                addImportData(mspMeter, mspVendor, templateName, "");
            } else {
                String substationName = mspMeter.getUtilityInfo().getSubstationName();
                err = isValidSubstation(substationName, meterNo, mspVendor);
                if (err != null) {
                    errorObjects.add(err);
                } else {
                    addImportData(mspMeter, mspVendor, templateName, substationName);
                }
            }
        }
        return errorObjects;
	}
	
	/**
     * Returns ErrorObject when substation name does not return any valid SubstationToRouteMappings.
     * Returns null when a substation name has routes mapped to it.
     * @param templateName
     * @param meterNumber
     * @param mspVendor
     * @return
     */
    private ErrorObject isValidSubstation(String substationName, String meterNumber, MultispeakVendor mspVendor) {
        List<String> routeNames = DBFuncs.getRouteNamesFromSubstationName(substationName);
        if( routeNames.isEmpty()){
            ErrorObject err = mspObjectDao.getErrorObject(meterNumber, 
                                                             "Error: MeterNumber(" + meterNumber + ") - SubstationName(" + 
                                                             substationName + ") - no RouteMappings found in Yukon.  Meter was NOT added",
                                                             "Meter");
            logMSPActivity("MeterAddNotification", err.getErrorString(), mspVendor.getCompanyName());
            return err;
        }
        return null;
    }
	
    /**
     * Returns ErrorObject when templateName is not valid.
     * Returns null when a meter is found in Yukon for templateName.
     * @param templateName
     * @param meterNumber
     * @param mspVendor
     * @return
     */
    private ErrorObject isValidTemplate(String templateName, String meterNumber, MultispeakVendor mspVendor ) {
        try {
            //Verify that a meter exists with templateName PaoName
            meterDao.getForPaoName(templateName);
        } catch (NotFoundException e) {
            ErrorObject err = mspObjectDao.getErrorObject(meterNumber, 
                                                             "Error: MeterNumber(" + meterNumber + ")- AMRMeterType(" + templateName + ") not found in Yukon. Meter was NOT added.",
                                                             "Meter");
            logMSPActivity("MeterAddNotification", err.getErrorString(), mspVendor.getCompanyName());
            return err;
        }
        return null;
    }
    
    /**
     * Returns ErrorObject when meter is enabled.
     * Returns null when meter is disabled.
     * @param meter
     * @param mspVendor
     * @return
     */
    private ErrorObject isMeterDisabled(com.cannontech.amr.meter.model.Meter meter, MultispeakVendor mspVendor) {
        if( !meter.isDisabled()) {
            //Meter is currently enabled in Yukon
            ErrorObject err = mspObjectDao.getErrorObject(meter.getMeterNumber(), 
                                                             "Error: MeterNumber(" + meter.getMeterNumber()+ ") - Meter is already active." + 
                                                             "DeviceName: " + meter.getName() + "; Address: " + meter.getAddress() + ". No updates were made.",
                                                             "Meter");
            logMSPActivity("MeterAddNotification", err.getErrorString(), mspVendor.getCompanyName());
            return err;
        }
        return null;
    }
    
    /**
     * Returns ErrorObject when mspMeter is not valid.
     * Returns null when mspMeter is valid.
     * Validates    1) Meter.MeterNo field is not blank.
     *              2) Meter.Nameplate is not null AND Meter.Nameplate.TransponderId is not blank 
     * @param mspMeter
     * @param mspVendor
     * @return
     */
    private ErrorObject isValidMeter(Meter mspMeter, MultispeakVendor mspVendor) {

        //Check for valid MeterNo 
        if ( StringUtils.isBlank(mspMeter.getMeterNo()) ) {

            ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(), 
                                                             "Error: MeterNo is invalid (empty or null).  No updates were made.",
                                                             "Meter");
            logMSPActivity("MeterAddNotification", err.getErrorString(), mspVendor.getCompanyName());
            return err;
        }

        //Check for valid TransponderID
        if ( mspMeter.getNameplate() == null ||
                StringUtils.isBlank(mspMeter.getNameplate().getTransponderID()) &&
                StringUtils.isNumeric(mspMeter.getNameplate().getTransponderID().trim()) ) {

            ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(), 
                                                             "Error: MeterNumber(" + mspMeter.getMeterNo() + ") - TransponderID is invalid.  No updates were made.",
                                                             "Meter");
            logMSPActivity("MeterAddNotification", err.getErrorString(), mspVendor.getCompanyName());
            return err;
        }
        return null;
    }
    
    /**
     * Enables and "adds" meter (treated as an update since meter already exists) with mspMeter data. 
     * (Use this method when you have a meter that already exists with Same MeterNumber and Address as mspMeter
     * and is disabled).
     * Remove meter from Inventory Group.
     * Update it's billingCycle
     * Updates PaoName and enables meter.
     */
    private void addExistingMeter(Meter mspMeter, 
            com.cannontech.amr.meter.model.Meter meter, 
            MultispeakVendor mspVendor,
            int paoAlias) {
        
        final String paoAliasStr = MultispeakVendor.paoNameAliasStrings[paoAlias];
        //Load the CIS serviceLocation.
        ServiceLocation mspServiceLocation = mspObjectDao.getMspServiceLocation(meter.getMeterNumber(), mspVendor);
        String billingCycle = mspServiceLocation.getBillingCycle();
        
        //Enable Meter and update applicable fields.
        removeFromGroup(meter, SystemGroupEnum.INVENTORY, "MeterAddNotification", mspVendor);
    
        //update the billing group from CIS billingCyle
        updateBillingCyle(billingCycle, meter, "MeterAddNotification", mspVendor);
        
        LiteYukonPAObject liteYukonPaobject = paoDao.getLiteYukonPAO(meter.getDeviceId());
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
        String logString = "";
        String currentPaoName = meter.getName();
        String newPaoName = getPaoNameFromMspMeter(mspMeter, paoAlias, currentPaoName);
        
        if( newPaoName == null) {
            logString = "; PAOName(" + paoAliasStr + ") No Change - MSP " + paoAliasStr + " is empty.";
        } else if (currentPaoName.equalsIgnoreCase(newPaoName)) {
            logString = "; PAOName(" + paoAliasStr + ") No change in value.";
        } else {
            yukonPaobject.setPAOName(newPaoName);
            logString = "PAOName(" + paoAliasStr + ")(OLD:" + currentPaoName + " NEW:" + newPaoName + ").";
        }
        
        //Enable the meter
        yukonPaobject.setDisabled(false);
        //Update the meter
        dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
        logMSPActivity("MeterAddNotification",
                       "MeterNumber(" + meter.getMeterNumber()+ ") - Meter Enabled; " + logString,
                       mspVendor.getCompanyName());
        //TODO Read the Meter.
    }
}