package com.cannontech.dr.itron.service;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceResponse;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceResponse;

@WebService
public interface ItronSimulatorService {

    @WebMethod
    AddHANDeviceResponse addHANDevice(AddHANDeviceRequest request);

    @WebMethod
    EditHANDeviceResponse editHANDevice(EditHANDeviceRequest request);

    boolean isSimulatorRunning();

    void stopSimulator();

    void startSimulator();
}
