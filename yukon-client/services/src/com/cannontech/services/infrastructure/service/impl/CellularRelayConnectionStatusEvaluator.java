package com.cannontech.services.infrastructure.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.service.RfnRelayService;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

/**
 * Generates warnings for cellular relays whose connection status has been "disconnected for a configurable amount of time
 */
public class CellularRelayConnectionStatusEvaluator extends BaseConnectionStatusEvaluator {

    @Autowired private RfnRelayService rfnRelayService;
    @Autowired private GlobalSettingDao globalSettingDao;

    @Override
    public Set<PaoType> getSupportedTypes() {
        return new HashSet<PaoType>(Arrays.asList(PaoType.CRLY856));
    }
    
    @Override
    protected Iterable<? extends YukonPao> getDevices() {
        return rfnRelayService.getRelaysOfType(PaoType.CRLY856);
    }
    
    @Override
    protected InfrastructureWarningType getWarningType() {
        return InfrastructureWarningType.CELLULAR_RELAY_CONNECTION_STATUS;
    }
    
    @Override
    protected int getWarnableMinutes() {
        return globalSettingDao.getInteger(GlobalSettingType.DEVICE_CONNECTION_WARNING_MINUTES);
    }
}
