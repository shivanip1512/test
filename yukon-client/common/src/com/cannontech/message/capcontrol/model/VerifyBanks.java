package com.cannontech.message.capcontrol.model;


public class VerifyBanks extends ItemCommand {
    
    boolean disableOvUv;
	
	public VerifyBanks() {
		super();
	}

	public VerifyBanks (int itemId, int commandId, boolean disableOvUv) {
	    setItemId(itemId);
	    setCommandId(commandId);
		this.disableOvUv = disableOvUv;
	}

	public boolean isDisableOvUv() {
	    return disableOvUv;
	}
	
	public void setDisableOvUv(boolean disableOvUv) {
	    this.disableOvUv = disableOvUv;
	}
	
}