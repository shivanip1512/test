package com.cannontech.multispeak.deploy.service.impl.v3;

import java.util.List;
import java.util.Map;

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
import com.cannontech.msp.beans.v3.Action;
import com.cannontech.msp.beans.v3.ArrayOfErrorObject;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.EventInstance;
import com.cannontech.msp.beans.v3.EventInstances;
import com.cannontech.msp.beans.v3.ExtensionsItem;
import com.cannontech.msp.beans.v3.ExtensionsItem.ExtensionsItemExtType;
import com.cannontech.msp.beans.v3.ExtensionsList;
import com.cannontech.msp.beans.v3.MeterEvent;
import com.cannontech.msp.beans.v3.MeterEventList;
import com.cannontech.msp.beans.v3.MeterEventNotification;
import com.cannontech.msp.beans.v3.MeterEventNotificationResponse;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.ServiceType;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.CBClient;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventDomain;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventDomainPart;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventIndex;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventType;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class MRServerDemandResetCallback implements DemandResetCallback {
    private final static Logger log = YukonLogManager.getLogger(MRServerDemandResetCallback.class);
    private final ObjectFactory objectFactory = YukonSpringHook.getBean("objectFactory", ObjectFactory.class);
    private final CBClient cbClient = YukonSpringHook.getBean("cbClient", CBClient.class);

    private final MspObjectDao mspObjectDao;

    private final List<ErrorObject> errors = Lists.newArrayList();
    private final MultispeakEventLogService multispeakEventLogService;
    private final MultispeakVendor vendor;
    private final Map<PaoIdentifier, String> meterNumbersByPaoId;
    private final String responseUrl;
    private final String transactionId;

    public MRServerDemandResetCallback(MspObjectDao mspObjectDao, MultispeakEventLogService multispeakEventLogService, 
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
    public void verified(SimpleDevice device, PointValueHolder value) {
        log.debug("device " + device + " passed verification");
        String meterNumber = meterNumbersByPaoId.get(device.getPaoIdentifier());
        MeterEvent meterEvent = objectFactory.createMeterEvent();
        meterEvent.setDomain(EndDeviceEventDomain.ELECTRICT_METER.code);
        meterEvent.setDomainPart(EndDeviceEventDomainPart.DEMAND.code);
        meterEvent.setType(EndDeviceEventType.COMMAND.code);
        meterEvent.setIndex(EndDeviceEventIndex.RESET.code);
        EventInstances eventInstances = objectFactory.createEventInstances();
        List<EventInstance> listEventInstance = eventInstances.getEventInstance();
        EventInstance eventInstance = objectFactory.createEventInstance();
        eventInstance.setServiceType(ServiceType.ELECTRIC);
        eventInstance.setMeterNo(meterNumber);
        eventInstance.setMeterEvent(meterEvent);
        listEventInstance.add(eventInstance);

        ExtensionsItem extensionItem = objectFactory.createExtensionsItem();
        extensionItem.setExtName("transactionId");
        extensionItem.setExtValue(transactionId);
        extensionItem.setExtType(ExtensionsItemExtType.STRING);

        ExtensionsList extensionsList = objectFactory.createExtensionsList();
        List<ExtensionsItem> extensionItems = extensionsList.getExtensionsItem();
        extensionItems.add(extensionItem);
        MeterEventList events = objectFactory.createMeterEventList();
        events.setObjectID(meterNumber);
        events.setVerb(Action.CHANGE);
        events.setExtensionsList(extensionsList);
        events.setEventinstances(eventInstances);

        try {
            MeterEventNotification meterEventNotification = objectFactory.createMeterEventNotification();
            meterEventNotification.setEvents(events);
            MeterEventNotificationResponse meterEventNotificationResponse = cbClient.meterEventNotification(vendor,
                                                                                                            responseUrl,
                                                                                                            meterEventNotification);

            ErrorObject[] errObjects = null;
            if (meterEventNotificationResponse != null) {
                ArrayOfErrorObject arrayOfErrorObject = meterEventNotificationResponse.getMeterEventNotificationResult();
                List<ErrorObject> errorObjects = arrayOfErrorObject.getErrorObject();
                if (errorObjects != null) {
                    errObjects = mspObjectDao.toErrorObject(errorObjects);
                }
            }
            multispeakEventLogService.notificationResponse("InitiateDemandReset",
                                                           transactionId,
                                                           meterNumber,
                                                           "Reset and Verified",
                                                           errObjects.length,
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
