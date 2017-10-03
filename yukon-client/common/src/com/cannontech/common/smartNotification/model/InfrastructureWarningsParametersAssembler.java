package com.cannontech.common.smartNotification.model;

import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

public class InfrastructureWarningsParametersAssembler {  
    public static final String PAO_ID = "paoId";
    public static final String WARNING_TYPE = "WarningType";
    public static final String WARNING_SEVERITY = "WarningSeverity";
    public static final String ARGUMENTS = "Arguments";
    
    public static SmartNotificationEvent assemble(Instant now, InfrastructureWarning warning) {
        SmartNotificationEvent event = new SmartNotificationEvent(now);
        event.setParameters(ImmutableMap.of(
            PAO_ID,  warning.getPaoIdentifier().getPaoId(), 
            WARNING_TYPE, warning.getWarningType(),
            WARNING_SEVERITY, warning.getSeverity()));
        if (warning.getArguments() != null && warning.getArguments().length > 0) {
            event.getParameters().put(ARGUMENTS, Joiner.on(",").join(warning.getArguments()));
        }
        return event;
    }
    
    public static int getPaoId(Map<String, Object> parameters){
        int paoId = Integer.parseInt(parameters.get(PAO_ID).toString());
        return paoId;
    }
}
