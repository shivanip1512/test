package com.cannontech.common.dr.setup;

import com.cannontech.common.pao.PaoType;

/**
 * Factory to return LM Model objects for a paoType
 */
public class LMModelFactory {
    public final static LoadGroupBase createLoadGroup(PaoType paoType) {

        LoadGroupBase loadGroup = null;

        switch (paoType) {
        case LM_GROUP_METER_DISCONNECT:
        case LM_GROUP_HONEYWELL:
        case LM_GROUP_ECOBEE:
        case LM_GROUP_NEST:
            loadGroup = new LoadGroupBase();
            break;
        case LM_GROUP_EXPRESSCOMM:
        case LM_GROUP_RFN_EXPRESSCOMM:
            loadGroup = new LoadGroupExpresscom();
            break;
        case LM_GROUP_DIGI_SEP:
            loadGroup = new LoadGroupDigiSep();
            break;
        case LM_GROUP_ITRON:
            loadGroup = new LoadGroupItron();
            break;
        case LM_GROUP_EMETCON:
            loadGroup = new LoadGroupEmetcon();
            break;

        }
        return loadGroup;
    }
}
