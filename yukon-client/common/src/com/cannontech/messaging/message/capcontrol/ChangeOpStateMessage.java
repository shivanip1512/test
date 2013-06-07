package com.cannontech.messaging.message.capcontrol;

import com.cannontech.capcontrol.BankOpState;

public class ChangeOpStateMessage extends ItemCommandMessage {

    private BankOpState state;

    public ChangeOpStateMessage() {}

    public ChangeOpStateMessage(int bankId) {
        super(CommandType.CHANGE_OP_STATE.getCommandId(), bankId);
    }

    public void setState(BankOpState state) {
        this.state = state;
    }

    public BankOpState getState() {
        return state;
    }
}
