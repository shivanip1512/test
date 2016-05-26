package com.cannontech.web.input;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;

/**
 * States exist only in relationship to State Groups, so any web parameters
 * for a state are in a paired format:
 *      SG:S     [StateGroupId]:[StateId]       eg. 55:2
 * 
 * This PES works when a single pair is entered directly from the web page, ie. no encodings.
 * 
 * This is a more passive binding since there'll be times when no
 * state exists for an attribute (eg: no inputs).
 */
public class StateIdPairingPropertyEditor extends PropertyEditorSupport {
    @Autowired private StateGroupDao stateGroupDao;

    /**
     * @param stateGroupIdAndStateId String         [stateGroupId]:[stateId]        eg. 55:31
     */
    @Override
    public void setAsText(String stateGroupIdAndStateId) throws IllegalArgumentException {
        setValue(null);
        if (stateGroupIdAndStateId == null) {
            return;
        }
        final int iSplit = stateGroupIdAndStateId.indexOf(":");
        if (iSplit < 1) {
            return;
        }
        final String stateGroupId = stateGroupIdAndStateId.substring(0, iSplit);
        final String stateId = stateGroupIdAndStateId.substring(iSplit + 1);
        final int iStateId = Integer.valueOf(stateId);

        // Try finding the correct State object per the 2 IDs
        final LiteStateGroup stateGroup = stateGroupDao.getStateGroup(Integer.valueOf(stateGroupId));
        for (LiteState state: stateGroup.getStatesList()) {
            if (iStateId == state.getLiteID()) {
                setValue(state);
                break;
            }
        }
    }
    @Override
    public String getAsText() {
        final LiteState state = (LiteState) getValue();
        if (state == null) {
            return null;
        }
        return String.valueOf(state.getStateText());
    }
}
