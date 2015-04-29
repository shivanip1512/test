package com.cannontech.multispeak.deploy.service.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.MultispeakMeterService;
import com.cannontech.multispeak.service.OD_Server;

@Service
public class OD_ServerImpl implements OD_Server {

    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakMeterService multispeakMeterService;
    @Autowired private MultispeakFuncs multispeakFuncs;
    
    private void init() throws MultispeakWebServiceException {
        multispeakFuncs.init();
    }
    
    @Override
    public void pingURL() throws MultispeakWebServiceException {
        init();
    }
    
    @Override
    public List<String> getMethods() throws MultispeakWebServiceException {
        init();
        String[] methods = new String[] { "pingURL", "getMethods", "initiateOutageDetectionEventRequest" };
        return multispeakFuncs.getMethods(MultispeakDefines.OD_Server_STR, Arrays.asList(methods));
    }

    @Override
    public List<ErrorObject> initiateOutageDetectionEventRequest(List<String> meterNos, Calendar requestDate,
            String responseURL, String transactionID, Float expirationTime) throws MultispeakWebServiceException {
        init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateOutageDetectionEventRequest", vendor.getCompanyName());
        String actualResponseUrl = multispeakFuncs.getResponseUrl(vendor, responseURL, MultispeakDefines.OA_Server_STR);

        List<ErrorObject> errorObjects = multispeakMeterService.odEvent(vendor, meterNos, transactionID, actualResponseUrl);
        multispeakFuncs.logErrorObjects(MultispeakDefines.OD_Server_STR, "initiateOutageDetectionEventRequest",
            errorObjects);
        return errorObjects;
    }
}