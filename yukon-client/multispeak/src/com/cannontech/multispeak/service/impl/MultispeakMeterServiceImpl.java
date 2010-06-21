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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.search.dao.MeterSearchDao;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchField;
import com.cannontech.amr.meter.search.model.OrderBy;
import com.cannontech.amr.meter.search.model.StandardFilterBy;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.model.Route;
import com.cannontech.common.model.Substation;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.SubstationDao;
import com.cannontech.core.dao.SubstationToRouteMappingDao;
import com.cannontech.core.roleproperties.MultispeakMeterLookupFieldEnum;
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
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.ExtensionsItem;
import com.cannontech.multispeak.deploy.service.LoadActionCode;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.MeterGroup;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
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
    private PaoDefinitionDao paoDefinitionDao = null;
    private DeviceCreationService deviceCreationService = null;
    private DeviceUpdateService deviceUpdateService = null;
    private SubstationDao substationDao = null;
    private SubstationToRouteMappingDao substationToRouteMappingDao = null;
    private DeviceDao deviceDao = null;
    private MeterSearchDao meterSearchDao = null;

	/** Singleton incrementor for messageIDs to send to porter connection */
	private static long messageID = 1;

	/** A map of Long(userMessageID) to MultispeakEvent values */
	private static Map<Long,MultispeakEvent> eventsMap = Collections.synchronizedMap(new HashMap<Long,MultispeakEvent>());

	private static final String EXTENSION_POSITION_NUMBER_STRING = "positionNumber";
	private static final String EXTENSION_DEVICE_TEMPLATE_STRING = "AMRMeterType";

	// Strings to represent MultiSpeak method calls, generally used for logging.
    private static final String METER_REMOVE_STRING = "MeterRemoveNotification";
    private static final String METER_CHANGED_STRING = "MeterChangedNotification";
    private static final String METER_ADD_STRING = "MeterAddNotification";
    private static final String SERV_LOC_CHANGED_STRING = "ServiceLocationChangedNotification";
    
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
            writePilRequest(meter, commandStr, id, 13);
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
        writePilRequest(meter, commandStr, id, 13);
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
                String voltageProfileCommandStr = "getvalue lp channel 4 " + format.format(new Date());
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
                writePilRequest(meter, voltageProfileCommandStr, id, 13);
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
                       
                        ErrorObject errorObject = isValidMeter(mspMeter, mspVendor, METER_ADD_STRING);
                        if( errorObject == null) {
                            try {
                                com.cannontech.amr.meter.model.Meter meterToAdd = getMeterByMspMeter(mspMeter, paoAlias, mspVendor);
                                // have existing meter to "update"
                                errorObject = addExistingMeter(mspMeter, meterToAdd, mspVendor, paoAlias);
                            } catch (NotFoundException e) { //and NEW meter
                                List<ErrorObject> addMeterErrors = addNewMeter(mspMeter, mspVendor);
                                errorObjects.addAll(addMeterErrors);
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
                                                                 "Meter", METER_ADD_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                CTILogger.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(), 
                                                                 "X Error: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(), 
                                                                 "Meter", METER_ADD_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                CTILogger.error(ex);
            }
        }//end for

        return mspObjectDao.toErrorObject(errorObjects);
    }
    
    /**
     * Helper method to return the Yukon Meter object for mspMeter object.
     * Returns Meter when mspMeter is found in Yukon by the mspMeterLookup (roleProperty value field)
     * @param mspMeter
     * @param paoAlias
     * @return
     * @throws NotFoundException
     */
    private com.cannontech.amr.meter.model.Meter getMeterByMspMeter(Meter mspMeter, int paoAlias, MultispeakVendor mspVendor) throws NotFoundException {
        
    	com.cannontech.amr.meter.model.Meter meter = null;
    	MultispeakMeterLookupFieldEnum multispeakMeterLookupFieldEnum= multispeakFuncs.getMeterLookupField();

    	if( multispeakMeterLookupFieldEnum == MultispeakMeterLookupFieldEnum.METER_NUMBER) {
            meter = getMeterByMeterNumber(mspMeter);
        } else if( multispeakMeterLookupFieldEnum == MultispeakMeterLookupFieldEnum.ADDRESS ){ 
    			meter = getMeterByAddress(mspMeter);
    	} else if ( multispeakMeterLookupFieldEnum == MultispeakMeterLookupFieldEnum.DEVICE_NAME ) {
    	    meter = getMeterByPaoName(mspMeter, paoAlias, mspVendor);
        } else if ( multispeakMeterLookupFieldEnum == MultispeakMeterLookupFieldEnum.AUTO_METER_NUMBER_FIRST) {
	       try { //Lookup by MeterNo
	           meter = getMeterByMeterNumber(mspMeter);
           } catch (NotFoundException e) { //Doesn't exist by MeterNumber
               try { //Lookup by Address
                   meter = getMeterByAddress(mspMeter);
               } catch (NotFoundException e2){ //Doesn't exist by Address
                   meter = getMeterByPaoName(mspMeter, paoAlias,mspVendor);
                   //Not Found Exception thrown in the end if never found
               }
           }
        } else if ( multispeakMeterLookupFieldEnum == MultispeakMeterLookupFieldEnum.AUTO_DEVICE_NAME_FIRST) {
            try { //Lookup by Device Name
                meter = getMeterByPaoName(mspMeter, paoAlias,mspVendor);
            } catch (NotFoundException e) { //Doesn't exist by Device name
                try { //Lookup by Meter Number
                    meter = getMeterByMeterNumber(mspMeter);
                } catch (NotFoundException e2) {
                    //Lookup by Address
                    meter = getMeterByAddress(mspMeter);
                //Not Found Exception thrown in the end if never found
                }
            }
        }
 
    	return meter;
    }

    /**
     * Helper method to return a Meter object for PaoName
     * PaoName is looked up based on PaoNameAlias
     * @param mspMeter
     * @param paoAlias
     * @param mspVendor
     * @return
     */
    private com.cannontech.amr.meter.model.Meter getMeterByPaoName(Meter mspMeter, int paoAlias, MultispeakVendor mspVendor) {
        String paoName = getPaoNameFromMspMeter(mspMeter, paoAlias, null, mspVendor);
        // TODO??? What should be done if we can't find a paoName to lookup by?  throw exception?
        if (paoName != null) {
            return meterDao.getForPaoName(paoName);
        }
        return null;
    }

    /**
     * Helper method to return a Meter object for Address
     * @param mspMeter
     * @return
     */
    private com.cannontech.amr.meter.model.Meter getMeterByAddress(Meter mspMeter) {
        String mspAddress = mspMeter.getNameplate().getTransponderID().trim();
        return meterDao.getForPhysicalAddress(mspAddress);
    }

    /**
     * Helper method to return a Meter object for Meter Number
     * @param mspMeter
     * @return
     */
    private com.cannontech.amr.meter.model.Meter getMeterByMeterNumber(Meter mspMeter) {
        String mspMeterNo = mspMeter.getMeterNo().trim();
        return meterDao.getForMeterNumber(mspMeterNo);
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
                    addToGroup(meter, SystemGroupEnum.INVENTORY, METER_REMOVE_STRING, mspVendor);

                    LiteYukonPAObject liteYukonPaobject = paoDao.getLiteYukonPAO(meter.getDeviceId());
                    YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
                    if (yukonPaobject instanceof MCTBase){
                            yukonPaobject.setDisabled(true);
                            dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                            mspObjectDao.logMSPActivity(METER_REMOVE_STRING,
                                           "MeterNumber(" + meterNo + ") - Meter Disabled.",
                                           mspVendor.getCompanyName());
                    }
                }
                else {
                    ErrorObject err = mspObjectDao.getErrorObject(meterNo,
                                                                     "Warning: MeterNumber(" + meterNo + ") - Meter is already disabled. No updates were made.",
                                                                     "Meter", METER_REMOVE_STRING, mspVendor.getCompanyName());
                    errorObjects.add(err);
                }
            } 
            catch (NotFoundException e) {
 	               
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNo, "MeterNumber", "Meter",
                										METER_REMOVE_STRING, mspVendor.getCompanyName());
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
                        
                        if( paoAlias == MultispeakVendor.SERVICE_LOCATION_PAONAME ) {
                            // With Service Location, all we need to do is update fields on the Service Location object itself that may be in Yukon.
                            String paoName = mspServiceLocation.getObjectID();
                            if (StringUtils.isNotBlank(paoName)) {
                                com.cannontech.amr.meter.model.Meter meter = meterDao.getForPaoName(paoName);
                                if ( !meter.isDisabled()) {
                                    //Update billing cycle information
                                    String billingCycle = mspServiceLocation.getBillingCycle();
                                    updateBillingCyle(billingCycle, meter.getMeterNumber(), meter, SERV_LOC_CHANGED_STRING, mspVendor);
                                }
                            }
                        } else if ( paoAlias == MultispeakVendor.SERVICE_LOCATION_WITH_POSITION_NO_PAONAME) {
                            //Use the Service Location value to find all meters that have this name.
                            List<FilterBy> searchFilter = new ArrayList<FilterBy>(1);
                            FilterBy filterBy = new StandardFilterBy("Device Name", MeterSearchField.PAONAME);
                            filterBy.setFilterValue(mspServiceLocation.getObjectID());
                            searchFilter.add(filterBy);
                            OrderBy orderBy = new OrderBy(MeterSearchField.PAONAME.toString(), true);
                            SearchResult<com.cannontech.amr.meter.model.Meter> result = meterSearchDao.search(searchFilter, orderBy, 0, 25);
                            List<com.cannontech.amr.meter.model.Meter> meters = result.getResultList();
                            
                            for (com.cannontech.amr.meter.model.Meter meter: meters) {
                                if ( !meter.isDisabled()) {
                                    //Update billing cycle information
                                    String billingCycle = mspServiceLocation.getBillingCycle();
                                    updateBillingCyle(billingCycle, meter.getMeterNumber(), meter, SERV_LOC_CHANGED_STRING, mspVendor);
                                }
                            }
                        } else {
                            // Must get meters from MSP CB call to process.
                            List<Meter> mspMeters = mspObjectDao.getMspMetersByServiceLocation(mspServiceLocation, mspVendor);
                            
                            if( !mspMeters.isEmpty()) {
    
                                for (Meter mspMeter : mspMeters) {
                                    String meterNumber = mspMeter.getMeterNo();
                                    try {
                                        com.cannontech.amr.meter.model.Meter meter = meterDao.getForMeterNumber(meterNumber);
                                        changeMeter(mspMeter, meter, mspVendor, paoAlias, SERV_LOC_CHANGED_STRING);
                                        
                                        //Update billing cycle information
                                        String billingCycle = mspServiceLocation.getBillingCycle();
                                        updateBillingCyle(billingCycle, meterNumber, meter, SERV_LOC_CHANGED_STRING, mspVendor);
                                    } catch ( NotFoundException e) {
                                        ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                        errorObjects.add(err);            
                                    }
                                }
                                   
                            } else {
                                ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(),
                                        paoAliasStr + "ServiceLocation(" + mspServiceLocation.getObjectID()+ ") - No meters returned from vendor for location.",
                                        "ServiceLocation",
                                        SERV_LOC_CHANGED_STRING,
                                        mspVendor.getCompanyName());
                                errorObjects.add(err);
                            }
                        } 
                    };
                });
            }catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(), 
                                                                 "X Exception: (ServLoc:" + mspServiceLocation.getObjectID() + ")-" + ex.getMessage(), 
                                                                 "ServiceLocation",
                                                                 SERV_LOC_CHANGED_STRING,
                                                                 mspVendor.getCompanyName());
                errorObjects.add(err);
                CTILogger.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(), 
                                                                 "X Error: (ServLoc:" + mspServiceLocation.getObjectID() + ")-" + ex.getMessage(), 
                                                                 "ServiceLocation",
                                                                 SERV_LOC_CHANGED_STRING,
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
                        
                        ErrorObject invalidMeterError = isValidMeter(mspMeter, mspVendor, METER_CHANGED_STRING);
                        if( invalidMeterError == null) {
                            mspAddress = mspMeter.getNameplate().getTransponderID().trim();
                            try {
                                com.cannontech.amr.meter.model.Meter meter = meterDao.getForPhysicalAddress(mspAddress);
                                changeMeter(mspMeter, meter, mspVendor, paoAlias, METER_CHANGED_STRING);
                            } 
                            catch (NotFoundException e) {
                                ErrorObject err = mspObjectDao.getNotFoundErrorObject(mspMeter.getMeterNo(), "Address: " + mspAddress, "Meter",
                                                                                      METER_CHANGED_STRING, mspVendor.getCompanyName());
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
                                                                 "Meter", METER_CHANGED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                CTILogger.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(), 
                                                                 "X Error: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(), 
                                                                 "Meter", METER_CHANGED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                CTILogger.error(ex);
            }
        }//end for

        return mspObjectDao.toErrorObject(errorObjects);
    }
    
    @Override
    public ErrorObject[] addMetersToGroup(MeterGroup meterGroup, MultispeakVendor mspVendor) {

    	List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();    	    	
    	
    	//Convert MeterNumbers to YukonDevices
    	List<SimpleDevice> yukonDevices = new ArrayList<SimpleDevice>();
		if (meterGroup.getMeterList() != null) {
	    	for (String meterNumber : meterGroup.getMeterList()) {
	    		try {
    				SimpleDevice yukonDevice = deviceDao.getYukonDeviceObjectByMeterNumber(meterNumber);
    				yukonDevices.add(yukonDevice);
    			}catch (EmptyResultDataAccessException e) {
	    			ErrorObject errorObject = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "addMetersToGroup", mspVendor.getCompanyName());
    				errorObjects.add(errorObject);
    			}
	    	}
	    }
    	
    	String groupName = meterGroup.getGroupName();
    	StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(groupName, true);
    	deviceGroupMemberEditorDao.addDevices(storedGroup, yukonDevices);
        mspObjectDao.logMSPActivity("addMetersToGroup", "Added " + yukonDevices.size() + " Meters to group: "+ storedGroup.getFullName() + ".",
                       mspVendor.getCompanyName());
        return mspObjectDao.toErrorObject(errorObjects);
    }
    
    @Override
    public ErrorObject[] removeMetersFromGroup(String groupName, String[] meterNumbers, MultispeakVendor mspVendor) {
    	List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();    	    	
    	List<SimpleDevice> yukonDevices = new ArrayList<SimpleDevice>();
    	
    	try {
    		StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(groupName, false);
    		if(meterNumbers != null) {
    	    	for (String meterNumber : meterNumbers) {
    	    		try {
    	    			SimpleDevice yukonDevice = deviceDao.getYukonDeviceObjectByMeterNumber(meterNumber);
    	    			yukonDevices.add(yukonDevice);
    	    		}catch (EmptyResultDataAccessException e) {
    	    			ErrorObject errorObject = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "removeMetersFromGroup", mspVendor.getCompanyName());
    	    			errorObjects.add(errorObject);
    	    		}
    	    	}
        	}
        	
        	deviceGroupMemberEditorDao.removeDevices(storedGroup, yukonDevices);
            mspObjectDao.logMSPActivity("removeMetersFromGroup", "Removed " + yukonDevices.size() + " Meters from group: "+ storedGroup.getFullName() + ".",
                           mspVendor.getCompanyName());
    	} catch (NotFoundException e) {
			ErrorObject errorObject = mspObjectDao.getNotFoundErrorObject(groupName, "GroupName", "MeterGroup", "removeMetersFromGroup", mspVendor.getCompanyName());
			errorObjects.add(errorObject);
   		}
        return mspObjectDao.toErrorObject(errorObjects);
    }

    @Override
    public ErrorObject deleteGroup(String groupName, MultispeakVendor mspVendor) {
    	try {
    		StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(groupName, false);
    		deviceGroupEditorDao.removeGroup(storedGroup);
    	} catch (NotFoundException e) {
			ErrorObject errorObject = mspObjectDao.getNotFoundErrorObject(groupName, "meterGroupId", "MeterGroup", "deleteGroup", mspVendor.getCompanyName());
			return errorObject;
    	}
        return null;
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
        String paoName = getPaoNameFromMspMeter(mspMeter, paoAlias, meterNumber, mspVendor);
        // it is possible that a device with this name already exists, specifically if only looking up by METER_NUMBER.
        // So lets make sure to get one that works for us.
        paoName = getNewPaoName(paoName);
        
        // ADDRESS
        String address = mspMeter.getNameplate().getTransponderID().trim();
        
        // CREATE DEVICE - new device is automatically added to template's device groups
        SimpleDevice newDevice = deviceCreationService.createDeviceByTemplate(templatePaoName, paoName, true);
        mspObjectDao.logMSPActivity(METER_ADD_STRING, "DeviceName(" + paoName + ") - Meter created.", mspVendor.getCompanyName());
        
        // UPDATE DEVICE METERNUMBER, ADDRESS
        deviceUpdateService.changeAddress(newDevice, Integer.valueOf(address));
        deviceUpdateService.changeMeterNumber(newDevice, meterNumber);
        mspObjectDao.logMSPActivity(METER_ADD_STRING, "DeviceName(" + paoName+ ") - Set Meter number (" + meterNumber + ") and Address (" + address + ").", mspVendor.getCompanyName());
        
        // UPDATE BILLING GROUP
        ServiceLocation mspServiceLocation = mspObjectDao.getMspServiceLocation(meterNumber, mspVendor);
        String cycleGroupName = mspServiceLocation.getBillingCycle();
        updateBillingCyle(cycleGroupName, meterNumber, newDevice, METER_ADD_STRING, mspVendor);
        
        // UPDATE SubstationName GROUP
        updateSubstationGroup(substationName, meterNumber, newDevice, METER_ADD_STRING, mspVendor);
        
        // ROUTES
        updateMeterRouteForSubstation(newDevice, mspVendor, substationName, meterNumber);
    }
    
    private void updateMeterRouteForSubstation(YukonDevice meterDevice, MultispeakVendor mspVendor, String substationName, String meterNumber) {
    	
    	try {
    		
	    	// get routes
	        Substation substation = substationDao.getByName(substationName);
	        List<Route> routes = substationToRouteMappingDao.getRoutesBySubstationId(substation.getId());
	        
	        // set route
	        if (routes.size() > 0) {

	            List<String> routeNames = new ArrayList<String>(routes.size());
	            List<Integer> routeIds = new ArrayList<Integer>(routes.size());
                for (Route route : routes) {
                    routeNames.add(route.getName());
                    routeIds.add(route.getId());
                }

	            // initally set route to first sub mapping
	            deviceUpdateService.changeRoute(meterDevice, routeIds.get(0));
	            mspObjectDao.logMSPActivity(METER_ADD_STRING, "MeterNumber(" + meterNumber + ") - Route initially set to " + routeNames.get(0) + ", will run route discovery.", mspVendor.getCompanyName());
	        
	            // run route discovery
	            LiteYukonUser liteYukonUser = UserUtils.getYukonUser(); 
	            deviceUpdateService.routeDiscovery(meterDevice, routeIds, liteYukonUser);
	            
	            mspObjectDao.logMSPActivity(METER_ADD_STRING, "MeterNumber(" + meterNumber + ") - Route discovery started on: " + StringUtils.join(routeNames, ",") + ".", mspVendor.getCompanyName());
	        
	        // no routes for sub
	        } else {
	        	mspObjectDao.logMSPActivity(METER_ADD_STRING, "MeterNumber(" + meterNumber + ") - No Routes for substation (" + substationName + "), using route from template device.", mspVendor.getCompanyName());
	        }
        
	     // bad sub name
	    } catch(NotFoundException e) {
	        mspObjectDao.logMSPActivity(METER_ADD_STRING, "MeterNumber(" + meterNumber + ") - " + e.getMessage() + ", Bad substation name.", mspVendor.getCompanyName());
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
                    if ( extName.equalsIgnoreCase(EXTENSION_DEVICE_TEMPLATE_STRING)) {
                        templateName = eItem.getExtValue();
                        loaded = true;
                    }
                }
            }
        }

        return templateName;
    }

    /**
     * Returns the meter position number (sequence number) from Meter object.
     * If no value is provided in the Meter object, then null is returned. 
     * @param mspMeter
     * @return
     */
    private String getMeterPosition(Meter mspMeter) {
        String positionNumber = null;
        ExtensionsItem[] extensionItems = mspMeter.getExtensionsList();
        if( extensionItems != null) {
            for (ExtensionsItem eItem : extensionItems) {
                String extName = eItem.getExtName();
                if ( extName.equalsIgnoreCase(EXTENSION_POSITION_NUMBER_STRING)) {
                    positionNumber = eItem.getExtValue();
                }
            }
        }

        return positionNumber;
    }
    
    // UPDATE BILLING CYCLE DEVICE GROUP
    public boolean updateBillingCyle(String newBilling, String meterNumber, YukonDevice yukonDevice,
            String logActionStr, MultispeakVendor mspVendor) {

        if (!StringUtils.isBlank(newBilling)) {

            //Remove from all billing membership groups
            DeviceGroup billingCycledeviceGroup = multispeakFuncs.getBillingCycleDeviceGroup();
            StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getStoredGroup(billingCycledeviceGroup);
            return updatePrefixGroup(newBilling, meterNumber, yukonDevice, logActionStr, mspVendor, deviceGroupParent);
        }
        
        return false;
    }
    
    // UPDATE SUBSTATION DEVICE GROUP
    public boolean updateSubstationGroup(String substationName, String meterNumber, YukonDevice yukonDevice,
            String logActionStr, MultispeakVendor mspVendor) {

        if (!StringUtils.isBlank(substationName)) {
            
            //Remove from all substation membership groups
            DeviceGroup substationNameDeviceGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.SUBSTATION_NAME);
            StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getStoredGroup(substationNameDeviceGroup);
            return updatePrefixGroup(substationName, meterNumber, yukonDevice, logActionStr, mspVendor, deviceGroupParent);
        }
        return false;
    }
    
    // UPDATE DEVICE GROUP
    public boolean updateDeviceGroup(String groupName, StoredDeviceGroup groupParent, String meterNumber, YukonDevice yukonDevice, String logActionStr, MultispeakVendor mspVendor) {

        if (!StringUtils.isBlank(groupName)) {
            return updatePrefixGroup(groupName, meterNumber, yukonDevice, logActionStr, mspVendor, groupParent);
        }
        return false;
    }

    /**
     * Removes meter from all immediate descendants of deviceGroupParent.
     * Adds meter to a subgroup of deviceGroupParent called groupName.
     * If groupName does not exist, a new group will be created.
     * @param groupName
     * @param meterNumber
     * @param yukonDevice
     * @param logActionStr
     * @param mspVendor
     * @param deviceGroupParent
     * @return true if added to new prefix group.
     */
    private boolean updatePrefixGroup(String groupName, String meterNumber,
                             YukonDevice yukonDevice, String logActionStr,
                             MultispeakVendor mspVendor,
                             StoredDeviceGroup deviceGroupParent) {
        boolean alreadyInGroup = false;
        
        Set<StoredDeviceGroup> deviceGroups = deviceGroupMemberEditorDao.getGroupMembership(deviceGroupParent, yukonDevice);
        for (StoredDeviceGroup deviceGroup : deviceGroups) {
            if( deviceGroup.getName().equalsIgnoreCase(groupName) ) {
                CTILogger.debug("MeterNumber(" + meterNumber + ") - Already in group:  " + groupName);
                alreadyInGroup = true;
            } else {
                deviceGroupMemberEditorDao.removeDevices(deviceGroup, yukonDevice);
                mspObjectDao.logMSPActivity(logActionStr,
                               "MeterNumber(" + meterNumber + ") - Removed from Group: " + deviceGroup.getFullName() + ".",
                               mspVendor.getCompanyName());
            }
        }

        if (!alreadyInGroup) {
            StoredDeviceGroup deviceGroup = deviceGroupEditorDao.getGroupByName(deviceGroupParent, groupName, true);
            deviceGroupMemberEditorDao.addDevices(deviceGroup, yukonDevice);
            mspObjectDao.logMSPActivity(logActionStr,
                           "MeterNumber(" + meterNumber+ ") - Added to Group: " + deviceGroup.getFullName() + ".",
                           mspVendor.getCompanyName());
        }
        return !alreadyInGroup;
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
    private String getPaoNameFromMspMeter(Meter mspMeter, int paoNameAlias, String defaultValue, MultispeakVendor mspVendor ) {
			
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
            case MultispeakVendor.SERVICE_LOCATION_WITH_POSITION_NO_PAONAME:
			{
				if( mspMeter.getUtilityInfo() == null ||
						StringUtils.isBlank(mspMeter.getUtilityInfo().getServLoc()))
                   return null;
				
				String serviceLocation = mspMeter.getUtilityInfo().getServLoc();
				String positionNumber = getMeterPosition(mspMeter);
				return multispeakFuncs.buildServiceLocationWithPosition(serviceLocation, positionNumber);
			}
            case MultispeakVendor.POLE_NUMBER_PAONAME:
            {
                if (StringUtils.isBlank(mspMeter.getMeterNo())) {
                    return null;
                }
                ServiceLocation mspServiceLocation = mspObjectDao.getMspServiceLocation(mspMeter.getMeterNo(), mspVendor);
                if( mspServiceLocation.getNetwork()== null || StringUtils.isBlank(mspServiceLocation.getNetwork().getPoleNo())){
                    return null;
                }
                return mspServiceLocation.getNetwork().getPoleNo();                
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
        	String substationName = getMeterSubstationName(mspMeter);
        	err = isValidSubstation(substationName, mspMeter.getMeterNo().trim(), mspVendor);
    		if (err == null) {
    			createMeter(mspMeter, mspVendor, templateName, substationName);
            } else {
            	errorObjects.add(err);
            }
        }
        return errorObjects;
	}
	
	/**
	 * Return the substation name of a Meter.
	 * If the Meter does not contain a substation name in its utility info, empty string is returned.
	 * an empty string is returned. 
	 * @param mspMeter
	 * @return
	 */
	private String getMeterSubstationName(Meter mspMeter) {
		
		String substationName = "";
		if(!(mspMeter.getUtilityInfo() == null || mspMeter.getUtilityInfo().getSubstationName()== null)){
			substationName = mspMeter.getUtilityInfo().getSubstationName();
		}
		
		return substationName;
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
                                                          substationName + ") - " + errorReason + ".  Meter was NOT added or updated",
                                                          "Meter", METER_ADD_STRING, mspVendor.getCompanyName());
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
                                                          "Meter", METER_ADD_STRING, mspVendor.getCompanyName());
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
                                                             "Meter", METER_ADD_STRING, mspVendor.getCompanyName());
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
        
        // Verify substation name
        String substationName = getMeterSubstationName(mspMeter);
        if (StringUtils.isBlank(substationName)) {
        	mspObjectDao.logMSPActivity(METER_ADD_STRING, "MeterNumber(" + meter.getMeterNumber() + ") - substation name not provided, route locate will not happen.", mspVendor.getCompanyName());
        } else {
        	err = isValidSubstation(substationName, meter.getMeterNumber(), mspVendor);
        	if (err != null) {
        		mspObjectDao.logMSPActivity(METER_ADD_STRING, "MeterNumber(" + meter.getMeterNumber() + ") - substation name is invalid.", mspVendor.getCompanyName());
        		return err;
        	}
        }

        final String paoAliasStr = MultispeakVendor.paoNameAliasStrings[paoAlias];
        //Load the CIS serviceLocation.
        ServiceLocation mspServiceLocation = mspObjectDao.getMspServiceLocation(meter.getMeterNumber(), mspVendor);
        String billingCycle = mspServiceLocation.getBillingCycle();
        
        //Enable Meter and update applicable fields.
        removeFromGroup(meter, SystemGroupEnum.INVENTORY, METER_ADD_STRING, mspVendor);
    
        //update the billing group from CIS billingCyle
        updateBillingCyle(billingCycle, meter.getMeterNumber(), meter, METER_ADD_STRING, mspVendor);
        
        LiteYukonPAObject liteYukonPaobject = paoDao.getLiteYukonPAO(meter.getDeviceId());
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
        String logString = "";

        //Update PaoName
        String currentPaoName = meter.getName();
        String newPaoName = getPaoNameFromMspMeter(mspMeter, paoAlias, currentPaoName, mspVendor);
        
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
        mspObjectDao.logMSPActivity(METER_ADD_STRING,
                       "MeterNo(" + meter.getMeterNumber()+ ");Enabled;" + logString,
                       mspVendor.getCompanyName());

        changeDeviceType(mspMeter, meter, mspVendor, METER_ADD_STRING);
        
        if (!StringUtils.isBlank(substationName)) {
            //update the substation group
            updateSubstationGroup(substationName, meter.getMeterNumber(), meter, METER_ADD_STRING, mspVendor);
            //Update route (_after_ meter is enabled).
            updateMeterRouteForSubstation(meter, mspVendor, substationName, meter.getMeterNumber());
        }
        //TODO Read the Meter.
        return null;
    }

    /** Helper method to update meter based on the mspMeter.
     * PaoName and MeterNumber will be updated if changed.
     * Substation group information will be updated.
     * @param mspMeter
     * @param meter
     * @param mspVendor
     * @param paoAlias
     */
    private void changeMeter(Meter mspMeter, 
            com.cannontech.amr.meter.model.Meter meter, 
            MultispeakVendor mspVendor,
            int paoAlias, String logActionStr) {
        
        final String paoAliasStr = MultispeakVendor.paoNameAliasStrings[paoAlias];
        boolean dbChange = false;
        LiteYukonPAObject liteYukonPaobject = paoDao.getLiteYukonPAO(meter.getDeviceId());
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
            
        String paoNameLog = "";
        String meterNumberLog = "";
        String currentPaoName = meter.getName();
        String newPaoName = getPaoNameFromMspMeter(mspMeter, paoAlias, currentPaoName, mspVendor);
        
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

        //Update the meter
        if(dbChange) {
            dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
        }
        
        //update the substation group
        String substationName = getMeterSubstationName(mspMeter);
        boolean addedToGroup = updateSubstationGroup(substationName, meter.getMeterNumber(), meter, logActionStr, mspVendor);
        if (addedToGroup) {
            //If the substation changed, we should attempt to update the route info too.
            updateMeterRouteForSubstation(meter, mspVendor, substationName, meter.getMeterNumber());
        }

        mspObjectDao.logMSPActivity(logActionStr,
                       paoNameLog,
                       mspVendor.getCompanyName());
        mspObjectDao.logMSPActivity(logActionStr,
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
                PaoDefinition paoDefinition = paoDefinitionDao.getPaoDefinition(PaoType.getForId(templateMeter.getType()));
                deviceUpdateService.changeDeviceType(meter, paoDefinition);
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
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
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
    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}
    @Autowired
    public void setMeterSearchDao(MeterSearchDao meterSearchDao) {
        this.meterSearchDao = meterSearchDao;
    }
}
