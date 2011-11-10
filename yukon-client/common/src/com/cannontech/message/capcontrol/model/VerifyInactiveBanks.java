package com.cannontech.message.capcontrol.model;


public class VerifyInactiveBanks extends ItemCommand {
    
    long bankInactiveTime = -1;  // Default to -1?
    boolean disableOvUv;
    
    public VerifyInactiveBanks() {
        super();
        setCommandId(CommandType.VERIFY_INACTIVE_BANKS.getCommandId());
    }

    public VerifyInactiveBanks (int itemId, long bankInactiveTime, boolean disableOvUv) {
        setCommandId(CommandType.VERIFY_INACTIVE_BANKS.getCommandId());
        setItemId(itemId);
        
        this.bankInactiveTime = bankInactiveTime;
        this.disableOvUv = disableOvUv;
    }

    public boolean isDisableOvUv() {
        return disableOvUv;
    }
    
    public void setDisableOvUv(boolean disableOvUv) {
        this.disableOvUv = disableOvUv;
    }
    
    public long getCbInactivityTime() {
        return bankInactiveTime;
    }

    public void setCbInactivityTime(long cbInactivityTime) {
        this.bankInactiveTime = cbInactivityTime;
    }

}