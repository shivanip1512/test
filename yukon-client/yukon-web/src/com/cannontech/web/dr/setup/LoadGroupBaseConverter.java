package com.cannontech.web.dr.setup;

import org.apache.logging.log4j.Logger;
import org.springframework.core.convert.converter.Converter;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.dr.setup.LoadGroupDigiSep;
import com.cannontech.common.dr.setup.LoadGroupDisconnect;
import com.cannontech.common.dr.setup.LoadGroupEcobee;
import com.cannontech.common.dr.setup.LoadGroupEmetcon;
import com.cannontech.common.dr.setup.LoadGroupExpresscom;
import com.cannontech.common.dr.setup.LoadGroupHoneywell;
import com.cannontech.common.dr.setup.LoadGroupItron;
import com.cannontech.common.dr.setup.LoadGroupMCT;
import com.cannontech.common.dr.setup.LoadGroupNest;
import com.cannontech.common.dr.setup.LoadGroupRipple;
import com.cannontech.common.dr.setup.LoadGroupVersacom;
import com.cannontech.common.pao.PaoType;

/**
 * Converter class for load group base.
 * This will take groupType as string and return the appropriate object.
 * Converter is required when a inherited class have to be passed for a base class input.
 */
public class LoadGroupBaseConverter implements Converter<String, LoadGroupBase> {
    private static final Logger log = YukonLogManager.getLogger(LoadGroupBaseConverter.class);

    @Override
    public LoadGroupBase convert(String groupType) {
        LoadGroupBase loadGroup = null;
        PaoType paoType = null;
        try {
            paoType = PaoType.valueOf(groupType);
        } catch (IllegalArgumentException e) {
            log.error(groupType + " pao type doesn't match with existing pao types", e);
        }
        switch (paoType) {
        case LM_GROUP_EXPRESSCOMM:
        case LM_GROUP_RFN_EXPRESSCOMM:
            loadGroup = new LoadGroupExpresscom();
            break;
        case LM_GROUP_ITRON:
            loadGroup = new LoadGroupItron();
            break;
        case LM_GROUP_DIGI_SEP:
            loadGroup = new LoadGroupDigiSep();
            break;
        case LM_GROUP_EMETCON:
            loadGroup = new LoadGroupEmetcon();
            break;
        case LM_GROUP_VERSACOM:
            loadGroup = new LoadGroupVersacom();
            break;
        case LM_GROUP_ECOBEE:
            loadGroup = new LoadGroupEcobee();
            break;
        case LM_GROUP_HONEYWELL:
            loadGroup = new LoadGroupHoneywell();
            break;
        case LM_GROUP_METER_DISCONNECT:
            loadGroup = new LoadGroupDisconnect();
            break;
        case LM_GROUP_NEST:
            loadGroup = new LoadGroupNest();
            break;
        case LM_GROUP_MCT:
            loadGroup = new LoadGroupMCT();
            break;
        case LM_GROUP_RIPPLE:
            loadGroup = new LoadGroupRipple();
            break;
        }
        return loadGroup;
        }
}
