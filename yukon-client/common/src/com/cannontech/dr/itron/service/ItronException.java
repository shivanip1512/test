package com.cannontech.dr.itron.service;

import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceResponse;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceResponse;

public class ItronException extends RuntimeException {

    // Exception will contain and response if the response has error. If there is communication error the
    // exception is thrown but response is null.
    private AddHANDeviceResponse errorAddDevice;
    private EditHANDeviceResponse errorEditDevice;
   
    public ItronException(String message) {
        super(message);
    }

    public ItronException(String message, Throwable cause) {
        super(message, cause);
    }

    public AddHANDeviceResponse getErrorAddDevice() {
        return errorAddDevice;
    }

    public void setErrorAddDevice(AddHANDeviceResponse errorAddDevice) {
        this.errorAddDevice = errorAddDevice;
    }

    public EditHANDeviceResponse getErrorEditDevice() {
        return errorEditDevice;
    }

    public void setErrorEditDevice(EditHANDeviceResponse errorEditDevice) {
        this.errorEditDevice = errorEditDevice;
    }


}