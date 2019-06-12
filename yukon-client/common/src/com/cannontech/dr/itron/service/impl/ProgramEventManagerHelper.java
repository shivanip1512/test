package com.cannontech.dr.itron.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Source;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
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
import com.google.common.collect.ImmutableMap;

public class ProgramEventManagerHelper implements SoapFaultParser {
    
    private static Map<Integer, EventControlType> eventControlTypes = ImmutableMap.of(
        0, EventControlType.STANDARD,            // Standard
        1, EventControlType.ADVANCED_OPTION_1,   // True Cycle
        2, EventControlType.ADVANCED_OPTION_2 ); // Smart Cycle

    public static CancelHANLoadControlProgramEventOnDevicesRequest buildRestoreRequest(Long itronGroupId, long eventID, 
            String macAddress, boolean enableRandomization) {
        
        CancelHANLoadControlProgramEventOnDevicesRequest request = new CancelHANLoadControlProgramEventOnDevicesRequest();
        if(itronGroupId != null) {
            request.getDeviceGroupIDs().add(itronGroupId);
        }
        if(macAddress != null) {
            request.getNicMacIDs().add(macAddress);
        }
        request.setEventID(eventID);
        request.setEnableRandomization(enableRandomization);
        return request;
    }
    
    public static AddHANLoadControlProgramEventRequest buildDrEvent(int dutyCyclePercent, int dutyCycleType, int dutyCyclePeriod,
            int criticality, int relay, int itronProgramId, String programName, boolean rampIn, boolean rampOut, Duration duration) {
        AddHANLoadControlProgramEventRequest request = new AddHANLoadControlProgramEventRequest();
        AddHANLoadControlProgramEventType event = new AddHANLoadControlProgramEventType();
        event.setProgramID(itronProgramId);
        event.setName(programName);
        event.setDutyCyclePercentage((short) dutyCyclePercent);
        event.setRandomizeStartTime(rampIn);
        event.setRandomizeEndTime(rampOut);
        //DatatypeFactory.newInstance().newXMLGregorianCalendar(startTime.toDateTime().toGregorianCalendar())
        LoadControlProgramEventD2GParamsType d2GParams = new LoadControlProgramEventD2GParamsType();
        d2GParams.setCriticality((short) criticality);
        d2GParams.setDutyCyclePeriod((short) dutyCyclePeriod);
        /**
         * Let's say the duty cycle period is 30 minutes and the event is scheduled for 45 minutes
         * we "round up" to duty cycle count of 2 (which adds up to an hour)
         * then LM will send the stop message at 45 minutes and we send that out to end the event
         */
        
        short dutyCycleCount =
            new BigDecimal(duration.getStandardMinutes()).divide(new BigDecimal(dutyCyclePeriod), RoundingMode.UP).shortValue();
        d2GParams.setDutyCycleCount(dutyCycleCount);
        d2GParams.setVirtualRelayAddress((short) relay);
        d2GParams.setEventControl(eventControlTypes.get(dutyCycleType));
        event.setD2GParams(d2GParams);
        request.setHANLoadControlProgramEvent(event);
        return request;
    }
    
    @Override
    public void handleSoapFault(SoapFaultClientException e, Set<String> faultCodesToIgnore, Logger log) {
        SoapFaultDetail soapFaultDetail = e.getSoapFault().getFaultDetail();
        soapFaultDetail.getDetailEntries().forEachRemaining(detail -> {
            SoapFaultDetailElement detailElementChild =
                soapFaultDetail.getDetailEntries().next();
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
