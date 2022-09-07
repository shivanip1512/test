package com.cannontech.multispeak.deploy.service.impl.v4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v4.MultispeakMeterService;
import com.cannontech.multispeak.service.v4.OD_Server;

@Service("OD_ServerImplV4")
public class OD_ServerImpl implements OD_Server {
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakMeterService multispeakMeterService;

    private final Logger log = YukonLogManager.getLogger(OD_ServerImpl.class);
    private final static String[] methods = new String[] { "PingURL",
                                                           "GetMethods", 
                                                           "InitiateOutageDetectionEventRequest" };

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
        return multispeakFuncs.getMethods(MultispeakDefines.OD_Server_STR, Arrays.asList(methods));
    }

    @Override
    public List<ErrorObject> initiateOutageDetectionEventRequest(List<MeterID> meterIds,
            String responseURL, String transactionId) throws MultispeakWebServiceException {
        init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InitiateOutageDetectionEventRequest", vendor.getCompanyName());
        String actualResponseUrl = multispeakFuncs.getResponseUrl(vendor, responseURL, MultispeakDefines.OA_Server_STR);

        List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();
        if (!CollectionUtils.isEmpty(meterIds)) {
            errorObjects = multispeakMeterService.odEvent(vendor, meterIds, transactionId, actualResponseUrl);
        }
        multispeakFuncs.logErrorObjects(MultispeakDefines.OD_Server_STR, "InitiateOutageDetectionEventRequest", errorObjects);
        return errorObjects;
    }
}
