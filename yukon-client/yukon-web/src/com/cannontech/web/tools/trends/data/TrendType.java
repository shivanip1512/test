package com.cannontech.web.tools.trends.data;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.database.db.graph.GDSTypesFuncs;

public class TrendType {

    public static enum GraphType implements DisplayableEnum {
        BASIC_TYPE, USAGE_TYPE, PEAK_TYPE, YESTERDAY_TYPE, MARKER_TYPE, DATE_TYPE;
        @Override
        public String getFormatKey() {
            return "yukon.web.modules.tools.trends.data.trendType.graphType." + name();
        }
    }

    private final GraphType graphType;
    private final boolean isGraphType;
    private final boolean isPrimaryType;
    
    /**
     * TrendType is the higher level scope for a trend graph. It provides the primitive graph state
     as well as whether or not to render it, or guard the data for summary, and if it is a primary type.
     * <p>
     * 
     * @param type {@link GraphType} is graph state.
     *            
     * @param isGraphType render a graph or not.
     * @param isPrimaryType if the graph caries a primary point. 
     * @return {@link TrendType}
     */
    private TrendType(GraphType type, boolean isGraphType, boolean isPrimaryType) {
        this.graphType = type;
        this.isGraphType = isGraphType;
        this.isPrimaryType = isPrimaryType;
    }
    
    /**
     * TrendType of is the public accessor/constructor. The int is assumed graph type bit value -> (0x00) returned from 
     * the datastore for the specific trend item.   
     * <p>
     * 
     * @param type is graph bit from the trend item.
     * @return {@link TrendType}
     */
    public static TrendType of(int type) {
        boolean isGraphType = GDSTypesFuncs.isGraphType(type);
        boolean isPrimaryType = GDSTypesFuncs.isPrimaryType(type);

        if (GDSTypesFuncs.isUsageType(type)) {
            return new TrendType(GraphType.USAGE_TYPE, isGraphType, isPrimaryType);
        }
        if (GDSTypesFuncs.isPeakType(type)) {
            return new TrendType(GraphType.PEAK_TYPE, isGraphType, isPrimaryType);
        }
        if (GDSTypesFuncs.isYesterdayType(type)) {
            return new TrendType(GraphType.YESTERDAY_TYPE, isGraphType, isPrimaryType);
        }
        if (GDSTypesFuncs.isMarkerType(type)) {
            return new TrendType(GraphType.MARKER_TYPE, isGraphType, isPrimaryType);
        }
        if (GDSTypesFuncs.isDateType(type)) {
            return new TrendType(GraphType.DATE_TYPE, isGraphType, isPrimaryType);
        }
        return new TrendType(GraphType.BASIC_TYPE, isGraphType, isPrimaryType);
    }

    /**
     * getGraphType gets the encapsulated graph type for the trend item. 
     * the datastore for the specific trend item.   
     * <p>
     * @return {@link GraphType}
     */
    public GraphType getGraphType() {
        return graphType;
    }
    /**
     * isGraphType lets us know if we should render it.
     * <p>
     * @return true/false-(boolean)
     */
    public boolean isGraphType() {
        return isGraphType;
    }
    /**
     * isPrimaryType lets us know if the bit converted also carried a primary point render. 
     * <p>
     * @return true/false-(boolean)
     */
    public boolean isPrimaryType() {
        return isPrimaryType;
    }
}
