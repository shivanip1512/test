package com.cannontech.web.dr.setup;

import org.apache.logging.log4j.Logger;
import org.springframework.core.convert.converter.Converter;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LoadGroupCopy;
import com.cannontech.common.pao.PaoType;

/**
 * Class to convert appropriate Load Group copy object based upon the group type
 */
public class LMCopyConverter implements Converter<String, LMCopy> {
    
    private static final Logger log = YukonLogManager.getLogger(LMCopyConverter.class);

    @Override
    public LMCopy convert(String groupType) {
        LMCopy loadGroup = null;
        PaoType paoType = null;
        
        try {
            paoType = PaoType.valueOf(groupType);
        } catch (IllegalArgumentException e) {
            log.error(groupType + " pao type doesn't match with existing pao types", e);
        }

        switch (paoType) {
        case LM_GROUP_EXPRESSCOMM:
        case LM_GROUP_EMETCON:
        case LM_GROUP_VERSACOM:
        case LM_GROUP_MCT:
        case LM_GROUP_RIPPLE:
            loadGroup = new LoadGroupCopy();
            break;
        default:
            loadGroup = new LMCopy();
            break;
        }
        return loadGroup;
    }

}
