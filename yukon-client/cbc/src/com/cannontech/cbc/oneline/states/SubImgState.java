package com.cannontech.cbc.oneline.states;

import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.yukon.cbc.CBCDisplay;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;

public class SubImgState implements OnelineState {

    private String groupName;

    public int getLiteStateId(StreamableCapObject o, int state) {
        SubBus subBus = (SubBus) o;
        CBCDisplay disp = new CBCDisplay();
        if (subBus.getCcDisableFlag().booleanValue()) {
            state = OnelineUtil.SUB_ST_DIS;
        } else if (subBus.getRecentlyControlledFlag().booleanValue()) {

            if (subBus.getRecentlyControlledFlag().booleanValue()) {
                state = OnelineUtil.SUB_ST_PENDING;
            }

        } else if (subBus.getSwitchOverStatus().booleanValue() && disp.isDualBusEnabled(subBus)) {
            state = OnelineUtil.SUB_ST_EN_ALBUS;
        }

        else
            state = OnelineUtil.SUB_ST_EN;
        return state;

    }

    public void setGroupName(String n) {
        groupName = n;
     }

}
