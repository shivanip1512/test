package com.cannontech.dr.itron.service.impl;

import java.util.List;
import java.util.Set;

import javax.xml.transform.Source;

import org.apache.logging.log4j.Logger;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.ErrorFault;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.ServicePointEnrollmentType;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.SetServicePointEnrollmentRequest;

public class ProgramManagerHelper implements SoapFaultParser {
    
    public static SetServicePointEnrollmentRequest buildEnrollmentRequest(String accountNumber, List<Long> itronProgramIds) {
        SetServicePointEnrollmentRequest servicePointRequest = new SetServicePointEnrollmentRequest();
        ServicePointEnrollmentType type = new ServicePointEnrollmentType();
        type.setUtilServicePointID(accountNumber);
        type.getProgramIDs().addAll(itronProgramIds);
        servicePointRequest.getEnrolls().add(type);
        return servicePointRequest;
    }
    
    @Override
    public void handleSoapFault(SoapFaultClientException e, Set<String> faultCodesToIgnore, Logger log) {
        SoapFaultDetail soapFaultDetail = e.getSoapFault().getFaultDetail();
        soapFaultDetail.getDetailEntries().forEachRemaining(detail -> {
            SoapFaultDetailElement detailElementChild = soapFaultDetail.getDetailEntries().next();
            Source detailSource = detailElementChild.getSource();
            ErrorFault fault = (ErrorFault) ItronEndpointManager.PROGRAM.getMarshaller().unmarshal(detailSource);
            log.debug(XmlUtils.getPrettyXml(fault));
            fault.getErrors().forEach(error -> checkIfErrorShouldBeIgnored(error.getErrorCode(),
                error.getErrorMessage(), faultCodesToIgnore, log));
        });
    }

    @Override
    public boolean isSupported(ItronEndpointManager manager) {
        return ItronEndpointManager.PROGRAM == manager;
    }
}
