package com.cannontech.dr.itron.service.impl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceResponse;


//not using
//@Endpoint
//@RequestMapping("/dev/itron/")
public class ItronSimulatorServiceEndpoint {

    //classpath:com/cannontech/dr/itron/schema/DeviceManager.wsdl
   /* @PayloadRoot(localPart = "addHANDevice", namespace="urn:com:ssn:dr:xmlschema:service:v1.8:DeviceManager")
    public @ResponsePayload
    AddHANDeviceResponse addHANDevice(@RequestPayload AddHANDeviceRequest request) {
        return null;
    }*/
}