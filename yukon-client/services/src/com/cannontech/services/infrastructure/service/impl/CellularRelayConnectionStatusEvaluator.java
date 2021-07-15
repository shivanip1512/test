package com.cannontech.services.infrastructure.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.service.RfnRelayService;
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
 * Generates warnings for cellular relays whose connection status has been "disconnected for a configurable amount of time
 */
public class CellularRelayConnectionStatusEvaluator implements InfrastructureWarningEvaluator {

    private static final Logger log = YukonLogManager.getLogger(CellularRelayConnectionStatusEvaluator.class);
    private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private RfnRelayService rfnRelayService;
    @Autowired private RawPointHistoryDao rphDao;

    @Override
    public Set<PaoType> getSupportedTypes() {
        return new HashSet<PaoType>(Arrays.asList(PaoType.CRLY856));
    }

    @Override
    public List<InfrastructureWarning> evaluate() {
        log.debug("Running Connection status evaluator for PaoTypes: {}", getSupportedTypes());
        
        int warnableTimeMinutes = globalSettingDao.getInteger(GlobalSettingType.GATEWAY_CONNECTION_WARNING_MINUTES);
        Duration warnableDuration = Duration.standardMinutes(warnableTimeMinutes);
        log.debug("Required disconnected minutes to warn: {}", warnableTimeMinutes);
        
        Map<PaoIdentifier, PointValueQualityHolder> deviceToPointValue =
            rphDao.getMostRecentAttributeDataByValue(getDevices(), BuiltInAttribute.COMM_STATUS,
                    false, CommStatusState.CONNECTED.getRawState(), null);
        
        Instant now = Instant.now();
        
        return deviceToPointValue.entrySet()
                .stream()
                .map(entry -> buildConnectionStatusInfo(entry, now, warnableDuration))
                .filter(ConnectionStatusInfo::isWarnable)
                .map(CellularRelayConnectionStatusEvaluator::buildWarning)
                .collect(Collectors.toList());
    }

    /**
     * Build an object containing all the relevant info for a gateway connection status check.
     */
    private ConnectionStatusInfo buildConnectionStatusInfo(Map.Entry<PaoIdentifier, PointValueQualityHolder> entry, 
                                                   Instant evaluationTime, Duration warnableDuration) {
        
        ConnectionStatusInfo info = new ConnectionStatusInfo(entry.getKey(), warnableDuration, evaluationTime, entry.getValue());
        if (info.isLastConnectedTimestampWarnable()) {
            AdjacentPointValues adjacentPointValues = rphDao.getAdjacentPointValues(info.getLastConnectedPointValue());
            info.setNextDisconnectedPointValue(adjacentPointValues.getSucceeding());
        }
        return info;
    }

    /**
     * Builds a CELLULAR_RELAY_CONNECTION_STATUS warning for the specified paoIdentifier and point value.
     */
    static InfrastructureWarning buildWarning(ConnectionStatusInfo info) {
        return new InfrastructureWarning(info.getDevicePaoId(),
                                         InfrastructureWarningType.CELLULAR_RELAY_CONNECTION_STATUS,
                                         dateFormat.format(info.getNextDisconnectedTimestamp().toDate()));
    }
    
    private Iterable<? extends YukonPao> getDevices() {
        return rfnRelayService.getAllCellularRelays();
    }
}
