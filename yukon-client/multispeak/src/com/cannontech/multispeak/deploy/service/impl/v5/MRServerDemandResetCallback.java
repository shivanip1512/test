package com.cannontech.multispeak.deploy.service.impl.v5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfEndDeviceEventList;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.commontypes.ObjectRef;
import com.cannontech.msp.beans.v5.enumerations.Action;
import com.cannontech.msp.beans.v5.multispeak.EndDeviceEvent;
import com.cannontech.msp.beans.v5.multispeak.EndDeviceEventList;
import com.cannontech.msp.beans.v5.multispeak.EndDeviceEventType;
import com.cannontech.msp.beans.v5.multispeak.EndDeviceEventTypeItem;
import com.cannontech.msp.beans.v5.multispeak.EndDeviceEventTypeList;
import com.cannontech.msp.beans.v5.multispeak.EndDeviceEvents;
import com.cannontech.msp.beans.v5.multispeak.ReadingQualityCode;
import com.cannontech.msp.beans.v5.multispeak.ReadingValue;
import com.cannontech.msp.beans.v5.multispeak.ReadingValues;
import com.cannontech.msp.beans.v5.not_server.EndDeviceEventsNotification;
import com.cannontech.msp.beans.v5.not_server.ObjectFactory;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.v5.NOTClient;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventDomain;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventDomainPart;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventIndex;
import com.cannontech.multispeak.dao.v5.MspObjectDao;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class MRServerDemandResetCallback implements DemandResetCallback {
    private final static Logger log = YukonLogManager.getLogger(MRServerDemandResetCallback.class);
    private final ObjectFactory objectFactory = YukonSpringHook.getBean("notObjectFactory", ObjectFactory.class);
    private final NOTClient notClient = YukonSpringHook.getBean("notClientV5", NOTClient.class);
    private final MultispeakFuncs multispeakFuncs = YukonSpringHook.getBean("multispeakFuncsV5", MultispeakFuncs.class);
    private final MspObjectDao mspObjectDao;
    
    private final MultispeakEventLogService multispeakEventLogService;
    private final List<ErrorObject> errors = Lists.newArrayList();
    private final MultispeakVendor vendor;
    private final Map<PaoIdentifier, String> meterNumbersByPaoId;
    private final String responseUrl;
    private final String transactionId;

    public MRServerDemandResetCallback(MspObjectDao mspObjectDao,MultispeakEventLogService multispeakEventLogService,
                                       MultispeakVendor vendor, Map<PaoIdentifier, String> meterNumbersByPaoId,
                                       String responseURL, String transactionId) {
        this.multispeakEventLogService = multispeakEventLogService;
        this.mspObjectDao = mspObjectDao;
        this.vendor = vendor;
        this.meterNumbersByPaoId = meterNumbersByPaoId;
        this.responseUrl = responseURL;
        this.transactionId = transactionId;
    }

    public List<ErrorObject> getErrors() {
        return errors;
    }

    @Override
    public void initiated(Results results) {
        Map<SimpleDevice, SpecificDeviceErrorDescription> drErrors = results.getErrors();
        if (log.isDebugEnabled()) {
            log.debug("demand reset had " + results.getNumErrors() + " errors");
        }
        for (YukonPao device : drErrors.keySet()) {
            SpecificDeviceErrorDescription error = drErrors.get(device);
            String meterNumber = meterNumbersByPaoId.get(device.getPaoIdentifier());

            errors.add(mspObjectDao.getErrorObject(meterNumber, error.getPorter(), "Meter",
                                                   "initiateDemandReset",
                                                   vendor.getCompanyName()));
        }
    }

    @Override
    public void verified(SimpleDevice device, PointValueHolder pointValue) {
        log.debug("device " + device + " passed verification");
        String meterNumber = meterNumbersByPaoId.get(device.getPaoIdentifier());
        
        ArrayOfEndDeviceEventList arrayOfEndDeviceEventList = new ArrayOfEndDeviceEventList();
        List<EndDeviceEventList> endDeviceEventList = arrayOfEndDeviceEventList.getEndDeviceEventList();
        
        EndDeviceEventList deviceEventList = new EndDeviceEventList();
        EndDeviceEvents deviceEvents = new EndDeviceEvents();

        List<EndDeviceEvent> endDeviceEvent = deviceEvents.getEndDeviceEvent();
        EndDeviceEvent deviceEvent = new EndDeviceEvent();
        
        ReadingValues readingValues = new ReadingValues();
        List<ReadingValue> readingValue = readingValues.getReadingValue();
        ReadingValue value = new ReadingValue();
        ReadingQualityCode readingQualityCode = new ReadingQualityCode();
        readingQualityCode.setCodeIndex(EndDeviceEventIndex.RESET.code);
        value.setReadingQualityCode(readingQualityCode);
        readingValue.add(value);
        deviceEvent.setAssociatedReadingValues(readingValues);
        
        ObjectRef objectRef = new ObjectRef();
        objectRef.setPrimaryIdentifierValue(meterNumber);
        objectRef.setSystemName(MultispeakDefines.MSP_APPNAME_YUKON);
        objectRef.setRegisteredName(MultispeakDefines.REGISTERED_NAME);
        objectRef.setNoun(new QName("http://www.multispeak.org/V5.0/commonTypes", "MeterID", "com"));
        
        deviceEvent.setDeviceReference(objectRef);

        endDeviceEvent.add(deviceEvent);
        deviceEventList.setEndDeviceEvents(deviceEvents);

        EndDeviceEventTypeList deviceEventTypeList = new EndDeviceEventTypeList();
        List<EndDeviceEventTypeItem> endDeviceEventTypeItem = deviceEventTypeList.getEndDeviceEventTypeItem();
        EndDeviceEventTypeItem deviceEventTypeItem = new EndDeviceEventTypeItem();
        EndDeviceEventType endDeviceEventType = new EndDeviceEventType();
        endDeviceEventType.setEndDeviceDomain(EndDeviceEventDomain.ELECTRICT_METER.code);
        endDeviceEventType.setEndDeviceSubdomain(EndDeviceEventDomainPart.DEMAND.code);
        endDeviceEventType.setEndDeviceType(com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventType.COMMAND.code);
        endDeviceEventType.setEventOrAction(Action.CHANGE.value());
        deviceEventTypeItem.setEndDeviceEventType(endDeviceEventType);
        endDeviceEventTypeItem.add(deviceEventTypeItem);
        deviceEventList.setEndDeviceEventTypeList(deviceEventTypeList);
        deviceEventList.setReferableID(meterNumber);
        endDeviceEventList.add(deviceEventList);
        try {
            EndDeviceEventsNotification deviceEventsNotification = objectFactory.createEndDeviceEventsNotification();
            deviceEventsNotification.setArrayOfEndDeviceEventList(arrayOfEndDeviceEventList);
            deviceEventsNotification.setTransactionID(transactionId);
            notClient.endDeviceEventsNotification(vendor, responseUrl, deviceEventsNotification);
            List<ErrorObject> errObjects = new ArrayList<>();
            errObjects = multispeakFuncs.getErrorObjectsFromResponse();
            if (errObjects != null) {
                mspObjectDao.toErrorObject(errObjects);
            }

            multispeakEventLogService.notificationResponse("InitiateDemandReset", 
                                                            transactionId, 
                                                            meterNumber,
                                                           "Reset and Verified", 
                                                            CollectionUtils.size(errObjects), 
                                                            responseUrl);

        } catch (MultispeakWebServiceClientException re) {
            log.error("error pushing verification notice", re);
        }
    }

    @Override
    public void failed(SimpleDevice device, SpecificDeviceErrorDescription error) {
        log.error("device " + device + " failed verification because \"" + error.getDescription() + '"');
    }

    @Override
    public void cannotVerify(SimpleDevice device, SpecificDeviceErrorDescription error) {
        log.error("device " + device + " cannot be verified because \"" + error.getDescription() + '"');
    }
}
