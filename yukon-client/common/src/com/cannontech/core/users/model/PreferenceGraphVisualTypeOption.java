package com.cannontech.core.users.model;

import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.i18n.DisplayableEnum;

public enum PreferenceGraphVisualTypeOption implements DisplayableEnum {
    BAR(GraphType.COLUMN),
    LINE(GraphType.LINE);

    final private GraphType graphType;
    private PreferenceGraphVisualTypeOption(GraphType type) {
        this.graphType = type;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.user.preferences."+ getParentName() +"."+ name();
    }


    public static String getParentName() {
        return "GRAPH_DISPLAY_VISUAL_TYPE";
    }

    public static PreferenceGraphVisualTypeOption getDefault() {
        return LINE;
    }

    public GraphType getGraphType() {
        return graphType;
    }

    public static PreferenceGraphVisualTypeOption fromGraphType(GraphType type) {
        switch (type) {
            case COLUMN:
                return BAR;
            case LINE:
                return LINE;
            case PIE:
                return null;
        }
        return null;
    }

}