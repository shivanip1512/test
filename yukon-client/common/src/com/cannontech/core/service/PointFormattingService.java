package com.cannontech.core.service;

import com.cannontech.common.util.FormattingTemplateProcessor;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.user.YukonUserContext;

/**
 * This service is responsible for formatting PointValueHolder objects (which currently
 * only consist of PointData message) into a String. All of these methods end up
 * just calling
 *   getValueString(PointValueHolder value, String format, TimeZone timeZone)
 * So, the documentation there should be consulted for specifics on the format.
 * 
 * If you can't decide which version of these methods to use, please use
 *   getValueString(<value>, Format.FULL, <user>);
 * This is the most generic and allows for the most future expansion.
 */
public interface PointFormattingService {
    
    static public enum Format {
        FULL,
        SHORT,
        SHORTDATE,
        DATE,
        RAWVALUE,
        VALUE,
        VALUE_UNIT,
        UNIT,
        QUALITY,
        SHORT_QUALITY,
        DATE_QUALITY;
        
        private final static String keyPrefix = "yukon.common.point.pointFormatting.formats.";

        public String getFormatKey() {
            return keyPrefix + name();
        }
    }

    /**
     * Gets the time zone for user and then delegates to
     *   getValueString(PointValueHolder, Format, TimeZone);
     *   
     * This is the preferred method of formatting a PointValueHolder with this service.
     *   
     * @param value
     * @param format
     * @param user
     * @return
     */
    public String getValueString(PointValueHolder value, Format format, YukonUserContext userContext);
    
    /**
     * Formats a PointValueHolder with a given format and timezone. The SimpleTemplateProcessor
     * is used to process the format, its documentation should be consulted for details on the
     * syntax of a format string.
     * 
     * The available keys that can be used within the format string are:
     *   value      - for status points, a String with the state text; 
     *                for other types, a Double with the current value
     *   decimals   - the number of decimal digits the point is configured for
     *   default    - for status points, same as 'value';
     *                for other types, the current value formatted as a String with the
     *                number of decimal places taken from PointUnit
     *   status     - a Boolean that is true for status points and false for all others
     *   state      - the state text or "" if not applicable
     *   unit       - the value returned from getUnitMeasureName() for the points unit of measure,
     *                may be "" if the point doesn't have an attached unit of measure
     *   time       - a Date with the PointValueQualityHolder's timestamp 
	 *   stateColor - HTML-style color for the current status state  
	 *   rawValue   - the original Double from the PointValueHolder
	 *   quality	- the PointValueQualityHolder's PointQuality enum value 
	 *           
     * The format string is scanned for instances of the above key's before the DAO calls to
     * retrieve the information are made.
     * 
     * @param value the value to format
     * @param format a SimpleTemplateProcessor compatible template
     * @param timeZone time zone to be used for formatting dates
     * @see FormattingTemplateProcessor
     * @return
     */
    public String getValueString(PointValueHolder value, String format, YukonUserContext userContext);
    
    public CachingPointFormattingService getCachedInstance();

}