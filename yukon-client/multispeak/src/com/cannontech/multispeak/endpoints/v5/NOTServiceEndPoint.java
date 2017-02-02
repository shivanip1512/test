package com.cannontech.multispeak.endpoints.v5;

import java.util.List;

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
import com.cannontech.msp.beans.v5.not_server.*;
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

            multispeakFuncs.addErrorObjectsInResponseHeader(not_server.metersChangedNotification(electricMeters));
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

            multispeakFuncs.addErrorObjectsInResponseHeader(not_server.metersExchangedNotification(exchangeMeters));
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

            multispeakFuncs.addErrorObjectsInResponseHeader(not_server.metersInstalledNotification(electricMeters));
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

        multispeakFuncs.addErrorObjectsInResponseHeader(not_server.serviceLocationsChangedNotification(serviceLocation));

        return serviceLocationsChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "AccountsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload AccountsChangedNotificationResponse accountsChangedNotification(
            @RequestPayload AccountsChangedNotification accountsChangedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "AccountsCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload AccountsCreatedNotificationResponse accountsCreatedNotification(
            @RequestPayload AccountsCreatedNotification accountsCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "AccountsDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload AccountsDeletedNotificationResponse accountsDeletedNotification(
            @RequestPayload AccountsDeletedNotification accountsDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "AssetsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload AssetsChangedNotificationResponse assetsChangedNotification(
            @RequestPayload AssetsChangedNotification assetsChangedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "AssetsCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload AssetsCreatedNotificationResponse assetsCreatedNotification(
            @RequestPayload AssetsCreatedNotification assetsCreatedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "AssetsDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload AssetsDeletedNotificationResponse assetsDeletedNotification(
            @RequestPayload AssetsDeletedNotification assetsDeletedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "AttachmentsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload AttachmentsChangedNotificationResponse attachmentsChangedNotification(
            @RequestPayload AttachmentsChangedNotification attachmentsChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "AttachmentsDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload AttachmentsDeletedNotificationResponse attachmentsDeletedNotification(
            @RequestPayload AttachmentsDeletedNotification attachmentsDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "CDDevicesChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload CDDevicesChangedNotificationResponse cdDevicesChangedNotification(
            @RequestPayload CDDevicesChangedNotification cdDevicesChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "CDDevicesCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload CDDevicesCreatedNotificationResponse cdDevicesCreatedNotification(
            @RequestPayload CDDevicesCreatedNotification cdDevicesCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "CDDevicesDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload CDDevicesDeletedNotificationResponse cdDevicesDeletedNotification(
            @RequestPayload CDDevicesDeletedNotification cdDevicesDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "CDDevicesExchangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload CDDevicesExchangedNotificationResponse cdDevicesExchangedNotification(
            @RequestPayload CDDevicesExchangedNotification cdDevicesExchangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "CDDevicesInstalledNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload CDDevicesInstalledNotificationResponse cdDevicesInstalledNotification(
            @RequestPayload CDDevicesInstalledNotification cdDevicesInstalledNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "CDDevicesUninstalledNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload CDDevicesUninstalledNotificationResponse cdDevicesUninstalledNotification(
            @RequestPayload CDDevicesUninstalledNotification cdDevicesUninstalledNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "CDStatesChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload CDStatesChangedNotificationResponse cdStatesChangedNotification(
            @RequestPayload CDStatesChangedNotification cdStatesChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "CDStatesNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload CDStatesNotificationResponse cdStatesNotification(
            @RequestPayload CDStatesNotification cdStatesNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "ChargeableDevicesChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ChargeableDevicesChangedNotificationResponse chargeableDevicesChangedNotification(
            @RequestPayload ChargeableDevicesChangedNotification chargeableDevicesChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "ChargeableDevicesCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ChargeableDevicesCreatedNotificationResponse chargeableDevicesCreatedNotification(
            @RequestPayload ChargeableDevicesCreatedNotification chargeableDevicesCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "ChargeableDevicesDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ChargeableDevicesDeletedNotificationResponse chargeableDevicesDeletedNotification(
            @RequestPayload ChargeableDevicesDeletedNotification chargeableDevicesDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "ClearancesAcknowledgedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ClearancesAcknowledgedNotificationResponse clearancesAcknowledgedNotification(
            @RequestPayload ClearancesAcknowledgedNotification clearancesAcknowledgedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "ClearanceTagsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ClearanceTagsChangedNotificationResponse clearanceTagsChangedNotification(
            @RequestPayload ClearanceTagsChangedNotification clearanceTagsChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "ClearanceTagsCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ClearanceTagsCreatedNotificationResponse clearanceTagsCreatedNotification(
            @RequestPayload ClearanceTagsCreatedNotification clearanceTagsCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "ClearanceTagsDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ClearanceTagsDeletedNotificationResponse clearanceTagsDeletedNotification(
            @RequestPayload ClearanceTagsDeletedNotification clearanceTagsDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "ClearanceTagsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ClearanceTagsNotificationResponse clearanceTagsNotification(
            @RequestPayload ClearanceTagsNotification clearanceTagsNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "ConnectDisconnectEventsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ConnectDisconnectEventsNotificationResponse connectDisconnectEventsNotification(
            @RequestPayload ConnectDisconnectEventsNotification connectDisconnectEventsNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "ConnectivityChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ConnectivityChangedNotificationResponse connectivityChangedNotification(
            @RequestPayload ConnectivityChangedNotification connectivityChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "ConnectivityCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ConnectivityCreatedNotificationResponse connectivityCreatedNotification(
            @RequestPayload ConnectivityCreatedNotification connectivityCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "ConnectivityDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ConnectivityDeletedNotificationResponse connectivityDeletedNotification(
            @RequestPayload ConnectivityDeletedNotification connectivityDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "ContactInfoChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ContactInfoChangedNotificationResponse contactInfoChangedNotification(
            @RequestPayload ContactInfoChangedNotification contactInfoChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "ContactRequestStatusesNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ContactRequestStatusesNotificationResponse contactRequestStatusesNotification(
            @RequestPayload ContactRequestStatusesNotification contactRequestStatusesNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "ControlActionNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ControlActionNotificationResponse controlActionNotification(
            @RequestPayload ControlActionNotification controlActionNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "CPRsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload CPRsChangedNotificationResponse cprsChangedNotification(
            @RequestPayload CPRsChangedNotification cprsChangedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "CPRsCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload CPRsCreatedNotificationResponse cprsCreatedNotification(
            @RequestPayload CPRsCreatedNotification cprsCreatedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "CPRsDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload CPRsDeletedNotificationResponse cprsDeletedNotification(
            @RequestPayload CPRsDeletedNotification cprsDeletedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "CustomersChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload CustomersChangedNotificationResponse customersChangedNotification(
            @RequestPayload CustomersChangedNotification customersChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "CustomersCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload CustomersCreatedNotificationResponse customersCreatedNotification(
            @RequestPayload CustomersCreatedNotification customersCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "CustomersDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload CustomersDeletedNotificationResponse customersDeletedNotification(
            @RequestPayload CustomersDeletedNotification customersDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "DemandResetsCancelledNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload DemandResetsCancelledNotificationResponse demandResetsCancelledNotification(
            @RequestPayload DemandResetsCancelledNotification demandResetsCancelledNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "DemandResetsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload DemandResetsNotificationResponse demandResetsNotification(
            @RequestPayload DemandResetsNotification demandResetsNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "DemandResponseEventStatusesNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload DemandResponseEventStatusesNotificationResponse demandResponseEventStatusesNotification(
            @RequestPayload DemandResponseEventStatusesNotification demandResponseEventStatusesNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "DemandResponseSetupNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload DemandResponseSetupNotificationResponse demandResponseSetupNotification(
            @RequestPayload DemandResponseSetupNotification demandResponseSetupNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "DesignedWorkOrdersNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload DesignedWorkOrdersNotificationResponse designedWorkOrdersNotification(
            @RequestPayload DesignedWorkOrdersNotification designedWorkOrdersNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "DesignSubmissionsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload DesignSubmissionsNotificationResponse designSubmissionsNotification(
            @RequestPayload DesignSubmissionsNotification designSubmissionsNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "DomainsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload DomainsChangedNotificationResponse domainsChangedNotification(
            @RequestPayload DomainsChangedNotification domainsChangedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "DRProgramEnrollmentsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload DRProgramEnrollmentsNotificationResponse drProgramEnrollmentsNotification(
            @RequestPayload DRProgramEnrollmentsNotification drProgramEnrollmentsNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "DRProgramsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload DRProgramsChangedNotificationResponse drProgramsChangedNotification(
            @RequestPayload DRProgramsChangedNotification drProgramsChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "DRProgramsCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload DRProgramsCreatedNotificationResponse drProgramsCreatedNotification(
            @RequestPayload DRProgramsCreatedNotification drProgramsCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "DRProgramsRescindedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload DRProgramsRescindedNotificationResponse drProgramsRescindedNotification(
            @RequestPayload DRProgramsRescindedNotification drProgramsRescindedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "DRProgramUnenrollmentsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload DRProgramUnenrollmentsNotificationResponse drProgramUnenrollmentsNotification(
            @RequestPayload DRProgramUnenrollmentsNotification drProgramUnenrollmentsNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "EndDeviceEventResetsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload EndDeviceEventResetsNotificationResponse endDeviceEventResetsNotification(
            @RequestPayload EndDeviceEventResetsNotification endDeviceEventResetsNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "EndDeviceEventsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload EndDeviceEventsNotificationResponse endDeviceEventsNotification(
            @RequestPayload EndDeviceEventsNotification endDeviceEventsNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "EndDeviceStateChangesNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload EndDeviceStateChangesNotificationResponse endDeviceStateChangesNotification(
            @RequestPayload EndDeviceStateChangesNotification endDeviceStateChangesNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "EndDeviceStatesNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload EndDeviceStatesNotificationResponse endDeviceStatesNotification(
            @RequestPayload EndDeviceStatesNotification endDeviceStatesNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "EquipmentChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload EquipmentChangedNotificationResponse equipmentChangedNotification(
            @RequestPayload EquipmentChangedNotification equipmentChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "EquipmentCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload EquipmentCreatedNotificationResponse equipmentCreatedNotification(
            @RequestPayload EquipmentCreatedNotification equipmentCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "EquipmentDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload EquipmentDeletedNotificationResponse equipmentDeletedNotification(
            @RequestPayload EquipmentDeletedNotification equipmentDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "FormattedBlockNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload FormattedBlockNotificationResponse formattedBlockNotification(
            @RequestPayload FormattedBlockNotification formattedBlockNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }



    @PayloadRoot(localPart = "IncidentEvaluationNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload IncidentEvaluationNotificationResponse incidentEvaluationNotification(
            @RequestPayload IncidentEvaluationNotification incidentEvaluationNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "IntervalDataNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload IntervalDataNotificationResponse intervalDataNotification(
            @RequestPayload IntervalDataNotification intervalDataNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "LaborCategoriesChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LaborCategoriesChangedNotificationResponse laborCategoriesChangedNotification(
            @RequestPayload LaborCategoriesChangedNotification laborCategoriesChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "LaborCategoriesCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LaborCategoriesCreatedNotificationResponse laborCategoriesCreatedNotification(
            @RequestPayload LaborCategoriesCreatedNotification laborCategoriesCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "LaborCategoriesDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LaborCategoriesDeletedNotificationResponse laborCategoriesDeletedNotification(
            @RequestPayload LaborCategoriesDeletedNotification laborCategoriesDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "LMDevicesChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LMDevicesChangedNotificationResponse lmDevicesChangedNotification(
            @RequestPayload LMDevicesChangedNotification lmDevicesChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "LMDevicesCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LMDevicesCreatedNotificationResponse lmDevicesCreatedNotification(
            @RequestPayload LMDevicesCreatedNotification lmDevicesCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "LMDevicesDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LMDevicesDeletedNotificationResponse lmDevicesDeletedNotification(
            @RequestPayload LMDevicesDeletedNotification lmDevicesDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "LMDevicesExchangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LMDevicesExchangedNotificationResponse lmDevicesExchangedNotification(
            @RequestPayload LMDevicesExchangedNotification lmDevicesExchangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "LMDevicesInstalledNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LMDevicesInstalledNotificationResponse lmDevicesInstalledNotification(
            @RequestPayload LMDevicesInstalledNotification lmDevicesInstalledNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "LMDevicesUninstalledNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LMDevicesUninstalledNotificationResponse lmDevicesUninstalledNotification(
            @RequestPayload LMDevicesUninstalledNotification lmDevicesUninstalledNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "LoadCycleTiersChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LoadCycleTiersChangedNotificationResponse loadCycleTiersChangedNotification(
            @RequestPayload LoadCycleTiersChangedNotification loadCycleTiersChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "LocationTrackingLogsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LocationTrackingLogsNotificationResponse locationTrackingLogsNotification(
            @RequestPayload LocationTrackingLogsNotification locationTrackingLogsNotification)
            throws MultispeakWebServiceException {

        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "LocationTrackingPositionsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LocationTrackingPositionsNotificationResponse locationTrackingPositionsNotification(
            @RequestPayload LocationTrackingPositionsNotification locationTrackingPositionsNotification)
            throws MultispeakWebServiceException {

        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "LTSupportedResourcesChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LTSupportedResourcesChangedNotificationResponse ltSupportedResourcesChangedNotification(
            @RequestPayload LTSupportedResourcesChangedNotification ltSupportedResourcesChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "LTSupportedResourcesCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LTSupportedResourcesCreatedNotificationResponse ltSupportedResourcesCreatedNotification(
            @RequestPayload LTSupportedResourcesCreatedNotification ltSupportedResourcesCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "LTSupportedResourcesDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LTSupportedResourcesDeletedNotificationResponse ltSupportedResourcesDeletedNotification(
            @RequestPayload LTSupportedResourcesDeletedNotification ltSupportedResourcesDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "ManufacturerSpecificCommandsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ManufacturerSpecificCommandsNotificationResponse manufacturerSpecificCommandsNotification(
            @RequestPayload ManufacturerSpecificCommandsNotification manufacturerSpecificCommandsNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "MaterialItemsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload MaterialItemsChangedNotificationResponse materialItemsChangedNotification(
            @RequestPayload MaterialItemsChangedNotification materialItemsChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "MaterialItemsCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload MaterialItemsCreatedNotificationResponse materialItemsCreatedNotification(
            @RequestPayload MaterialItemsCreatedNotification materialItemsCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "MaterialItemsDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload MaterialItemsDeletedNotificationResponse materialItemsDeletedNotification(
            @RequestPayload MaterialItemsDeletedNotification materialItemsDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "MaterialManagementAssembliesChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload MaterialManagementAssembliesChangedNotificationResponse materialManagementAssembliesChangedNotification(
            @RequestPayload MaterialManagementAssembliesChangedNotification materialManagementAssembliesChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "MaterialManagementAssembliesCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload MaterialManagementAssembliesCreatedNotificationResponse materialManagementAssembliesCreatedNotification(
            @RequestPayload MaterialManagementAssembliesCreatedNotification materialManagementAssembliesCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "MaterialManagementAssembliesDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload MaterialManagementAssembliesDeletedNotificationResponse materialManagementAssembliesDeletedNotification(
            @RequestPayload MaterialManagementAssembliesDeletedNotification materialManagementAssembliesDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "MeterConfigurationsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload MeterConfigurationsNotificationResponse meterConfigurationsNotification(
            @RequestPayload MeterConfigurationsNotification meterConfigurationsNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "MeterReadingsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload MeterReadingsNotificationResponse meterReadingsNotification(
            @RequestPayload MeterReadingsNotification meterReadingsNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "MeterTopologyNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload MeterTopologyNotificationResponse meterTopologyNotification(
            @RequestPayload MeterTopologyNotification meterTopologyNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ObjectRefChangesNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ObjectRefChangesNotificationResponse objectRefChangesNotification(
            @RequestPayload ObjectRefChangesNotification objectRefChangesNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "OutageReasonChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload OutageReasonChangedNotificationResponse outageReasonChangedNotification(
            @RequestPayload OutageReasonChangedNotification outageReasonChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "OutageReasonListChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload OutageReasonListChangedNotificationResponse outageReasonListChangedNotification(
            @RequestPayload OutageReasonListChangedNotification outageReasonListChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "OutagesChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload OutagesChangedNotificationResponse outagesChangedNotification(
            @RequestPayload OutagesChangedNotification outagesChangedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "OutagesCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload OutagesCreatedNotificationResponse outagesCreatedNotification(
            @RequestPayload OutagesCreatedNotification outagesCreatedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "OutagesDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload OutagesDeletedNotificationResponse outagesDeletedNotification(
            @RequestPayload OutagesDeletedNotification outagesDeletedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "OutagesMergedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload OutagesMergedNotificationResponse outagesMergedNotification(
            @RequestPayload OutagesMergedNotification outagesMergedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "OutagesSplitNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload OutagesSplitNotificationResponse outagesSplitNotification(
            @RequestPayload OutagesSplitNotification outagesSplitNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "OutageStatusesChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload OutageStatusesChangedNotificationResponse outageStatusesChangedNotification(
            @RequestPayload OutageStatusesChangedNotification outageStatusesChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "OutageWorkUpdateNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload OutageWorkUpdateNotificationResponse outageWorkUpdateNotification(
            @RequestPayload OutageWorkUpdateNotification outageWorkUpdateNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "PANCommissioningNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PANCommissioningNotificationResponse panCommissioningNotification(
            @RequestPayload PANCommissioningNotification panCommissioningNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "PANPricingNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PANPricingNotificationResponse panPricingNotification(
            @RequestPayload PANPricingNotification panPricingNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "PANRegistrationNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PANRegistrationNotificationResponse panRegistrationNotification(
            @RequestPayload PANRegistrationNotification panRegistrationNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }



    @PayloadRoot(localPart = "PMAcknowledgementNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PMAcknowledgementNotificationResponse pmAcknowledgementNotification(
            @RequestPayload PMAcknowledgementNotification pmAcknowledgementNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "PolesChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PolesChangedNotificationResponse polesChangedNotification(
            @RequestPayload PolesChangedNotification polesChangedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "PolesCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PolesCreatedNotificationResponse polesCreatedNotification(
            @RequestPayload PolesCreatedNotification polesCreatedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "PolesDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PolesDeletedNotificationResponse polesDeletedNotification(
            @RequestPayload PolesDeletedNotification polesDeletedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "PowerFactorManagementEventsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PowerFactorManagementEventsNotificationResponse powerFactorManagementEventsNotification(
            @RequestPayload PowerFactorManagementEventsNotification powerFactorManagementEventsNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "PowerMonitorsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PowerMonitorsNotificationResponse powerMonitorsNotification(
            @RequestPayload PowerMonitorsNotification powerMonitorsNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "PremisesDisplayCapabilitySettingsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PremisesDisplayCapabilitySettingsNotificationResponse premisesDisplayCapabilitySettingsNotification(
            @RequestPayload PremisesDisplayCapabilitySettingsNotification premisesDisplayCapabilitySettingsNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "PremisesDisplayMessagesConfirmationsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PremisesDisplayMessagesConfirmationsNotificationResponse premisesDisplayMessagesConfirmationsNotification(
            @RequestPayload PremisesDisplayMessagesConfirmationsNotification premisesDisplayMessagesConfirmationsNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "PremisesDisplayMessagesStatusesNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PremisesDisplayMessagesStatusesNotificationResponse premisesDisplayMessagesStatusesNotification(
            @RequestPayload PremisesDisplayMessagesStatusesNotification premisesDisplayMessagesStatusesNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "PremisesDisplaysChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PremisesDisplaysChangedNotificationResponse premisesDisplaysChangedNotification(
            @RequestPayload PremisesDisplaysChangedNotification premisesDisplaysChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "PremisesDisplaysCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PremisesDisplaysCreatedNotificationResponse premisesDisplaysCreatedNotification(
            @RequestPayload PremisesDisplaysCreatedNotification premisesDisplaysCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "PremisesDisplaysDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PremisesDisplaysDeletedNotificationResponse premisesDisplaysDeletedNotification(
            @RequestPayload PremisesDisplaysDeletedNotification premisesDisplaysDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "PremisesDisplaysExchangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PremisesDisplaysExchangedNotificationResponse premisesDisplaysExchangedNotification(
            @RequestPayload PremisesDisplaysExchangedNotification premisesDisplaysExchangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "PremisesDisplaysInstalledNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PremisesDisplaysInstalledNotificationResponse premisesDisplaysInstalledNotification(
            @RequestPayload PremisesDisplaysInstalledNotification premisesDisplaysInstalledNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "PremisesDisplaysUninstalledNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PremisesDisplaysUninstalledNotificationResponse PremisesDisplaysUninstalledNotification(
            @RequestPayload PremisesDisplaysUninstalledNotification premisesDisplaysUninstalledNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "PricingEventsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PricingEventsNotificationResponse pricingEventsNotification(
            @RequestPayload PricingEventsNotification pricingEventsNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "PricingTiersChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PricingTiersChangedNotificationResponse pricingTiersChangedNotification(
            @RequestPayload PricingTiersChangedNotification pricingTiersChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ReadingScheduleResultsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ReadingScheduleResultsNotificationResponse readingScheduleResultsNotification(
            @RequestPayload ReadingScheduleResultsNotification readingScheduleResultsNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ResourcesChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ResourcesChangedNotificationResponse resourcesChangedNotification(
            @RequestPayload ResourcesChangedNotification resourcesChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ResourcesCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ResourcesCreatedNotificationResponse resourcesCreatedNotification(
            @RequestPayload ResourcesCreatedNotification resourcesCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ResourcesDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ResourcesDeletedNotificationResponse resourcesDeletedNotification(
            @RequestPayload ResourcesDeletedNotification resourcesDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }



    @PayloadRoot(localPart = "SCADAAnalogsChangedNotificationForPower", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SCADAAnalogsChangedNotificationForPowerResponse scadaAnalogsChangedNotificationForPower(
            @RequestPayload SCADAAnalogsChangedNotificationForPower scadaAnalogsChangedNotificationForPower)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAAnalogsChangedNotificationForVoltage", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SCADAAnalogsChangedNotificationForVoltageResponse scadaAnalogsChangedNotificationForVoltage(
            @RequestPayload SCADAAnalogsChangedNotificationForVoltage scadaAnalogsChangedNotificationForVoltage)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAPointsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SCADAPointsChangedNotificationResponse scadaPointsChangedNotification(
            @RequestPayload SCADAPointsChangedNotification scadaPointsChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAPointsCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SCADAPointsCreatedNotificationResponse scadaPointsCreatedNotification(
            @RequestPayload SCADAPointsCreatedNotification scadaPointsCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAPointsDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SCADAPointsDeletedNotificationResponse scadaPointsDeletedNotification(
            @RequestPayload SCADAPointsDeletedNotification scadaPointsDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAStatusesChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SCADAStatusesChangedNotificationResponse scadaStatusesChangedNotification(
            @RequestPayload SCADAStatusesChangedNotification scadaStatusesChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SecurityLightsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SecurityLightsChangedNotificationResponse securityLightsChangedNotification(
            @RequestPayload SecurityLightsChangedNotification securityLightsChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SecurityLightsCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SecurityLightsCreatedNotificationResponse securityLightsCreatedNotification(
            @RequestPayload SecurityLightsCreatedNotification securityLightsCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SecurityLightsDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SecurityLightsDeletedNotificationResponse securityLightsDeletedNotification(
            @RequestPayload SecurityLightsDeletedNotification securityLightsDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ServiceLocationsCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ServiceLocationsCreatedNotificationResponse serviceLocationsCreatedNotification(
            @RequestPayload ServiceLocationsCreatedNotification serviceLocationsCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ServiceLocationsDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ServiceLocationsDeletedNotificationResponse serviceLocationsDeletedNotification(
            @RequestPayload ServiceLocationsDeletedNotification serviceLocationsDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ServiceOrdersChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ServiceOrdersChangedNotificationResponse serviceOrdersChangedNotification(
            @RequestPayload ServiceOrdersChangedNotification serviceOrdersChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ServiceOrdersCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ServiceOrdersCreatedNotificationResponse serviceOrdersCreatedNotification(
            @RequestPayload ServiceOrdersCreatedNotification serviceOrdersCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ServiceOrdersDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ServiceOrdersDeletedNotificationResponse serviceOrdersDeletedNotification(
            @RequestPayload ServiceOrdersDeletedNotification serviceOrdersDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ServicePointOutageDurationsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ServicePointOutageDurationsNotificationResponse servicePointOutageDurationsNotification(
            @RequestPayload ServicePointOutageDurationsNotification servicePointOutageDurationsNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ServicePointsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ServicePointsChangedNotificationResponse servicePointsChangedNotification(
            @RequestPayload ServicePointsChangedNotification servicePointsChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ServicePointsCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ServicePointsCreatedNotificationResponse servicePointsCreatedNotification(
            @RequestPayload ServicePointsCreatedNotification servicePointsCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ServicePointsDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ServicePointsDeletedNotificationResponse servicePointsDeletedNotification(
            @RequestPayload ServicePointsDeletedNotification servicePointsDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SpatialFeaturesContainerNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SpatialFeaturesContainerNotificationResponse spatialFeaturesContainerNotification(
            @RequestPayload SpatialFeaturesContainerNotification spatialFeaturesContainerNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "StreetLightsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload StreetLightsChangedNotificationResponse streetLightsChangedNotification(
            @RequestPayload StreetLightsChangedNotification streetLightsChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "StreetLightsCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload StreetLightsCreatedNotificationResponse streetLightsCreatedNotification(
            @RequestPayload StreetLightsCreatedNotification streetLightsCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "StreetLightsDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload StreetLightsDeletedNotificationResponse streetLightsDeletedNotification(
            @RequestPayload StreetLightsDeletedNotification streetLightsDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SurgeArrestorsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SurgeArrestorsChangedNotificationResponse surgeArrestorsChangedNotification(
            @RequestPayload SurgeArrestorsChangedNotification surgeArrestorsChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SurgeArrestorsCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SurgeArrestorsCreatedNotificationResponse surgeArrestorsCreatedNotification(
            @RequestPayload SurgeArrestorsCreatedNotification surgeArrestorsCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SurgeArrestorsDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SurgeArrestorsDeletedNotificationResponse surgeArrestorsDeletedNotification(
            @RequestPayload SurgeArrestorsDeletedNotification surgeArrestorsDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SwitchingOrdersChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SwitchingOrdersChangedNotificationResponse switchingOrdersChangedNotification(
            @RequestPayload SwitchingOrdersChangedNotification switchingOrdersChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SwitchingOrdersCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SwitchingOrdersCreatedNotificationResponse switchingOrdersCreatedNotification(
            @RequestPayload SwitchingOrdersCreatedNotification switchingOrdersCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SwitchingOrdersDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SwitchingOrdersDeletedNotificationResponse switchingOrdersDeletedNotification(
            @RequestPayload SwitchingOrdersDeletedNotification switchingOrdersDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SystemStateNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SystemStateNotificationResponse systemStateNotification(
            @RequestPayload SystemStateNotification systemStateNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "TariffsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload TariffsChangedNotificationResponse tariffsChangedNotification(
            @RequestPayload TariffsChangedNotification tariffsChangedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "TemperatureTiersChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload TemperatureTiersChangedNotificationResponse temperatureTiersChangedNotification(
            @RequestPayload TemperatureTiersChangedNotification temperatureTiersChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ThermostatConfigurationsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ThermostatConfigurationsNotificationResponse thermostatConfigurationsNotification(
            @RequestPayload ThermostatConfigurationsNotification thermostatConfigurationsNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ThermostatsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ThermostatsChangedNotificationResponse thermostatsChangedNotification(
            @RequestPayload ThermostatsChangedNotification thermostatsChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ThermostatScheduleConfirmationsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ThermostatScheduleConfirmationsNotificationResponse thermostatScheduleConfirmationsNotification(
            @RequestPayload ThermostatScheduleConfirmationsNotification thermostatScheduleConfirmationsNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ThermostatSchedulesNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ThermostatSchedulesNotificationResponse thermostatSchedulesNotification(
            @RequestPayload ThermostatSchedulesNotification thermostatSchedulesNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ThermostatsCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ThermostatsCreatedNotificationResponse thermostatsCreatedNotification(
            @RequestPayload ThermostatsCreatedNotification thermostatsCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ThermostatsDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ThermostatsDeletedNotificationResponse thermostatsDeletedNotification(
            @RequestPayload ThermostatsDeletedNotification thermostatsDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ThresholdEventsNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ThresholdEventsNotificationResponse thresholdEventsNotification(
            @RequestPayload ThresholdEventsNotification thresholdEventsNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "TopologyElementsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload TopologyElementsChangedNotificationResponse topologyElementsChangedNotification(
            @RequestPayload TopologyElementsChangedNotification topologyElementsChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "TrafficLightsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload TrafficLightsChangedNotificationResponse trafficLightsChangedNotification(
            @RequestPayload TrafficLightsChangedNotification trafficLightsChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "TransformerBanksChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload TransformerBanksChangedNotificationResponse transformerBanksChangedNotification(
            @RequestPayload TransformerBanksChangedNotification transformerBanksChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "TransformerBanksCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload TransformerBanksCreatedNotificationResponse transformerBanksCreatedNotification(
            @RequestPayload TransformerBanksCreatedNotification transformerBanksCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "TransformerBanksDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload TransformerBanksDeletedNotificationResponse transformerBanksDeletedNotification(
            @RequestPayload TransformerBanksDeletedNotification transformerBanksDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "UsagesChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload UsagesChangedNotificationResponse usagesChangedNotification(
            @RequestPayload UsagesChangedNotification usagesChangedNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "VoiceRecordingsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload VoiceRecordingsChangedNotificationResponse voiceRecordingsChangedNotification(
            @RequestPayload VoiceRecordingsChangedNotification voiceRecordingsChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "VoiceRecordingsCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload VoiceRecordingsCreatedNotificationResponse voiceRecordingsCreatedNotification(
            @RequestPayload VoiceRecordingsCreatedNotification voiceRecordingsCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "VoiceRecordingsDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload VoiceRecordingsDeletedNotificationResponse voiceRecordingsDeletedNotification(
            @RequestPayload VoiceRecordingsDeletedNotification voiceRecordingsDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "WorkAssignmentsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload WorkAssignmentsChangedNotificationResponse workAssignmentsChangedNotification(
            @RequestPayload WorkAssignmentsChangedNotification workAssignmentsChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "WorkItemsChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload WorkItemsChangedNotificationResponse workItemsChangedNotification(
            @RequestPayload WorkItemsChangedNotification workItemsChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "WorkItemsCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload WorkItemsCreatedNotificationResponse workItemsCreatedNotification(
            @RequestPayload WorkItemsCreatedNotification workItemsCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "WorkItemsDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload WorkItemsDeletedNotificationResponse workItemsDeletedNotification(
            @RequestPayload WorkItemsDeletedNotification workItemsDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "WorkItemsUnscheduledNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload WorkItemsUnscheduledNotificationResponse workItemsUnscheduledNotification(
            @RequestPayload WorkItemsUnscheduledNotification workItemsUnscheduledNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "WorkOrdersChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload WorkOrdersChangedNotificationResponse workOrdersChangedNotification(
            @RequestPayload WorkOrdersChangedNotification workOrdersChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "WorkOrdersCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload WorkOrdersCreatedNotificationResponse workOrdersCreatedNotification(
            @RequestPayload WorkOrdersCreatedNotification workOrdersCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "WorkOrdersDeletedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload WorkOrdersDeletedNotificationResponse workOrdersDeletedNotification(
            @RequestPayload WorkOrdersDeletedNotification workOrdersDeletedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "WorkProgressNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload WorkProgressNotificationResponse workProgressNotification(
            @RequestPayload WorkProgressNotification workProgressNotification) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "WorkRequestResultNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload WorkRequestResultNotificationResponse workRequestResultNotification(
            @RequestPayload WorkRequestResultNotification workRequestResultNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "WorkScheduleChangedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload WorkScheduleChangedNotificationResponse workScheduleChangedNotification(
            @RequestPayload WorkScheduleChangedNotification workScheduleChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "WorkScheduleCreatedNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload WorkScheduleCreatedNotificationResponse workScheduleCreatedNotification(
            @RequestPayload WorkScheduleCreatedNotification workScheduleCreatedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "WorkUnassignmentNotification", namespace = NOT_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload WorkUnassignmentNotificationResponse workUnassignmentNotification(
            @RequestPayload WorkUnassignmentNotification workUnassignmentNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

}
