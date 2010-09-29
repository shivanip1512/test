package com.cannontech.capcontrol;

import java.util.Set;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableSet;

public enum ControlMethod implements DatabaseRepresentationSource {
    
	INDIVIDUAL_FEEDER("IndividualFeeder", "Individual Feeder", ControlAlgorithm.KVAR, ControlAlgorithm.MULTIVOLT, 
	                  ControlAlgorithm.MULTIVOLTVAR, ControlAlgorithm.PFACTORKWKVAR, ControlAlgorithm.VOLTS),
	                  
	SUBSTATION_BUS("SubstationBus", "Substation Bus", ControlAlgorithm.INTEGRATED_VOLT_VAR, ControlAlgorithm.KVAR, 
	               ControlAlgorithm.MULTIVOLT, ControlAlgorithm.MULTIVOLTVAR, ControlAlgorithm.PFACTORKWKVAR, ControlAlgorithm.VOLTS),
	               
	BUSOPTIMIZED_FEEDER("BusOptimizedFeeder", "Bus Optimized Feeder", ControlAlgorithm.KVAR, ControlAlgorithm.MULTIVOLT, 
	                    ControlAlgorithm.MULTIVOLTVAR, ControlAlgorithm.PFACTORKWKVAR, ControlAlgorithm.VOLTS),
	                    
	MANUAL_ONLY("ManualOnly", "Manual Only", ControlAlgorithm.KVAR, ControlAlgorithm.MULTIVOLT, ControlAlgorithm.MULTIVOLTVAR, 
	            ControlAlgorithm.PFACTORKWKVAR, ControlAlgorithm.VOLTS),
	            
	TIME_OF_DAY("TimeOfDay", "Time of Day", ControlAlgorithm.TIME_OF_DAY),
	NONE("NONE", "NONE");
	
	private String dbName;
	private String displayName;
	private Set<ControlAlgorithm> supportedAlgorithms;
	
	private ControlMethod(String dbName, String displayName, ControlAlgorithm... supportedAlgorithms) {
		this.dbName = dbName;
		this.displayName = displayName;
		this.supportedAlgorithms = ImmutableSet.of(supportedAlgorithms);
	}
	
	public static ControlMethod getForDbString(String type) {
		for (ControlMethod controlMethod : ControlMethod.values()) {
			if ( controlMethod.getDatabaseRepresentation().equals(type)) {
				return controlMethod;
			}
		}
		throw new IllegalArgumentException("Invalid control method: " + type);
	}
	
	@Override
	public String getDatabaseRepresentation() {
		return dbName;
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