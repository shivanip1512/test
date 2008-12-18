package com.cannontech.stars.dr.optout.model;

public enum OverrideStatus {

    Active, Cancelled, Scheduled;
    
    public static OverrideStatus valueOf(OptOutEventState state) {
    	
    	if (state == OptOutEventState.START_OPT_OUT_SENT) {
    		return OverrideStatus.Active;
    	} else if (state == OptOutEventState.CANCEL_SENT) {
    		return OverrideStatus.Cancelled;
    	}  else if (state == OptOutEventState.SCHEDULED) {
    		return OverrideStatus.Scheduled;
    	}
    	return null;
    }

}
