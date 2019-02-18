package com.cannontech.dr.itron.service.impl;

import com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6.CancelHANLoadControlProgramEventOnDevicesRequest;

public class ProgramEventManagerHelper {
    
    public static CancelHANLoadControlProgramEventOnDevicesRequest buildOptOutRequest(long itronGroupId, String macAddress) {
        CancelHANLoadControlProgramEventOnDevicesRequest request = new CancelHANLoadControlProgramEventOnDevicesRequest();
        request.getDeviceGroupIDs().add(itronGroupId);
        request.getNicMacIDs().add(macAddress);
        return request;
    }
}
