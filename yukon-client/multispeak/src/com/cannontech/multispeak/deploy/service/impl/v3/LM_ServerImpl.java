package com.cannontech.multispeak.deploy.service.impl.v3;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.LoadManagementEvent;
import com.cannontech.msp.beans.v3.ScadaAnalog;
import com.cannontech.msp.beans.v3.SubstationLoadControlStatus;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.db.MspLoadControl;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v3.LM_Server;
import com.cannontech.multispeak.service.v3.MspValidationService;
import com.cannontech.multispeak.service.v3.MultispeakLMService;
import com.google.common.collect.Lists;

@Service
public class LM_ServerImpl implements LM_Server
{
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakLMService multispeakLMService;
    @Autowired private MspValidationService mspValidationService;
    
    private void init() throws MultispeakWebServiceException{
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
                "InitiateLoadManagementEvents"
                };
        return multispeakFuncs.getMethods(MultispeakDefines.LM_Server_STR, Arrays.asList(methods));
    }

    @Override
    public List<ErrorObject> SCADAAnalogChangedNotification(
            List<ScadaAnalog> scadaAnalogs) throws MultispeakWebServiceException {
        init();
        LiteYukonUser liteYukonUser = multispeakFuncs.authenticateMsgHeader();
        // multispeakEventLogService.methodInvoked("SCADAAnalogChangedNotification", vendor.getCompanyName()); - stop logging this, it's occurring every minute or more
        
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
    public List<SubstationLoadControlStatus> getAllSubstationLoadControlStatuses()
            throws MultispeakWebServiceException {
        init();
        multispeakFuncs.authenticateMsgHeader();
        return multispeakLMService.getActiveLoadControlStatus();
    } 
 
    @Override
    public ErrorObject initiateLoadManagementEvent(LoadManagementEvent theLMEvent)
            throws MultispeakWebServiceException {

        init();
        LiteYukonUser liteYukonUser = multispeakFuncs.authenticateMsgHeader();
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InitiateLoadManagementEvent", vendor.getCompanyName());

        ErrorObject errorObject = mspValidationService.isValidLoadManagementEvent(theLMEvent);
        if (errorObject == null) {
            MspLoadControl mspLoadControl = new MspLoadControl();
            List<ErrorObject> errorObject2 = multispeakLMService.buildMspLoadControl(theLMEvent, mspLoadControl, vendor);
            if (errorObject2.size() > 0) {
            	//We may have more than one error possibly, just return the first error.
            	return errorObject2.get(0);
            }
            errorObject = multispeakLMService.control(mspLoadControl, liteYukonUser);
        } 
        return errorObject;
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
                List<ErrorObject> errorObject2 =
                        multispeakLMService.buildMspLoadControl(loadManagementEvent, mspLoadControl, vendor);
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