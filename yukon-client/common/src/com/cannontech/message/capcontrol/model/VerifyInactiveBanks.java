package com.cannontech.message.capcontrol.model;


public class VerifyInactiveBanks extends VerifyBanks {
    
    long bankInactiveTime = -1;  // Default to -1?
    
    public VerifyInactiveBanks() {
        super();
        setCommandId(CommandType.VERIFY_INACTIVE_BANKS.getCommandId());
    }

    public VerifyInactiveBanks (int itemId, long bankInactiveTime, boolean disableOvUv) {
        setCommandId(CommandType.VERIFY_INACTIVE_BANKS.getCommandId());
        setItemId(itemId);
        setDisableOvUv(disableOvUv);
        
        this.bankInactiveTime = bankInactiveTime;
    }
    
    public long getCbInactivityTime() {
        return bankInactiveTime;
    }

    public void setCbInactivityTime(long cbInactivityTime) {
        this.bankInactiveTime = cbInactivityTime;
    }

}