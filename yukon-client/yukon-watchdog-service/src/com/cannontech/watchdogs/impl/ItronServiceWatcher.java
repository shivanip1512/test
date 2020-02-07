package com.cannontech.watchdogs.impl;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.Source;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.DeviceGroupType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIGroupRequestType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ErrorFault;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ObjectFactory;
import com.cannontech.dr.itron.service.ItronCommunicationException;
import com.cannontech.dr.itron.service.impl.ItronEndpointManager;
import com.cannontech.dr.itron.simulator.model.ItronBasicError;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdog.service.WatchdogWatcherService;

@Service
public class ItronServiceWatcher extends ServiceStatusWatchdogImpl {

    @Autowired private GlobalSettingDao settingDao;
    @Autowired private WatchdogWatcherService watcherService;
    private static final String ITRON_SERVICE_READ_GROUP = "ITRON_PING_READ_GROUP";

    private static final Logger log = YukonLogManager.getLogger(ItronServiceWatcher.class);

    public ItronServiceWatcher(ConfigurationSource configSource) {
        super(configSource);
    }

    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus connectionStatus = getItronDeviceStatus();
        log.info("Status of Itron Service " + connectionStatus);
        return generateWarning(WatchdogWarningType.YUKON_ITRON_SERVICE, connectionStatus);
    }

    private ServiceStatus getItronDeviceStatus() {
        return createItronGroup();
    }

    private ServiceStatus createItronGroup() {

        try {
            String url = ItronEndpointManager.DEVICE.getUrl(settingDao);
            ESIGroupRequestType requestType = buildGroupEditRequest();
            JAXBElement<ESIGroupRequestType> request;
            try {
                request = new ObjectFactory().createEditESIGroupRequest(requestType);
                ItronEndpointManager.DEVICE.getTemplate(settingDao).marshalSendAndReceive(url, request);
            } catch (Exception e) {
                if (e instanceof SoapFaultClientException) {
                    handleSoapFault((SoapFaultClientException) e);
                }
                log.info("Editing Itron group failed, attempting to create group.");
                request = new ObjectFactory().createAddESIGroupRequest(requestType);
                ItronEndpointManager.DEVICE.getTemplate(settingDao).marshalSendAndReceive(url, request);
            }
        } catch (Exception ex) {
            log.error("Communication error:", ex);
            return ServiceStatus.STOPPED;
        }
        return ServiceStatus.RUNNING;
    }

    public static ESIGroupRequestType buildGroupEditRequest() {

        // Create outer ESIGroupRequest
        ESIGroupRequestType request = new ESIGroupRequestType();
        // Add GroupName to request
        request.setGroupName(ITRON_SERVICE_READ_GROUP);
        // Add GroupType to request
        request.setGroupType(DeviceGroupType.STATIC_GROUP);

        return request;
    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.ITRON;
    }

    @Override
    public boolean shouldRun() {
        return watcherService.isServiceRequired(getServiceName());
    }
    
    /**
     * Handle SOAP fault for Authorization Failure.
     */
    private void handleSoapFault(SoapFaultClientException e) {
        SoapFaultDetail soapFaultDetail = e.getSoapFault().getFaultDetail();
        soapFaultDetail.getDetailEntries().forEachRemaining(detail -> {
            SoapFaultDetailElement detailElementChild = soapFaultDetail.getDetailEntries().next();
            Source detailSource = detailElementChild.getSource();
            ErrorFault fault = (ErrorFault) ItronEndpointManager.DEVICE.getMarshaller().unmarshal(detailSource);

            fault.getErrors().forEach(errorType -> {
                if (errorType.getErrorCode().equalsIgnoreCase(ItronBasicError.AUTHORIZATION_FAILURE.toString())) {
                    throw new ItronCommunicationException("Authorization failure: " + errorType.getErrorCode() + ":" + errorType.getErrorMessage());
                }
            });
        });
    }
}
