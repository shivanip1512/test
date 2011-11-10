package com.cannontech.web.capcontrol.models;

import java.util.List;

import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.message.capcontrol.streamable.Feeder;

public class ViewableFeeder {

	private Feeder feeder = null;
	private String subBusName = null;
	private boolean movedFeeder = false;
	private List<ViewableCapBank> capbanks = null;
	private boolean ivvcControlled = false;
    private boolean showTargetTooltip = true;
	
	public Feeder getFeeder() {
		return feeder;
	}

	public void setFeeder(Feeder feeder) {
		this.feeder = feeder;
	}

	public String getSubBusName() {
		return subBusName;
	}

	public void setSubBusName(String subBusName) {
		this.subBusName = subBusName;
	}
	
	public void setCapBanks(List<ViewableCapBank> capbanks) {
	    this.capbanks = capbanks;
	}
	
	public List<ViewableCapBank> getCapBanks() {
        return capbanks;
    }

    public boolean isMovedFeeder() {
        return movedFeeder;
    }

    public void setMovedFeeder(boolean movedFeeder) {
        this.movedFeeder = movedFeeder;
    }

    public boolean isIvvcControlled() {
        return ivvcControlled;
    }

    public void setIvvcControlled(boolean ivvcControlled) {
        this.ivvcControlled = ivvcControlled;
    }
    
    public boolean isIndividualFeederControlled() {
    	try {
	    	return getFeeder().getControlmethod() == ControlMethod.INDIVIDUAL_FEEDER;
    	} catch (IllegalArgumentException e) {
			return false;
		}
    }

    public boolean isShowTargetTooltip() {
        return showTargetTooltip;
    }

    public void setShowTargetTooltip(boolean showTargetTooltip) {
        this.showTargetTooltip = showTargetTooltip;
    }

}
