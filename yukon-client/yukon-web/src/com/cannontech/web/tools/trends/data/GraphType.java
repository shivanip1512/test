/**
 * Enum GraphType
 * <p>
 * GraphType is responsible of providing a type to graph data. The relationships contained are:
 * <ul>
 * <li> From DataSource
 * <li> to native model contained
 * <li> to cannonical label defined
 * <li> to type payload definition
 * </ul>
 *
 * <p>
 * This embodies the legacy format for each type as descibed in the datasource.
 * 
 * The types defined are:
 * <ul>
 * <li>Basic Graph -> trend interval default
 * <li>Usage Graph -> Energy usage
 * <li>Peak Graph -> Actual peak day data
 * <li>Yesterday Graph-> Yesterday data
 * <li>Date Graph -> User defined timestamp -> delivers all points for that day
 * <li>Marker -> Used to provide a horizontal marker on the chart. 
 * </ul>
 * 
 * TODO: There are non graphs defined, and they are used to primarily shunt the response default to the the graph for
 * now until it is determined whether or not we will be using them in future. TBD. 
 * 
 * @author      Thomas Red-Cloud
 * @email       ThomasRedCloud@Eaton.com
 * @version     %I%, %G%
 * @since       1.0
 */
package com.cannontech.web.tools.trends.data;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.core.dao.GraphDao;

public enum GraphType implements DisplayableEnum {
    BASIC_GRAPH_TYPE(0x0009), 
    USAGE_GRAPH_TYPE(0x0005), 
    PEAK_GRAPH_TYPE(0x0011), 
    YESTERDAY_GRAPH_TYPE(0x0021),
    DATE_GRAPH_TYPE(0x0081),
    GRAPH_TYPE(0x0001),
    PRIMARY_TYPE(0x0002), 
    USAGE_TYPE(0x0004), 
    BASIC_TYPE(0x0008), 
    PEAK_TYPE(0x0010), 
    YESTERDAY_TYPE(0x0020), 
    MARKER_TYPE(0x0041), 
    DATE_TYPE(0x0080);
    
    /**
     * The {@link int} datasource representation of each enumerable graph
     */
    private int value;
    
    /**
     * Class constructor
     * serializes each enumerable with a specific value to coincide with datasource entry
     * <p>
     * @param {@link int} in hex, the value contained in datasource
     * @return void
     */
    
    private GraphType(int hexval) {
        this.value = hexval;
    }
    /**
     * getValue
     * retrieves the {@link int} value defined in the datasource
     * @return {@link int}
     */
    public int getValue() {
        return this.value;
    }
    
    /**
     * getByType
     *  retrieves the enumerable associate to the int definition from datasource. 
     * <p>
     * @param {@link in}
     * @return {@link GraphType}
     */
    public static GraphType getByType(int type) {
        for (GraphType graphType : values()) {
            if (graphType.value == type)
                return graphType;
        }
        throw new IllegalArgumentException("GraphType has no such type " + type);
    }
    
    /**
     * getFormatKey
     * retrieves "this" instance of the {@link GraphType} name i.e. class name and delivers it as the entire namespace.
     * @return {@link String}
     */
    @Override public String getFormatKey() {
        return "yukon.web.modules.tools.trend.graphType." + name();
    }

}
