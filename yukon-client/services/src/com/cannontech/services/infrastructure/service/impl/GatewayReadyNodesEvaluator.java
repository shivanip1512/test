package com.cannontech.services.infrastructure.service.impl;

import static com.cannontech.infrastructure.model.InfrastructureWarningType.GATEWAY_READY_NODES;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.db.point.stategroup.CommStatusState;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.services.infrastructure.service.InfrastructureWarningEvaluator;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Generates warnings for gateways that are not in a gateway connection warning state and whose ready node count drops 
 * below a configurable threshold.
 */
public class GatewayReadyNodesEvaluator implements InfrastructureWarningEvaluator {
    private static final Logger log = YukonLogManager.getLogger(GatewayReadyNodesEvaluator.class);
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private GatewayConnectionStatusEvaluator gatewayConnectionStatusEvaluator;
    @Autowired private IDatabaseCache cache;

    @Override
    public Set<PaoType> getSupportedTypes() {
        return PaoType.getRfGatewayTypes();
    }

    @Override
    public List<InfrastructureWarning> evaluate() {
        log.debug("Running RF Gateway ready nodes evaluator");
        
        // Retrieve the relevant global settings
        int connectionWarningMinutes = globalSettingDao.getInteger(GlobalSettingType.GATEWAY_CONNECTION_WARNING_MINUTES);
        Duration connectionWarningDuration = Duration.standardMinutes(connectionWarningMinutes);
        
        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
                
        // Get most recent "ready nodes" value for each gateway
        Map<PaoIdentifier, PointValueQualityHolder> gatewayToReadyNodes = 
                rphDao.getSingleAttributeData(gateways, BuiltInAttribute.READY_NODES, false, null);
        
        // Get most recent connection status for each gateway
        Map<PaoIdentifier, PointValueQualityHolder> gatewayToConnectionStatus =
            rphDao.getMostRecentAttributeDataByValue(gateways, BuiltInAttribute.COMM_STATUS,
                false, CommStatusState.CONNECTED.getRawState(), null);
        
        // Look for gateways whose ready node count is at or below the configurable threshold, and the gateway is
        // connected
        return gatewayToReadyNodes.entrySet()
                                  .stream()
                                  .filter(entry -> isWarnable(entry))
                                  .filter(entry -> isConnected(entry.getKey(), gatewayToConnectionStatus, connectionWarningDuration)) 
                                  .map(entry -> buildWarning(entry))
                                  .collect(Collectors.toList());
    }
    
    private boolean isWarnable(Map.Entry<PaoIdentifier,PointValueQualityHolder> gatewayToReadyNodes) {
        if (gatewayToReadyNodes.getKey().getPaoType() == PaoType.VIRTUAL_GATEWAY
                && cache.getAllPaoTypes().stream().noneMatch(PaoType::isWifiDevice)) {
            log.debug("Vitual Gateway {} is Found. No WiFi meters found on the system.", gatewayToReadyNodes.getKey());
            return false;
        }
        int threshold = getThreshold(gatewayToReadyNodes.getKey());
        boolean isWarnable = gatewayToReadyNodes.getValue().getValue() <= threshold;
        log.debug("Gateway {} threshold {} nodes {} warnable {}", gatewayToReadyNodes.getKey(), threshold, gatewayToReadyNodes.getValue().getValue(), isWarnable);
        return isWarnable;
    }
    
    private int getThreshold(PaoIdentifier gateway) {
        return gateway.getPaoType() == PaoType.VIRTUAL_GATEWAY ? 0 : globalSettingDao
                .getInteger(GlobalSettingType.GATEWAY_READY_NODES_THRESHOLD);
    }

    /**
     * Considers the gateway connected if it does not meet the criteria to generate a gateway connection status warning.
     */
    private boolean isConnected(PaoIdentifier gateway, 
                                Map<PaoIdentifier, PointValueQualityHolder> gatewaysToConnectionStatus,
                                Duration connectionWarningDuration) {
        return !gatewayConnectionStatusEvaluator.isWarnable(gatewaysToConnectionStatus.get(gateway), 
                                                           connectionWarningDuration);
    }

    private InfrastructureWarning buildWarning(Map.Entry<PaoIdentifier,PointValueQualityHolder> gatewayToReadyNodes) {
        PaoIdentifier gateway = gatewayToReadyNodes.getKey();
        int readyNodes = (int) gatewayToReadyNodes.getValue().getValue();
        return new InfrastructureWarning(gateway, GATEWAY_READY_NODES, readyNodes, getThreshold(gateway));
    }
}
