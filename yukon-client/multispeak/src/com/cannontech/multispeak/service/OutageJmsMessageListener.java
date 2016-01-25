package com.cannontech.multispeak.service;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.monitors.message.OutageJmsMessage;
import com.cannontech.amr.statusPointMonitoring.model.OutageActionType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.OutageEventLogService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
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
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.OAClient;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class OutageJmsMessageListener implements MessageListener {

    private ImmutableMap<OutageActionType, OutageEventType> outageMap = ImmutableMap.of();
    private static final Logger log = YukonLogManager.getLogger(OutageJmsMessageListener.class);
    private MultispeakDao multispeakDao;
    private ImmutableList<MultispeakVendor> vendorsToSendOutageMsg = ImmutableList.of();
    private OutageEventLogService outageEventLogService;
    private MultispeakFuncs multispeakFuncs;
    private MspIdentifiablePaoService mspIdentifiablePaoService;
    private PaoDefinitionDao paoDefinitionDao;
    private AtomicLong atomicLong = new AtomicLong();
    @Autowired private ObjectFactory objectFactory;
    @Autowired private OAClient oaClient;
    @Autowired private MspObjectDao mspObjectDao;
    
    
    @PostConstruct
    public void initialize() {
        ImmutableMap.Builder<OutageActionType, OutageEventType> mapBuilder = ImmutableMap.builder();
        mapBuilder.put(OutageActionType.NoResponse, OutageEventType.NO_RESPONSE);
        mapBuilder.put(OutageActionType.Outage, OutageEventType.OUTAGE);
        mapBuilder.put(OutageActionType.Restoration, OutageEventType.RESTORATION);
        outageMap = mapBuilder.build();
        
        List<MultispeakVendor> allVendors = multispeakDao.getMultispeakVendors();
        ImmutableList.Builder<MultispeakVendor> supportsOutage = ImmutableList.builder();
        for (MultispeakVendor mspVendor : allVendors) {
            if (mspVendor.getMspInterfaceMap().get(MultispeakDefines.OA_Server_STR) != null) {
                String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.OA_Server_STR);
                try {
                    GetMethods getMethods = objectFactory.createGetMethods();
                    GetMethodsResponse getMethodsResponse = oaClient.getMethods(mspVendor, endpointUrl, getMethods);
                    ArrayOfString arrayOfMethods = getMethodsResponse.getGetMethodsResult();
                    List<String> mspMethodNames = arrayOfMethods.getString();
                    //not sure where a static variable containing this method exists.. doing this for now
                    if (mspMethodNames.contains("ODEventNotification")) {
                        supportsOutage.add(mspVendor);
                    }
                } catch (MultispeakWebServiceClientException e) {
                    log.warn("caught exception in initialize");
                }
            }
        }
        
        vendorsToSendOutageMsg = supportsOutage.build();
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
                if (object instanceof OutageJmsMessage) {
                    OutageJmsMessage outageJmsMessage = (OutageJmsMessage) object;
                    handleMessage(outageJmsMessage);
                }
            } catch (JMSException e) {
                log.warn("Unable to extract OutageJmsMessage from message", e);
            }
        }
    }
    
    private void handleMessage(OutageJmsMessage outageJmsMessage) {
        
        if (vendorsToSendOutageMsg.isEmpty()) {
            log.debug("Recieved outage message from jms queue: not generating message because no vendors are configured");
            return;
        }
        log.debug("Recieved outage message from jms queue: " + outageJmsMessage);
        
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
                                                                     mspVendor.getCompanyName());
                    }
                
            } catch (MultispeakWebServiceClientException e) {
                log.error("TargetService: " + endpointUrl + " - initiateOutageDetection (" + mspVendor.getCompanyName() + ")");
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
                                                   outageDetectionEvent.getObjectID());
        
        return outageDetectionEvent;
    }
    
    private OutageDetectDeviceType getOutageDetectDeviceType(YukonPao paoIdentifier) {
        if (isMeter(paoIdentifier)) {
            return OutageDetectDeviceType.METER;
        }
        return OutageDetectDeviceType.OTHER;
    }
    
    private boolean isMeter(YukonPao paoIdentifier) {
        if (paoDefinitionDao.isTagSupported(paoIdentifier.getPaoIdentifier().getPaoType(), PaoTag.USES_METER_NUMBER_FOR_MSP)) {
            return true;
        }
        return false;
    }
    
    @Autowired
    public void setMultispeakDao(MultispeakDao multispeakDao) {
        this.multispeakDao = multispeakDao;
    }
    
    @Autowired
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }
    
    @Autowired
    public void setOutageEventLogService(OutageEventLogService outageEventLogService) {
        this.outageEventLogService = outageEventLogService;
    }
    
    @Autowired
    public void setMspIdentifiablePaoService(MspIdentifiablePaoService mspIdentifiablePaoService) {
        this.mspIdentifiablePaoService = mspIdentifiablePaoService;
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
}