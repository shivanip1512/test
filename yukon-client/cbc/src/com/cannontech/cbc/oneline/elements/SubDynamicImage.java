package com.cannontech.cbc.oneline.elements;

import org.apache.commons.lang.Validate;

import com.cannontech.cbc.oneline.states.OnelineState;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.esub.element.StateImage;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;

public class SubDynamicImage extends StateImage implements OnelineLxElement {
    SubBus subBusMsg;
    OnelineState dynamicState;

    public SubDynamicImage(SubBus msg, OnelineState state) {
        subBusMsg = msg;
        dynamicState = state;
    }

    public OnelineState getDynamicState() {
        return dynamicState;
    }

    public StreamableCapObject getStreamable(String compName) {
        return subBusMsg;
    }

    public boolean update(boolean change) {
        LiteStateGroup group = null;

        group = OnelineUtil.getOnelineStateGroup(OnelineUtil.ONELN_STATE_GROUP);
        Validate.notNull(group,
                         "Oneline State Group is not mapped in the Database");
        LiteState ls = null;
        int state = -1;
        state = dynamicState.getLiteStateId(subBusMsg, state);

        ls = DaoFactory.getStateDao().getLiteState(group.getStateGroupID(),
                                                   state);

        if (ls != null) {
            setCurrentState(ls);
            updateImage();
            change = true;
        }
        updateImage();
        return change;
    }

}
