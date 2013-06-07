package com.cannontech.messaging.message.capcontrol;

public class VerifyInactiveBanksMessage extends VerifyBanksMessage {

    long bankInactiveTime = -1; // Default to -1?

    public VerifyInactiveBanksMessage() {
        super();
        setCommandId(CommandType.VERIFY_INACTIVE_BANKS.getCommandId());
    }

    public VerifyInactiveBanksMessage(int itemId, long bankInactiveTime, boolean disableOvUv) {
        super(itemId, CommandType.VERIFY_INACTIVE_BANKS.getCommandId(), disableOvUv);

        this.bankInactiveTime = bankInactiveTime;
    }

    public long getCbInactivityTime() {
        return bankInactiveTime;
    }

    public void setCbInactivityTime(long cbInactivityTime) {
        this.bankInactiveTime = cbInactivityTime;
    }

}
