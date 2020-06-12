package com.cannontech.web.tools.trends.helper;

import com.cannontech.common.trend.model.TrendType.GraphType;

public class TrendEditorHelper {
    
    public static boolean isDateType(GraphType type) {
        return type == GraphType.DATE_TYPE;
    }

}
