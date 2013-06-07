package com.cannontech.cbc.oneline.states;

import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.messaging.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.messaging.message.capcontrol.streamable.SubBus;

public class SubImgState implements OnelineState {

    private String groupName;

    public int getLiteStateId(StreamableCapObject o, int state) {
        SubBus subBus = (SubBus) o;
        if (subBus.getCcDisableFlag()) {
            state = OnelineUtil.SUB_ST_DIS;
        } else if (subBus.getRecentlyControlledFlag()) {

            if (subBus.getRecentlyControlledFlag()) {
                state = OnelineUtil.SUB_ST_PENDING;
            }

        } else if (subBus.getSwitchOverStatus() && CapControlUtils.isDualBusEnabled(subBus)) {
            state = OnelineUtil.SUB_ST_EN_ALBUS;
        }

        else
            state = OnelineUtil.SUB_ST_EN;
        return state;

    }

    public void setGroupName(String n) {
        groupName = n;
     }

    public String getGroupName() {
        return groupName;
    }

}
