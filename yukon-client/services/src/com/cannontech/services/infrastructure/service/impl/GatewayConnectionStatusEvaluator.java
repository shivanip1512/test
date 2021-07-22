package com.cannontech.services.infrastructure.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

/**
 * Generates warnings for gateways whose connection status has been "disconnected" for a configurable duration.
 */
public class GatewayConnectionStatusEvaluator extends BaseConnectionStatusEvaluator {

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private RfnGatewayService rfnGatewayService;
    
    @Override
    public Set<PaoType> getSupportedTypes() {
        return PaoType.getRfGatewayTypes();
    }

    @Override
    Iterable<? extends YukonPao> getDevices() {
        return rfnGatewayService.getAllGateways();
    }

    @Override
    InfrastructureWarningType getWarningType() {
        return InfrastructureWarningType.GATEWAY_CONNECTION_STATUS;
    }

    @Override
    int getWarnableMinutes() {
        return globalSettingDao.getInteger(GlobalSettingType.DEVICE_CONNECTION_WARNING_MINUTES);
    }
    

}
