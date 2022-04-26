package com.cannontech.multispeak.deploy.service.impl.v4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.LoadManagementEvent;
import com.cannontech.msp.beans.v4.ScadaAnalog;
import com.cannontech.msp.beans.v4.SubstationLoadControlStatus;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.db.v4.MspLoadControl;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v4.DR_Server;
import com.cannontech.multispeak.service.v4.MspValidationService;
import com.cannontech.multispeak.service.v4.MultispeakLMService;
import com.google.common.collect.Lists;

@Service("DR_ServerImplV4")
public class DR_ServerImpl implements DR_Server {
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakLMService multispeakLMService;
    @Autowired private MspValidationService mspValidationService;
    @Autowired private MultispeakEventLogService multispeakEventLogService;

    private final Logger log = YukonLogManager.getLogger(DR_ServerImpl.class);

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
        String[] methods = new String[] {
                "PingURL",
                "GetMethods",
                "SCADAAnalogChangedNotification",
                "GetAllSubstationLoadControlStatuses",
                "InitiateLoadManagementEvent",
                "InitiateLoadManagementEvents"};

        return multispeakFuncs.getMethods(MultispeakDefines.DR_Server_STR, Arrays.asList(methods));
    }

    @Override
    public List<ErrorObject> SCADAAnalogChangedNotification(List<ScadaAnalog> scadaAnalogs)
            throws MultispeakWebServiceException {
        init();
        multispeakFuncs.getMultispeakVendorFromHeader();
        LiteYukonUser liteYukonUser = multispeakFuncs.authenticateMsgHeader();

        List<ErrorObject> errorObjects = Lists.newArrayList();
        for (ScadaAnalog scadaAnalog : scadaAnalogs) {
            ErrorObject errorObject = mspValidationService.isValidScadaAnalog(scadaAnalog);
            if (errorObject == null) {
                errorObject = multispeakLMService.writeAnalogPointData(scadaAnalog, liteYukonUser);
            }
            if (errorObject != null) {
                errorObjects.add(errorObject);
            }
        }
        return errorObjects;
    }
    
    @Override
    public List<SubstationLoadControlStatus> getAllSubstationLoadControlStatuses() throws MultispeakWebServiceException {
        init();
        multispeakFuncs.authenticateMsgHeader();
        multispeakFuncs.getMultispeakVendorFromHeader();
        return multispeakLMService.getActiveLoadControlStatus();
    }

    @Override
    public List<ErrorObject> initiateLoadManagementEvent(LoadManagementEvent theLMEvent)
            throws MultispeakWebServiceException {

        init();
        LiteYukonUser liteYukonUser = multispeakFuncs.authenticateMsgHeader();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InitiateLoadManagementEvent", vendor.getCompanyName());

        List<ErrorObject> errorObjectList = new ArrayList<>();
        ErrorObject errorObject = mspValidationService.isValidLoadManagementEvent(theLMEvent);
        if (errorObject == null) {
            MspLoadControl mspLoadControl = new MspLoadControl();
            List<ErrorObject> errorObjectList2 = multispeakLMService.buildMspLoadControl(theLMEvent, mspLoadControl, vendor);
            if (!errorObjectList2.isEmpty()) {
                errorObjectList.addAll(errorObjectList2);
            }
            errorObject = multispeakLMService.control(mspLoadControl, liteYukonUser);
        }
        if (errorObject != null) {
            errorObjectList.add(errorObject);
        }
        return errorObjectList;
    }

    @Override
    public List<ErrorObject> initiateLoadManagementEvents(List<LoadManagementEvent> theLMEvents)
            throws MultispeakWebServiceException {
        init();
        LiteYukonUser liteYukonUser = multispeakFuncs.authenticateMsgHeader();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InitiateLoadManagementEvents", vendor.getCompanyName());

        List<ErrorObject> errorObjects = Lists.newArrayList();

        for (LoadManagementEvent loadManagementEvent : theLMEvents) {
            ErrorObject errorObject = mspValidationService.isValidLoadManagementEvent(loadManagementEvent);
            if (errorObject == null) {
                MspLoadControl mspLoadControl = new MspLoadControl();
                // If errorObjects are returned, we still continue on and control what we can.
                List<ErrorObject> errorObject2 = multispeakLMService.buildMspLoadControl(loadManagementEvent, mspLoadControl,
                        vendor);
                for (ErrorObject err : errorObject2) {
                    errorObjects.add(err);
                }
                errorObject = multispeakLMService.control(mspLoadControl, liteYukonUser);
            }
            if (errorObject != null) {
                errorObjects.add(errorObject);
            }
        }
        return errorObjects;
    }
}
