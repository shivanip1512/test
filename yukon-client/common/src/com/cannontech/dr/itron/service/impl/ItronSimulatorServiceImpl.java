package com.cannontech.dr.itron.service.impl;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceResponse;
import com.cannontech.dr.itron.service.ItronSimulatorService;

@WebService
public class ItronSimulatorServiceImpl implements ItronSimulatorService{

    @WebMethod
    @SOAPBinding(style=Style.DOCUMENT)
    public AddHANDeviceResponse addHANDeviceRequest() {
        AddHANDeviceResponse response = new AddHANDeviceResponse();
        // get response options from simulator settings
        
        // possible responses:

        // success
        response.setMacID("1");

        // error

        // ErrorFault
        return response;
    }
}
