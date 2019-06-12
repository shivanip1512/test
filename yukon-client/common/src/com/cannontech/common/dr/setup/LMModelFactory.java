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
             loadGroup = new LoadGroupBase();
             break;
        case LM_GROUP_EXPRESSCOMM:
        case LM_GROUP_RFN_EXPRESSCOMM: 
             loadGroup = new LoadGroupExpresscom();
            break;

        case LM_GROUP_ITRON:
            loadGroup = new LoadGroupItron();
            break;
        }
        return loadGroup;
    }
}
