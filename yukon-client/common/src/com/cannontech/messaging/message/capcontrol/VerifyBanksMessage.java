package com.cannontech.messaging.message.capcontrol;

public class VerifyBanksMessage extends ItemCommandMessage {

    boolean disableOvUv;

    public VerifyBanksMessage() {
        super();
    }

    public VerifyBanksMessage(int itemId, int commandId, boolean disableOvUv) {
        super(itemId, commandId);
        this.disableOvUv = disableOvUv;
    }

    public boolean isDisableOvUv() {
        return disableOvUv;
    }

    public void setDisableOvUv(boolean disableOvUv) {
        this.disableOvUv = disableOvUv;
    }

}
