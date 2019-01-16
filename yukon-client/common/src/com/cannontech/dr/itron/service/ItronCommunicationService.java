package com.cannontech.dr.itron.service;

import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceResponse;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceResponse;

public interface ItronCommunicationService {

    AddHANDeviceResponse addHANDevice();

    EditHANDeviceResponse editHANDevice();

}
