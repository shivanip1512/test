package com.eaton.builders.tools.trends;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.framework.TestDbDataType;

public class TrendCreateService {

    public static Pair<JSONObject, JSONObject> buildAndCreateTrendAllFields() {
        Integer pointId = TestDbDataType.TrendPointData.CREATE_TREND_ANALOG_POINT_ID.getId();
        return new TrendCreateBuilder.Builder(Optional.empty())
                .withPoints(new JSONObject[] { new TrendPointBuilder.Builder()
                        .withpointId(pointId)
                        .withLabel(Optional.empty())
                        .withColor(Optional.empty())
                        .withStyle(Optional.empty())
                        .withType(Optional.empty())
                        .withAxis(Optional.empty())
                        .withMultiplier(Optional.empty())
                        .withDate(Optional.empty())
                        .build() })
                .withMarkers(new JSONObject[] { new TrendMarkerBuilder.Builder()
                        .withAxis(Optional.empty())
                        .withColor(Optional.empty())
                        .withLabel(Optional.empty())
                        .withMultiplier(Optional.empty())
                        .build() })
                .create();
    }

    public static Pair<JSONObject, JSONObject> buildAndCreateTrendWithPoint(Optional<Integer> pointId, Optional<TrendTypes.Type> pointType) {
        Optional<TrendTypes.Type> type = pointType;
        Integer trendPointId = TestDbDataType.TrendPointData.CREATE_TREND_ANALOG_POINT_ID.getId();
        Integer point = pointId.orElse(trendPointId);
        
        return new TrendCreateBuilder.Builder(Optional.empty())
                .withPoints(new JSONObject[] { new TrendPointBuilder.Builder()
                        .withpointId(point)
                        .withLabel(Optional.empty())
                        .withColor(Optional.empty())
                        .withStyle(Optional.empty())
                        .withType(type)
                        .withAxis(Optional.empty())
                        .withMultiplier(Optional.empty())
                        .withDate(Optional.empty())
                        .build() })
                .create();
    }

    public static Pair<JSONObject, JSONObject> buildAndCreateTrendWithMarker() {
        return new TrendCreateBuilder.Builder(Optional.empty())
                .withMarkers(new JSONObject[] { new TrendMarkerBuilder.Builder()
                        .withAxis(Optional.empty())
                        .withColor(Optional.empty())
                        .withLabel(Optional.empty())
                        .withMultiplier(Optional.empty())
                        .build() })
                .create();
    }

    public static Pair<JSONObject, JSONObject> buildAndCreateTrendOnlyRequiredFields() {
        return new TrendCreateBuilder.Builder(Optional.empty())
                .create();
    }
}
