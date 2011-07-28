package com.cannontech.thirdparty.messaging.activemq;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.messaging.SmartUpdateRequestMessage;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.service.ZigbeeUpdateService;

public class DigiSmartUpdateListener {

    private GatewayDeviceDao gatewayDeviceDao;
    private ZigbeeDeviceDao zigbeeDeviceDao;
    private ZigbeeUpdateService zigbeeUpdateService;
    
    public void handleSmartUpdateRequest(SmartUpdateRequestMessage message) {
        PaoIdentifier paoIdentifier = message.getPaoIdentifier();
        ZigbeeDevice device = null;
        
        if (paoIdentifier.getPaoType() == PaoType.DIGIGATEWAY) {
            device = gatewayDeviceDao.getZigbeeGateway(paoIdentifier.getPaoId());
        } else if (paoIdentifier.getPaoType() == PaoType.ZIGBEEUTILPRO) {
            device = zigbeeDeviceDao.getZigbeeDevice(paoIdentifier.getPaoId());
        }
        
        zigbeeUpdateService.enableActiveDevicePoll(device);
    }
    
    @Autowired
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
    }
    
    @Autowired
    public void setZigbeeDeviceDao(ZigbeeDeviceDao zigbeeDeviceDao) {
        this.zigbeeDeviceDao = zigbeeDeviceDao;
    }
    
    @Autowired
    public void setZigbeeUpdateService(ZigbeeUpdateService zigbeeUpdateService) {
        this.zigbeeUpdateService = zigbeeUpdateService;
    }
}
