package com.cannontech.multispeak.deploy.service.impl;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventDomain;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventDomainPart;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventIndex;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventType;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.deploy.service.Action;
import com.cannontech.multispeak.deploy.service.CB_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.EventInstance;
import com.cannontech.multispeak.deploy.service.ExtensionsItem;
import com.cannontech.multispeak.deploy.service.ExtensionsItemExtType;
import com.cannontech.multispeak.deploy.service.MeterEvent;
import com.cannontech.multispeak.deploy.service.MeterEventList;
import com.cannontech.multispeak.deploy.service.ServiceType;
import com.google.common.collect.Lists;

public class MRServerDemandResetCallback implements DemandResetCallback {
    private final static Logger log = YukonLogManager.getLogger(MRServerDemandResetCallback.class);

    private final MspObjectDao mspObjectDao;

    private final List<ErrorObject> errors = Lists.newArrayList();
    private final MultispeakVendor vendor;
    private final Map<PaoIdentifier, String> meterNumbersByPaoId;
    private final CB_ServerSoap_PortType port;
    private final String transactionId;

    public MRServerDemandResetCallback(MspObjectDao mspObjectDao, MultispeakVendor vendor,
                                       Map<PaoIdentifier, String> meterNumbersByPaoId,
                                       String responseURL, String transactionId) {
        this.mspObjectDao = mspObjectDao;
        this.vendor = vendor;
        this.meterNumbersByPaoId = meterNumbersByPaoId;

        // Using CB objects even though the responseURL could be for CB or MDM.  Per Stacey, CB
        // objects should also work either way.  (The API is identical.)
        port = MultispeakPortFactory.getCB_ServerPort(vendor, responseURL);
        if (port == null) {
            log.error("Port not found for (" + vendor.getCompanyName() + ") with URL "
                    + responseURL);
            errors.add(mspObjectDao.getErrorObject(transactionId, "could not get responseURL port",
                                                   "Meter", "initiateDemandResponse", null));
        }

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
    public void verified(SimpleDevice device) {
        log.debug("device " + device + " passed verification");
        if (port == null) {
            return;
        }
        String meterNumber = meterNumbersByPaoId.get(device.getPaoIdentifier());
        MeterEvent meterEvent = new MeterEvent();
        meterEvent.setDomain(EndDeviceEventDomain.ELECTRICT_METER.code);
        meterEvent.setDomainPart(EndDeviceEventDomainPart.DEMAND.code);
        meterEvent.setType(EndDeviceEventType.COMMAND.code);
        meterEvent.setIndex(EndDeviceEventIndex.RESET.code);
        EventInstance eventInstance = new EventInstance(null, meterNumber,
                                                        ServiceType.Electric,
                                                        null, meterEvent);
        ExtensionsItem extensionItem = new ExtensionsItem("transactionId", transactionId,
            ExtensionsItemExtType.value40);
        MeterEventList events = new MeterEventList(meterNumber, Action.Change, null, null,
            null, null, null, new ExtensionsItem[] { extensionItem },
            new EventInstance[] { eventInstance });
        try {
            port.meterEventNotification(events);
        } catch (RemoteException re) {
            log.error("error pushing verification notice", re);
        }
    }

    @Override
    public void failed(SimpleDevice device) {
        log.error("device " + device + " failed verification");
    }

    @Override
    public void cannotVerify(SimpleDevice device, String reason) {
        log.error("device " + device + " cannot be verified because \"" + reason + '"');
    }
}
