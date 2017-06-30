package com.cannontech.services.infrastructure.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.impl.DeviceDaoImpl;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningType;

public abstract class InfrastructureStatusWarningEvaluator implements InfrastructureWarningEvaluator {
    
    private static final Logger log = YukonLogManager.getLogger(InfrastructureStatusWarningEvaluator.class);
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private DeviceDaoImpl deviceDao;
    
    @Override
    public List<InfrastructureWarning> evaluate() {
        log.debug("Running "+  getAttribute()+ " evaluator on "+ getSupportedTypes());
        List<SimpleDevice> devices = deviceDao.getDevicesForPaoTypes(getSupportedTypes());
        Map<PaoIdentifier, PointValueQualityHolder> pao = rphDao.getSingleAttributeData(devices, getAttribute(), false, null, null);

        return pao.keySet().stream().filter(device -> pao.get(device).getValue() == getBadState()).
                map(device -> {
                    return new InfrastructureWarning(device, getWarningType());
                }).collect(Collectors.toList());
    }
    
    public abstract InfrastructureWarningType getWarningType();
    
    public abstract BuiltInAttribute getAttribute();
    
    public abstract int getBadState();
}
