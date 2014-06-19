package com.cannontech.amr.phaseDetect.data;

import java.util.Set;

import com.cannontech.enums.Phase;
import com.google.common.collect.ImmutableSet;

public enum DetectedPhase {
    A(Phase.A),
    AB(Phase.A, Phase.B),
    AC(Phase.A, Phase.C),
    B(Phase.B),
    BC(Phase.B, Phase.C),
    C(Phase.C),
    ABC(Phase.A, Phase.B,Phase.C),
    UNKNOWN();
    
    private final Set<Phase> phaseSet;
    
    private DetectedPhase(Phase... phases){
        phaseSet = ImmutableSet.copyOf(phases);
    }
    
    public Set<Phase> getPhaseSet() {
        return phaseSet;
    }
    
    static public DetectedPhase getPhase(Set<Phase> type) {
        for (DetectedPhase value : DetectedPhase.values()) {
            if (type.equals(value.phaseSet)) {
                return value;
            }
        }
        throw new IllegalArgumentException();
    }
}
