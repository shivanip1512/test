package com.cannontech.services.infrastructure.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
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
    private static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private static final int CONNECTED = 0;
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
        log.debug("Required disconnected minutes to warn: " + warnableTimeMinutes);
        
        Map<PaoIdentifier, PointValueQualityHolder> gatewayToPointValue = rphDao.getSingleAttributeData(
            rfnGatewayService.getAllGateways(), BuiltInAttribute.COMM_STATUS, false, null, null);

        return gatewayToPointValue.entrySet()
                                  .stream()
                                  .filter(entry -> isWarnable(entry.getValue(), warnableDuration))
                                  .map(entry -> buildWarning(entry.getKey(), entry.getValue()))
                                  .collect(Collectors.toList());
    }
    
    /**
     * Returns true if warning should be generated.
     */
    protected static boolean isWarnable(PointValueQualityHolder point, Duration warnableDuration) {
        if (point.getValue() == CONNECTED
            || new Instant(point.getPointDataTimeStamp()).plus(warnableDuration).isAfterNow()) {
            return false;
        }
        return true;
    }
    
    /**
     * Builds a GATEWAY_CONNECTION_STATUS warning for the specified paoIdentifier and point value.
     */
    private static InfrastructureWarning buildWarning(PaoIdentifier paoId, PointValueQualityHolder pvqh) {
        return new InfrastructureWarning(paoId,
                                         InfrastructureWarningType.GATEWAY_CONNECTION_STATUS,
                                         df.format(pvqh.getPointDataTimeStamp()));
    }
}
