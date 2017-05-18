package com.cannontech.multispeak.endpoints.v5;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v5.commonarrays.ArrayOfString;
import com.cannontech.msp.beans.v5.multispeak.ElectricMeter;
import com.cannontech.msp.beans.v5.multispeak.ElectricMeterExchange;
import com.cannontech.msp.beans.v5.multispeak.ObjectDeletion;
import com.cannontech.msp.beans.v5.multispeak.SCADAAnalog;
import com.cannontech.msp.beans.v5.multispeak.ServiceLocation;
import com.cannontech.msp.beans.v5.not_server.GetMethods;
import com.cannontech.msp.beans.v5.not_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.not_server.MetersChangedNotification;
import com.cannontech.msp.beans.v5.not_server.MetersChangedNotificationResponse;
import com.cannontech.msp.beans.v5.not_server.MetersCreatedNotification;
import com.cannontech.msp.beans.v5.not_server.MetersCreatedNotificationResponse;
import com.cannontech.msp.beans.v5.not_server.MetersDeletedNotification;
import com.cannontech.msp.beans.v5.not_server.MetersDeletedNotificationResponse;
import com.cannontech.msp.beans.v5.not_server.MetersExchangedNotification;
import com.cannontech.msp.beans.v5.not_server.MetersExchangedNotificationResponse;
import com.cannontech.msp.beans.v5.not_server.MetersInstalledNotification;
import com.cannontech.msp.beans.v5.not_server.MetersInstalledNotificationResponse;
import com.cannontech.msp.beans.v5.not_server.MetersUninstalledNotification;
import com.cannontech.msp.beans.v5.not_server.MetersUninstalledNotificationResponse;
import com.cannontech.msp.beans.v5.not_server.ObjectFactory;
import com.cannontech.msp.beans.v5.not_server.PingURL;
import com.cannontech.msp.beans.v5.not_server.PingURLResponse;
import com.cannontech.msp.beans.v5.not_server.SCADAAnalogsChangedNotification;
import com.cannontech.msp.beans.v5.not_server.SCADAAnalogsChangedNotificationResponse;
import com.cannontech.msp.beans.v5.not_server.ServiceLocationsChangedNotification;
import com.cannontech.msp.beans.v5.not_server.ServiceLocationsChangedNotificationResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v5.NOT_Server;

@Endpoint("NOTServiceEndPointV5")
@RequestMapping("/multispeak/v5/NOT_Server")
public class NOTServiceEndPoint {

    @Autowired private ObjectFactory notObjectFactory;
    @Autowired private com.cannontech.msp.beans.v5.commonarrays.ObjectFactory commonObjectFactory;
    @Autowired private NOT_Server not_server;
    @Autowired private MultispeakFuncs multispeakFuncs;
    private static final String NOT_V5_ENDPOINT_NAMESPACE = MultispeakDefines.NAMESPACE_v5 + "/wsdl/NOT_Server";

