package com.cannontech.cbc.oneline.states;

import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.util.CBCDisplay;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;

public class DynamicLineState implements OnelineState {

    @SuppressWarnings("unused")
    private String groupName;

    public int getLiteStateId(StreamableCapObject o, int state) {
        CBCDisplay disp = new CBCDisplay();
        if (o instanceof SubBus) {
            SubBus subBus = (SubBus) o;
            
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
        else if (o instanceof Feeder) {
            Feeder feeder = (Feeder)o;
            
            
            if( feeder.getCcDisableFlag().booleanValue() )
            {
               state = OnelineUtil.SUB_ST_DIS;
            }
            else if( feeder.getRecentlyControlledFlag().booleanValue() )
            {
                if ( disp.getFeederPendingState( feeder ) != null)
                {
                    state = OnelineUtil.SUB_ST_PENDING;
                }
                
            }
            else
                state = OnelineUtil.SUB_ST_EN;;
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
