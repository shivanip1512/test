package com.cannontech.message.capcontrol.model;

import com.cannontech.capcontrol.BankOpState;

public class ChangeOpState extends ItemCommand {

    private BankOpState state;
    
    public ChangeOpState() {}
    
    public ChangeOpState(int bankId) {
        setItemId(bankId);
        setCommandId(CommandType.CHANGE_OP_STATE.getCommandId());
    }
    
    public void setState(BankOpState state) {
        this.state = state;
    }
    
    public BankOpState getState() {
        return state;
    }
    
}