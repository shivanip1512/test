package com.cannontech.common.smartNotification.model;

import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class InfrastructureWarningsParametersAssembler {  
    public static final String PAO_ID = "paoId";
    public static final String WARNING_TYPE = "WarningType";
    public static final String WARNING_SEVERITY = "WarningSeverity";
    public static final String ARGUMENTS = "Arguments";
    
    public static SmartNotificationEvent assemble(Instant now, InfrastructureWarning warning) {
        SmartNotificationEvent event = new SmartNotificationEvent(now);
        Builder<String, Object> b = new ImmutableMap.Builder<>();
        b.put(PAO_ID,  warning.getPaoIdentifier().getPaoId())
        .put(WARNING_TYPE, warning.getWarningType())
        .put(WARNING_SEVERITY, warning.getSeverity());
        //TODO: separate into 3 argument parameters
        if (warning.getArguments() != null && warning.getArguments().length > 0) {
            b.put(ARGUMENTS, Joiner.on(",").join(warning.getArguments()));
        }
        event.setParameters(b.build());
        return event;
    }
    
    public static int getPaoId(Map<String, Object> parameters){
        int paoId = Integer.parseInt(parameters.get(PAO_ID).toString());
        return paoId;
    }
}
