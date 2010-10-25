package com.cannontech.capcontrol;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public enum ControlMethod {
    
	INDIVIDUAL_FEEDER("Individual Feeder", ControlAlgorithm.KVAR, ControlAlgorithm.MULTI_VOLT, 
	                  ControlAlgorithm.MULTI_VOLT_VAR, ControlAlgorithm.PFACTOR_KW_KVAR, ControlAlgorithm.VOLTS),
	                  
	SUBSTATION_BUS("Substation Bus", ControlAlgorithm.INTEGRATED_VOLT_VAR, ControlAlgorithm.KVAR, 
	               ControlAlgorithm.MULTI_VOLT, ControlAlgorithm.MULTI_VOLT_VAR, ControlAlgorithm.PFACTOR_KW_KVAR, ControlAlgorithm.VOLTS),
	               
	BUSOPTIMIZED_FEEDER("Bus Optimized Feeder", ControlAlgorithm.KVAR, ControlAlgorithm.MULTI_VOLT, 
	                    ControlAlgorithm.MULTI_VOLT_VAR, ControlAlgorithm.PFACTOR_KW_KVAR, ControlAlgorithm.VOLTS),
	                    
	MANUAL_ONLY("Manual Only", ControlAlgorithm.KVAR, ControlAlgorithm.MULTI_VOLT, ControlAlgorithm.MULTI_VOLT_VAR, 
	            ControlAlgorithm.PFACTOR_KW_KVAR, ControlAlgorithm.VOLTS),
	            
	TIME_OF_DAY("Time of Day", ControlAlgorithm.TIME_OF_DAY),
	NONE( "NONE");
	
	private String displayName;
	private Set<ControlAlgorithm> supportedAlgorithms;
	
	private ControlMethod(String displayName, ControlAlgorithm... supportedAlgorithms) {
		this.displayName = displayName;
		this.supportedAlgorithms = ImmutableSet.of(supportedAlgorithms);
	}
	
	public static ControlMethod getForDisplayName(String displayName) {
		for (ControlMethod controlMethod : ControlMethod.values()) {
			if ( controlMethod.getDisplayName().equals(displayName)) {
				return controlMethod;
			}
		}
		throw new IllegalArgumentException("Invalid control method: " + displayName);
	}
	
	public String getDisplayName() {
        return displayName;
    }
	
	public Set<ControlAlgorithm> getSupportedAlgorithms() {
        return supportedAlgorithms;
    }

    public boolean supportsAlgorithm(ControlAlgorithm algorithm) {
        return supportedAlgorithms.contains(algorithm);
    }
	
}