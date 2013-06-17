package com.cannontech.message.capcontrol.model;

public class VerifySelectedBank extends VerifyBanks {
    
    long bankId = -1;  // Default to -1?
    
    public VerifySelectedBank() {
        super();
        setCommandId(CommandType.VERIFY_SELECTED_BANK.getCommandId());
    }

    public VerifySelectedBank (int itemId, long bankId, boolean disableOvUv) {
        setCommandId(CommandType.VERIFY_SELECTED_BANK.getCommandId());
        setItemId(itemId);
        setDisableOvUv(disableOvUv);
        
        this.bankId = bankId;
    }
    
    public long getBankId() {
        return bankId;
    }

    public void setBankId(long bankId) {
        this.bankId = bankId;
    }
}
