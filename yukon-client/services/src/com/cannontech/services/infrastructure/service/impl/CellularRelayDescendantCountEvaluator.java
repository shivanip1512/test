package com.cannontech.services.infrastructure.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.dao.model.DynamicRfnDeviceData;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.model.RfnRelay;
import com.cannontech.common.rfn.service.RfnRelayService;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningSeverity;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureWarningEvaluator;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

/**
 * Evaluates all Cellular Relays. Warns for each relay whose descendant count is higher than the warning limit.
 *
 */
public class CellularRelayDescendantCountEvaluator implements InfrastructureWarningEvaluator {

    private static final Logger log = YukonLogManager.getLogger(GatewayReadyNodesEvaluator.class);
    
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnRelayService rfnRelayService;

    @Override
    public Set<PaoType> getSupportedTypes() {
        return new HashSet<PaoType>(Arrays.asList(PaoType.CRLY856));
    }

    @Override
    public List<InfrastructureWarning> evaluate() {
        log.debug("Running Cellular Relay descendant count evaluator");
        
        int descendantWarningLimit = globalSettingDao.getInteger(GlobalSettingType.CELLULAR_RELAY_DESCENDANT_COUNT_WARNING_THRESHOLD);
        Set<RfnRelay> relays = rfnRelayService.getRelaysOfType(PaoType.CRLY856);
        Set<Integer> relayIds = relays.stream()
                                      .map(relay -> relay.getPaoIdentifier().getPaoId())
                                      .collect(Collectors.toSet());
        List<DynamicRfnDeviceData> deviceDatas = rfnDeviceDao.getDynamicRfnDeviceData(relayIds);
        
        return deviceDatas.stream()
                          .filter(data -> data.getDescendantCount() >= descendantWarningLimit)
                          .map(data -> {
                              return new InfrastructureWarning(data.getDevice().getPaoIdentifier(),
                                                               InfrastructureWarningType.CELLULAR_RELAY_DESCENDANT_COUNT,
                                                               InfrastructureWarningSeverity.LOW,
                                                               data.getDescendantCount());
                          })
                          .collect(Collectors.toList());
        
    }

}