    @PayloadRoot(localPart = "PingURL", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PingURLResponse pingURL(@RequestPayload PingURL pingURL)
            throws MultispeakWebServiceException {
        PingURLResponse response = notObjectFactory.createPingURLResponse();
        not_server.pingURL();
        return response;

    }

    @PayloadRoot(localPart = "GetMethods", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods)
            throws MultispeakWebServiceException {
        GetMethodsResponse response = notObjectFactory.createGetMethodsResponse();

        List<String> methodNames = not_server.getMethods();
        ArrayOfString methods = commonObjectFactory.createArrayOfString();
        methods.getTheString().addAll(methodNames);
        response.setArrayOfString(methods);
        return response;

    }

    @PayloadRoot(localPart = "SCADAAnalogsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SCADAAnalogsChangedNotificationResponse scadaAnalogsChangedNotification(
            @RequestPayload SCADAAnalogsChangedNotification scadaAnalogsChangedNotification)
            throws MultispeakWebServiceException {

        SCADAAnalogsChangedNotificationResponse resonse =
            notObjectFactory.createSCADAAnalogsChangedNotificationResponse();
        List<SCADAAnalog> scadaAnalogs =
            (null != scadaAnalogsChangedNotification.getArrayOfSCADAAnalog())
                ? scadaAnalogsChangedNotification.getArrayOfSCADAAnalog().getSCADAAnalog() : null;

        multispeakFuncs.addErrorObjectsInResponseHeader(not_server.scadaAnalogsChangedNotification(scadaAnalogs));
        
        return resonse;
    }

    @PayloadRoot(localPart = "MetersChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload MetersChangedNotificationResponse metersChangedNotification(
            @RequestPayload MetersChangedNotification metersChangedNotification) throws MultispeakWebServiceException {

        MetersChangedNotificationResponse response = notObjectFactory.createMetersChangedNotificationResponse();

        if (null != metersChangedNotification.getMeters()) {

            List<ElectricMeter> electricMeters =
                (null != metersChangedNotification.getMeters().getElectricMeters())
                    ? metersChangedNotification.getMeters().getElectricMeters().getElectricMeter() : null;
            if (CollectionUtils.isNotEmpty(electricMeters)) {
                multispeakFuncs.addErrorObjectsInResponseHeader(not_server.metersChangedNotification(electricMeters));
            }
        }
        return response;
    }

    @PayloadRoot(localPart = "MetersCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload MetersCreatedNotificationResponse metersCreatedNotification(
            @RequestPayload MetersCreatedNotification metersCreatedNotification) throws MultispeakWebServiceException {
        MetersCreatedNotificationResponse response = notObjectFactory.createMetersCreatedNotificationResponse();

        if (null != metersCreatedNotification.getMeters()) {

            List<ElectricMeter> electricMeters =
                (null != metersCreatedNotification.getMeters().getElectricMeters())
                    ? metersCreatedNotification.getMeters().getElectricMeters().getElectricMeter() : null;
            multispeakFuncs.addErrorObjectsInResponseHeader(not_server.metersCreatedNotification(electricMeters));
            
        }
        return response;
    }

    @PayloadRoot(localPart = "MetersDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload MetersDeletedNotificationResponse metersDeletedNotification(
            @RequestPayload MetersDeletedNotification metersDeletedNotification) throws MultispeakWebServiceException {

        MetersDeletedNotificationResponse response = notObjectFactory.createMetersDeletedNotificationResponse();

        List<ObjectDeletion> electricMeters =
            (null != metersDeletedNotification.getArrayOfObjectDeletion())
                ? metersDeletedNotification.getArrayOfObjectDeletion().getObjectDeletion() : null;
        multispeakFuncs.addErrorObjectsInResponseHeader(not_server.metersDeletedNotification(electricMeters));
        return response;
    }

    @PayloadRoot(localPart = "MetersExchangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload MetersExchangedNotificationResponse metersExchangedNotification(
            @RequestPayload MetersExchangedNotification metersExchangedNotification)
            throws MultispeakWebServiceException {

        MetersExchangedNotificationResponse response = notObjectFactory.createMetersExchangedNotificationResponse();

        if (null != metersExchangedNotification.getMeterExchanges()) {

            List<ElectricMeterExchange> exchangeMeters =
                (null == metersExchangedNotification.getMeterExchanges())
                    ? null: (null != metersExchangedNotification.getMeterExchanges().getElectricMeterExchanges())
                        ? metersExchangedNotification.getMeterExchanges().getElectricMeterExchanges().getElectricMeterExchange() : null;

            if (CollectionUtils.isNotEmpty(exchangeMeters)) {
                multispeakFuncs.addErrorObjectsInResponseHeader(not_server.metersExchangedNotification(exchangeMeters));
            }
        }
        return response;
    }

    @PayloadRoot(localPart = "MetersInstalledNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload MetersInstalledNotificationResponse metersInstalledNotification(
            @RequestPayload MetersInstalledNotification metersInstalledNotification)
            throws MultispeakWebServiceException {
        MetersInstalledNotificationResponse response = notObjectFactory.createMetersInstalledNotificationResponse();

        if (null != metersInstalledNotification.getMeters()) {

            List<ElectricMeter> electricMeters =
                (null != metersInstalledNotification.getMeters().getElectricMeters())
                    ? metersInstalledNotification.getMeters().getElectricMeters().getElectricMeter() : null;

            if (CollectionUtils.isNotEmpty(electricMeters)) {
                multispeakFuncs.addErrorObjectsInResponseHeader(not_server.metersInstalledNotification(electricMeters));
            }
        }
        return response;
    }

    @PayloadRoot(localPart = "MetersUninstalledNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload MetersUninstalledNotificationResponse metersUninstalledNotification(
            @RequestPayload MetersUninstalledNotification metersUninstalledNotification)
            throws MultispeakWebServiceException {
        MetersUninstalledNotificationResponse response = notObjectFactory.createMetersUninstalledNotificationResponse();

        if (null != metersUninstalledNotification.getMeters()) {

            List<ElectricMeter> electricMeters =
                (null != metersUninstalledNotification.getMeters().getElectricMeters())
                    ? metersUninstalledNotification.getMeters().getElectricMeters().getElectricMeter() : null;

            multispeakFuncs.addErrorObjectsInResponseHeader(not_server.metersUninstalledNotification(electricMeters));
        }
        return response;
    }

    @PayloadRoot(localPart = "ServiceLocationsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ServiceLocationsChangedNotificationResponse serviceLocationsChangedNotification(
            @RequestPayload ServiceLocationsChangedNotification serviceLocationsChangedNotification)
            throws MultispeakWebServiceException {

        ServiceLocationsChangedNotificationResponse serviceLocationsChangedNotificationResponse =
            notObjectFactory.createServiceLocationsChangedNotificationResponse();
        List<ServiceLocation> serviceLocation =
            (null != serviceLocationsChangedNotification.getArrayOfServiceLocation())
                ? serviceLocationsChangedNotification.getArrayOfServiceLocation().getServiceLocation() : null;
        if (CollectionUtils.isNotEmpty(serviceLocation)) {
            multispeakFuncs.addErrorObjectsInResponseHeader(not_server.serviceLocationsChangedNotification(serviceLocation));
        }
        return serviceLocationsChangedNotificationResponse;
    }
}
