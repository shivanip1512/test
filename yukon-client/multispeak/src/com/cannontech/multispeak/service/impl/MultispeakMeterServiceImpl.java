 /*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.service.impl;

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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.service.DeviceDefinitionService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.multispeak.block.FormattedBlockService;
import com.cannontech.multispeak.block.impl.LoadFormattedBlockImpl;
import com.cannontech.multispeak.block.impl.OutageFormattedBlockImpl;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.SubstationDao;
import com.cannontech.multispeak.dao.SubstationToRouteMappingDao;
import com.cannontech.multispeak.db.Substation;
import com.cannontech.multispeak.deploy.service.CB_ServerSoap_BindingStub;
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
import com.cannontech.multispeak.service.MultispeakMeterService;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.BasicServerConnection;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MultispeakMeterServiceImpl implements MultispeakMeterService, MessageListener {
	
    private MultispeakFuncs multispeakFuncs;
    private BasicServerConnection porterConnection;
    private MspMeterDao mspMeterDao;
    private DBPersistentDao dbPersistentDao;
    private PaoDao paoDao;
    private MspObjectDao mspObjectDao;
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private MeterDao meterDao;
    private TransactionTemplate transactionTemplate = null;
    private DeviceDefinitionDao deviceDefinitionDao = null;
    private DeviceDefinitionService deviceDefinitionService  = null;
    private DeviceCreationService deviceCreationService = null;
    private DeviceUpdateService deviceUpdateService = null;
    private SubstationDao substationDao = null;
    private SubstationToRouteMappingDao substationToRouteMappingDao = null;

	/** Singleton incrementor for messageIDs to send to porter connection */
	private static long messageID = 1;

	/** A map of Long(userMessageID) to MultispeakEvent values */
	private static Map<Long,MultispeakEvent> eventsMap = Collections.synchronizedMap(new HashMap<Long,MultispeakEvent>());

    /**
     * Get the static instance of Multispeak (this) object.
     * Adds a message listener to the pil connection instance. 
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

	@Override
	public void messageReceived(MessageEvent e)
	{
		Message in = e.getMessage();		
		if(in instanceof Return)
		{
			final Return returnMsg = (Return) in;
			final MultispeakEvent event = getEventsMap().get(new Long (returnMsg.getUserMessageID()) );
			
			if(event != null) {
			    
				final Logger eLogger = YukonLogManager.getLogger(MultispeakMeterServiceImpl.class); 
    			Runnable eventRunner = new Runnable() {
    			
    			    @Override
    			    public void run() {
                        
                        eLogger.info("Message Received [ID:"+ returnMsg.getUserMessageID() + 
                                        " DevID:" + returnMsg.getDeviceID() + 
                                        " Command:" + returnMsg.getCommandString() +
                                        " Result:" + returnMsg.getResultString() + 
                                        " Status:" + returnMsg.getStatus() +
                                        " More:" + returnMsg.getExpectMore()+"]");
        
                        if(returnMsg.getExpectMore() == 0) {
                            
        					eLogger.info("Received Message From ID:" + returnMsg.getDeviceID() + " - " + returnMsg.getResultString());
        
                            boolean doneProcessing = event.messageReceived(returnMsg);
                            if (doneProcessing)
                                getEventsMap().remove(new Long(event.getPilMessageID()));
        				}
    			    }
			    };
			    
			    eventRunner.run();
			}
		}
	}
	
    @Override
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
            mspObjectDao.logMSPActivity("getCDMeterState",
            				"(ID:" + meter.getDeviceId() + ") MeterNumber (" + meterNumber + ") - " + commandStr,
            				mspVendor.getCompanyName());    

            synchronized (event) {
                boolean timeout = !waitOnEvent(event, mspVendor.getRequestMessageTimeout());
                if( timeout ) {
                    
                    event.setResultMessage("Reading Timed out after " + (mspVendor.getRequestMessageTimeout()/1000)+ " seconds.");
                    mspObjectDao.logMSPActivity("getCDMeterState", "Reading Timed out after 2 minutes.  No reading collected.", mspVendor.getCompanyName());
                    if(event.getLoadActionCode() == null)
                        event.setLoadActionCode(LoadActionCode.Unknown);
                }

            }
      }

        return event.getLoadActionCode();
    }
    
    @Override
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
        mspObjectDao.logMSPActivity("getLatestReadingByMeterNo",
						"(ID:" + meter.getDeviceId() + ") MeterNumber (" + meterNumber + ") - " + commandStr,
						mspVendor.getCompanyName());

        synchronized (event) {
            boolean timeout = !waitOnEvent(event, mspVendor.getRequestMessageTimeout());
            if( timeout ) {
                mspObjectDao.logMSPActivity("getLatestReadingByMeterNo", "MeterNumber (" + meterNumber + ") - Reading Timed out after " + 
                               (mspVendor.getRequestMessageTimeout()/1000) + 
                               " seconds.  No reading collected.", mspVendor.getCompanyName());
            }
        }

        return event.getDevice().getMeterRead();
    }
    
    @Override
	public synchronized ErrorObject[] odEvent(MultispeakVendor vendor, 
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
			    ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "ODEvent", vendor.getCompanyName());
			    errorObjects.add(err);              
			}
		}
    		
		return mspObjectDao.toErrorObject(errorObjects);
	}

	
    @Override
    public synchronized ErrorObject[] meterReadEvent(MultispeakVendor vendor, 
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
 	               
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "MeterReadEvent", vendor.getCompanyName());
            	errorObjects.add(err);            
            }
        }
        
        return mspObjectDao.toErrorObject(errorObjects);
    }
    
    @Override
    public synchronized ErrorObject[] blockMeterReadEvent(MultispeakVendor vendor, 
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
               
            ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "BlockMeterReadEvent", vendor.getCompanyName());
            errorObjects.add(err);
        }
        
        return mspObjectDao.toErrorObject(errorObjects);
    }
    
    @Override
    public synchronized ErrorObject[] cdEvent(MultispeakVendor vendor, 
            ConnectDisconnectEvent [] cdEvents,
            String transactionID) throws RemoteException
    {
        if ( ! porterConnection.isValid() )
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        CTILogger.info("Received " + cdEvents.length + " Meter(s) for Connect/Disconnect from " + vendor.getCompanyName());
        
        for (ConnectDisconnectEvent cdEvent : cdEvents) {
            String meterNumber = cdEvent.getObjectID();
            
            //Try to load MeterNumber from another element
            if( StringUtils.isBlank(meterNumber)) {
                meterNumber = cdEvent.getMeterID(); //SEDC
                if( StringUtils.isBlank(meterNumber)) {
                    meterNumber = cdEvent.getMeterNo();
                }
            }
            if (meterNumber != null) {
                meterNumber = meterNumber.trim();
            }
            
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
 	                mspObjectDao.logMSPActivity("initiateConnectDisconnect",
 	        						"(ID:" + meter.getDeviceId() + ") MeterNumber (" + meterNumber + ") - " + commandStr + 
 	        						((cdEvent.getReasonCode() != null) ? " sent for ReasonCode: " + cdEvent.getReasonCode().getValue() : ""),
 	        						vendor.getCompanyName());
 	            } else {
 	                ErrorObject err = mspObjectDao.getErrorObject(meterNumber, 
 	                												"MeterNumber (" + meterNumber + ") - Invalid Yukon Connect/Disconnect Meter.",
 	                												"Meter",
 	                												"CDEvent", vendor.getCompanyName());
 	                errorObjects.add(err);
 	            }
            } 
            catch (NotFoundException e) {
 	               
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "CDEvent", vendor.getCompanyName());
                errorObjects.add(err);
            }
        }
            
        return mspObjectDao.toErrorObject(errorObjects);
    }
    
    /*
     * Writes a request to pil for the meter and commandStr using the id for mspVendor.
     * CTILogger a message for the method name.
     */
    private void writePilRequest(com.cannontech.amr.meter.model.Meter meter, String commandStr, long id, int priority) {
        
        Request pilRequest = setupPilRequest(meter, commandStr, id, priority);
        doWritePilRequest(pilRequest);
    }
    
    private Request setupPilRequest(com.cannontech.amr.meter.model.Meter meter, String commandStr, long id, int priority) {
        
        Request pilRequest = null;
        commandStr += " update";
        commandStr += " noqueue";
        pilRequest = new Request(meter.getDeviceId(), commandStr, id);
        pilRequest.setPriority(priority);
        
        return pilRequest;
    }
    
    private void doWritePilRequest(Request pilRequest) {
        porterConnection.write(pilRequest);
    }
    
    /**
     * Returns true if event processes without timing out, false if event times out.
     * @param event
     * @return
     */
    private boolean waitOnEvent(MultispeakEvent event, long timeout) {
        
        long millisTimeOut = 0; //
        while (!event.isPopulated() && millisTimeOut < timeout)  //quit after timeout
        {
            try {
                Thread.sleep(10);
                millisTimeOut += 10;
            } catch (InterruptedException e) {
                CTILogger.error(e);
            }
        }
        if( millisTimeOut >= timeout) {// this broke the loop, more than likely, have to kill it sometime
            return false;
        }
        return true;
    }

	private static Map<Long,MultispeakEvent> getEventsMap() {
		return eventsMap;
	}

    @Override
    public ErrorObject[] initiateDisconnectedStatus(MultispeakVendor mspVendor, String[] meterNos) {
        
        return addToGroup(meterNos, SystemGroupEnum.DISCONNECTSTATUS, "initiateDisconnectedStatus", mspVendor);
    }

    @Override
    public ErrorObject[] initiateUsageMonitoringStatus(MultispeakVendor mspVendor, String[] meterNos) {
        
        return addToGroup(meterNos, SystemGroupEnum.USAGEMONITORING, "initiateUsageMonitoring", mspVendor);
    }

    @Override
    public ErrorObject[] cancelDisconnectedStatus(MultispeakVendor mspVendor, String[] meterNos) {

        return removeFromGroup(meterNos, SystemGroupEnum.DISCONNECTSTATUS, "cancelDisconnectedStatus", mspVendor);
    }

    @Override
    public ErrorObject[] cancelUsageMonitoringStatus(MultispeakVendor mspVendor, String[] meterNos) {

        return removeFromGroup(meterNos, SystemGroupEnum.USAGEMONITORING, "cancelUsageMonitoring", mspVendor);
    }
    
    @Override
    public ErrorObject[] addMeterObject(final MultispeakVendor mspVendor, Meter[] addMeters) throws RemoteException{
        final List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();
        final int paoAlias = multispeakFuncs.getPaoNameAlias();

        for (final Meter mspMeter : addMeters) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult(){
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status){
                       
                        ErrorObject errorObject = isValidMeter(mspMeter, mspVendor, "meterAddNotification");
                        if( errorObject == null) {
                        	com.cannontech.amr.meter.model.Meter meterToAdd = getMeterToAdd(mspMeter, paoAlias);
                            if (meterToAdd == null) {	//and NEW meter
                                List<ErrorObject> addMeterErrors = addNewMeter(mspMeter, mspVendor);
                                errorObjects.addAll(addMeterErrors);
                            } else { //have existing meter to "update"
                            	errorObject = addExistingMeter(mspMeter, meterToAdd, mspVendor, paoAlias);
                            }
                        } 
                        if (errorObject != null) { 
                            errorObjects.add(errorObject);
                        }
                    };
                });
            }catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(), 
                                                                 "X Exception: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(), 
                                                                 "Meter", "MeterAddNotification", mspVendor.getCompanyName());
                errorObjects.add(err);
                CTILogger.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(), 
                                                                 "X Error: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(), 
                                                                 "Meter", "MeterAddNotification", mspVendor.getCompanyName());
                errorObjects.add(err);
                CTILogger.error(ex);
            }
        }//end for

        return mspObjectDao.toErrorObject(errorObjects);
    }
    
    /**
     * Helper method to return the Meter object to "add" to Yukon.
     * Returns null when no existing meter is found in Yukon relating to mspMeter.
     * Returns Meter when mspMeter is found in Yukon by MeterNumber, else by Physical Address, else by PaoName.
     * @param mspMeter
     * @param paoAlias
     * @return
     */
    private com.cannontech.amr.meter.model.Meter getMeterToAdd (Meter mspMeter, int paoAlias) {

    	com.cannontech.amr.meter.model.Meter meter = null;
    	try { //Lookup by MeterNo
    		String mspMeterNo = mspMeter.getMeterNo().trim();
    		meter = meterDao.getForMeterNumber(mspMeterNo);
    	} catch (NotFoundException e) {	//Doesn't exist by MeterNumber
	    	
    		try { //Lookup by Address
    			String mspAddress = mspMeter.getNameplate().getTransponderID().trim();
    			meter = meterDao.getForPhysicalAddress(mspAddress);
    		} catch (NotFoundException e2){ //Doesn't exist by Address
    			
            	try { //Lookup meter by PaoName
            		String paoName = getPaoNameFromMspMeter(mspMeter, paoAlias, null);
            		if (paoName != null) {
            			meter = meterDao.getForPaoName(paoName);
            		}
                } // doesn't exist by PaoName
                catch (NotFoundException e3) {
                	return null;
                }
	        }
    	}
    	return meter;
    
    }
    
    @Override
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
                            mspObjectDao.logMSPActivity("MeterRemoveNotification",
                                           "MeterNumber(" + meterNo + ") - Meter Disabled.",
                                           mspVendor.getCompanyName());
                    }
                }
                else {
                    ErrorObject err = mspObjectDao.getErrorObject(meterNo,
                                                                     "Warning: MeterNumber(" + meterNo + ") - Meter is already disabled. No updates were made.",
                                                                     "Meter", "MeterRemoveNotification", mspVendor.getCompanyName());
                    errorObjects.add(err);
                }
            } 
            catch (NotFoundException e) {
 	               
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNo, "MeterNumber", "Meter",
                										"MeterRemoveNotification", mspVendor.getCompanyName());
				errorObjects.add(err);
            } 
        }
        
        return mspObjectDao.toErrorObject(errorObjects);
    }
    
    @Override
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
                                //Meter exists in Yukon with newPaoName
                                if( newPaoName == null) {
                                    mspObjectDao.logMSPActivity("ServiceLocationChangedNotification",
                                                   "MeterNumber(" + meter.getMeterNumber()+ ") - PAOName(" + paoAliasStr + ") NO CHANGE - MSP " + paoAliasStr + " IS EMPTY.",
                                                   mspVendor.getCompanyName());
                                } else if (currentPaoName.equalsIgnoreCase(newPaoName)) {
                                    mspObjectDao.logMSPActivity("ServiceLocationChangedNotification",
                                                   "MeterNumber(" + meter.getMeterNumber()+ ") - PAOName(" + paoAliasStr + ") NO CHANGE - MSP " + paoAliasStr + " has no change.",
                                                   mspVendor.getCompanyName());
                                } else {
                                    LiteYukonPAObject liteYukonPaobject = paoDao.getLiteYukonPAO(meter.getDeviceId());
                                    YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
                                    if (yukonPaobject instanceof MCTBase) {
                                        newPaoName = getNewPaoName(newPaoName);
                                        yukonPaobject.setPAOName(newPaoName);
                                        dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                                        mspObjectDao.logMSPActivity("ServiceLocationChangedNotification",
                                                       "MeterNumber(" + meter.getMeterNumber()+ ") - PAOName(" + paoAliasStr + ")(OLD:" + currentPaoName + " NEW:" + newPaoName + ").",
                                                       mspVendor.getCompanyName());
                                    }
                                }
                                
                                //Update billing cycle information
                                String billingCycle = mspServiceLocation.getBillingCycle();
                                updateBillingCyle(billingCycle, meter.getMeterNumber(), meter, "ServiceLocationChangedNotification", mspVendor);
                               
                            } catch (NotFoundException e) {
                                ErrorObject err = mspObjectDao.getNotFoundErrorObject(mspServiceLocation.getObjectID(),
                                													  paoAliasStr + "(" + newPaoName + ")", "ServiceLocation",
                                                                                      "ServiceLocationChangeNotification",
                                                                                      mspVendor.getCompanyName());
                                errorObjects.add(err);
                            }
            
                        } else {
                            ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(),
                            		paoAliasStr + "(" + newPaoName + ") - valid ServiceLocation was NOT found in MSP Message.",
                            		"ServiceLocation",
                            		"ServiceLocationChangedNotification",
                            		mspVendor.getCompanyName());
                            errorObjects.add(err);
                        }
                    };
                });
            }catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(), 
                                                                 "X Exception: (ServLoc:" + mspServiceLocation.getObjectID() + ")-" + ex.getMessage(), 
                                                                 "ServiceLocation",
                                                                 "ServiceLocationChangedNotification",
                                                                 mspVendor.getCompanyName());
                errorObjects.add(err);
                CTILogger.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(), 
                                                                 "X Error: (ServLoc:" + mspServiceLocation.getObjectID() + ")-" + ex.getMessage(), 
                                                                 "ServiceLocation",
                                                                 "ServiceLocationChangedNotification",
                                                                 mspVendor.getCompanyName());
                errorObjects.add(err);
                CTILogger.error(ex);
            }
        }
        return mspObjectDao.toErrorObject(errorObjects);
    }

    @Override
    public ErrorObject[] changeMeterObject(final MultispeakVendor mspVendor, Meter[] changedMeters) throws RemoteException{
        final List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();
        final int paoAlias = multispeakFuncs.getPaoNameAlias();

        for (final Meter mspMeter : changedMeters) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult(){
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status){
                        String mspAddress;
                        
                        ErrorObject invalidMeterError = isValidMeter(mspMeter, mspVendor, "meterChangedNotification");
                        if( invalidMeterError == null) {
                            mspAddress = mspMeter.getNameplate().getTransponderID().trim();
                            try {
                                com.cannontech.amr.meter.model.Meter meter = meterDao.getForPhysicalAddress(mspAddress);
                                changeMeter(mspMeter, meter, mspVendor, paoAlias);
                            } 
                            catch (NotFoundException e) {
                                ErrorObject err = mspObjectDao.getNotFoundErrorObject(mspMeter.getMeterNo(), "Address: " + mspAddress, "Meter",
                                		"MeterChangedNotification", mspVendor.getCompanyName());
                                errorObjects.add(err);              
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
                                                                 "Meter", "MeterChangedNotification", mspVendor.getCompanyName());
                errorObjects.add(err);
                CTILogger.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(), 
                                                                 "X Error: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(), 
                                                                 "Meter", "MeterChangedNotification", mspVendor.getCompanyName());
                errorObjects.add(err);
                CTILogger.error(ex);
            }
        }//end for

        return mspObjectDao.toErrorObject(errorObjects);
    }
    
    /**
     * Creates a new meter, sets it's name, address, meter number, and guesses a route to set.
     * 
     * @param mspMeter
     * @param mspVendor
     * @param templatePaoName
     * @param substationName
     */
    private void createMeter(Meter mspMeter, MultispeakVendor mspVendor, String templatePaoName, String substationName) {
        
        // METER_NUMBER
        String meterNumber = mspMeter.getMeterNo().trim();
        
        // NAME
        int paoAlias = multispeakFuncs.getPaoNameAlias();
        String paoName = getPaoNameFromMspMeter(mspMeter, paoAlias, meterNumber);
        
        // ADDRESS
        String address = mspMeter.getNameplate().getTransponderID().trim();
        
        // CREATE DEVICE - new device is automatically added to template's device groups
        YukonDevice newDevice = deviceCreationService.createDeviceByTemplate(templatePaoName, paoName, true);
        mspObjectDao.logMSPActivity("MeterAddNotification", "DeviceName(" + paoName + ") - Meter created.", mspVendor.getCompanyName());
        
        // UPDATE DEVICE METERNUMBER, ADDRESS
        deviceUpdateService.changeAddress(newDevice, Integer.valueOf(address));
        deviceUpdateService.changeMeterNumber(newDevice, meterNumber);
        mspObjectDao.logMSPActivity("MeterAddNotification", "DeviceName(" + paoName+ ") - Set Meter number (" + meterNumber + ") and Address (" + address + ").", mspVendor.getCompanyName());
        
        // UPDATE BILLING GROUP
        ServiceLocation mspServiceLocation = mspObjectDao.getMspServiceLocation(meterNumber, mspVendor);
        String cycleGroupName = mspServiceLocation.getBillingCycle();
        updateBillingCyle(cycleGroupName, meterNumber, newDevice, "MeterAddNotification", mspVendor);
        
        // ROUTES
        try {
            
            // get routes
            Substation substation = substationDao.getByName(substationName);
            List<Integer> routeIds = substationToRouteMappingDao.getRouteIdsBySubstationId(substation.getId());
            
            // set route
            if (routeIds.size() > 0) {
                
                // initally set route to first sub mapping
                deviceUpdateService.changeRoute(newDevice, routeIds.get(0));
                mspObjectDao.logMSPActivity("MeterAddNotification", "MeterNumber(" + meterNumber + ") - Route initially set to that of template device, will run route discovery.", mspVendor.getCompanyName());
            
                // run route discovery
                LiteYukonUser liteYukonUser = UserUtils.getYukonUser(); 
                deviceUpdateService.routeDiscovery(newDevice, routeIds, liteYukonUser);
                
                List<String> routeNames = new ArrayList<String>(routeIds.size());
                for (int routeId : routeIds) {
                    routeNames.add(paoDao.getRouteNameForRouteId(routeId));
                }
                mspObjectDao.logMSPActivity("MeterAddNotification", "MeterNumber(" + meterNumber + ") - Route discovery started on: " + StringUtils.join(routeNames, ",") + ".", mspVendor.getCompanyName());
            
            // no routes for sub
            } else {
            	mspObjectDao.logMSPActivity("MeterAddNotification", "MeterNumber(" + meterNumber + ") - No Routes for substation (" + substationName + "), using route from template device.", mspVendor.getCompanyName());
            }
            
        // bad sub name
        } catch(NotFoundException e) {
            mspObjectDao.logMSPActivity("MeterAddNotification", "MeterNumber(" + meterNumber + ") - " + e.getMessage() + ", using route from template device.", mspVendor.getCompanyName());
        }
    }
    
    /**
     * Returns a Yukon PaoName (template) to model new meters after.
     * If no value is provided in the mspMeter object, then the defaultTemplateName is returned. 
     * @param mspMeter
     * @param defaultTemplateName
     * @return
     */
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
    private void updateBillingCyle(String newBilling, String meterNumber, YukonDevice meter,
            String logActionStr, MultispeakVendor mspVendor) {

        boolean alreadyInGroup = false;
        if (!StringUtils.isBlank(newBilling)) {

            //Remove from all billing membership groups
            DeviceGroup billingCycledeviceGroup = multispeakFuncs.getBillingCycleDeviceGroup();
            StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getStoredGroup(billingCycledeviceGroup);
            Set<StoredDeviceGroup> deviceGroups = deviceGroupMemberEditorDao.getGroupMembership(deviceGroupParent, meter);
            for (StoredDeviceGroup deviceGroup : deviceGroups) {
                if( deviceGroup.getName().equalsIgnoreCase(newBilling) ) {
                    System.out.println("Already in billing group:  " + newBilling);
                    alreadyInGroup = true;
                } else {
                    deviceGroupMemberEditorDao.removeDevices(deviceGroup, meter);
                    mspObjectDao.logMSPActivity(logActionStr,
                                   "MeterNumber(" + meterNumber + ") - Removed from Group: " + deviceGroup.getFullName() + ".",
                                   mspVendor.getCompanyName());
                }
            }

            if (!alreadyInGroup) {
                StoredDeviceGroup billingGroup = deviceGroupEditorDao.getGroupByName(deviceGroupParent, newBilling, true);
                deviceGroupMemberEditorDao.addDevices(billingGroup, meter);
                mspObjectDao.logMSPActivity(logActionStr,
                               "MeterNumber(" + meterNumber+ ") - Added to Group: " + billingGroup.getFullName() + ".",
                               mspVendor.getCompanyName());
            }
        }
    }

    /**
     * Helper method to remove meter from systemGroup
     * @param meter
     * @param systemGroup
     * @param logActionStr
     * @param mspVendor
     */
    private void removeFromGroup(com.cannontech.amr.meter.model.Meter meter, SystemGroupEnum systemGroup, 
            String logActionStr, MultispeakVendor mspVendor){

        DeviceGroup deviceGroup = deviceGroupEditorDao.getSystemGroup(systemGroup);
        deviceGroupMemberEditorDao.removeDevices((StoredDeviceGroup)deviceGroup, Collections.singletonList(meter));

        mspObjectDao.logMSPActivity(logActionStr,
                       "MeterNumber(" + meter.getMeterNumber() + ") - Removed from " + systemGroup.getFullPath(),
                       mspVendor.getCompanyName());
    }

    /**
     * Helper method to add meter to systemGroup
     * @param meter
     * @param systemGroup
     * @param logActionStr
     * @param mspVendor
     */
    private void addToGroup(com.cannontech.amr.meter.model.Meter meter, SystemGroupEnum systemGroup, 
            String logActionStr, MultispeakVendor mspVendor){

        DeviceGroup deviceGroup = deviceGroupEditorDao.getSystemGroup(systemGroup);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup)deviceGroup, Collections.singletonList(meter));

        mspObjectDao.logMSPActivity(logActionStr,
                       "MeterNumber(" + meter.getMeterNumber() + ") - Added to " + systemGroup.getFullPath(), 
                       mspVendor.getCompanyName());
    }
   
    /**
     * Helper method to add meterNos to systemGroup
     * @param meterNos
     * @param systemGroup
     * @param logActionStr
     * @param mspVendor
     * @return
     */
    private ErrorObject[] addToGroup(String[] meterNos, SystemGroupEnum systemGroup, String logActionStr, MultispeakVendor mspVendor) {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();

        for (String meterNumber : meterNos) {
            com.cannontech.amr.meter.model.Meter meter;
            try {
                meter = meterDao.getForMeterNumber(meterNumber);
                addToGroup(meter, systemGroup, logActionStr, mspVendor);
            } 
            catch (NotFoundException e) {
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "addToGroup", mspVendor.getCompanyName());
                errorObjects.add(err);              
            }
        }
        return mspObjectDao.toErrorObject(errorObjects);
    }

    /**
     * Helper method to remove meterNos from systemGroup 
     * @param meterNos
     * @param systemGroup
     * @param logActionStr
     * @param mspVendor
     * @return
     */
    private ErrorObject[] removeFromGroup(String[] meterNos, SystemGroupEnum systemGroup, String logActionStr, MultispeakVendor mspVendor) {

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();

        for (String meterNumber : meterNos) {
            com.cannontech.amr.meter.model.Meter meter;
            try {
                meter = meterDao.getForMeterNumber(meterNumber);
                removeFromGroup(meter, systemGroup, logActionStr, mspVendor);
            } 
            catch (NotFoundException e) {
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "removeFromGroup", mspVendor.getCompanyName());
                errorObjects.add(err);
            }
        }

        return mspObjectDao.toErrorObject(errorObjects);
    }
    
    /**
     * Helper method to return the PaoName alias value for the MultiSpeak mspMeter object.
     * defaultValue is returned when the paoNameAlias is not found for the MultiSpeakVendor enums. 
     * @param mspMeter
     * @param paoNameAlias
     * @param defaultValue
     * @return
     */
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
            case MultispeakVendor.GRID_LOCATION_PAONAME:
            {
                if(StringUtils.isBlank(mspMeter.getFacilityID()) )
                   return null;
               return mspMeter.getFacilityID();
            }
            case MultispeakVendor.DEFAULT_PAONAME:
            { // lookup by meter number
				return mspMeter.getMeterNo();
            }
			default:
				return defaultValue;
		}
	}
	
    /**
    /**
     * Helper method to return the PaoName alias value for the MultiSpeak mspServiceLocation object.
     * defaultValue is returned when the paoNameAlias is not found for the MultiSpeakVendor enums. 
     * @param mspServiceLocation
     * @param mspVendor
     * @param paoNameAlias
     * @param defaultValue
     * @return
     */
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
            case MultispeakVendor.GRID_LOCATION_PAONAME:
            {
                if(StringUtils.isBlank(mspServiceLocation.getGridLocation()))
                   return null;
               return mspServiceLocation.getGridLocation();
            }   
            case MultispeakVendor.DEFAULT_PAONAME:
            { // lookup by meter number
                //lookup meter by servicelocation
                String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_MR_STR);
        
                try {
                    String serviceLocationStr = mspServiceLocation.getObjectID();
                    CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor, MultispeakDefines.CB_MR_STR);
                    if (port != null) {
                        long start = System.currentTimeMillis();
                        CTILogger.debug("Begin call to getMeterByServLoc for ServLoc:" + serviceLocationStr);
                        Meter[] mspMeters = port.getMeterByServLoc(serviceLocationStr);
                        CTILogger.debug("End call to getMeterByServLoc for ServLoc:" + serviceLocationStr + "  (took " + (System.currentTimeMillis() - start) + " millis)");
                        if( mspMeters != null ) {
                            for (Meter mspMeter : mspMeters) {
                                return mspMeter.getMeterNo();
                            }
                        }
                    } else {
                        CTILogger.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for ServLoc: " + mspServiceLocation.getObjectID());
                    }
                } catch (RemoteException e) {
                    CTILogger.error("TargetService: " + endpointURL + " - updateServiceLocation (" + mspVendor.getCompanyName() + ") for ServLoc: " + mspServiceLocation.getObjectID());
                    CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
                }
                return null;
            }
            default:
                return defaultValue;
        }
    }
	
    /**
     * Helper method to add a new meter to Yukon.
     * An errorObject will be returned when the templateName is not a valid YukonPaobject Name.
     * An errorObject will be returned when the substation name is not a valid Substation To Route Mapping.
     * @param mspMeter
     * @param mspVendor
     * @return
     */
	private List<ErrorObject> addNewMeter(Meter mspMeter, MultispeakVendor mspVendor){
	    
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
                createMeter(mspMeter, mspVendor, templateName, "");
            } else {
                String substationName = mspMeter.getUtilityInfo().getSubstationName();
                err = isValidSubstation(substationName, meterNo, mspVendor);
                if (err != null) {
                    errorObjects.add(err);
                } else {
                    createMeter(mspMeter, mspVendor, templateName, substationName);
                }
            }
        }
        return errorObjects;
	}
	
	/**
     * Returns ErrorObject when substation name is invalid or 
     * the substation does not return any valid SubstationToRouteMappings.
     * Returns null when a substation name has routes mapped to it.
     * @param templateName
     * @param meterNumber
     * @param mspVendor
     * @return
     */
    private ErrorObject isValidSubstation(String substationName, String meterNumber, MultispeakVendor mspVendor) {
        
        String errorReason = null;
        try {
            Substation substation = substationDao.getByName(substationName);
            List<Integer> routeIds = substationToRouteMappingDao.getRouteIdsBySubstationId(substation.getId());
            
            if(routeIds.size() < 1){
                errorReason = "No RouteMappings found in Yukon";
            }
            
        } catch (NotFoundException e) {
            errorReason = e.getMessage();
        }
        
        if (errorReason != null) {
            ErrorObject err = mspObjectDao.getErrorObject(meterNumber, 
                                                          "Error: MeterNumber(" + meterNumber + ") - SubstationName(" + 
                                                          substationName + ") - " + errorReason + ".  Meter was NOT added",
                                                          "Meter", "MeterAddNotification", mspVendor.getCompanyName());
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
                                                          "Error: MeterNumber(" + meterNumber + ")- AMRMeterType(" + templateName + ") NOT found in Yukon.",
                                                          "Meter", "MeterAddNotification", mspVendor.getCompanyName());
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
                                                             "Meter", "MeterAddNotification", mspVendor.getCompanyName());
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
    private ErrorObject isValidMeter(Meter mspMeter, MultispeakVendor mspVendor, String method) {

        //Check for valid MeterNo 
        if ( StringUtils.isBlank(mspMeter.getMeterNo()) ) {

            ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(), 
                                                             "Error: MeterNo is invalid (empty or null).  No updates were made.",
                                                             "Meter", method, mspVendor.getCompanyName());
            return err;
        }

        //Check for valid TransponderID
        if ( mspMeter.getNameplate() == null ||
                StringUtils.isBlank(mspMeter.getNameplate().getTransponderID()) ||
                !StringUtils.isNumeric(mspMeter.getNameplate().getTransponderID().trim()) ) {

            ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(), 
                                                             "Error: MeterNumber(" + mspMeter.getMeterNo() + ") - TransponderID is invalid.  No updates were made.",
                                                             "Meter", method, mspVendor.getCompanyName());
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
     * Returns an ErrorObject if meter is "active" (not disabled).  
     * Returns an ErrorObject if meter's address is not the same as mspMeter's address and the device in Yukon 
     *  for the mspMeter's address is not disabled
     */
    private ErrorObject addExistingMeter(Meter mspMeter, 
            com.cannontech.amr.meter.model.Meter meter, 
            MultispeakVendor mspVendor,
            int paoAlias) {
        
    	//Verify the meter to "update" is disabled.
        ErrorObject err = isMeterDisabled(meter, mspVendor);
        if (err != null) {
            return err;
        }
        
        //Verify the meter to "update" does not have the same address as another enabled meter.
        String mspAddress = mspMeter.getNameplate().getTransponderID().trim();
        String address = meter.getAddress();
        if( !address.equalsIgnoreCase(mspAddress)) { //Same MeterNumber, Different Address

        	try { //Lookup meter by mspAddress
        		com.cannontech.amr.meter.model.Meter meterByAddress = meterDao.getForPhysicalAddress(mspAddress);
        		err = isMeterDisabled(meterByAddress, mspVendor);
        		if (err != null) { //Address is already active
                    return err;
                }
        	} catch (NotFoundException e) {
        		//Ignore Exception...this is what we want, for the address to NOT already exist in Yukon
        	}
        }

        final String paoAliasStr = MultispeakVendor.paoNameAliasStrings[paoAlias];
        //Load the CIS serviceLocation.
        ServiceLocation mspServiceLocation = mspObjectDao.getMspServiceLocation(meter.getMeterNumber(), mspVendor);
        String billingCycle = mspServiceLocation.getBillingCycle();
        
        //Enable Meter and update applicable fields.
        removeFromGroup(meter, SystemGroupEnum.INVENTORY, "MeterAddNotification", mspVendor);
    
        //update the billing group from CIS billingCyle
        updateBillingCyle(billingCycle, meter.getMeterNumber(), meter, "MeterAddNotification", mspVendor);
        
        LiteYukonPAObject liteYukonPaobject = paoDao.getLiteYukonPAO(meter.getDeviceId());
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
        String logString = "";

        //Update PaoName
        String currentPaoName = meter.getName();
        String newPaoName = getPaoNameFromMspMeter(mspMeter, paoAlias, currentPaoName);
        
        if( newPaoName == null) {
            logString = "PAOName(" + paoAliasStr + ")No Change - MSP " + paoAliasStr + " is empty;";
        } else if (currentPaoName.equalsIgnoreCase(newPaoName)) {
            logString = "PAOName(" + paoAliasStr + ")No Change;";
        } else {
            newPaoName = getNewPaoName(newPaoName);
            yukonPaobject.setPAOName(newPaoName);
            logString = "PAOName(" + paoAliasStr + ")(" + currentPaoName + " to " + newPaoName + ");";
        }
        
        //Update MeterNumber
        String currentMeterNumber = meter.getMeterNumber();
        String newMeterNumber = mspMeter.getMeterNo();
        
        if (!currentMeterNumber.equalsIgnoreCase(newMeterNumber)) {
            DeviceMeterGroup deviceMeterGroup = ((MCTBase)yukonPaobject).getDeviceMeterGroup();
            deviceMeterGroup.setMeterNumber(newMeterNumber);
            logString += "(MeterNo-" + currentMeterNumber + " to " + newMeterNumber + ");";
        }

        //Update Address
        String currentAddress = meter.getAddress();
        String newAddress = mspMeter.getNameplate().getTransponderID().trim();
        
        if (!currentAddress.equalsIgnoreCase(newAddress)) {
            DeviceCarrierSettings deviceCarrierSettings = ((MCTBase)yukonPaobject).getDeviceCarrierSettings();
            deviceCarrierSettings.setAddress(Integer.valueOf(newAddress));
            logString += "(Addr-" + currentAddress + " to " + newAddress + ");";
        }
        
        //Enable the meter
        yukonPaobject.setDisabled(false);
        //Update the meter
        dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
        mspObjectDao.logMSPActivity("MeterAddNotification",
                       "MeterNo(" + meter.getMeterNumber()+ ");Enabled;" + logString,
                       mspVendor.getCompanyName());

        changeDeviceType(mspMeter, meter, mspVendor, "MeterAddNotification");
        //TODO Read the Meter.
        return null;
    }

    /** Helper method to update meter based on the mspMeter.
     * PaoName and MeterNumber will be updated if changed.
     * Device will be enabled.
     * @param mspMeter
     * @param meter
     * @param mspVendor
     * @param paoAlias
     */
    private void changeMeter(Meter mspMeter, 
            com.cannontech.amr.meter.model.Meter meter, 
            MultispeakVendor mspVendor,
            int paoAlias) {
        
        final String paoAliasStr = MultispeakVendor.paoNameAliasStrings[paoAlias];
        boolean dbChange = false;
        LiteYukonPAObject liteYukonPaobject = paoDao.getLiteYukonPAO(meter.getDeviceId());
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
            
        String paoNameLog = "";
        String meterNumberLog = "";
        String currentPaoName = meter.getName();
        String newPaoName = getPaoNameFromMspMeter(mspMeter, paoAlias, currentPaoName);
        
        if( newPaoName == null) {
            paoNameLog = "Address (" + meter.getAddress() + ") PAOName(" + paoAliasStr + ") No Change - MSP " + paoAliasStr + " is empty.";
        } else if (currentPaoName.equalsIgnoreCase(newPaoName)) {
            paoNameLog = "Address (" + meter.getAddress() + ") PAOName(" + paoAliasStr + ") No change in PaoName.";
        } else {
            newPaoName = getNewPaoName(newPaoName);
            yukonPaobject.setPAOName(newPaoName);
            dbChange = true;
            paoNameLog = "Address (" + meter.getAddress() + ") PAOName(" + paoAliasStr + ") PaoName Changed(OLD:" + currentPaoName + " NEW:" + newPaoName + ").";
        }

        String currentMeterNumber = meter.getMeterNumber();
        String newMeterNumber = mspMeter.getMeterNo();
        
        if (currentMeterNumber.equalsIgnoreCase(newMeterNumber)) {
            meterNumberLog = "Address (" + meter.getAddress() + ") No change in MeterNumber.";
        } else {
            DeviceMeterGroup deviceMeterGroup = ((MCTBase)yukonPaobject).getDeviceMeterGroup();
            deviceMeterGroup.setMeterNumber(newMeterNumber);
            dbChange = true;
            meterNumberLog = "Address (" + meter.getAddress() + ") MeterNumber Changed(OLD:" + currentMeterNumber + " NEW:" + newMeterNumber + ").";
        }

        if (yukonPaobject.isDisabled()) {
            yukonPaobject.setDisabled(false);
            dbChange = true;
        }
        //Update the meter
        if(dbChange) {
            dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
        }
        mspObjectDao.logMSPActivity("MeterChangedNotification",
                       paoNameLog,
                       mspVendor.getCompanyName());
        mspObjectDao.logMSPActivity("MeterChangedNotification",
                       meterNumberLog,
                       mspVendor.getCompanyName());

        //TODO Read the Meter.
    }
    
    /**
     * Helper method to check if the deviceType of meter is different than the deviceType of the template meter.
     * If different types of meters, then the deviceType will be changed for meter.
     * @param mspMeter
     * @param meter
     * @param mspVendor
     * @param method
     */
    private void changeDeviceType(Meter mspMeter, com.cannontech.amr.meter.model.Meter meter, MultispeakVendor mspVendor, String method) {
        
        String templateName = getMeterTemplate(mspMeter, mspVendor.getTemplateNameDefault());
        com.cannontech.amr.meter.model.Meter templateMeter;
        try {
            templateMeter = meterDao.getForPaoName(templateName);
        } catch (NotFoundException e) {
            //No template found to compare to
            CTILogger.warn(e.getMessage() + "No TemplateName found in Yukon for ChangeDeviceType method, Device Type not checked.");
            return;
        }

        String existingType = meter.getTypeStr();
        if( templateMeter.getType() != meter.getType()) {   //different types of meters...change type
            try {
                DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(templateMeter.getType());
                deviceDefinitionService.changeDeviceType(meter, deviceDefinition);
                mspObjectDao.logMSPActivity(method, "MeterNumber (" + meter.getMeterNumber() + ") - Changed DeviceType from:" + existingType + " to:" + templateMeter.getTypeStr() + ").", mspVendor.getCompanyName());
            } catch (DataRetrievalFailureException e) {
                CTILogger.warn(e);
            } catch (PersistenceException e) {
                CTILogger.warn(e);
            }
        }
    }
    /**
     * Returns an "unused" paoName for newPaoName.
     * If a meter already exists with newPaoName, then a numeric incremented value will be appended to the newPaoName.
     * @param newPaoName
     * @return 
     */
    private String getNewPaoName(String newPaoName) {

        int retryCount = 0;
        String tempPaoName = newPaoName;
        boolean found = false;
        do {
            try {
                if (retryCount > 0) {
                    tempPaoName = newPaoName + " (" + retryCount + ")";
                }
                //Try to find if a meter already exists with this paoName
                meterDao.getForPaoName(tempPaoName);
                retryCount++;
            } catch (NotFoundException e) {
                //this is good!
                found = true;
            }
        } while (!found);

        return tempPaoName;
    }
    
    @Autowired
	public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }
    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }
    @Autowired
    public void setMspMeterDao(MspMeterDao mspMeterDao) {
        this.mspMeterDao = mspMeterDao;
    }
    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    @Autowired
    public void setMspObjectDao(MspObjectDao mspObjectDao) {
        this.mspObjectDao = mspObjectDao;
    }
    @Autowired
    public void setDeviceGroupMemberEditorDao(
            DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    @Autowired
    public void setDeviceGroupEditorDao(
            DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
    @Autowired
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    @Autowired
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
    @Autowired
    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }
    @Autowired
    public void setDeviceDefinitionService(DeviceDefinitionService deviceDefinitionService) {
        this.deviceDefinitionService = deviceDefinitionService;
    }
    @Autowired
    public void setDeviceCreationService(
            DeviceCreationService deviceCreationService) {
        this.deviceCreationService = deviceCreationService;
    }
    @Autowired
    public void setDeviceUpdateService(DeviceUpdateService deviceUpdateService) {
        this.deviceUpdateService = deviceUpdateService;
    }
    @Autowired
    public void setSubstationDao(SubstationDao substationDao) {
        this.substationDao = substationDao;
    }
    @Autowired
    public void setSubstationToRouteMappingDao(
            SubstationToRouteMappingDao substationToRouteMappingDao) {
        this.substationToRouteMappingDao = substationToRouteMappingDao;
    }
}
