package com.cannontech.messaging.message.capcontrol;

public class VerifySelectedBankMessage extends VerifyBanksMessage {

    long bankId = -1; // Default to -1?

    public VerifySelectedBankMessage() {
        super();
        setCommandId(CommandType.VERIFY_SELECTED_BANK.getCommandId());
    }

    public VerifySelectedBankMessage(int itemId, long bankId, boolean disableOvUv) {
        super(itemId, CommandType.VERIFY_SELECTED_BANK.getCommandId(), disableOvUv);

        this.bankId = bankId;
    }

    public long getBankId() {
        return bankId;
    }

    public void setBankId(long bankId) {
        this.bankId = bankId;
    }
}
