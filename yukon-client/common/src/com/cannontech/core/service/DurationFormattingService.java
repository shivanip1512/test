package com.cannontech.core.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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
        DH,
        HMS,
        HM,
        H,
        M,
        HM_SHORT;
        
        private static final String keyPrefix = "yukon.common.durationFormatting.pattern.";
        
        @Override
        public String getFormatKey() {
            return keyPrefix + name();
        }
    }

    /**
     * Method to get a formatted string representation of a time duration - uses simple math
     * to calculate days, mins, seconds ignoring time zones and daylight savings
     * @param duration - Duration (in milliseconds) to format
     * @param type - Format type for the time duration
     * @param yukonUserContext - Current user context
     * @return String representing duration in given format
     */
    public String formatDuration(final long duration, TimeUnit unit, final DurationFormat type,
            final YukonUserContext yukonUserContext);

    /**
     * Method to get a formatted string representation of a time period
     * @param startDate - Start date of period
     * @param endDate - End date of period
     * @param type - Format type for the time period
     * @param yukonUserContext - Current user context
     * @return String representing time period in given format
     */
    public String formatDuration(Date startDate, Date endDate, DurationFormat type, YukonUserContext yukonUserContext);
    
}
