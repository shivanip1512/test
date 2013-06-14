package com.cannontech.multispeak.service.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.dao.WaitableDeviceAttributeReadCallback;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.meter.search.dao.MeterSearchDao;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchField;
import com.cannontech.amr.meter.search.model.MeterSearchOrderBy;
import com.cannontech.amr.meter.search.model.StandardFilterBy;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectCallback;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.config.MasterConfigHelper;
import com.cannontech.common.config.MasterConfigStringKeysEnum;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.impl.CommandCallbackBase;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.exception.InsufficientMultiSpeakDataException;
import com.cannontech.common.model.Route;
import com.cannontech.common.model.Substation;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.roleproperties.MspPaoNameAliasEnum;
import com.cannontech.core.roleproperties.MultispeakMeterLookupFieldEnum;
import com.cannontech.core.substation.dao.SubstationDao;
import com.cannontech.core.substation.dao.SubstationToRouteMappingDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.porter.RequestMessage;
import com.cannontech.messaging.message.porter.ReturnMessage;
import com.cannontech.messaging.util.MessageEvent;
import com.cannontech.messaging.util.MessageListener;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.FormattedBlockProcessingService;
import com.cannontech.multispeak.dao.FormattedBlockUpdater;
import com.cannontech.multispeak.dao.FormattedBlockUpdaterChain;
import com.cannontech.multispeak.dao.MeterReadProcessingService;
import com.cannontech.multispeak.dao.MeterReadUpdater;
import com.cannontech.multispeak.dao.MeterReadUpdaterChain;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.data.MspLoadActionCode;
import com.cannontech.multispeak.deploy.service.CB_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.CB_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.ExtensionsItem;
import com.cannontech.multispeak.deploy.service.FormattedBlock;
import com.cannontech.multispeak.deploy.service.LoadActionCode;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.MeterGroup;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.multispeak.deploy.service.OutageEventType;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.multispeak.deploy.service.impl.MultispeakPortFactory;
import com.cannontech.multispeak.event.CDEvent;
import com.cannontech.multispeak.event.MeterReadEvent;
import com.cannontech.multispeak.event.MultispeakEvent;
import com.cannontech.multispeak.event.ODEvent;
import com.cannontech.multispeak.service.MultispeakMeterService;
import com.cannontech.user.SystemUserContext;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.BasicServerConnection;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.SetMultimap;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MultispeakMeterServiceImpl implements MultispeakMeterService, MessageListener {
	
    private static final Logger log = YukonLogManager.getLogger(MultispeakMeterServiceImpl.class);
    
    private BasicServerConnection porterConnection;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MspMeterDao mspMeterDao;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private MeterDao meterDao;
    @Autowired private TransactionTemplate transactionTemplate;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private DeviceCreationService deviceCreationService;
    @Autowired private DeviceUpdateService deviceUpdateService;
    @Autowired private SubstationDao substationDao;
    @Autowired private SubstationToRouteMappingDao substationToRouteMappingDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private MeterSearchDao meterSearchDao;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private AttributeService attributeService;
    @Autowired private PointDao pointDao;
    @Autowired private MeterReadProcessingService meterReadProcessingService;
    @Autowired private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
    @Autowired private RfnMeterDisconnectService rfnMeterDisconnectService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private DeviceGroupService deviceGroupService;
    
	/** Singleton incrementor for messageIDs to send to porter connection */
	private static long messageID = 1;

	/** A map of Long(userMessageID) to MultispeakEvent values */
	private static Map<Long,MultispeakEvent> eventsMap = Collections.synchronizedMap(new HashMap<Long,MultispeakEvent>());

    private ImmutableSet<OutageEventType> supportedEventTypes;
    private ImmutableSetMultimap<OutageEventType, Integer> outageConfig;
	
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
    @PostConstruct
    public void initialize() throws Exception {
        log.info("New MSP instance created");
        porterConnection.addMessageListener(this);

        ConfigurationSource configurationSource = MasterConfigHelper.getConfiguration();
        Builder<OutageEventType, Integer> builder = ImmutableMultimap.builder();
        builder.putAll(OutageEventType.Outage, 20, 57, 72);
        builder.putAll(OutageEventType.Restoration, 1, 17, 74, 0);
        ImmutableMultimap<OutageEventType, Integer> systemDefault = builder.build();

        supportedEventTypes = ImmutableSet.of(OutageEventType.Outage,
                                              OutageEventType.NoResponse,
                                              OutageEventType.Restoration,
                                              OutageEventType.PowerOff,
                                              OutageEventType.PowerOn,
                                              OutageEventType.Instantaneous,
                                              OutageEventType.Inferred);

        SetMultimap<OutageEventType, Integer> outageConfigTemp = HashMultimap.create(systemDefault);
        for (OutageEventType eventType : supportedEventTypes) {
            String valueStr = configurationSource.getString("MSP_OUTAGE_EVENT_TYPE_CONFIG_" + eventType.getValue().toUpperCase());
            if (valueStr != null) {
                int[] errorCodes = com.cannontech.common.util.StringUtils.parseIntStringAfterRemovingWhitespace(valueStr);
                List<Integer> errorCodeList = Arrays.asList(ArrayUtils.toObject(errorCodes));
                outageConfigTemp.values().removeAll(errorCodeList);
                outageConfigTemp.putAll(eventType, errorCodeList);
            }
        }

        outageConfig = ImmutableSetMultimap.copyOf(outageConfigTemp);
        log.info("outage event configuation: " + outageConfig);
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
		BaseMessage in = e.getMessage();		
		if(in instanceof ReturnMessage)
		{
			final ReturnMessage returnMsg = (ReturnMessage) in;
			final MultispeakEvent event = getEventsMap().get(new Long (returnMsg.getUserMessageId()) );
			
			if(event != null) {
			    
				final Logger eLogger = YukonLogManager.getLogger(MultispeakMeterServiceImpl.class); 
    			Runnable eventRunner = new Runnable() {
    			
    			    @Override
    			    public void run() {
                        
                        eLogger.info("Message Received [ID:"+ returnMsg.getUserMessageId() + 
                                        " DevID:" + returnMsg.getDeviceId() + 
                                        " Command:" + returnMsg.getCommandString() +
                                        " Result:" + returnMsg.getResultString() + 
                                        " Status:" + returnMsg.getStatus() +
                                        " More:" + returnMsg.getExpectMore()+"]");
        
                        if(returnMsg.getExpectMore() == false) {
                            
        					eLogger.info("Received Message From ID:" + returnMsg.getDeviceId() + " - " + returnMsg.getResultString());
        
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
    public LoadActionCode CDMeterState(final MultispeakVendor mspVendor, final YukonMeter meter) throws RemoteException {
        
        log.info("Received " + meter.getMeterNumber() + " for CDMeterState from " + mspVendor.getCompanyName());
        if ( ! porterConnection.isValid() ) {
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }
        
        List<YukonMeter> allPaosToRead = Collections.singletonList(meter);
        final EnumSet<BuiltInAttribute> attributes = EnumSet.of(BuiltInAttribute.DISCONNECT_STATUS);
                

        LACWaitableDeviceAttributeReadCallback waitableCallback = new LACWaitableDeviceAttributeReadCallback(mspVendor.getRequestMessageTimeout()) {

            @Override
            public void complete() {
                super.complete();
                // do we need to do anything here once we received all of the data?
                log.debug("deviceAttributeReadCallback.complete for cdEvent");
            }

            @Override
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                // the following is expensive but unavoidable until PointData is changed
                PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(value.getId());
                BuiltInAttribute thisAttribute = attributeService.findAttributeForPoint(paoPointIdentifier.getPaoTypePointIdentifier(), attributes);
                if (thisAttribute == null) return;
                if (thisAttribute == BuiltInAttribute.DISCONNECT_STATUS) {
                    setLoadActionCode(multispeakFuncs.getLoadActionCode(meter, value));
                }
            }
            
            @Override
            public void receivedLastValue(PaoIdentifier pao) {
                log.debug("deviceAttributeReadCallback.receivedLastValue for cdEvent");
            }

            @Override
            public void receivedError(PaoIdentifier pao, DeviceAttributeReadError error) {
                // do we need to send something to the foreign system here?
                log.warn("received error for " + pao + ": " + error);
            }

            @Override
            public void receivedException(DeviceAttributeReadError error) {
                log.warn("received exception in meterReadEvent callback: " + error);
            }
            
        };
        deviceAttributeReadService.initiateRead(allPaosToRead, attributes, waitableCallback, DeviceRequestType.MULTISPEAK_METER_READ_EVENT, UserUtils.getYukonUser());
        try {
            waitableCallback.waitForCompletion();
        } catch (InterruptedException e) { /* Ignore */ }
        return waitableCallback.getLoadActionCode();
    }
    
    /**
     * Wrapper class for holding attribute read response value.
     */
    private abstract class LACWaitableDeviceAttributeReadCallback extends WaitableDeviceAttributeReadCallback {
        private LoadActionCode loadActionCode = LoadActionCode.Unknown;

        public LACWaitableDeviceAttributeReadCallback(long timeoutInMillis) {
            super(timeoutInMillis);
        }
        public void setLoadActionCode(LoadActionCode loadActionCode) {
            this.loadActionCode = loadActionCode;
        }
        public LoadActionCode getLoadActionCode() {
            return loadActionCode;
        }
    };
    
    /**
     * This method still does not support attributes.
     * The issue is that SEDC does not support the ability to receive ReadingChangedNotification messages.
     * Therefore, just for them, we initiate a real time read, wait, and return.
     * This DOES need to be changed in future versions. 
     */
    @Override
    public MeterRead getLatestReadingInterrogate(MultispeakVendor mspVendor, 
            YukonMeter meter,
            String transactionID, String responseUrl)
    {
    	long id = generateMessageID();      
        MeterReadEvent event = new MeterReadEvent(mspVendor, id, meter, transactionID, responseUrl);
        
        getEventsMap().put(new Long(id), event);
        String commandStr = "getvalue kwh";
        if( DeviceTypesFuncs.isMCT4XX(meter.getPaoIdentifier().getPaoType().getDeviceTypeId()) )
            commandStr = "getvalue peak";    // getvalue peak returns the peak kW and the total kWh
        
        final String meterNumber = meter.getMeterNumber();
        log.info("Received " + meterNumber + " for LatestReadingInterrogate from " + mspVendor.getCompanyName());
        writePilRequest(meter, commandStr, id, 13);
        mspObjectDao.logMSPActivity("getLatestReadingByMeterNo",
						"(ID:" + meter.getPaoIdentifier().getPaoId() + ") MeterNumber (" + meterNumber + ") - " + commandStr,
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
    public ImmutableSet<OutageEventType> getSupportedEventTypes() {
        return supportedEventTypes;
    }

    @Override
    public ImmutableSetMultimap<OutageEventType, Integer> getOutageConfig() {
        return outageConfig;
    }

    @Override
	public synchronized ErrorObject[] odEvent(MultispeakVendor vendor, 
	        String[] meterNumbers,
	        String transactionID, String responseUrl) throws RemoteException
	{
        if( vendor.getUrl() == null || vendor.getUrl().equalsIgnoreCase(CtiUtilities.STRING_NONE)) {
            throw new RemoteException("OMS vendor unknown.  Please contact Yukon administrator to set the Multispeak Vendor Role Property value in Yukon.");
        }        
        if (! porterConnection.isValid()) {
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
		log.info("Received " + meterNumbers.length + " Meter(s) for Outage Verification Testing from " + vendor.getCompanyName());
		
        for (String meterNumber : meterNumbers) {
            
			try {
				YukonMeter meter = meterDao.getYukonMeterForMeterNumber(meterNumber);

                // Validate meter can receive porter command requests and command string exists, then perform action
				// This is limiting us to PLC support only for this request.
				boolean canInitiatePorterRequest = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS);
				if (!canInitiatePorterRequest) {
				    ErrorObject err = mspObjectDao.getErrorObject(meterNumber, 
                                                                  "MeterNumber (" + meterNumber + ") - Meter cannot receive requests from porter.",
                                                                  "Meter",
                                                                  "ODEvent", vendor.getCompanyName());
                    errorObjects.add(err);   
                } else { // do archaic way
    				long id = generateMessageID();		
    				ODEvent event = new ODEvent(vendor, id, transactionID, responseUrl);
    				getEventsMap().put(new Long(id), event);
    				writePilRequest(meter, "ping", id, 13);
                }

			} catch (NotFoundException e) {
			    ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "ODEvent", vendor.getCompanyName());
			    errorObjects.add(err);              
			}
		}
    		
		return mspObjectDao.toErrorObject(errorObjects);
	}

	
    @Override
    public synchronized ErrorObject[] meterReadEvent(final MultispeakVendor vendor, 
            String[] meterNumbers,
            final String transactionID, final String responseUrl) {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        log.info("Received " + meterNumbers.length + " Meter(s) for MeterReading from " + vendor.getCompanyName());
        
        final CB_ServerSoap_PortType port = MultispeakPortFactory.getCB_ServerPort(vendor, responseUrl);
        if (port == null) {
            log.error("Port not found for CB_MR (" + vendor.getCompanyName() + ")");
            return new ErrorObject[0];	//return without any processing, since we have no one to send to when we're done anyways.
        }
        
        List<YukonMeter> allPaosToRead = Lists.newArrayListWithCapacity(meterNumbers.length);
        for (String meterNumber : meterNumbers) {
        	
            try {
                YukonMeter meter = meterDao.getYukonMeterForMeterNumber(meterNumber); //TODO probably need a better load method
                allPaosToRead.add(meter);
 	        } catch (NotFoundException e) {
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "MeterReadEvent", vendor.getCompanyName());
            	errorObjects.add(err);            
            }
        }
        
        final ImmutableMap<PaoIdentifier, YukonMeter> meterLookup =
            PaoUtils.indexYukonPaos(allPaosToRead);
        
        final EnumSet<BuiltInAttribute> attributes = EnumSet.of(BuiltInAttribute.USAGE, BuiltInAttribute.PEAK_DEMAND);
        
        final ConcurrentMap<PaoIdentifier, MeterReadUpdater> updaterMap = 
            new MapMaker().concurrencyLevel(2).initialCapacity(meterNumbers.length).makeMap();
        
        DeviceAttributeReadCallback callback = new DeviceAttributeReadCallback() {

            @Override
            public void complete() {
                // do we need to do anything here once we received all of the data?
                log.debug("deviceAttributeReadCallback.complete for meterReadEvent");
            }

            @Override
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                // the following is expensive but unavoidable until PointData is changed
                PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(value.getId());
                BuiltInAttribute thisAttribute = attributeService.findAttributeForPoint(paoPointIdentifier.getPaoTypePointIdentifier(), attributes);
                if (thisAttribute == null) return;
                
                // Get a new updater object for the current value
                MeterReadUpdater meterReadUpdater = meterReadProcessingService.buildMeterReadUpdater(thisAttribute, value);
                // if the map is empty, place the updater into it
                MeterReadUpdater oldValue = updaterMap.putIfAbsent(pao, meterReadUpdater);
                while (oldValue != null) {
                    // looks like the map was not empty, combine the existing updater with the
                    // new one and then place it back in the map, but we must be careful
                    // that someone hasn't changed the map out from under us (thus the while loop)
                    MeterReadUpdaterChain chain = new MeterReadUpdaterChain(oldValue, meterReadUpdater);
                    boolean success = updaterMap.replace(pao, oldValue, chain);
                    if (success) break;
                    oldValue = updaterMap.putIfAbsent(pao, meterReadUpdater);
                }
            }
            
            /**
             * The unfortunate part is that this method is going to fire off a readingChangeNotification for each 
             *  set of attributes that happened to be able to be collected using the same command 
             *  (as derived by MeterReadCommandGenerationService.getMinimalCommandSet(...))
             */
            @Override
            public void receivedLastValue(PaoIdentifier pao) {
                YukonMeter meter = meterLookup.get(pao);
                MeterRead meterRead = meterReadProcessingService.createMeterRead(meter);
                
                // because we were so careful about putting updater or updater chains into the
                // map, we know we can safely remove it and generate a MeterRead from it
                // whenever we want; but this happens to be a perfect time
                MeterReadUpdater updater = updaterMap.remove(pao);
                updater.update(meterRead);
                
                MeterRead[] meterReads = new MeterRead[] {meterRead};
                try {
                    log.info("Sending ReadingChangedNotification ("+ responseUrl + "): Meter Number " + meterRead.getObjectID());
                    ErrorObject[] errObjects = port.readingChangedNotification(meterReads, transactionID);
                    if (!ArrayUtils.isEmpty(errObjects)) {
                        multispeakFuncs.logErrorObjects(responseUrl, "ReadingChangedNotification", errObjects);
                    }
                } catch (RemoteException e) {
                    log.warn("caught exception in receivedValue of meterReadEvent", e);
                }
            }

            @Override
            public void receivedError(PaoIdentifier pao, DeviceAttributeReadError error) {
                // do we need to send something to the foreign system here?
                log.warn("received error for " + pao + ": " + error);
            }

            @Override
            public void receivedException(DeviceAttributeReadError error) {
                log.warn("received exception in meterReadEvent callback: " + error);
            }
        };
        deviceAttributeReadService.initiateRead(allPaosToRead, attributes, callback, DeviceRequestType.MULTISPEAK_METER_READ_EVENT, UserUtils.getYukonUser());
        
        return mspObjectDao.toErrorObject(errorObjects);
    }
    
    @Override
    public synchronized ErrorObject[] blockMeterReadEvent(final MultispeakVendor vendor, 
            String meterNumber, final FormattedBlockProcessingService<Block> blockProcessingService,
            final String transactionId, final String responseUrl) {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        log.info("Received " + meterNumber+ " for BlockMeterReading from " + vendor.getCompanyName());
        
        final CB_ServerSoap_PortType port = MultispeakPortFactory.getCB_ServerPort(vendor, responseUrl);
        if (port == null) {
            log.error("Port not found for CB_Server (" + vendor.getCompanyName() + ")");
            return new ErrorObject[0];	//return without any processing, since we have no one to send to when we're done anyways. 
        }
        
        final YukonMeter paoToRead;
        try {
            paoToRead = meterDao.getYukonMeterForMeterNumber(meterNumber);
        } catch (NotFoundException e) {
            ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "MeterReadEvent", vendor.getCompanyName());
            errorObjects.add(err);
            return mspObjectDao.toErrorObject(errorObjects);
        }
        
        final EnumSet<BuiltInAttribute> attributes = blockProcessingService.getAttributeSet();

        //retain only those that ARE readable.
        //profile attributes are not readable right now, which means that the LoadBlock specifically, will not get many new values.
        //this is a change from before where we built up a command to retrieve at least the last 6 profile reads. Oh well...
        //a solution could be implemented a new command for collected the "latest" profile reads available. 
        attributes.retainAll(attributeService.getReadableAttributes());
        
        final ConcurrentMap<PaoIdentifier, FormattedBlockUpdater<Block>> updaterMap = 
            new MapMaker().concurrencyLevel(2).initialCapacity(1).makeMap();
        
        DeviceAttributeReadCallback callback = new DeviceAttributeReadCallback() {

            /**
             * Because we only have one meterNumber that is being processed, we will wait until all reads are
             * returned to fire off the formattedBlockNotification. 
             * This allows us to group them all together into one Block, which is desired.
             * This is different than the meterReadEvent which may have multiple meterNumbers it is processing and 
             * therefore fires notifications for each read that comes in (basically using this same layout of code, only
             * implemented in receivedLastValue instead)
             */
            @Override
            public void complete() {

                Block block = blockProcessingService.createBlock(paoToRead);
                
                // because we were so careful about putting updater or updater chains into the
                // map, we know we can safely remove it and generate a MeterRead from it
                // whenever we want; but this happens to be a perfect time
                FormattedBlockUpdater<Block> updater = updaterMap.remove(paoToRead.getPaoIdentifier());
                if (updater != null) {
                    updater.update(block);
                } else {
                    log.warn("no data updates for meter. notification will contain no readings");
                }

                FormattedBlock formattedBlock = blockProcessingService.createMspFormattedBlock(block);
                
                try {
                    ErrorObject[] errObjects = port.formattedBlockNotification(formattedBlock, transactionId, "errorString?");
                    if (!ArrayUtils.isEmpty(errObjects)) {
                        multispeakFuncs.logErrorObjects(responseUrl, "FormattedBlockNotification", errObjects);
                    }
                } catch (RemoteException e) {
                    log.warn("caught exception in receivedValue of formattedBlockEvent", e);
                }
            }

            @Override
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                // the following is expensive but unavoidable until PointData is changed
                PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(value.getId());
                BuiltInAttribute thisAttribute = attributeService.findAttributeForPoint(paoPointIdentifier.getPaoTypePointIdentifier(), attributes);
                if (thisAttribute == null) return;
                
                // Get a new updater object for the current value
                FormattedBlockUpdater<Block> formattedBlockUpdater = blockProcessingService.buildFormattedBlockUpdater(thisAttribute, value);

                // if the map is empty, place the updater into it
                FormattedBlockUpdater<Block> oldValue = updaterMap.putIfAbsent(pao, formattedBlockUpdater);

                while (oldValue != null) {
                    // looks like the map was not empty, combine the existing updater with the
                    // new one and then place it back in the map, but we must be careful
                    // that someone hasn't changed the map out from under us (thus the while loop)
                    FormattedBlockUpdaterChain<Block> chain = new FormattedBlockUpdaterChain<Block>(oldValue, formattedBlockUpdater);
                    boolean success = updaterMap.replace(pao, oldValue, chain);
                    if (success) break;
                    oldValue = updaterMap.putIfAbsent(pao, formattedBlockUpdater);
                }
            }
            
            @Override
            public void receivedLastValue(PaoIdentifier pao) {
                log.debug("deviceAttributeReadCallback.receivedLastValue for formattedBlockEvent");
            }

            @Override
            public void receivedError(PaoIdentifier pao, DeviceAttributeReadError error) {
                // do we need to send something to the foreign system here?
                log.warn("received error for " + pao + ": " + error);
            }

            @Override
            public void receivedException(DeviceAttributeReadError error) {
                log.warn("received exception in FormattedBlockEvent callback: " + error);
            }
        };
        deviceAttributeReadService.initiateRead(Collections.singleton(paoToRead), attributes, callback, DeviceRequestType.MULTISPEAK_FORMATTED_BLOCK_READ_EVENT, UserUtils.getYukonUser());
        
        return mspObjectDao.toErrorObject(errorObjects);
    }
    
    /**
     * This method is still in the works and being tested. SLN 20120307
     * It will eventually replace cdEvent.
     * @param vendor
     * @param cdEvents
     * @param transactionId
     * @return
     * @throws RemoteException
     */
    public synchronized ErrorObject[] cdEvent_DO_NOT_USE_YET(final MultispeakVendor vendor, 
            ConnectDisconnectEvent [] cdEvents,
            final String transactionId, final String responseUrl) throws RemoteException
    {
        
        if ( ! porterConnection.isValid() )
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        log.info("Received " + cdEvents.length + " Meter(s) for Connect/Disconnect from " + vendor.getCompanyName());
        List<CommandRequestDevice> plcCommandRequests = Lists.newArrayList();
        
        for (ConnectDisconnectEvent cdEvent : cdEvents) {
            final String meterNumber = getMeterNumberFromCDEvent(cdEvent);
            
            try {
                YukonMeter meter = meterDao.getYukonMeterForMeterNumber(meterNumber);
                MspLoadActionCode mspLoadActionCode = MspLoadActionCode.getForLoadActionCode(cdEvent.getLoadActionCode());

                // validate is CD supported meter
                if (!mspMeterDao.isCDSupportedMeter(meterNumber)) {
                    ErrorObject err = mspObjectDao.getErrorObject(meterNumber, 
                                                                  "MeterNumber (" + meterNumber + ") - Invalid Yukon Connect/Disconnect Meter.",
                                                                  "Meter",
                                                                  "CDEvent", vendor.getCompanyName());
                    errorObjects.add(err);
                    continue;
                }

                // check for rf disconnect meter type and perform action
                boolean isRfnDisconnect = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.DISCONNECT_RFN);
                if (isRfnDisconnect) {
                    RfnMeter rfnMeter = rfnDeviceDao.getMeter(meter);
                    rfnMeter.setMeterNumber(meterNumber);   //total hack until RFNMeter properly loads meterNumber values
                    doRfnConnectDisconnect(rfnMeter, mspLoadActionCode.getRfnState().getType(), vendor, transactionId, responseUrl);
                    continue;
                }
                
                // Assume plc if we made it this far, validate meter can receive porter command requests and command string exists, then perform action
                boolean canInitiatePorterRequest = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS);
                if (!canInitiatePorterRequest || StringUtils.isBlank(mspLoadActionCode.getPlcCommandString())) {
                    ErrorObject err = mspObjectDao.getErrorObject(meterNumber, 
                                                                  "MeterNumber (" + meterNumber + ") - Meter cannot receive requests from porter or no control command exists. " + 
                                                                          "LoadActionCode="+ cdEvent.getLoadActionCode(),
                                                                  "Meter",
                                                                  "CDEvent", vendor.getCompanyName());
                    errorObjects.add(err);
                } else {    // build up a list of plc command requests (to be sent later)
                    CommandRequestDevice request = new CommandRequestDevice();
                    request.setDevice(new SimpleDevice(meter));
                    request.setCommandCallback(new CommandCallbackBase(mspLoadActionCode.getPlcCommandString()));
                    plcCommandRequests.add(request);
                    mspObjectDao.logMSPActivity("initiateConnectDisconnect",
                                              "(ID:" + meter.getPaoIdentifier().getPaoId() + ") MeterNumber (" + meterNumber + ") - " + mspLoadActionCode.getPlcCommandString() + 
                                              ((cdEvent.getReasonCode() != null) ? " sent for ReasonCode: " + cdEvent.getReasonCode().getValue() : ""),
                                              vendor.getCompanyName());
                }
            } 
            catch (NotFoundException e) {
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "CDEvent", vendor.getCompanyName());
                errorObjects.add(err);
            }
            
        }
        
        // perform plc action on list of commandRequests
        doPlcConnectDisconnect(plcCommandRequests, vendor, transactionId, responseUrl);

        return mspObjectDao.toErrorObject(errorObjects);
    }
    
    /**
     * Performs the PLC meter disconnect. 
     * Returns immediately, does not wait for a response.
     * Callback will initiate a cdEventNotification on receivedLastResultString (should possibly change with YUK-10746).
     * NOTE: The callback will always send a cdEventNotification.UNKNOWN status until YUK-10746 is completed. 
     * @param meter
     * @param action
     * @param vendor
     * @param transactionId
     */
    private void doPlcConnectDisconnect(List<CommandRequestDevice> plcCommandRequests, final MultispeakVendor vendor, 
            final String transactionId, final String responseUrl) {
        YukonUserContext yukonUserContext = new SystemUserContext();
        
        CommandCompletionCallbackAdapter<CommandRequestDevice> callback = new CommandCompletionCallbackAdapter<CommandRequestDevice>() {
            
            @Override
            public void receivedIntermediateError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                log.warn("receivedIntermediateError for cdEvent " + error.getDescription());
            }
            
            @Override
            public void receivedIntermediateResultString(CommandRequestDevice command, String value) {
                log.debug("receivedIntermediateResultString for cdEvent " + value);
            }
            
            @Override
            public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
                log.debug("receivedValue for cdEvent" + value);
            }
            
            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                log.debug("receivedLastResultString for cdEvent "  + value);
                // THIS IS NOT CORRECT!!!! Figure out a better way to handle in the future.
                YukonMeter yukonMeter = meterDao.getYukonMeterForId(command.getDevice().getDeviceId());
                sendCDEventNotification(yukonMeter, LoadActionCode.Unknown, vendor, transactionId, responseUrl);
            }
            
            @Override
            public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                log.warn("receivedLastError for cdEvent " + error.getDescription());
            }
            
            @Override
            public void complete() {
                log.debug("complete for cdEvent");
            }
            
            @Override
            public void processingExceptionOccured(String reason) {
                log.warn("processingExceptionOccured for cdEvent " + reason);   
            }
        };
        
        commandRequestDeviceExecutor.execute(plcCommandRequests, callback, DeviceRequestType.CONTROL_CONNECT_DISCONNECT_COMAMND, yukonUserContext.getYukonUser());
    }
    
    /**
     * Performs the RFN meter disconnect. 
     * Returns immediately, does not wait for a response.
     * Callback will initiate a cdEventNotification on success or error.
     * @param meter
     * @param action
     * @param vendor
     * @param transactionId
     */
    private void doRfnConnectDisconnect(final RfnMeter meter, RfnMeterDisconnectStatusType action, final MultispeakVendor vendor,
            final String transactionId, final String responseUrl) {

        RfnMeterDisconnectCallback rfnCallback = new RfnMeterDisconnectCallback() {
            
            @Override
            public void receivedSuccess(RfnMeterDisconnectState state) {
                log.debug("rfn receivedSuccess for cdEvent " + state);
                MspLoadActionCode mspLoadActionCode = MspLoadActionCode.getForRfnState(RfnDisconnectStatusState.getForNmState(state));
                sendCDEventNotification(meter, mspLoadActionCode.getLoadActionCode(), vendor, transactionId, responseUrl);
            }
            
            @Override
            public void receivedError(MessageSourceResolvable message) {
                log.warn("rfn receivedError for cdEvent " + message);
                sendCDEventNotification(meter, LoadActionCode.Unknown, vendor, transactionId, responseUrl);
            }
            
            @Override
            public void processingExceptionOccured(MessageSourceResolvable message) {
                log.warn("rfn processingExceptionOccured for cdEvent " + message);
            }
            
            @Override
            public void complete() {
                log.debug("rfn complete for cdEvent");
            }
        };
        
        rfnMeterDisconnectService.send(meter, action, rfnCallback);
    }
    
    /**
     * Returns meterNumber from ConnectDisconnectEvent object.
     * Tries to load from CDEvent's meterNo, then meterId (SEDC specific), then objectId.
     * @param cdEvent
     * @return
     */
    private String getMeterNumberFromCDEvent(ConnectDisconnectEvent cdEvent) {
        String meterNumber = cdEvent.getMeterNo();
        
        //Try to load MeterNumber from another element
        if( StringUtils.isBlank(meterNumber)) {
            meterNumber = cdEvent.getMeterID(); //SEDC
            if( StringUtils.isBlank(meterNumber)) { // this is only necessary for old integrations; moving forward, objectId will be a unique identifier of the cdevent. 
                meterNumber = cdEvent.getObjectID();
            }
        }
        if (meterNumber != null) {
            meterNumber = meterNumber.trim();
        }
        return meterNumber;
    }
    
    /**
     * Initates a CB_CD.CDStateChangedNotifaction message to vendor.  
     * @param yukonMeter - meter
     * @param loadActionCode - loadActionCode
     * @param vendor - msp vendor that made the inital request
     * @param transactionId - the token provided from the initial request
     */
    private void sendCDEventNotification(YukonMeter yukonMeter, LoadActionCode loadActionCode, MultispeakVendor vendor, 
            String transactionId, String responseUrl) {

        log.info("Sending CDStateChangedNotification ("+ responseUrl + "): Meter Number " + yukonMeter.getMeterNumber() + " Code:" + loadActionCode);

        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_CDPort(vendor, responseUrl);
            if (port != null) {
                port.CDStateChangedNotification(yukonMeter.getMeterNumber(), loadActionCode, transactionId, "errorString?");
            } else {
                log.error("Port not found for CB_Server (" + vendor.getCompanyName() + ")");
            }  
        } catch (RemoteException e) {
            log.error("TargetService: " + responseUrl + " - initiateConnectDisconnect (" + vendor.getCompanyName() + ")");
            log.error("RemoteExceptionDetail: "+e.getMessage());
        }   
    }
    
    @Override
    public synchronized ErrorObject[] cdEvent(final MultispeakVendor vendor, 
            final ConnectDisconnectEvent [] cdEvents,
            final String transactionId, final String responseUrl) throws RemoteException
    {
        if (! porterConnection.isValid()) {
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        log.info("Received " + cdEvents.length + " Meter(s) for Connect/Disconnect from " + vendor.getCompanyName());
        
        for (ConnectDisconnectEvent cdEvent : cdEvents) {
            final String meterNumber = getMeterNumberFromCDEvent(cdEvent);

            try {
                YukonMeter meter = meterDao.getYukonMeterForMeterNumber(meterNumber);
                MspLoadActionCode mspLoadActionCode = MspLoadActionCode.getForLoadActionCode(cdEvent.getLoadActionCode());
 	        	
                // validate is CD supported meter
        		if (!mspMeterDao.isCDSupportedMeter(meterNumber)) {
                    ErrorObject err = mspObjectDao.getErrorObject(meterNumber, 
                                                                  "MeterNumber (" + meterNumber + ") - Invalid Yukon Connect/Disconnect Meter.",
                                                                  "Meter",
                                                                  "CDEvent", vendor.getCompanyName());
                    errorObjects.add(err);
                    continue;
        		}
        		    
                // check for rf disconnect meter type and perform action
    		    boolean isRfnDisconnect = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.DISCONNECT_RFN);
                if (isRfnDisconnect) {
                    RfnMeter rfnMeter = rfnDeviceDao.getMeter(meter);
                    doRfnConnectDisconnect(rfnMeter, mspLoadActionCode.getRfnState().getType(), vendor, transactionId, responseUrl);
                    continue;                    
                } 

                // Assume plc if we made it this far, validate meter can receive porter command requests and command string exists, then perform action
                boolean canInitiatePorterRequest = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS);
                if (!canInitiatePorterRequest || StringUtils.isBlank(mspLoadActionCode.getPlcCommandString())) {
                    ErrorObject err = mspObjectDao.getErrorObject(meterNumber, 
                                                                  "MeterNumber (" + meterNumber + ") - Meter cannot receive requests from porter or no control command exists. " + 
                                                                          "LoadActionCode="+ cdEvent.getLoadActionCode(),
                                                                  "Meter",
                                                                  "CDEvent", vendor.getCompanyName());
                    errorObjects.add(err);
                } else { // do archaic way
                    long id = generateMessageID();      
                    CDEvent event = new CDEvent(vendor, id, transactionId, responseUrl);
                    getEventsMap().put(new Long(id), event);
 	                writePilRequest(meter, mspLoadActionCode.getPlcCommandString(), id, 13);
 	                mspObjectDao.logMSPActivity("initiateConnectDisconnect",
 	        						"(ID:" + meter.getPaoIdentifier().getPaoId() + ") MeterNumber (" + meterNumber + ") - " + 
 	        						        mspLoadActionCode.getPlcCommandString() + 
 	        						((cdEvent.getReasonCode() != null) ? " sent for ReasonCode: " + cdEvent.getReasonCode().getValue() : ""),
 	        						vendor.getCompanyName());
 	            }

            } catch (NotFoundException e) {
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "CDEvent", vendor.getCompanyName());
                errorObjects.add(err);
            }
        }
            
        return mspObjectDao.toErrorObject(errorObjects);
    }
    
    /*
     * Writes a request to pil for the meter and commandStr using the id for mspVendor.
     * log a message for the method name.
     */
    private void writePilRequest(YukonMeter meter, String commandStr, long id, int priority) {
        
        RequestMessage pilRequest = setupPilRequest(meter, commandStr, id, priority);
        doWritePilRequest(pilRequest);
    }
    
    private RequestMessage setupPilRequest(YukonMeter meter, String commandStr, long id, int priority) {
        
        RequestMessage pilRequest = null;
        commandStr += " update";
        commandStr += " noqueue";
        pilRequest = new RequestMessage(meter.getPaoIdentifier().getPaoId(), commandStr, id);
        pilRequest.setPriority(priority);
        
        return pilRequest;
    }
    
    private void doWritePilRequest(RequestMessage pilRequest) {
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
                log.error(e);
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
        
        return addToGroup(meterNos, SystemGroupEnum.DISCONNECTED_STATUS, "initiateDisconnectedStatus", mspVendor);
    }

    @Override
    public ErrorObject[] initiateUsageMonitoringStatus(MultispeakVendor mspVendor, String[] meterNos) {
        
        return addToGroup(meterNos, SystemGroupEnum.USAGE_MONITORING, "initiateUsageMonitoring", mspVendor);
    }

    @Override
    public ErrorObject[] cancelDisconnectedStatus(MultispeakVendor mspVendor, String[] meterNos) {

        return removeFromGroup(meterNos, SystemGroupEnum.DISCONNECTED_STATUS, "cancelDisconnectedStatus", mspVendor);
    }

    @Override
    public ErrorObject[] cancelUsageMonitoringStatus(MultispeakVendor mspVendor, String[] meterNos) {

        return removeFromGroup(meterNos, SystemGroupEnum.USAGE_MONITORING, "cancelUsageMonitoring", mspVendor);
    }
    
    @Override
    public ErrorObject[] addMeterObject(final MultispeakVendor mspVendor, Meter[] addMeters) throws RemoteException{
        final List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();
        
        for (final Meter mspMeter : addMeters) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult(){
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status){
                       
                        ErrorObject errorObject = isValidMeter(mspMeter, mspVendor, METER_ADD_STRING);
                        if( errorObject == null) {
                            //Load the CIS serviceLocation.
                            ServiceLocation mspServiceLocation = mspObjectDao.getMspServiceLocation(mspMeter.getMeterNo(), mspVendor);

                            try {
                                YukonMeter meterToAdd = getMeterByMspMeter(mspMeter, mspVendor);
                                // have existing meter to "update"
                                errorObject = addExistingMeter(mspMeter, mspServiceLocation, meterToAdd, mspVendor);
                            } catch (NotFoundException e) { //and NEW meter
                                List<ErrorObject> addMeterErrors = addNewMeter(mspMeter, mspServiceLocation, mspVendor);
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
                log.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(), 
                                                                 "X Error: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(), 
                                                                 "Meter", METER_ADD_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                log.error(ex);
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
    private YukonMeter getMeterByMspMeter(Meter mspMeter, MultispeakVendor mspVendor) throws NotFoundException {
        
    	YukonMeter meter = null;
    	MultispeakMeterLookupFieldEnum multispeakMeterLookupFieldEnum= multispeakFuncs.getMeterLookupField();

    	if( multispeakMeterLookupFieldEnum == MultispeakMeterLookupFieldEnum.METER_NUMBER) {
            meter = getMeterByMeterNumber(mspMeter);
        } else if( multispeakMeterLookupFieldEnum == MultispeakMeterLookupFieldEnum.ADDRESS ){ 
    			meter = getMeterByAddress(mspMeter);
    	} else if ( multispeakMeterLookupFieldEnum == MultispeakMeterLookupFieldEnum.DEVICE_NAME ) {
    	    meter = getMeterByPaoName(mspMeter, mspVendor);
        } else if ( multispeakMeterLookupFieldEnum == MultispeakMeterLookupFieldEnum.AUTO_METER_NUMBER_FIRST) {
	       try { //Lookup by MeterNo
	           meter = getMeterByMeterNumber(mspMeter);
           } catch (NotFoundException e) { //Doesn't exist by MeterNumber
               try { //Lookup by Address
                   meter = getMeterByAddress(mspMeter);
               } catch (NotFoundException e2){ //Doesn't exist by Address
                   meter = getMeterByPaoName(mspMeter, mspVendor);
                   //Not Found Exception thrown in the end if never found
               }
           }
        } else if ( multispeakMeterLookupFieldEnum == MultispeakMeterLookupFieldEnum.AUTO_DEVICE_NAME_FIRST) {
            try { //Lookup by Device Name
                meter = getMeterByPaoName(mspMeter, mspVendor);
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
    private YukonMeter getMeterByPaoName(Meter mspMeter, MultispeakVendor mspVendor) {
        String paoName = getPaoNameFromMspMeter(mspMeter, mspVendor);
        return meterDao.getForPaoName(paoName);
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
    private YukonMeter getMeterByMeterNumber(Meter mspMeter) {
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
            YukonMeter meter;
            try {
            	meter = meterDao.getForMeterNumber(meterNo);
            	//Meter exists if no exception was thrown
                if( !meter.isDisabled()) {//enabled
                    // Added meter to Inventory
                    addToGroup(meter, SystemGroupEnum.INVENTORY, METER_REMOVE_STRING, mspVendor);
                    deviceDao.disableDevice(meter);
                    
                    // TODO - if RFN meter, should we clear out RFNIdentifer values?
                    mspObjectDao.logMSPActivity(METER_REMOVE_STRING,
                                           "MeterNumber(" + meterNo + ") - Meter Disabled.",
                                           mspVendor.getCompanyName());
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
        final MspPaoNameAliasEnum paoAlias = multispeakFuncs.getPaoNameAlias();
        
        for (final ServiceLocation mspServiceLocation : serviceLocations) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult(){
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status){
                        
                        if (paoAlias == MspPaoNameAliasEnum.SERVICE_LOCATION) {

                            String serviceLocationStr = mspServiceLocation.getObjectID();
                            List<com.cannontech.amr.meter.model.Meter> meters = searchForMetersByPaoName(serviceLocationStr);

                            if (meters.isEmpty()) {
                                ErrorObject err = mspObjectDao.getNotFoundErrorObject(serviceLocationStr, "ServiceLocation", "ServiceLocation", SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                errorObjects.add(err);
                            } else {
                                for (com.cannontech.amr.meter.model.Meter meter: meters) {
                                    if ( !meter.isDisabled()) {
                                        //Update billing cycle information
                                        String billingCycle = mspServiceLocation.getBillingCycle();
                                        updateBillingCyle(billingCycle, meter.getMeterNumber(), meter, SERV_LOC_CHANGED_STRING, mspVendor);
                                        updateAltGroup(mspServiceLocation, meter.getMeterNumber(), meter, SERV_LOC_CHANGED_STRING, mspVendor);
                                        //Update substation group and device's route
                                        // using null for mspMeter. See comments in getMeterSubstationName(...)
                                        String substationName = getMeterSubstationName(null, mspServiceLocation);
                                        updateSubstationGroupAndRoute(meter, mspVendor, substationName, SERV_LOC_CHANGED_STRING, false);
                                    }
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
                                        changeMeter(mspMeter, mspServiceLocation, meter, mspVendor, SERV_LOC_CHANGED_STRING);
                                        
                                        //Update billing cycle information
                                        String billingCycle = mspServiceLocation.getBillingCycle();
                                        updateBillingCyle(billingCycle, meterNumber, meter, SERV_LOC_CHANGED_STRING, mspVendor);
                                        updateAltGroup(mspServiceLocation, meterNumber, meter, SERV_LOC_CHANGED_STRING, mspVendor);
                                    } catch ( NotFoundException e) {
                                        ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                        errorObjects.add(err);            
                                    }
                                }
                                   
                            } else {
                                ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(),
                                        paoAlias.getDisplayName() + "ServiceLocation(" + mspServiceLocation.getObjectID()+ ") - No meters returned from vendor for location.",
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
                log.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspServiceLocation.getObjectID(), 
                                                                 "X Error: (ServLoc:" + mspServiceLocation.getObjectID() + ")-" + ex.getMessage(), 
                                                                 "ServiceLocation",
                                                                 SERV_LOC_CHANGED_STRING,
                                                                 mspVendor.getCompanyName());
                errorObjects.add(err);
                log.error(ex);
            }
        }
        return mspObjectDao.toErrorObject(errorObjects);
    }

    @Override
    public ErrorObject[] changeMeterObject(final MultispeakVendor mspVendor, Meter[] changedMeters) throws RemoteException{
        final List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();

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
                                
                                // using null for mspServiceLocation. See comments in getMeterSubstationName(...) 
                                changeMeter(mspMeter, null, meter, mspVendor, METER_CHANGED_STRING);
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
                log.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(mspMeter.getMeterNo(), 
                                                                 "X Error: (MeterNo:" + mspMeter.getMeterNo() + ")-" + ex.getMessage(), 
                                                                 "Meter", METER_CHANGED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                log.error(ex);
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
     * Creates a new meter, sets it's name, meter number.
     * If RFN, sets model, manufacturer, serial number.
     * Else (PLC), sets address and guesses a route to set.
     * @param mspMeter
     * @param mspVendor
     * @param templatePaoName
     * @param substationName
     */
    private void createMeter(Meter mspMeter, ServiceLocation mspServiceLocation, MultispeakVendor mspVendor, String templatePaoName, String substationName) {
        
        // METER_NUMBER
        String meterNumber = mspMeter.getMeterNo().trim();
        
        // NAME
        String paoName = getPaoNameFromMspMeter(mspMeter, mspVendor);
        // it is possible that a device with this name already exists, specifically if only looking up by METER_NUMBER.
        // So lets make sure to get one that works for us.
        paoName = getNewPaoName(paoName);
        
        // ADDRESS
        String address = mspMeter.getNameplate().getTransponderID().trim();
        
        // CREATE DEVICE - new device is automatically added to template's device groups
        SimpleDevice newDevice = deviceCreationService.createDeviceByTemplate(templatePaoName, paoName, true);
        mspObjectDao.logMSPActivity(METER_ADD_STRING, "DeviceName(" + paoName+ ") - Meter created. Address (" + address + ").", mspVendor.getCompanyName());
        
        // UPDATE DEVICE METERNUMBER, ADDRESS
        deviceUpdateService.changeAddress(newDevice, Integer.valueOf(address));
        deviceUpdateService.changeMeterNumber(newDevice, meterNumber);
        mspObjectDao.logMSPActivity(METER_ADD_STRING, "DeviceName(" + paoName+ ") - Set Meter number (" + meterNumber + ").", mspVendor.getCompanyName());
        
        // UPDATE BILLING GROUP
        String cycleGroupName = mspServiceLocation.getBillingCycle();
        updateBillingCyle(cycleGroupName, meterNumber, newDevice, METER_ADD_STRING, mspVendor);
        updateAltGroup(mspServiceLocation, meterNumber, newDevice, METER_ADD_STRING, mspVendor);
        
        // UPDATE SubstationName GROUP and ROUTES
        //update routing info regardless of substation "change" (like update method does)...because this is a _create new_ method.
        YukonMeter yukonMeter = new YukonMeter(newDevice.getPaoIdentifier(), meterNumber, paoName, false);
        updateSubstationGroupAndRoute(yukonMeter, mspVendor, substationName, METER_ADD_STRING, true);
    }
    
    private void updateMeterRouteForSubstation(YukonDevice meterDevice, MultispeakVendor mspVendor, String substationName, String meterNumber) {
    	
        // not valid for RFN meter types
        if (meterDevice.getPaoIdentifier().getPaoType().isRfn()) {
            mspObjectDao.logMSPActivity(METER_ADD_STRING, "MeterNumber(" + meterNumber + ") - RFN Meter type; No route locate perfomed.", mspVendor.getCompanyName());
            return;
        }

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
     * Helper method to load extension value from extensionItems for extensionName
     * @param extensionItems
     * @param extensionName
     * @param defaultValue
     * @return
     */
    private String getExtensionValue(ExtensionsItem[] extensionItems, String extensionName, String defaultValue) {
        log.debug("Attempting to load extension value for key:" + extensionName);
        if( extensionItems != null) {
            for (ExtensionsItem eItem : extensionItems) {
                String extName = eItem.getExtName();
                if ( extName.equalsIgnoreCase(extensionName)) {
                    return eItem.getExtValue();
                }
            }
        }
        log.warn("Extension " + extensionName + " key was not found. Returning default value: " + defaultValue);
        return defaultValue;
    }
    
    /**
     * Returns a Yukon PaoName (template) to model new meters after.
     * If no value is provided in the mspMeter object, then the defaultTemplateName is returned. 
     * @param mspMeter
     * @param defaultTemplateName
     * @return
     */
    private String getMeterTemplate(Meter mspMeter, String defaultTemplateName) {
       
        if( mspMeter.getAMRDeviceType() != null) {
            return mspMeter.getAMRDeviceType();
        }
       
        ExtensionsItem[] extensionItems = mspMeter.getExtensionsList();
        return getExtensionValue(extensionItems, EXTENSION_DEVICE_TEMPLATE_STRING, defaultTemplateName);
    }

    /**
     * Returns the value of the paoName alias extension field from Meter object.
     * If no value is provided in the Meter object, then null is returned.
     * NOTE:  meterno - this extension will return mspMeter.meterNo directly. 
     * @param mspMeter
     * @return
     */
    private String getExtensionValue(Meter mspMeter) {
        
        boolean usesExtension = multispeakFuncs.usesPaoNameAliasExtension();
        
        if (usesExtension) {
            String extensionName = multispeakFuncs.getPaoNameAliasExtension();
            if (extensionName.equalsIgnoreCase("meterno")) {    // specific field
                return mspMeter.getMeterNo();
            } else {    // use extensions
                
                ExtensionsItem[] extensionItems = mspMeter.getExtensionsList();
                return getExtensionValue(extensionItems, extensionName, null);
            }
        } 
        return null;
    }

    // UPDATE BILLING CYCLE DEVICE GROUP
    @Override
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

    // UPDATE ALtGroup DEVICE GROUP
    @Override
    public boolean updateAltGroup(ServiceLocation mspServiceLocation, String meterNumber, YukonDevice yukonDevice, String logActionStr, MultispeakVendor mspVendor) {
        boolean updateAltGroup = configurationSource.getBoolean(MasterConfigBooleanKeysEnum.MSP_ENABLE_ALTGROUP_EXTENSION);
        if (updateAltGroup) {
            ExtensionsItem[] extensionItems = mspServiceLocation.getExtensionsList();
            String extensionName = configurationSource.getString(MasterConfigStringKeysEnum.MSP_ALTGROUP_EXTENSION, "altGroup");
            String altGroup = getExtensionValue(extensionItems, extensionName, null);
            if (!StringUtils.isBlank(altGroup)) {
    
                //Remove from all alt group membership groups
                DeviceGroup altGroupDeviceGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.ALTERNATE);
                StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getStoredGroup(altGroupDeviceGroup);
                return updatePrefixGroup(altGroup, meterNumber, yukonDevice, logActionStr, mspVendor, deviceGroupParent);
            }
        }        
        return false;
    }
    
    // UPDATE SUBSTATION DEVICE GROUP
    @Override
    public boolean updateSubstationGroup(String substationName, String meterNumber, YukonDevice yukonDevice,
            String logActionStr, MultispeakVendor mspVendor) {

        if (!StringUtils.isBlank(substationName)) {
            
            //Remove from all substation membership groups
            DeviceGroup substationNameDeviceGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.CIS_SUBSTATION);
            StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getStoredGroup(substationNameDeviceGroup);
            return updatePrefixGroup(substationName, meterNumber, yukonDevice, logActionStr, mspVendor, deviceGroupParent);
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
                log.debug("MeterNumber(" + meterNumber + ") - Already in group:  " + groupName);
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
    private void removeFromGroup(YukonMeter meter, SystemGroupEnum systemGroup, 
            String logActionStr, MultispeakVendor mspVendor){

        DeviceGroup deviceGroup = deviceGroupEditorDao.getSystemGroup(systemGroup);
        deviceGroupMemberEditorDao.removeDevices((StoredDeviceGroup)deviceGroup, Collections.singletonList(meter));
        String basePath = deviceGroupService.getFullPath(systemGroup);
        mspObjectDao.logMSPActivity(logActionStr,
                       "MeterNumber(" + meter.getMeterNumber() + ") - Removed from " + basePath,  mspVendor.getCompanyName());
    }

    /**
     * Helper method to add meter to systemGroup
     * @param meter
     * @param systemGroup
     * @param logActionStr
     * @param mspVendor
     */
    private void addToGroup(YukonMeter meter, SystemGroupEnum systemGroup, 
            String logActionStr, MultispeakVendor mspVendor){

        DeviceGroup deviceGroup = deviceGroupEditorDao.getSystemGroup(systemGroup);
        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup)deviceGroup, Collections.singletonList(meter));
        String basePath = deviceGroupService.getFullPath(systemGroup);
        mspObjectDao.logMSPActivity(logActionStr,
                       "MeterNumber(" + meter.getMeterNumber() + ") - Added to " + basePath,  mspVendor.getCompanyName());
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
            try {
                YukonMeter meter = meterDao.getYukonMeterForMeterNumber(meterNumber);
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
            try {
                YukonMeter meter = meterDao.getYukonMeterForMeterNumber(meterNumber);
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
     * @throws InsufficientMultiSpeakDataException - when paoName not found (null)
     */
    private String getPaoNameFromMspMeter(Meter mspMeter, MultispeakVendor mspVendor ) {

        String paoName = null;
        final MspPaoNameAliasEnum paoAlias = multispeakFuncs.getPaoNameAlias();
        if (paoAlias == MspPaoNameAliasEnum.ACCOUNT_NUMBER) {
            if( mspMeter.getUtilityInfo() != null &&
                    StringUtils.isNotBlank(mspMeter.getUtilityInfo().getAccountNumber())) {
                paoName = mspMeter.getUtilityInfo().getAccountNumber();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.SERVICE_LOCATION) {
			if( mspMeter.getUtilityInfo() != null &&
					StringUtils.isNotBlank(mspMeter.getUtilityInfo().getServLoc())) {
			    paoName = mspMeter.getUtilityInfo().getServLoc();
			}
        } else if (paoAlias == MspPaoNameAliasEnum.CUSTOMER_ID) {
			if( mspMeter.getUtilityInfo() != null &&
					StringUtils.isNotBlank(mspMeter.getUtilityInfo().getCustID())) {
			    paoName = mspMeter.getUtilityInfo().getCustID();
			}
		} else if (paoAlias == MspPaoNameAliasEnum.EA_LOCATION) {
			if( mspMeter.getUtilityInfo() != null &&
			        mspMeter.getUtilityInfo().getEaLoc() != null &&
					StringUtils.isNotBlank(mspMeter.getUtilityInfo().getEaLoc().getName())) {
			    paoName = mspMeter.getUtilityInfo().getEaLoc().getName();
			}
		} else if (paoAlias == MspPaoNameAliasEnum.GRID_LOCATION) {
		    if(StringUtils.isNotBlank(mspMeter.getFacilityID()) ) {
		        paoName = mspMeter.getFacilityID();
		    }
        } else if (paoAlias == MspPaoNameAliasEnum.METER_NUMBER) {
            if(StringUtils.isNotBlank(mspMeter.getMeterNo()) ) {
                paoName = mspMeter.getMeterNo();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.POLE_NUMBER) {
            if (StringUtils.isNotBlank(mspMeter.getMeterNo())) {
                ServiceLocation mspServiceLocation = mspObjectDao.getMspServiceLocation(mspMeter.getMeterNo(), mspVendor);
                if( mspServiceLocation.getNetwork() != null && 
                        StringUtils.isNotBlank(mspServiceLocation.getNetwork().getPoleNo())){
                    paoName = mspServiceLocation.getNetwork().getPoleNo();
                }
            }
        } 
        
        if (paoName == null) {
            throw new InsufficientMultiSpeakDataException("Message does not contain sufficient data for Yukon Device Name.");
		}

        String extensionValue = getExtensionValue(mspMeter);
        return multispeakFuncs.buildAliasWithQuantifier(paoName, extensionValue, mspVendor);

	}
	
    /**
     * Helper method to add a new meter to Yukon.
     * An errorObject will be returned when the templateName is not a valid YukonPaobject Name.
     * An errorObject will be returned when the substation name is not a valid Substation To Route Mapping.
     * @param mspMeter
     * @param mspVendor
     * @return
     */
	private List<ErrorObject> addNewMeter(Meter mspMeter, ServiceLocation mspServiceLocation, MultispeakVendor mspVendor){
	    
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
        	String substationName = getMeterSubstationName(mspMeter, mspServiceLocation);
        	err = isValidSubstation(substationName, mspMeter.getMeterNo().trim(), mspVendor);
    		if (err == null) {
    			createMeter(mspMeter, mspServiceLocation, mspVendor, templateName, substationName);
            } else {
            	errorObjects.add(err);
            }
        }
        return errorObjects;
	}
	
	/**
	 * Return the substation name of a Meter.
	 * If MSP_ENABLE_SUBSTATIONNAME_EXTENSION cparm is set, then attempt to return from ServiceLocation extensions.
	 *   If ServiceLocation extensions return nothing, then attempt to return from Meter extensions.
	 *     If Meter extensions return nothing, then attempt to return "normally". 
	 * If MSP_ENABLE_SUBSTATIONNAME_EXTENSION cparm is NOT set, use normal loading from Meter object.
	 * Normal loading: If the Meter does not contain a substation name in its utility info, empty string is returned.
	 * @param mspMeter - always necessary, unless we're trying to use the extensions piece :( SEDC...argh!
	 * @param mspServiceLocation - only necessary when using MSP_ENABLE_SUBSTATIONNAME_EXTENSION cparm
	 * @return String substationName
	 */
	private String getMeterSubstationName(Meter mspMeter, ServiceLocation mspServiceLocation) {
		
        boolean useExtension = configurationSource.getBoolean(MasterConfigBooleanKeysEnum.MSP_ENABLE_SUBSTATIONNAME_EXTENSION);
        if (useExtension) {
            if (mspServiceLocation == null) {
                // null is passed in with MeterChangedNotification. This saves on an extra call to load mspServiceLocation when
                //   it isn't an expected function of this method call (only a "feature bi-product"). And only used by DEMCO/SEDC to-date 20120815.
                log.warn("Use of extension value to update substationName is not available for MeterChangedNotification...sorry.");
                return ""; 
            }
            
            String extensionName = configurationSource.getString(MasterConfigStringKeysEnum.MSP_SUBSTATIONNAME_EXTENSION, "readPath");

            // SEDC special....the extension is probably in the serviceLocation object and not the meter object... :(
            log.debug("Attempting to load extension value for substation name from multispeak _serviceLocation_.");
            String extensionValue = getExtensionValue(mspServiceLocation.getExtensionsList(), extensionName, null);
            
            if (StringUtils.isBlank(extensionValue) && mspMeter != null) {
                // But...if not SEDC AND using extensions...why not see if it's in the meter's extension list?
                log.debug("Attempting to load extension value for substation name from multispeak _meter_.");
                extensionValue = getExtensionValue(mspMeter.getExtensionsList(), extensionName, null);
            }
            
            // if we were able to load an extension value, return it....otherwise continue using normal loading from Meter object directly
            if (StringUtils.isNotBlank(extensionValue)) {
                log.debug("Extension value for substation name found, returning value: " + extensionValue);
                return extensionValue;                        
            }
        }
	    
		String substationName = "";
		if(!(mspMeter == null ||mspMeter.getUtilityInfo() == null || mspMeter.getUtilityInfo().getSubstationName() == null)){
			substationName = mspMeter.getUtilityInfo().getSubstationName();
		}
		
		return substationName;
	}
	
	/**
	 * Helper method to search devices by PaoName for filterValue.
	 * @param filterValue
	 * @return
	 */
    private List<com.cannontech.amr.meter.model.Meter> searchForMetersByPaoName(String filterValue) {
        List<FilterBy> searchFilter = new ArrayList<FilterBy>(1);
        FilterBy filterBy = new StandardFilterBy("deviceName", MeterSearchField.PAONAME);
        filterBy.setFilterValue(filterValue);
        searchFilter.add(filterBy);
        MeterSearchOrderBy orderBy = new MeterSearchOrderBy(MeterSearchField.PAONAME.toString(), true);
        SearchResult<com.cannontech.amr.meter.model.Meter> result = meterSearchDao.search(searchFilter, orderBy, 0, 25);
        List<com.cannontech.amr.meter.model.Meter> meters = result.getResultList();
       return meters;
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
            YukonMeter templateMeter = meterDao.getForPaoName(templateName);
            if (templateMeter.getPaoIdentifier().getPaoType().isRfn()) {
                ErrorObject err = mspObjectDao.getErrorObject(templateMeter.getMeterNumber(), 
                                                              "Error: MeterNumber(" + templateMeter.getMeterNumber() + 
                                                              ") - RFN Meter Type is not supported. Meter was NOT added or updated",
                                                              "Meter", METER_ADD_STRING, mspVendor.getCompanyName());
                return err;
            }

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
    private ErrorObject isMeterDisabled(YukonMeter meter, MultispeakVendor mspVendor) {
        if( !meter.isDisabled()) {
            //Meter is currently enabled in Yukon
            ErrorObject err = mspObjectDao.getErrorObject(meter.getMeterNumber(), 
                                                             "Error: MeterNumber(" + meter.getMeterNumber()+ ") - Meter is already active." + 
                                                             "DeviceName: " + meter.getName() + "; Address: " + ". No updates were made.",
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

        //Check for valid TransponderID (PLC meters)
        if ( mspMeter.getNameplate() == null ||
                StringUtils.isBlank(mspMeter.getNameplate().getTransponderID()) ||
                !StringUtils.isNumeric(mspMeter.getNameplate().getTransponderID().trim()) ) {

            //It's not valid for PLC, check if it may be valid for RFN 
            //Check for valid serial Number
            if (StringUtils.isBlank(mspMeter.getSerialNumber())) {
                return mspObjectDao.getErrorObject(mspMeter.getMeterNo(), 
                                                              "Error: MeterNumber(" + mspMeter.getMeterNo() + ") - SerialNumber nor TransponderId are valid.  No updates were made.",
                                                              "Meter", method, mspVendor.getCompanyName());
            }
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
    private ErrorObject addExistingMeter(Meter mspMeter, ServiceLocation mspServiceLocation, 
            YukonMeter meter,
            MultispeakVendor mspVendor) {
        
    	//Verify the meter to "update" is disabled.
        ErrorObject err = isMeterDisabled(meter, mspVendor);
        if (err != null) {
            return err;
        }
        
        String logString = "";
        if (meter.getPaoIdentifier().getPaoType().isRfn()) {
            err = mspObjectDao.getErrorObject(meter.getMeterNumber(), 
                                                          "Error: MeterNumber(" + meter.getMeterNumber() + 
                                                          ") - RFN Meter Type is not supported. Meter was NOT added or updated",
                                                          "Meter", METER_ADD_STRING, mspVendor.getCompanyName());
            return err;
        } else {  //assume PLC
            //update plc meter
            //Verify the meter to "update" does not have the same address as another enabled meter.
            String mspAddress = mspMeter.getNameplate().getTransponderID().trim();
            com.cannontech.amr.meter.model.Meter plcMeter = meterDao.getForYukonDevice(meter);
            String currentAddress = plcMeter.getAddress();
            if( !currentAddress.equalsIgnoreCase(mspAddress)) { //Same MeterNumber, Different Address

                try { //Lookup meter by mspAddress
                    com.cannontech.amr.meter.model.Meter meterByAddress = meterDao.getForPhysicalAddress(mspAddress);
                    err = isMeterDisabled(meterByAddress, mspVendor);
                    if (err != null) { //Address is already active
                        return err;
                    }
                } catch (NotFoundException e) {
                    //Ignore Exception...this is what we want, for the address to NOT already exist in Yukon
                }
                
                //Update Address
                deviceUpdateService.changeAddress(meter, Integer.valueOf(mspAddress));
                logString += "(Addr-" + currentAddress + " to " + mspAddress + ");";
            }
        }        

        // Verify substation name
        String substationName = getMeterSubstationName(mspMeter, mspServiceLocation);
        if (StringUtils.isBlank(substationName)) {
        	mspObjectDao.logMSPActivity(METER_ADD_STRING, "MeterNumber(" + meter.getMeterNumber() + ") - substation name not provided, route locate will not happen.", mspVendor.getCompanyName());
        } else {
        	err = isValidSubstation(substationName, meter.getMeterNumber(), mspVendor);
        	if (err != null) {
        		mspObjectDao.logMSPActivity(METER_ADD_STRING, "MeterNumber(" + meter.getMeterNumber() + ") - substation name is invalid.", mspVendor.getCompanyName());
        		return err;
        	}
        }

        final MspPaoNameAliasEnum paoAlias = multispeakFuncs.getPaoNameAlias();
        String billingCycle = mspServiceLocation.getBillingCycle();
        
        //Enable Meter and update applicable fields.
        removeFromGroup(meter, SystemGroupEnum.INVENTORY, METER_ADD_STRING, mspVendor);
    
        //update the billing group from CIS billingCyle
        updateBillingCyle(billingCycle, meter.getMeterNumber(), meter, METER_ADD_STRING, mspVendor);
        updateAltGroup(mspServiceLocation, meter.getMeterNumber(), meter, METER_ADD_STRING, mspVendor);
        
        //Update PaoName
        String currentPaoName = meter.getName();
        String newPaoName = getPaoNameFromMspMeter(mspMeter, mspVendor);
        
        if (currentPaoName.equalsIgnoreCase(newPaoName)) {
            logString = "PAOName(" + paoAlias.getDisplayName() + ")No Change;";
        } else {
            newPaoName = getNewPaoName(newPaoName);
            deviceDao.changeName(meter,  newPaoName);
            logString = "PAOName(" + paoAlias.getDisplayName() + ")(" + currentPaoName + " to " + newPaoName + ");";
        }
        
        //Update MeterNumber
        String currentMeterNumber = meter.getMeterNumber();
        String newMeterNumber = mspMeter.getMeterNo();
        
        if (!currentMeterNumber.equalsIgnoreCase(newMeterNumber)) {
            deviceUpdateService.changeMeterNumber(meter, newMeterNumber);
            logString += "(MeterNo-" + currentMeterNumber + " to " + newMeterNumber + ");";
        }

        //Enable the meter
        deviceDao.enableDevice(meter);

        mspObjectDao.logMSPActivity(METER_ADD_STRING,
                       "MeterNo(" + meter.getMeterNumber()+ ");Enabled;" + logString,
                       mspVendor.getCompanyName());

        changeDeviceType(mspMeter, meter, mspVendor, METER_ADD_STRING);
        
        updateSubstationGroupAndRoute(meter, mspVendor, substationName, METER_ADD_STRING, true);
        return null;
    }

    /**
     * Update the (CIS) Substation Group. If changed, update route (perform route locate).
     * If substationName is blank, do nothing.
     * @param meter
     * @param mspVendor
     * @param substationName
     * @param forceRouteUpdate - when true, route locate will occur; else, it will only occur if changed. 
     */
    private void updateSubstationGroupAndRoute(YukonMeter meter, MultispeakVendor mspVendor, String substationName, String logActionStr, boolean forceRouteUpdate) {
        if (!StringUtils.isBlank(substationName)) {
            //update the substation group
            boolean addedToGroup = updateSubstationGroup(substationName, meter.getMeterNumber(), meter, logActionStr, mspVendor);
            if (forceRouteUpdate || addedToGroup) {
                //If the substation changed, we should attempt to update the route info too.
                //Update route (_after_ meter is enabled).
                updateMeterRouteForSubstation(meter, mspVendor, substationName, meter.getMeterNumber());
            }
        }
    }

    /** Helper method to update meter based on the mspMeter.
     * PaoName and MeterNumber will be updated if changed.
     * Substation group information will be updated.
     * @param mspMeter
     * @param meter
     * @param mspVendor
     * @param paoAlias
     */
    private void changeMeter(Meter mspMeter, ServiceLocation mspServiceLocation, 
            YukonMeter meter, 
            MultispeakVendor mspVendor,
            String logActionStr) {
        
        final MspPaoNameAliasEnum paoAlias = multispeakFuncs.getPaoNameAlias();

        // update paoname
        String paoNameLog = "";
        String meterNumberLog = "";
        String currentPaoName = meter.getName();
        String newPaoName = getPaoNameFromMspMeter(mspMeter, mspVendor);
        
        if (currentPaoName.equalsIgnoreCase(newPaoName)) {
            paoNameLog = "MeterNumber (" + meter.getMeterNumber() + ") PAOName(" + paoAlias.getDisplayName()+ ") No change in PaoName.";
        } else {
            newPaoName = getNewPaoName(newPaoName);
            deviceDao.changeName(meter,  newPaoName);
            paoNameLog = "MeterNumber (" + meter.getMeterNumber() + ") PAOName(" + paoAlias.getDisplayName()+ ") PaoName Changed(OLD:" + currentPaoName + " NEW:" + newPaoName + ").";
        }
        mspObjectDao.logMSPActivity(logActionStr,
                                    paoNameLog.toString(),
                                    mspVendor.getCompanyName());

        // update meter number
        String currentMeterNumber = meter.getMeterNumber();
        String newMeterNumber = mspMeter.getMeterNo();
        
        if (currentMeterNumber.equalsIgnoreCase(newMeterNumber)) {
            meterNumberLog = "MeterNumber (" + meter.getMeterNumber() + ") No change in MeterNumber.";
        } else {
            deviceUpdateService.changeMeterNumber(meter, newMeterNumber);
            meterNumberLog = "MeterNumber (" + meter.getMeterNumber() + ") MeterNumber Changed(OLD:" + currentMeterNumber + " NEW:" + newMeterNumber + ").";
        }
        mspObjectDao.logMSPActivity(logActionStr,
                                    meterNumberLog.toString(),
                                    mspVendor.getCompanyName());

        //update the substation group
        String substationName = getMeterSubstationName(mspMeter, mspServiceLocation);
        updateSubstationGroupAndRoute(meter, mspVendor, substationName, logActionStr, false);
    }
    
    /**
     * Helper method to check if the deviceType of meter is different than the deviceType of the template meter.
     * If different types of meters, then the deviceType will be changed for meter.
     * @param mspMeter
     * @param meter
     * @param mspVendor
     * @param method
     */
    private void changeDeviceType(Meter mspMeter, YukonMeter meter, MultispeakVendor mspVendor, String method) {
        
        String templateName = getMeterTemplate(mspMeter, mspVendor.getTemplateNameDefault());
        YukonMeter templateMeter;
        try {
            templateMeter = meterDao.getForPaoName(templateName);
        } catch (NotFoundException e) {
            //No template found to compare to
            log.warn(e.getMessage() + " No TemplateName found in Yukon for ChangeDeviceType method, Device Type not checked.");
            return;
        }

        PaoType existingType = meter.getPaoIdentifier().getPaoType();
        if( templateMeter.getPaoIdentifier().getPaoType() != existingType) {   //different types of meters...change type
            try {
                PaoDefinition paoDefinition = paoDefinitionDao.getPaoDefinition(templateMeter.getPaoIdentifier().getPaoType());
                deviceUpdateService.changeDeviceType(meter, paoDefinition);
                mspObjectDao.logMSPActivity(method, 
                                            "MeterNumber (" + meter.getMeterNumber() + ") - Changed DeviceType from:" + existingType + " to:" + templateMeter.getPaoIdentifier().getPaoType() + ").", 
                                            mspVendor.getCompanyName());
            } catch (DataRetrievalFailureException e) {
                log.warn(e);
            } catch (PersistenceException e) {
                log.warn(e);
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
    
    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }
}