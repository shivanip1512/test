package com.cannontech.services.infrastructure.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Generates warnings for cellular relays whose connection status has been "disconnected for a configurable amount of time
 */
public class CellularDeviceConnectionStatusEvaluator extends BaseConnectionStatusEvaluator {

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private IDatabaseCache dbCache;

    @Override
    public Set<PaoType> getSupportedTypes() {
        return new HashSet<PaoType>(Arrays.asList(PaoType.CRLY856,
                                                  PaoType.CRL520FAXE,
                                                  PaoType.CRL520FAXED,
                                                  PaoType.CRL520FRXE,
                                                  PaoType.CRL520FRXED,
                                                  PaoType.CRLY856));
    }
    
    @Override
    protected Iterable<? extends YukonPao> getDevices() {
        return dbCache.getAllYukonPAObjects().stream()
                .filter(pao -> PaoType.getCellularTypes().contains(pao.getPaoType()))
                .collect(Collectors.toList());
    }
    
    @Override
    protected InfrastructureWarningType getWarningType() {
        return InfrastructureWarningType.CELLULAR_DEVICE_CONNECTION_STATUS;
    }
    
    @Override
    protected int getWarnableMinutes() {
        return globalSettingDao.getInteger(GlobalSettingType.DEVICE_CONNECTION_WARNING_MINUTES);
    }
}
