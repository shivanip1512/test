package com.cannontech.cbc.oneline.states;

import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.messaging.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.messaging.message.capcontrol.streamable.Feeder;
import com.cannontech.messaging.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.messaging.message.capcontrol.streamable.SubBus;

public class PanelTextState implements OnelineState {

    private String groupName;

    public int getLiteStateId(StreamableCapObject o, int state) {

        if (o instanceof SubBus) {
            if (groupName.equalsIgnoreCase(OnelineUtil.ONELN_STATE_GROUP)) {
                state = (((SubBus) o).getCcDisableFlag()) ? OnelineUtil.SUB_ST_EN
                        : OnelineUtil.SUB_ST_DIS;
            } else if (groupName.equalsIgnoreCase(OnelineUtil.ONELN_VERIFY_GROUP)) {
                //if sub under verification than show 'Verify Stop' command
                //if not than show 'Verify All' command
                //ps - we have a status group named accordingly for this to work
                state = ((SubBus) o).getVerificationFlag() ? OnelineUtil.VERIFY_DIS
                        : OnelineUtil.VERIFY_EN;
            }
        } else if (o instanceof Feeder) {
            state = (((Feeder) o).getCcDisableFlag()) ? OnelineUtil.SUB_ST_EN
                    : OnelineUtil.SUB_ST_DIS;
        } else if (o instanceof CapBankDevice) {
            state = (((CapBankDevice) o).getCcDisableFlag()) ? OnelineUtil.SUB_ST_EN
                    : OnelineUtil.SUB_ST_DIS;
        }
        return state;

    }

    public void setGroupName(String n) {
        groupName = n;
    }

    public String getGroupName() {
        return groupName;
    }

}
