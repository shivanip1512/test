package com.cannontech.common.smartNotification.model;

import java.util.Map;

import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.google.common.collect.ImmutableMap;

/**
 * A Smart Notification event that is sent when an Infrastructure Warning occurs.
 */
public class InfrastructureWarningsSmartNotificationEvent extends SmartNotificationEvent {
    private final InfrastructureWarning warning;
    
    public InfrastructureWarningsSmartNotificationEvent(InfrastructureWarning warning) {
        super(SmartNotificationEventType.INFRASTRUCTURE_WARNING);
        this.warning = warning;
    }
    
    @Override
    public Map<String, Object> getParameters() {
        return ImmutableMap.of(
            "PaoId", warning.getPaoIdentifier().getPaoId(),
            "WarningType", warning.getWarningType().toString()
        );
    }
    
    public InfrastructureWarning getWarning() {
        return warning;
    }
}
