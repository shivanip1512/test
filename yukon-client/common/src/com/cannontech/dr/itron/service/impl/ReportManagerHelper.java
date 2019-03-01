package com.cannontech.dr.itron.service.impl;

import java.util.Set;

import javax.xml.transform.Source;

import org.apache.logging.log4j.Logger;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.dr.itron.model.jaxb.reportManagerTypes_v1_2.ErrorFault;
import com.cannontech.dr.itron.model.jaxb.reportManagerTypes_v1_2.ExportDeviceLogRequest;
import com.cannontech.dr.itron.model.jaxb.reportManagerTypes_v1_2.ResultActionEnumeration;

public class ReportManagerHelper implements SoapFaultParser {
    
    public static ExportDeviceLogRequest buildExportDeviceLogRequest(long startRecordId, long endRecordId) {
        ExportDeviceLogRequest request =  new ExportDeviceLogRequest();
        request.getResultActions().add(ResultActionEnumeration.SCP);
        request.setRecordIDRangeStart(startRecordId);
        request.setRecordIDRangeEnd(endRecordId);
        return request;
    }
    
    @Override
    public void handleSoapFault(SoapFaultClientException e, Set<String> faultCodesToIgnore, Logger log) {
        SoapFaultDetail soapFaultDetail = e.getSoapFault().getFaultDetail();
        soapFaultDetail.getDetailEntries().forEachRemaining(detail -> {
            SoapFaultDetailElement detailElementChild =
                (SoapFaultDetailElement) soapFaultDetail.getDetailEntries().next();
            Source detailSource = detailElementChild.getSource();
            ErrorFault fault = (ErrorFault) ItronEndpointManager.REPORT.getMarshaller().unmarshal(detailSource);
            log.debug(XmlUtils.getPrettyXml(fault));
            fault.getErrors().forEach(error -> checkIfErrorShouldBeIgnored(error.getErrorCode(),
                error.getErrorMessage(), faultCodesToIgnore, log));
        });
    }

    @Override
    public boolean isSupported(ItronEndpointManager manager) {
        return ItronEndpointManager.REPORT == manager;
    }
}
