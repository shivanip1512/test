package com.cannontech.services.infrastructure.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.dao.model.DynamicRfnDeviceData;
import com.cannontech.common.pao.PaoType;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningSeverity;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureWarningEvaluator;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IDatabaseCache;

/**
 * 
 * Evaluates cellular meters to determine if their descendant count it too high and generates
 * infrastructure warnings if it is. 
 *
 */
public class CellularMeterDescendantCountEvaluator implements InfrastructureWarningEvaluator {

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    
    @Override
    public Set<PaoType> getSupportedTypes() {
        return new HashSet<PaoType>(PaoType.getCellularMeterTypes());
    }

    @Override
    public List<InfrastructureWarning> evaluate() {
        int descendantWarningLimit = globalSettingDao.getInteger(GlobalSettingType.CELLULAR_METER_DESCENDANT_COUNT_WARNING_THRESHOLD);
        Set<Integer> cellularMeterIds = dbCache.getAllYukonPAObjects().stream()
                                                    .filter(pao -> PaoType.getCellularMeterTypes().contains(pao.getPaoType()))
                                                    .map(litePao -> litePao.getPaoIdentifier().getPaoId())
                                                    .collect(Collectors.toSet());
        
        List<DynamicRfnDeviceData> deviceDatas = rfnDeviceDao.getDynamicRfnDeviceData(cellularMeterIds);
        
        return deviceDatas.stream()
                .filter(data -> data.getDescendantCount() >= descendantWarningLimit)
                .map(data -> {
                    return new InfrastructureWarning(data.getDevice().getPaoIdentifier(),
                                                     InfrastructureWarningType.CELLULAR_METER_DESCENDANT_COUNT,
                                                     InfrastructureWarningSeverity.LOW,
                                                     data.getDescendantCount());
                })
                .collect(Collectors.toList());
    }

}
