package com.cannontech.services.infrastructure.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.AdjacentPointValues;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.db.point.stategroup.CommStatusState;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureWarningEvaluator;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

/**
 * Generates warnings for gateways whose connection status has been "disconnected" for a configurable duration.
 */
public class GatewayConnectionStatusEvaluator implements InfrastructureWarningEvaluator {
    private static final Logger log = YukonLogManager.getLogger(GatewayConnectionStatusEvaluator.class);
    private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    @Autowired GlobalSettingDao globalSettingDao;
    @Autowired RfnGatewayService rfnGatewayService;
    @Autowired private RawPointHistoryDao rphDao;
    
    @Override
    public Set<PaoType> getSupportedTypes() {
        return PaoType.getRfGatewayTypes();
    }
    
    @Override
    public List<InfrastructureWarning> evaluate() {
        log.debug("Running RF Gateway Connection status evaluator");
        
        int warnableTimeMinutes = globalSettingDao.getInteger(GlobalSettingType.GATEWAY_CONNECTION_WARNING_MINUTES);
        Duration warnableDuration = Duration.standardMinutes(warnableTimeMinutes);
        log.debug("Required disconnected minutes to warn: {}", warnableTimeMinutes);
        
        Map<PaoIdentifier, PointValueQualityHolder> gatewayToPointValue =
            rphDao.getMostRecentAttributeDataByValue(rfnGatewayService.getAllGateways(), BuiltInAttribute.COMM_STATUS,
                false, CommStatusState.CONNECTED.getRawState(), null);
        
        return gatewayToPointValue.entrySet()
                .stream()
                .map(entry -> buildConnectionStatusInfo(entry, warnableDuration))
                .filter(ConnectionStatusInfo::isWarnable)
                .map(GatewayConnectionStatusEvaluator::buildWarning)
                .collect(Collectors.toList());
    }
    
    /**
     * Build an object containing all the relevant info for a gateway connection status check.
     */
    protected ConnectionStatusInfo buildConnectionStatusInfo(Map.Entry<PaoIdentifier, PointValueQualityHolder> entry, 
                                                           Duration warnableDuration) {
        
        ConnectionStatusInfo info = new ConnectionStatusInfo(entry.getKey(), warnableDuration, entry.getValue());
        if (info.isLastConnectedTimestampWarnable()) {
            AdjacentPointValues adjacentPointValues = rphDao.getAdjacentPointValues(info.getLastConnectedPointValue());
            info.setNextDisconnectedPointValue(adjacentPointValues.getSucceeding());
        }
        return info;
    }
    
    /**
     * Builds a GATEWAY_CONNECTION_STATUS warning for the specified paoIdentifier and point value.
     */
    private static InfrastructureWarning buildWarning(ConnectionStatusInfo info) {
        return new InfrastructureWarning(info.getGatewayPaoId(),
                                         InfrastructureWarningType.GATEWAY_CONNECTION_STATUS,
                                         dateFormat.format(info.getNextDisconnectedTimestamp()));
    }

}
