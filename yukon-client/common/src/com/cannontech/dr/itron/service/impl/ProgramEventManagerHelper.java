package com.cannontech.dr.itron.service.impl;

import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.transform.Source;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6.AddHANLoadControlProgramEventRequest;
import com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6.AddHANLoadControlProgramEventType;
import com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6.CancelHANLoadControlProgramEventOnDevicesRequest;
import com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6.ErrorFault;
import com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6.EventControlType;
import com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6.LoadControlProgramEventD2GParamsType;
import com.cannontech.dr.itron.service.ItronCommunicationException;

public class ProgramEventManagerHelper implements SoapFaultParser {
    
    public static CancelHANLoadControlProgramEventOnDevicesRequest buildRestoreRequest(Long itronGroupId, long eventID, String macAddress) {
        CancelHANLoadControlProgramEventOnDevicesRequest request = new CancelHANLoadControlProgramEventOnDevicesRequest();
        if(itronGroupId != null) {
            request.getDeviceGroupIDs().add(itronGroupId);
        }
        if(macAddress != null) {
            request.getNicMacIDs().add(macAddress);
        }
        request.setEventID(eventID);
        return request;
    }
    
    public static AddHANLoadControlProgramEventRequest buildDrEvent(int dutyCyclePercent, int dutyCyclePeriod,
            int criticality, Instant startTime, int relay, int itronProgramId, String programName) {
        AddHANLoadControlProgramEventRequest request = new AddHANLoadControlProgramEventRequest();
        AddHANLoadControlProgramEventType event = new AddHANLoadControlProgramEventType();
        event.setProgramID(itronProgramId);
        event.setName(programName);
        event.setDutyCyclePercentage((short) dutyCyclePercent);
        
        try {
            event.setDeploymentDate( DatatypeFactory.newInstance().newXMLGregorianCalendar(startTime.toDateTime().toGregorianCalendar()));
            event.setStartDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(startTime.toDateTime().toGregorianCalendar()));
        } catch (DatatypeConfigurationException e) {
            throw new ItronCommunicationException("Unable to convert start time " + startTime.toDateTime(), e);
        }
        
        LoadControlProgramEventD2GParamsType d2GParams = new LoadControlProgramEventD2GParamsType();
        d2GParams.setCriticality((short) criticality);
        d2GParams.setDutyCyclePeriod((short) dutyCyclePeriod);
        d2GParams.setVirtualRelayAddress((short) relay);
        d2GParams.setEventControl(EventControlType.STANDARD);
        event.setD2GParams(d2GParams);
        request.setHANLoadControlProgramEvent(event);
        return request;
    }
    
    @Override
    public void handleSoapFault(SoapFaultClientException e, Set<String> faultCodesToIgnore, Logger log) {
        SoapFaultDetail soapFaultDetail = e.getSoapFault().getFaultDetail();
        soapFaultDetail.getDetailEntries().forEachRemaining(detail -> {
            SoapFaultDetailElement detailElementChild =
                (SoapFaultDetailElement) soapFaultDetail.getDetailEntries().next();
            Source detailSource = detailElementChild.getSource();
            ErrorFault fault = (ErrorFault) ItronEndpointManager.PROGRAM_EVENT.getMarshaller().unmarshal(detailSource);
            log.debug(XmlUtils.getPrettyXml(fault));
            fault.getErrors().forEach(error -> checkIfErrorShouldBeIgnored(error.getErrorCode(),
                error.getErrorMessage(), faultCodesToIgnore, log));
        });
    }

    @Override
    public boolean isSupported(ItronEndpointManager manager) {
        return ItronEndpointManager.PROGRAM_EVENT == manager;
    }
}
