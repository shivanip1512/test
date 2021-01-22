package com.cannontech.watchdogs.impl;

import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.Source;

import org.apache.commons.compress.utils.Sets;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.DeviceGroupType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIGroupRequestType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ErrorFault;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ObjectFactory;
import com.cannontech.dr.itron.service.ItronCommunicationException;
import com.cannontech.dr.itron.service.impl.ItronEndpointManager;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdog.service.WatchdogWatcherService;

@Service
public class ItronServiceWatcher extends ServiceStatusWatchdogImpl {

    @Autowired private GlobalSettingDao settingDao;
    @Autowired private WatchdogWatcherService watcherService;
    private static final Set<String> faultCodesToIgnore = Sets.newHashSet("group.import.error.groupEmpty", "drm.group.not_found_with_name");
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
                try {
                    request = new ObjectFactory().createAddESIGroupRequest(requestType);
                    ItronEndpointManager.DEVICE.getTemplate(settingDao).marshalSendAndReceive(url, request);
                } catch (Exception ex) {
                    if (ex instanceof SoapFaultClientException) {
                        handleSoapFault((SoapFaultClientException) ex);
                    } else {
                        throw ex;
                    }
                }
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
     * Handle SOAP faults.
     */
    private void handleSoapFault(SoapFaultClientException e) {
        SoapFaultDetail soapFaultDetail = e.getSoapFault().getFaultDetail();
        soapFaultDetail.getDetailEntries().forEachRemaining(detail -> {
            SoapFaultDetailElement detailElementChild = soapFaultDetail.getDetailEntries().next();
            Source detailSource = detailElementChild.getSource();
            ErrorFault fault = (ErrorFault) ItronEndpointManager.DEVICE.getMarshaller().unmarshal(detailSource);
            fault.getErrors().forEach(error -> checkIfErrorShouldBeIgnored(error.getErrorCode(),
                    error.getErrorMessage(), faultCodesToIgnore, log));
            
        });
    }

    /**
     * Checks if error code is in faults to ignore list
     * 
     * @throws ItronCommunicationException - created from the first fault that is not ignored
     */
    private void checkIfErrorShouldBeIgnored(String errorCode, String errorMessage, Set<String> faultCodesToIgnore, Logger log)
            throws ItronCommunicationException {
        boolean ignore = faultCodesToIgnore.stream()
                                           .anyMatch(code -> code.equalsIgnoreCase(errorCode));
        if (ignore) {
            log.info("Ignored soap fault {}:{}", errorCode, errorMessage);
        } else {
            ItronCommunicationException exception = new ItronCommunicationException("Soap Fault: " + errorCode + ":" + errorMessage);
            log.error(exception);
            throw exception;
        }
    }
}
