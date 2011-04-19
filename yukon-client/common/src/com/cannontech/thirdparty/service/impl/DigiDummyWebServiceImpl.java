package com.cannontech.thirdparty.service.impl;

import com.cannontech.thirdparty.exception.GatewayCommissionException;
import com.cannontech.thirdparty.exception.ZigbeeClusterLibraryException;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.model.ZigbeeText;
import com.cannontech.thirdparty.service.DeviceResponseHandler;
import com.cannontech.thirdparty.service.ZigbeeWebService;

public class DigiDummyWebServiceImpl implements ZigbeeWebService {

    @Override
    public String getAllDevices() {
        return null;
    }

    @Override
    public int sendSEPControlMessage(int eventId, SepControlMessage controlMessage, DeviceResponseHandler responseHandler) {
        return 0;
    }

    @Override
    public void sendSEPRestoreMessage(int eventId, SepRestoreMessage restoreMessage, DeviceResponseHandler responseHandler) {
    }


    @Override
    public void installGateway(int gatewayId) throws GatewayCommissionException {
    }


    @Override
    public void removeGateway(int gatewayId) {
    }


    @Override
    public void installStat(int gatewayId, int deviceId) {
    }


    @Override
    public void uninstallStat(int gatewayId, int deviceId) {
    }


    @Override
    public void sendTextMessage(int gatewayId, ZigbeeText zigbeeText)
            throws ZigbeeClusterLibraryException {
    }

}
