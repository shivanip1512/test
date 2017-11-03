package com.cannontech.common.smartNotification.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningSeverity;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
public class InfrastructureWarningsEventAssembler {  
    public static final String PAO_ID = "paoId";
    public static final String WARNING_TYPE = "WarningType";
    public static final String WARNING_SEVERITY = "WarningSeverity";
    public static final String ARGUMENT_1 = "Argument1";
    public static final String ARGUMENT_2 = "Argument2";
    public static final String ARGUMENT_3 = "Argument3";
    
    public static SmartNotificationEvent assemble(Instant now, InfrastructureWarning warning) {
        SmartNotificationEvent event = new SmartNotificationEvent(now);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PAO_ID, warning.getPaoIdentifier().getPaoId());
        parameters.put(WARNING_TYPE , warning.getWarningType().name());
        parameters.put(WARNING_SEVERITY, warning.getSeverity().name());
        addArgument(parameters, warning, ARGUMENT_1, 0);
        addArgument(parameters, warning, ARGUMENT_2, 1);
        addArgument(parameters, warning, ARGUMENT_3, 2);
        event.setParameters(parameters);
        return event;
    }

    public static int getPaoId(Map<String, Object> parameters){
        int paoId = Integer.parseInt(parameters.get(PAO_ID).toString());
        return paoId;
    }
    
    public static InfrastructureWarningType getWarningType(Map<String, Object> parameters) {
        return InfrastructureWarningType.valueOf(parameters.get(WARNING_TYPE).toString());
    }
    
    public static InfrastructureWarningSeverity getWarningSeverity(Map<String, Object> parameters) {
        return InfrastructureWarningSeverity.valueOf(parameters.get(WARNING_SEVERITY).toString());
    }
    
    public static Object[] getWarningArguments(Map<String, Object> parameters) {
       List<Object> args = new ArrayList<>();
       addArgument(args, parameters, ARGUMENT_1);
       addArgument(args, parameters, ARGUMENT_2);
       addArgument(args, parameters, ARGUMENT_3);
       return args.toArray();
    }
    
    private static void addArgument(List<Object> args, Map<String, Object> parameters, String argument) {
        if (parameters.get(argument) != null) {
            args.add(parameters.get(argument));
        }
    }

    private static void addArgument(Map<String, Object> parameters, InfrastructureWarning warning, String argument,
            int index) {
        try {
            parameters.put(argument, warning.getArguments()[index]);
        } catch (Exception e) {
            //argument wasn't present, ignore
        }
    }
}
