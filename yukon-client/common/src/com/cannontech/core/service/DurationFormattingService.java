package com.cannontech.core.service;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.user.YukonUserContext;

/**
 * Please see the com/cannontech/yukon/common/durationFormatting.xml file
 * for the actual format strings.
 * 
 * @author nmeverden
 *
 */
public interface DurationFormattingService {

    public enum DurationFormat implements DisplayableEnum {
        DHMS,
        HMS,
        HM,
        H,
        M;
        
        private static final String keyPrefix = "yukon.common.durationFormatting.pattern.";
        
        @Override
        public String getFormatKey() {
            return keyPrefix + name();
        }
    }
    
    public String formatDuration(long duration, DurationFormat type, YukonUserContext yukonUserContext);
    
}
