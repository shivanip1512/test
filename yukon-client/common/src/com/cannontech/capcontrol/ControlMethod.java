package com.cannontech.capcontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public enum ControlMethod implements DisplayableEnum {
    
	INDIVIDUAL_FEEDER("Individual Feeder",
	                  true,
	                  ControlAlgorithm.KVAR,
	                  ControlAlgorithm.KVAR, ControlAlgorithm.MULTI_VOLT, ControlAlgorithm.MULTI_VOLT_VAR, 
	                  ControlAlgorithm.PFACTOR_KW_KVAR, ControlAlgorithm.VOLTS),
	                  
	SUBSTATION_BUS("Substation Bus",
	               true,
	               ControlAlgorithm.KVAR,
	               ControlAlgorithm.INTEGRATED_VOLT_VAR, ControlAlgorithm.KVAR, ControlAlgorithm.MULTI_VOLT, 
	               ControlAlgorithm.MULTI_VOLT_VAR, ControlAlgorithm.PFACTOR_KW_KVAR, ControlAlgorithm.VOLTS),
	               
	BUSOPTIMIZED_FEEDER("Bus Optimized Feeder",
	                    true,
	                    ControlAlgorithm.KVAR,
	                    ControlAlgorithm.KVAR, ControlAlgorithm.PFACTOR_KW_KVAR, ControlAlgorithm.VOLTS),
	                    
	MANUAL_ONLY("Manual Only",
                true,
	            ControlAlgorithm.KVAR,
	            ControlAlgorithm.KVAR, ControlAlgorithm.PFACTOR_KW_KVAR, ControlAlgorithm.VOLTS),
	            
	TIME_OF_DAY("Time of Day",
	            true,
	            ControlAlgorithm.TIME_OF_DAY,
	            ControlAlgorithm.TIME_OF_DAY),
	
	NONE("NONE",
	     false,
	     null);
	
	private String displayName;
	private boolean display;
	private ControlAlgorithm defaultAlgorithm;
	private Set<ControlAlgorithm> supportedAlgorithms;
	private static List<ControlMethod> valuesForDisplay;
	
	static {
	    ArrayList<ControlMethod> controlMethods = Lists.newArrayList();
        for (ControlMethod controlMethod : values()) {
            if(controlMethod.display) controlMethods.add(controlMethod);
        }
        valuesForDisplay = ImmutableList.copyOf(controlMethods);
	}
	
	private ControlMethod(String displayName, boolean display, ControlAlgorithm defaultAlgorithm, ControlAlgorithm... supportedAlgorithms) {
		this.displayName = displayName;
		this.display = display;
		this.defaultAlgorithm = defaultAlgorithm;
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
	
	public static List<ControlMethod> valuesForDisplay() {
	    return valuesForDisplay;
	}
	
	public String getDisplayName() {
        return displayName;
    }
	
	public boolean isDisplayed() {
	    return display;
	}
	
	public void setDefaultAlgorithm(ControlAlgorithm defaultAlgorithm) {
	    this.defaultAlgorithm = defaultAlgorithm;
	}
	
	public ControlAlgorithm getDefaultAlgorithm() {
	    return defaultAlgorithm;
	}
	
	public Set<ControlAlgorithm> getSupportedAlgorithms() {
        return supportedAlgorithms;
    }

    public boolean supportsAlgorithm(ControlAlgorithm algorithm) {
        return supportedAlgorithms.contains(algorithm);
    }
    
    public boolean isBusControlled() {
        return this == BUSOPTIMIZED_FEEDER || this == SUBSTATION_BUS;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.capcontrol.controlMethod." + name();
    }
    
}