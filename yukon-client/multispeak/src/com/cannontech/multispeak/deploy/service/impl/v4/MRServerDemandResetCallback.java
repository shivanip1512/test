package com.cannontech.multispeak.deploy.service.impl.v4;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.msp.beans.v4.Action;
import com.cannontech.msp.beans.v4.ArrayOfErrorObject;
import com.cannontech.msp.beans.v4.ArrayOfEventInstance;
import com.cannontech.msp.beans.v4.ArrayOfExtensionsItem;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.EventInstance;
import com.cannontech.msp.beans.v4.ExpirationTime;
import com.cannontech.msp.beans.v4.ExtType;
import com.cannontech.msp.beans.v4.ExtValue;
import com.cannontech.msp.beans.v4.ExtensionsItem;
import com.cannontech.msp.beans.v4.MeterEvent;
import com.cannontech.msp.beans.v4.MeterEventList;
import com.cannontech.msp.beans.v4.MeterEventNotification;
import com.cannontech.msp.beans.v4.MeterEventNotificationResponse;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.ObjectFactory;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.v4.CBClient;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventDomain;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventDomainPart;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventIndex;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventType;
import com.cannontech.multispeak.dao.v4.MspObjectDao;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class MRServerDemandResetCallback implements DemandResetCallback {
    private final static Logger log = YukonLogManager.getLogger(MRServerDemandResetCallback.class);
    private final ObjectFactory objectFactory = YukonSpringHook.getBean("objectFactory4", ObjectFactory.class);
    private final CBClient cbClient = YukonSpringHook.getBean("cbClientV4", CBClient.class);

    private final MspObjectDao mspObjectDao;

    private final List<ErrorObject> errors = Lists.newArrayList();
    private final MultispeakEventLogService multispeakEventLogService;
    private final MultispeakVendor vendor;
    private final Map<PaoIdentifier, String> meterNumbersByPaoId;
    private final String responseUrl;
    private final String transactionId;
    private ExpirationTime expirationTime;

    public MRServerDemandResetCallback(MspObjectDao mspObjectDao, MultispeakEventLogService multispeakEventLogService,
            MultispeakVendor vendor, Map<PaoIdentifier, String> meterNumbersByPaoId,
            String responseURL, String transactionId, ExpirationTime expirationTime) {
        this.multispeakEventLogService = multispeakEventLogService;
        this.mspObjectDao = mspObjectDao;
        this.vendor = vendor;
        this.meterNumbersByPaoId = meterNumbersByPaoId;
        this.responseUrl = responseURL;
        this.transactionId = transactionId;
        this.expirationTime = expirationTime;
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

            errors.add(mspObjectDao.getErrorObject(meterNumber, error.getPorter(), 
                                                   "MeterID",
                                                   "initiateDemandReset", 
                                                   vendor.getCompanyName()));
        }
    }

    @Override
    public void verified(SimpleDevice device, PointValueHolder value) {

        log.debug("device " + device + " passed verification");
        PaoIdentifier paoIdentifier = device.getPaoIdentifier();
        String meterNumber = meterNumbersByPaoId.get(paoIdentifier);
        MeterEvent meterEvent = objectFactory.createMeterEvent();
        
        //TODO check on EndDeviceEventDomain values standardisation
        if (paoIdentifier.getPaoType().isGasMeter()) {
            meterEvent.setDomain(EndDeviceEventDomain.GAS_METER.code);
        } else if (paoIdentifier.getPaoType().isWaterMeter()) {
            meterEvent.setDomain(EndDeviceEventDomain.WATER_METER.code);
        } else {
            meterEvent.setDomain(EndDeviceEventDomain.ELECTRICT_METER.code);
        }
        meterEvent.setDomainPart(EndDeviceEventDomainPart.DEMAND.code);
        meterEvent.setType(EndDeviceEventType.COMMAND.code);
        meterEvent.setIndex(EndDeviceEventIndex.RESET.code);

        ArrayOfEventInstance arrOfEventInstances = objectFactory.createArrayOfEventInstance();
        List<EventInstance> listEventInstance = arrOfEventInstances != null ? arrOfEventInstances.getEventInstance() : null;

        EventInstance eventInstance = objectFactory.createEventInstance();

        MeterID meterId = new MeterID();
        meterId.setMeterNo(meterNumber);
        eventInstance.setMeterID(meterId);
        eventInstance.setMeterEvent(meterEvent);
        listEventInstance.add(eventInstance);

        ExtensionsItem extensionItem = objectFactory.createExtensionsItem();
        extensionItem.setExtName("expirationTime");
        ExtValue extValue = new ExtValue();
        extValue.setValue(String.valueOf(expirationTime.getValue()));
        extensionItem.setExtValue(extValue);
        extensionItem.setExtType(ExtType.STRING);

        ArrayOfExtensionsItem extensionsList = objectFactory.createArrayOfExtensionsItem();
        List<ExtensionsItem> extensionItems = extensionsList.getExtensionsItem();
        extensionItems.add(extensionItem);
        MeterEventList events = objectFactory.createMeterEventList();
        events.setObjectID(meterNumber);
        events.setVerb(Action.CHANGE);
        events.setExtensionsList(extensionsList);
        events.setEventInstances(arrOfEventInstances);

        try {
            MeterEventNotification meterEventNotification = objectFactory.createMeterEventNotification();
            meterEventNotification.setEvents(events);
            meterEventNotification.setTransactionID(transactionId);
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
            if (errObjects != null) {
                multispeakEventLogService.notificationResponse("InitiateDemandReset",
                                                                transactionId,
                                                                meterNumber,
                                                                "Reset and Verified",
                                                                errObjects.length,
                                                                responseUrl);
            }
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
