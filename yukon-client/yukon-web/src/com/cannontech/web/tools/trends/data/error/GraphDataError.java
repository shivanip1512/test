
/**
 * Enum GraphDataError
 * provides a error state to push to the data payload delivered to the view via ajax/jquery  
 * <ul>
 * <li>No Trend Data Available means the query for the point in reference has no data therefor 
 * it is an empty payload/sub payload.
 * <li>DB_ERROR this occurswhen making the requst a transactions fails. Post it to the client
 * <li>Parse Error happens when an exception is throw, non-specific to a database request.   
 * </ul>
 *
 * @author      Thomas Red-Cloud
 * @email       ThomasRedCloud@Eaton.com
 * @version     %I%, %G%
 * @since       1.0
 */package com.cannontech.web.tools.trends.data.error;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.web.tools.trends.data.GraphType;

public enum GraphDataError implements DisplayableEnum {
    NO_TREND_DATA_AVAILABLE, DB_ERROR, PARSE_ERROR;

    /**
     * getFormatKey
     * retrieves "this" instance of the {@link GraphDataError} name i.e. class name and delivers it as the entire namespace.
     * @return {@link String}
     */
    @Override public String getFormatKey() {
        return "yukon.web.modules.tools.trend.graphDataState." + name();
    }
}
