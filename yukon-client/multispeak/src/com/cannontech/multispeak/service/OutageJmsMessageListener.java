package com.cannontech.multispeak.service;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

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
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.OA_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.OutageDetectDeviceType;
import com.cannontech.multispeak.deploy.service.OutageDetectionEvent;
import com.cannontech.multispeak.deploy.service.OutageEventType;
import com.cannontech.multispeak.deploy.service.OutageLocation;
import com.cannontech.multispeak.deploy.service.impl.MultispeakPortFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

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
    
    @PostConstruct
    public void initialize() {
        ImmutableMap.Builder<OutageActionType, OutageEventType> mapBuilder = ImmutableMap.builder();
        mapBuilder.put(OutageActionType.NoResponse, OutageEventType.NoResponse);
        mapBuilder.put(OutageActionType.Outage, OutageEventType.Outage);
        mapBuilder.put(OutageActionType.Restoration, OutageEventType.Restoration);
        outageMap = mapBuilder.build();
        
        List<MultispeakVendor> allVendors = multispeakDao.getMultispeakVendors();
        ImmutableList.Builder<MultispeakVendor> supportsOutage = ImmutableList.builder();
        for (MultispeakVendor mspVendor : allVendors) {

            if (mspVendor.getMspInterfaceMap().get(MultispeakDefines.OA_Server_STR) != null) {
                String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.OA_Server_STR); 
                                                                    
                OA_ServerSoap_BindingStub port = MultispeakPortFactory.getOA_ServerPort(mspVendor, endpointUrl);
                
                if (port != null) {
                    try {
                        String[] methods = new String[]{};
                        methods = port.getMethods();
                        
                        List<String>  mspMethodNames = Lists.newArrayList();
                        if (methods != null) {
                            mspMethodNames = Arrays.asList(methods);
                        }
                        
                        //not sure where a static variable containing this method exists.. doing this for now
                        if (mspMethodNames.contains("ODEventNotification")) {
                            supportsOutage.add(mspVendor);
                        }
                    } catch (RemoteException e) {
                        log.warn("caught exception in initialize");
                    }
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
                    OutageJmsMessage outageJmsMessage = (OutageJmsMessage)object;
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
                OutageDetectionEvent[] odEvents = new OutageDetectionEvent[1];
                odEvents [0] = outageDetectionEvent;
                
                OA_ServerSoap_BindingStub port = MultispeakPortFactory.getOA_ServerPort(mspVendor, endpointUrl);
                if (port != null) {
                    String transactionId = String.valueOf(atomicLong.getAndIncrement());
                    ErrorObject[] errObjects = port.ODEventNotification(odEvents, transactionId);
                    if( errObjects != null && errObjects.length > 0) {
                        multispeakFuncs.logErrorObjects(endpointUrl, "ODEventNotification", errObjects);
                    } else {
                        outageEventLogService.mspMessageSentToVendor(outageJmsMessage.getSource(), 
                                                                     outageDetectionEvent.getOutageEventType().toString(), 
                                                                     outageDetectionEvent.getObjectID(), 
                                                                     outageDetectionEvent.getOutageDetectDeviceType().toString(),
                                                                     mspVendor.getCompanyName());
                    }
                } else {
                    log.error("Port not found for OA_Server (" + mspVendor.getCompanyName() + ")");
                    return;
                }
            } catch (RemoteException e) {
                log.error("TargetService: " + endpointUrl + " - initiateOutageDetection (" + mspVendor.getCompanyName() + ")");
                log.error("RemoteExceptionDetail: " + e.getMessage());
            }
        }
    }
    
    private OutageDetectionEvent getOutageDetectionEvent(OutageJmsMessage outageJmsMessage) {
        PaoIdentifier paoIdentifier = outageJmsMessage.getPaoIdentifier();
        
        OutageDetectionEvent outageDetectionEvent = new OutageDetectionEvent();
        outageDetectionEvent.setOutageEventType(outageMap.get(outageJmsMessage.getActionType()));
        
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(outageJmsMessage.getPointValueQualityHolder().getPointDataTimeStamp());
        outageDetectionEvent.setEventTime(cal);
        
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

        outageEventLogService.outageEventGenerated(outageDetectionEvent.getOutageEventType().getValue(), 
                                                   outageDetectionEvent.getEventTime().getTime(), 
                                                   outageDetectionEvent.getOutageDetectDeviceType().getValue(), 
                                                   outageDetectionEvent.getObjectID());
        
        return outageDetectionEvent;
    }
    
    private OutageDetectDeviceType getOutageDetectDeviceType(YukonPao paoIdentifier) {
        if (isMeter(paoIdentifier)) {
            return OutageDetectDeviceType.Meter;
        }
        return OutageDetectDeviceType.Other;
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