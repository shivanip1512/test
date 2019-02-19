package com.cannontech.multispeak.service.v3;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.monitors.message.OutageJmsMessage;
import com.cannontech.amr.statusPointMonitoring.model.OutageActionType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.OutageEventLogService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.msp.beans.v3.ArrayOfErrorObject;
import com.cannontech.msp.beans.v3.ArrayOfOutageDetectionEvent;
import com.cannontech.msp.beans.v3.ArrayOfString;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.ODEventNotification;
import com.cannontech.msp.beans.v3.ODEventNotificationResponse;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.OutageDetectDeviceType;
import com.cannontech.msp.beans.v3.OutageDetectionEvent;
import com.cannontech.msp.beans.v3.OutageEventType;
import com.cannontech.msp.beans.v3.OutageLocation;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.OAClient;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.cannontech.multispeak.service.MspIdentifiablePaoService;
import com.cannontech.multispeak.service.OutageJmsMessageService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class OutageJmsMessageListener extends OutageJmsMessageService {

    @Autowired private MultispeakDao multispeakDao;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MspIdentifiablePaoService mspIdentifiablePaoService;
    @Autowired private OAClient oaClient;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private OutageEventLogService outageEventLogService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;

    private ImmutableMap<OutageActionType, OutageEventType> outageMap = ImmutableMap.of();
    private static final Logger log = YukonLogManager.getLogger(OutageJmsMessageListener.class);
    private ImmutableList<MultispeakVendor> vendorsToSendOutageMsg = ImmutableList.of();
    private AtomicLong atomicLong = new AtomicLong();

    @PostConstruct
    public void initialize() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.MULTISPEAK,
            new DatabaseChangeEventListener() {
                @Override
                public void eventReceived(DatabaseChangeEvent event) {
                    loadOutageSupportedVendors();
                }
            });

        ImmutableMap.Builder<OutageActionType, OutageEventType> mapBuilder = ImmutableMap.builder();
        mapBuilder.put(OutageActionType.NoResponse, OutageEventType.NO_RESPONSE);
        mapBuilder.put(OutageActionType.Outage, OutageEventType.OUTAGE);
        mapBuilder.put(OutageActionType.Restoration, OutageEventType.RESTORATION);
        outageMap = mapBuilder.build();
        // To make this call asynchronous, added new thread for calling method.
        new Thread(() -> {
            loadOutageSupportedVendors();
        }).start();

    }

    private void loadOutageSupportedVendors() {
        List<MultispeakVendor> allVendors = multispeakDao.getMultispeakVendors(true);
        ImmutableList.Builder<MultispeakVendor> supportsOutage = ImmutableList.builder();
        for (MultispeakVendor mspVendor : allVendors) {
            Pair<String, MultiSpeakVersion> keyPair =
                MultispeakVendor.buildMapKey(MultispeakDefines.OA_Server_STR, MultiSpeakVersion.V3);
            if (mspVendor.getMspInterfaceMap().get(keyPair) != null) {
                String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.OA_Server_STR);
                try {
                    GetMethods getMethods = objectFactory.createGetMethods();
                    GetMethodsResponse getMethodsResponse = oaClient.getMethods(mspVendor, endpointUrl, getMethods);
                    ArrayOfString arrayOfMethods = getMethodsResponse.getGetMethodsResult();
                    List<String> mspMethodNames = arrayOfMethods.getString();
                    // not sure where a static variable containing this method exists.. doing this for now
                    if (mspMethodNames.stream().anyMatch("ODEventNotification"::equalsIgnoreCase)) {
                        supportsOutage.add(mspVendor);
                        log.info("Added OMS vendor to receive Status Point Monitor messages: "
                            + mspVendor.getCompanyName());
                    }
                } catch (MultispeakWebServiceClientException e) {
                    log.warn("caught exception in initialize " + e);
                }
            }
        }
        
        vendorsToSendOutageMsg = supportsOutage.build();
    }

    @Override
    public void handleMessage(OutageJmsMessage outageJmsMessage) {
        
        if (vendorsToSendOutageMsg.isEmpty()) {
            log.debug("Received outage message from jms queue: not generating message because no vendors are configured");
            return;
        }
        log.debug("Received outage message from jms queue: " + outageJmsMessage);
        
        OutageDetectionEvent outageDetectionEvent = getOutageDetectionEvent(outageJmsMessage);
        
        for (MultispeakVendor mspVendor : vendorsToSendOutageMsg) {
            String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.OA_Server_STR);
            
            log.info("Sending ODEventNotification ("+ endpointUrl + "): ObjectID: " + outageDetectionEvent.getObjectID() + " Type: " + outageDetectionEvent.getOutageEventType());
            
            try {
                String transactionId = String.valueOf(atomicLong.getAndIncrement());
                ODEventNotification odEventNotification = objectFactory.createODEventNotification();
                ArrayOfOutageDetectionEvent odEvents = objectFactory.createArrayOfOutageDetectionEvent();
                List<OutageDetectionEvent> listOfODEvents = odEvents.getOutageDetectionEvent();
                listOfODEvents.add(outageDetectionEvent);
                odEventNotification.setTransactionID(transactionId);
                odEventNotification.setODEvents(odEvents);
                ODEventNotificationResponse odEventNotificationResponse = oaClient.odEventNotification(mspVendor,
                                                                                                       endpointUrl,
                                                                                                       odEventNotification);
                List<ErrorObject> errObjects = null;
                if (odEventNotificationResponse != null) {
                    ArrayOfErrorObject arrOfErrorObject = odEventNotificationResponse.getODEventNotificationResult();
                    if (null != arrOfErrorObject) {
                        List<ErrorObject> errorObjects = arrOfErrorObject.getErrorObject();
                        if (errorObjects != null) {
                            errObjects = errorObjects;
                        }
                    }
                }
                    if (CollectionUtils.isNotEmpty(errObjects)) {
                        multispeakFuncs.logErrorObjects(endpointUrl, "ODEventNotification", errObjects);
                    } else {
                        outageEventLogService.mspMessageSentToVendor(outageJmsMessage.getSource(), 
                                                                     outageDetectionEvent.getOutageEventType().toString(), 
                                                                     outageDetectionEvent.getObjectID(), 
                                                                     outageDetectionEvent.getOutageDetectDeviceType().toString(),
                                                                     mspVendor.getCompanyName(),
                                                                     "ODEventNotification");
                    }
                
            } catch (MultispeakWebServiceClientException e) {
                log.error("TargetService: " + endpointUrl + " - ODEventNotification (" + mspVendor.getCompanyName() + ")");
                log.error("MultispeakWebServiceClientException: " + e.getMessage());
            }
        }
    }
    
    private OutageDetectionEvent getOutageDetectionEvent(OutageJmsMessage outageJmsMessage) {
        PaoIdentifier paoIdentifier = outageJmsMessage.getPaoIdentifier();
        
        OutageDetectionEvent outageDetectionEvent = new OutageDetectionEvent();
        outageDetectionEvent.setOutageEventType(outageMap.get(outageJmsMessage.getActionType()));
        
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(outageJmsMessage.getPointValueQualityHolder().getPointDataTimeStamp());   
        outageDetectionEvent.setEventTime(MultispeakFuncs.toXMLGregorianCalendar(cal));
        String objectId = mspIdentifiablePaoService.getObjectId(paoIdentifier);
        outageDetectionEvent.setOutageDetectDeviceType(getOutageDetectDeviceType(paoIdentifier));
        outageDetectionEvent.setObjectID(objectId);
        
        if (paoIdentifier.getPaoType().isMeter()) {
            //This is kind of cheating, we're assuming that if we have a "meter" paoType, then objectId is a MeterNumber.
            OutageLocation outageLocation = new OutageLocation();
            outageLocation.setObjectID(objectId);
            outageLocation.setMeterNo(objectId);    //MeterNumber
            outageDetectionEvent.setOutageLocation(outageLocation);
            
            outageDetectionEvent.setOutageDetectDeviceID(objectId); //MeterNumber
        }

        outageEventLogService.outageEventGenerated(outageDetectionEvent.getOutageEventType().value(), 
                                                   outageDetectionEvent.getEventTime().toGregorianCalendar().getTime(), 
                                                   outageDetectionEvent.getOutageDetectDeviceType().value(), 
                                                   outageDetectionEvent.getObjectID(),
                                                   "ODEventNotification");
        
        return outageDetectionEvent;
    }
    
    private OutageDetectDeviceType getOutageDetectDeviceType(YukonPao paoIdentifier) {
        if (isMeter(paoIdentifier)) {
            return OutageDetectDeviceType.METER;
        }
        return OutageDetectDeviceType.OTHER;
    }
    
}