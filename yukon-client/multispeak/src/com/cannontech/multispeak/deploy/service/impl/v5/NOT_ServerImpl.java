package com.cannontech.multispeak.deploy.service.impl.v5;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.multispeak.ElectricMeter;
import com.cannontech.msp.beans.v5.multispeak.ElectricMeterExchange;
import com.cannontech.msp.beans.v5.multispeak.ObjectDeletion;
import com.cannontech.msp.beans.v5.multispeak.SCADAAnalog;
import com.cannontech.msp.beans.v5.multispeak.ServiceLocation;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v5.MspValidationService;
import com.cannontech.multispeak.service.v5.MultispeakLMService;
import com.cannontech.multispeak.service.v5.MultispeakMeterService;
import com.cannontech.multispeak.service.v5.NOT_Server;
import com.google.common.collect.Lists;

@Service("NOT_ServerImplV5")
public class NOT_ServerImpl implements NOT_Server {

    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakLMService multispeakLMService;
    @Autowired private MspValidationService mspValidationService;
    @Autowired private MultispeakMeterService multispeakMeterService;

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
        String[] methods = null;
        methods =
            new String[] { "PingURL", "GetMethods", "ServiceLocationsChangedNotification",
                "MetersCreatedNotification", "MetersInstalledNotification", "MetersUninstalledNotification",
                "MetersChangedNotification", "MetersExchangedNotification", "MetersDeletedNotification",
                "SCADAAnalogsChangedNotification" };
        return multispeakFuncs.getMethods(MultispeakDefines.NOT_Server_STR, Arrays.asList(methods));
    }

    @Override
    public List<ErrorObject> scadaAnalogsChangedNotification(List<SCADAAnalog> scadaAnalogs)
            throws MultispeakWebServiceException {
         init();
         LiteYukonUser user = multispeakFuncs.authenticateMsgHeader();
         multispeakFuncs.getMultispeakVendorFromHeader();
        
        // multispeakEventLogService.methodInvoked("ScadaAnalogsChangedNotification", vendor.getCompanyName());
        // - stop logging this, it's occurring every minute or more

        List<ErrorObject> errorObjects = Lists.newArrayList();
        for (SCADAAnalog scadaAnalog : scadaAnalogs) {
            ErrorObject errorObject = mspValidationService.isValidScadaAnalog(scadaAnalog);
            if (errorObject == null) {
                errorObject = multispeakLMService.writeAnalogPointData(scadaAnalog, user);
            }
            if (errorObject != null) {
                errorObjects.add(errorObject);
            }
        }
        return errorObjects;
    }

    @Override
    public List<ErrorObject> serviceLocationsChangedNotification(List<ServiceLocation> changedServiceLocations)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("ServiceLocationsChangedNotification", vendor.getCompanyName());

        List<ErrorObject> errorObject = multispeakMeterService.serviceLocationsChanged(vendor, changedServiceLocations);
        return errorObject;
    }
    
   
    @Override
    public List<ErrorObject> metersCreatedNotification(List<ElectricMeter> electricCreatedMeters) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("MetersCreatedNotification", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.metersCreated(vendor, electricCreatedMeters);
        return errorObject;
    }
 
    @Override
    public List<ErrorObject> metersInstalledNotification(List<ElectricMeter> electricInstalledMeters) throws MultispeakWebServiceException{
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("MetersInstalledNotification", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.metersInstalled(vendor, electricInstalledMeters);
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> metersUninstalledNotification(List<ElectricMeter> electricUninstalledMeters) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("MetersUninstalledNotification", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.metersUninstalled(vendor, electricUninstalledMeters);
        return errorObject;
    }

    @Override
    public List<ErrorObject> metersChangedNotification(List<ElectricMeter> electricChangedMeters) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("MetersChangedNotification", vendor.getCompanyName());
        
        List<ErrorObject> errorObject = multispeakMeterService.metersChanged(vendor, electricChangedMeters);
        return errorObject;
    }

    @Override
    public List<ErrorObject> metersExchangedNotification(List<ElectricMeterExchange> exchangeMeters) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("MetersExchangedNotification", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.metersExchanged(vendor, exchangeMeters);
        return errorObject;
    }

    @Override
    public List<ErrorObject> metersDeletedNotification(List<ObjectDeletion> electricMeters)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("MetersDeletedNotification", vendor.getCompanyName());
        List<ErrorObject> errorObject = multispeakMeterService.metersDeleted(vendor, electricMeters);
        return errorObject;
    }


}
