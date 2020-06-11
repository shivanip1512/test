package com.cannontech.rest.api.trend.helper;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.cannontech.rest.api.trend.request.MockColor;
import com.cannontech.rest.api.trend.request.MockRenderType;
import com.cannontech.rest.api.trend.request.MockTrendAxis;
import com.cannontech.rest.api.trend.request.MockTrendModel;
import com.cannontech.rest.api.trend.request.MockTrendSeries;
import com.cannontech.rest.api.trend.request.MockTrendType.MockGraphType;

public class TrendHelper {
    public final static String CONTEXT_TREND_ID = "trendId";

    public static MockTrendModel buildTrend() {
        DateTime date = DateTime.now().minusDays(1);
        MockTrendSeries series = MockTrendSeries.builder()
                .type(MockGraphType.DATE_TYPE)
                .pointId(-110)
                .label("System Device / System Point")
                .color(MockColor.BLACK)
                .axis(MockTrendAxis.LEFT)
                .multiplier(1.2)
                .style(MockRenderType.LINE)
                .date(date)
                .build();
        List<MockTrendSeries> trendSeries = new ArrayList<MockTrendSeries>();
        trendSeries.add(series);
        MockTrendModel trendModel = MockTrendModel.builder()
                .name("TEST_TREND")
                .trendSeries(trendSeries)
                .build();
        return trendModel;
    }
}
