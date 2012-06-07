package com.cannontech.stars.dr.optout.model;

import com.cannontech.stars.dr.optout.service.OptOutService;

/**
 * Model object used to hold counts related to Opt Outs
 */
public class OptOutCountHolder {

	private Integer inventoryId;
	private int usedOptOuts = 0;
	private int remainingOptOuts = 0;
	private int scheduledOptOuts = 0;

	public Integer getInventoryId() {
		return inventoryId;
	}

	public void setInventory(Integer inventory) {
		this.inventoryId = inventory;
	}

	public int getUsedOptOuts() {
		return usedOptOuts;
	}

	public void setUsedOptOuts(int usedOptOuts) {
		this.usedOptOuts = usedOptOuts;
	}

	public int getRemainingOptOuts() {
		return remainingOptOuts;
	}

	public void setRemainingOptOuts(int remainingOptOuts) {
		this.remainingOptOuts = remainingOptOuts;
	}
	
	public boolean isOptOutsRemaining() {
	    if(remainingOptOuts == OptOutService.NO_OPT_OUT_LIMIT) {
	        return true;
	    }
	    
	    return remainingOptOuts - scheduledOptOuts > 0;
	}

    public int getScheduledOptOuts() {
        return scheduledOptOuts;
    }

    public void setScheduledOptOuts(int scheduledOptOuts) {
        this.scheduledOptOuts = scheduledOptOuts;
    }
	
	
}