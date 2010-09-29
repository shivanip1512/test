package com.cannontech.amr;

import com.cannontech.common.i18n.DisplayableEnum;

public enum MonitorEvaluatorStatus implements DisplayableEnum{

	ENABLED("Enabled"),
	DISABLED("Disabled"),
	;
	
	private final String keyPrefix = "yukon.web.modules.amr.monitor.";
	
	MonitorEvaluatorStatus(String description) {
		this.description = description;
	}
	
	private String description;
	
	public String getDescription() {
		return description;
	}
	
	public static MonitorEvaluatorStatus invert(MonitorEvaluatorStatus current) {
	    
	    MonitorEvaluatorStatus inverted;
	    
	    if (current.equals(DISABLED)) {
            inverted = ENABLED;
        } else {
            inverted = DISABLED;
        }
	    
	    return inverted;
	}

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
