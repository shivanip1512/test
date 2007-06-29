package com.cannontech.core.service;

import java.util.TimeZone;

import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;

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
        FULL("{default} {status||{unit}} {time|MM/dd/yyyy HH:mm:ss z}"),
        SHORT("{default} {status||{unit}}"),
        DATE("{time|MM/dd/yyyy HH:mm:ss z}");
        
        private final String format;

        private Format(String format) {
            this.format = format;
        }
        
        public String getFormat() {
            return format;
        }
    }

    /**
     * This method determines a time zone and then delegates to
     *   getValueString(PointValueHolder, Format, TimeZone);
     *   
     * This method should not be used if it is possible to use one of the
     * other two versions. This may make sense to use in log files, but
     * never for display on a web page.
     * 
     * @param value
     * @param format
     * @return
     */
    public String getValueString(PointValueHolder value, Format format);
    
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
    public String getValueString(PointValueHolder value, Format format, LiteYukonUser user);
    
    /**
     * This method looks up the format string using the Format enum. For the time being the 
     * format is stored in the Enum itself, but this probably isn't the approach that would
     * be taken in the long term. For instance, the enums could refer to role properties that
     * contain the actual format string.
     * 
     * @param value
     * @param format
     * @param timeZone
     * @return
     */
    public String getValueString(PointValueHolder value, Format format, TimeZone timeZone);

    /**
     * This method determines a time zone and then delegates to
     *   getValueString(PointValueHolder, String, TimeZone);
     *   
     * This method should not be used if it is possible to use one of the
     * other two versions. This may make sense to use in log files, but
     * never for display on a web page.
     * 
     * @param value
     * @param format
     * @return
     */
    public String getValueString(PointValueHolder value, String format);
    
    /**
     * Gets the time zone for user and then delegates to
     *   getValueString(PointValueHolder, String, TimeZone);
     *   
     * 
     * @param value
     * @param format
     * @param user
     * @return
     */
    public String getValueString(PointValueHolder value, String format, LiteYukonUser user);
    
    /**
     * Formats a PointValueHolder with a given format and timezone. The SimpleTemplateProcessor
     * is used to process the format, its documentation should be consulted for details on the
     * syntax of a format string.
     * 
     * The available keys that can be used within the format string are:
     *   value   - for status points, a String with the state text; 
     *             for other types, a Double with the current value
     *   default - for status points, same as 'value';
     *             for other types, the current value formatted as a string with the
     *             number of decimal places taken from PointUnit
     *   status  - a Boolean that is true for status points and false for all others
     *   state   - the state text or "" if not applicable
     *   unit    - the value returned from getUnitMeasureName() for the points unit of measure,
     *             may be "" if the point doesn't have an attached unit of measure
     *   time    - a Calendar with the PointValueHolder's timestamp and the timeZone set
     *             on it
     *             
     * The format string is scanned for instances of the above key's before the DAO calls to
     * retrieve the information are made.
     * 
     * @param value the value to format
     * @param format a SimpleTemplateProcessor compatible template
     * @param timeZone time zone to be used for formatting dates
     * @return
     */
    public String getValueString(PointValueHolder value, String format, TimeZone timeZone);

}