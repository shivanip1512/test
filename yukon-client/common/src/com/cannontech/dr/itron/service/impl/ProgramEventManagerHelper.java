package com.cannontech.dr.itron.service.impl;

import java.util.Set;

import javax.xml.transform.Source;

import org.apache.logging.log4j.Logger;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6.CancelHANLoadControlProgramEventOnDevicesRequest;
import com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6.ErrorFault;
import com.cannontech.dr.itron.service.impl.ItronCommunicationServiceImpl.Manager;

public class ProgramEventManagerHelper implements SoapFaultParser {
    
    public static CancelHANLoadControlProgramEventOnDevicesRequest buildOptOutRequest(long itronGroupId, String macAddress) {
        CancelHANLoadControlProgramEventOnDevicesRequest request = new CancelHANLoadControlProgramEventOnDevicesRequest();
        request.getDeviceGroupIDs().add(itronGroupId);
        request.getNicMacIDs().add(macAddress);
        return request;
    }
    
    @Override
    public void handleSoapFault(SoapFaultClientException e, Set<String> faultCodesToIgnore, Logger log) {
        SoapFaultDetail soapFaultDetail = e.getSoapFault().getFaultDetail();
        soapFaultDetail.getDetailEntries().forEachRemaining(detail -> {
            SoapFaultDetailElement detailElementChild =
                (SoapFaultDetailElement) soapFaultDetail.getDetailEntries().next();
            Source detailSource = detailElementChild.getSource();
            ErrorFault fault = (ErrorFault) Manager.PROGRAM_EVENT.getMarshaller().unmarshal(detailSource);
            log.debug(XmlUtils.getPrettyXml(fault));
            fault.getErrors().forEach(error -> checkIfErrorShouldBeIgnored(error.getErrorCode(),
                error.getErrorMessage(), faultCodesToIgnore, log));
        });
    }

    @Override
    public boolean isSupported(Manager manager) {
        return Manager.PROGRAM_EVENT == manager;
    }
}
