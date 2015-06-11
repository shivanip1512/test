package com.cannontech.capcontrol;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public enum ControlMethod implements DisplayableEnum {

    INDIVIDUAL_FEEDER("Individual Feeder", ControlAlgorithm.KVAR,
                                           ControlAlgorithm.VOLTS,
                                           ControlAlgorithm.MULTI_VOLT,
                                           ControlAlgorithm.MULTI_VOLT_VAR, 
                                           ControlAlgorithm.PFACTOR_KW_KVAR),

    SUBSTATION_BUS("Substation Bus", ControlAlgorithm.KVAR, 
                                     ControlAlgorithm.VOLTS,
                                     ControlAlgorithm.MULTI_VOLT,
                                     ControlAlgorithm.MULTI_VOLT_VAR,
                                     ControlAlgorithm.PFACTOR_KW_KVAR,
                                     ControlAlgorithm.INTEGRATED_VOLT_VAR),

    BUSOPTIMIZED_FEEDER("Bus Optimized Feeder", ControlAlgorithm.KVAR,
                                                ControlAlgorithm.VOLTS,
                                                ControlAlgorithm.PFACTOR_KW_KVAR, 
                                                ControlAlgorithm.INTEGRATED_VOLT_VAR),

    MANUAL_ONLY("Manual Only", ControlAlgorithm.KVAR, 
                               ControlAlgorithm.PFACTOR_KW_KVAR,
                               ControlAlgorithm.VOLTS),

    TIME_OF_DAY("Time of Day", ControlAlgorithm.TIME_OF_DAY),
    ;

    private String displayName;
    private ControlAlgorithm defaultAlgorithm;
    private Set<ControlAlgorithm> supportedAlgorithms;

    private ControlMethod(String displayName, ControlAlgorithm defaultAlgorithm,
            ControlAlgorithm... supportedAlgorithms) {
        this.displayName = displayName;
        this.defaultAlgorithm = defaultAlgorithm;
        
        ImmutableSet.Builder<ControlAlgorithm> builder = ImmutableSet.builder();
        builder.add(defaultAlgorithm);
        builder.addAll(Arrays.asList(supportedAlgorithms));
        
        this.supportedAlgorithms = builder.build();
    }

    public String getDisplayName() {
        return displayName;
    }

    public ControlAlgorithm getDefaultAlgorithm() {
        return defaultAlgorithm;
    }

    public Set<ControlAlgorithm> getSupportedAlgorithms() {
        return supportedAlgorithms;
    }

    public boolean isBusControlled() {
        return this == BUSOPTIMIZED_FEEDER || this == SUBSTATION_BUS;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.capcontrol.controlMethod." + name();
    }

    private static final Map<ControlMethod, Set<ControlAlgorithm>> methodToAlgorithms;
    
    static {
        ImmutableMap.Builder<ControlMethod, Set<ControlAlgorithm>> builder = ImmutableMap.builder();
        for (ControlMethod method : ControlMethod.values()) {
            builder.put(method, method.getSupportedAlgorithms());
        }
        
        methodToAlgorithms = builder.build();
    }
    
    public static Map<ControlMethod, Set<ControlAlgorithm>> getMethodToAlgorithms() {
        return methodToAlgorithms;
    }

}