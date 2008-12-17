package com.cannontech.web.stars.dr;

import com.cannontech.stars.dr.displayable.model.DisplayableInventory;

/**
 * Data transfer object for opt out counts
 */
public class OptOutCountDto {

	private DisplayableInventory inventory;
	private int usedOptOuts = 0;
	private int remainingOptOuts = 0;

	public DisplayableInventory getInventory() {
		return inventory;
	}

	public void setInventory(DisplayableInventory inventory) {
		this.inventory = inventory;
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

}
