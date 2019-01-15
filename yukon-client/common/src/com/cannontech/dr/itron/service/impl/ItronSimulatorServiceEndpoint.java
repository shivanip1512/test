package com.cannontech.dr.itron.service.impl;

import javax.xml.bind.Element;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceResponse;

@Endpoint
@RequestMapping("/dev/itron/")
public class ItronSimulatorServiceEndpoint {

    @PayloadRoot(localPart = "AddHANDeviceRequest", namespace="urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager.xsd")
    public @ResponsePayload
    Element AddHANDeviceRequest(@RequestPayload Element element) {
        return null;
    }
}