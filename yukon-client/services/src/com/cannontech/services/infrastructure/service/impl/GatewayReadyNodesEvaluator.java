package com.cannontech.services.infrastructure.service.impl;

import static com.cannontech.infrastructure.model.InfrastructureWarningType.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

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

/**
 * Generates warnings for gateways that are not in a gateway connection warning state and whose ready node count drops 
 * below a configurable threshold.
 */
public class GatewayReadyNodesEvaluator implements InfrastructureWarningEvaluator {
    private static final Logger log = YukonLogManager.getLogger(GatewayReadyNodesEvaluator.class);
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired GatewayConnectionStatusEvaluator gatewayConnectionStatusEvaluator;

    @Override
    public Set<PaoType> getSupportedTypes() {
        return PaoType.getRfGatewayTypes();
    }

    @Override
    public List<InfrastructureWarning> evaluate() {
        log.debug("Running RF Gateway ready nodes evaluator");
        
        // Retrieve the relevant global settings
        int threshold = globalSettingDao.getInteger(GlobalSettingType.GATEWAY_READY_NODES_THRESHOLD);
        int connectionWarningMinutes = globalSettingDao.getInteger(GlobalSettingType.GATEWAY_CONNECTION_WARNING_MINUTES);
        Duration connectionWarningDuration = Duration.standardMinutes(connectionWarningMinutes);
        log.debug("Threshold: " + threshold + ", Connection warning minutes: " + connectionWarningMinutes);
        
        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
        
        // Get most recent "ready nodes" value for each gateway
        Map<PaoIdentifier, PointValueQualityHolder> gatewayToReadyNodes = 
                rphDao.getSingleAttributeData(gateways, BuiltInAttribute.READY_NODES, false, null);
        
        // Get most recent connection status for each gateway
        Map<PaoIdentifier, PointValueQualityHolder> gatewayToConnectionStatus =
            rphDao.getMostRecentAttributeDataByValue(rfnGatewayService.getAllGateways(), BuiltInAttribute.COMM_STATUS,
                false, CommStatusState.CONNECTED.getRawState(), null);
        
        // Look for gateways whose ready node count is at or below the configurable threshold, and the gateway is
        // connected
        return gatewayToReadyNodes.entrySet()
                                  .stream()
                                  .filter(entry -> entry.getValue().getValue() <= threshold)
                                  .filter(entry -> isConnected(entry.getKey(), gatewayToConnectionStatus, connectionWarningDuration)) 
                                  .map(entry -> buildWarning(entry, threshold))
                                  .collect(Collectors.toList());
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

    private InfrastructureWarning buildWarning(Map.Entry<PaoIdentifier,PointValueQualityHolder> gatewayToReadyNodes, 
                                               int threshold) {
        PaoIdentifier gateway = gatewayToReadyNodes.getKey();
        int readyNodes = (int) gatewayToReadyNodes.getValue().getValue();
        return new InfrastructureWarning(gateway, GATEWAY_READY_NODES, readyNodes, threshold);
    }
}
