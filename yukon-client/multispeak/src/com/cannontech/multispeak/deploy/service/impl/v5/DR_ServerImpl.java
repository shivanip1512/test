package com.cannontech.multispeak.deploy.service.impl.v5;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.multispeak.LoadManagementEvent;
import com.cannontech.msp.beans.v5.multispeak.SubstationLoadControlStatus;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.client.v5.UserDetailHolder;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.db.v5.MspLoadControl;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v5.DR_Server;
import com.cannontech.multispeak.service.v5.MspValidationService;
import com.cannontech.multispeak.service.v5.MultispeakLMService;
import com.google.common.collect.Lists;

@Service("DR_ServerImplV5")
public class DR_ServerImpl implements DR_Server {
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakLMService multispeakLMService;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MspValidationService mspValidationService;

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
        String[] methods =
            new String[] { "pingURL", "getMethods", "getAllSubstationLoadControlStatuses",
                "initiateLoadManagementEvents" };
        return multispeakFuncs.getMethods(MultispeakDefines.DR_Server_STR, Arrays.asList(methods));
    }

    @Override
    public List<SubstationLoadControlStatus> getAllSubstationLoadControlStatuses() throws MultispeakWebServiceException {
        init();
        return multispeakLMService.getActiveLoadControlStatus();
    }

    @Override
    public List<ErrorObject> initiateLoadManagementEvents(List<LoadManagementEvent> theLMEvents)
            throws MultispeakWebServiceException {
        init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("initiateLoadManagementEvents", vendor.getCompanyName());

        List<ErrorObject> errorObjects = Lists.newArrayList();

        for (LoadManagementEvent loadManagementEvent : theLMEvents) {
            ErrorObject errorObject = mspValidationService.isValidLoadManagementEvent(loadManagementEvent);
            if (errorObject == null) {
                MspLoadControl mspLoadControl = new MspLoadControl();
                // If errorObjects are returned, we still continue on and
                // control what we can.
                List<ErrorObject> errorObject2 =
                    multispeakLMService.buildMspLoadControl(loadManagementEvent, mspLoadControl, vendor);
                for (ErrorObject err : errorObject2) {
                    errorObjects.add(err);
                }
                errorObject = multispeakLMService.control(mspLoadControl, UserDetailHolder.getYukonUser());
            }
            if (errorObject != null) {
                errorObjects.add(errorObject);
            }
        }
        return errorObjects;
    }

}