package com.cannontech.services.serverDeviceCreation.service;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.message.DeviceCreationDescriptor;
import com.cannontech.message.porter.message.RfnDeviceCreationReply;
import com.cannontech.message.porter.message.RfnDeviceCreationRequest;
import com.cannontech.services.ThriftServiceBase;

public class ServerDeviceCreationMsgService extends ThriftServiceBase<RfnDeviceCreationRequest, RfnDeviceCreationReply> {
    private Logger log = YukonLogManager.getLogger(ServerDeviceCreationMsgService.class);
    
    @Autowired RfnDeviceCreationService rfnDeviceCreationService;

    @Override
    protected RfnDeviceCreationReply handleRequest(RfnDeviceCreationRequest request) {
        //create new device
        RfnIdentifier rfnId = new RfnIdentifier(request.getRfnIdentifier().getSensorSerialNumber(), request.getRfnIdentifier().getSensorManufacturer(), request.getRfnIdentifier().getSensorModel());
        RfnDevice newDevice = rfnDeviceCreationService.create(rfnId);
        log.debug("Created new RFN device: " + newDevice.toString());
        //build reply message
        PaoIdentifier paoId = newDevice.getPaoIdentifier();
            DeviceCreationDescriptor descriptor = new DeviceCreationDescriptor(paoId);
        RfnDeviceCreationReply reply = new RfnDeviceCreationReply(descriptor, true);
        log.debug("Created RfnDeviceCreationReply with paoIdentifier: " + paoId.toString());
        return reply;
    }

    @Override
    protected RfnDeviceCreationReply handleFailure() {
        RfnDeviceCreationReply reply = new RfnDeviceCreationReply(null, false);
        return reply;
    }
}
