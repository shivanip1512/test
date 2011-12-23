package com.cannontech.cbc.oneline.elements;

import org.apache.commons.lang.Validate;

import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.cap.OnelineCap;
import com.cannontech.cbc.oneline.model.feeder.OnelineFeeder;
import com.cannontech.cbc.oneline.model.sub.OnelineSub;
import com.cannontech.cbc.oneline.states.OnelineState;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.esub.element.LineElement;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;

public class DynamicLineElement extends LineElement implements OnelineLxElement {

    private OnelineState dynamicState;
    private OnelineObject parentObject;

    public DynamicLineElement(OnelineObject o, OnelineState state) {
        parentObject = o;
        dynamicState = state;
    }

    public OnelineState getDynamicState() {
        return dynamicState;
    }

    public StreamableCapObject getStreamable(String compName) {
        if (parentObject instanceof OnelineSub) {
           OnelineSub sub = (OnelineSub) parentObject;
            return sub.getSubBus();
        }
        if (parentObject instanceof OnelineFeeder) {
            OnelineFeeder feeder = (OnelineFeeder)parentObject;
            return feeder.getStreamable();
        }
        if (parentObject instanceof OnelineCap)
        {
            OnelineCap cap = (OnelineCap) parentObject;
            return cap.getParentFeeder().getStreamable();
        }
        return null;
    }

    public boolean update(boolean change) {
        LiteStateGroup group = null;
        String compName = getName();
        group = OnelineUtil.getOnelineStateGroup(OnelineUtil.ONELN_STATE_GROUP);
        Validate.notNull(group,
                         "Oneline State Group is not mapped in the Database");
        LiteState ls = null;

        StreamableCapObject o = null;

        o = getStreamable(compName);
        try {
            int state = -1;
            state = dynamicState.getLiteStateId(o, state);
            ls = DaoFactory.getStateDao().findLiteState(group.getStateGroupID(),
                                                       state);

        } catch (Exception e) {
            CTILogger.error(e);
        }
        if (ls != null) {
            setCurrentColorState(ls);
            updateColor();
            change = true;
        }
        updateColor();
        return change;
    }



}
