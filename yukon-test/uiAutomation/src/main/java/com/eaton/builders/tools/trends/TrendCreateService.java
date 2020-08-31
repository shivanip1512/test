package com.eaton.builders.tools.trends;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

import io.restassured.response.ExtractableResponse;

public class TrendCreateService {

    public static Pair<JSONObject, JSONObject> buildAndCreateTrendAllFields() {
        return new TrendCreateBuilder.Builder(Optional.empty())
                .withPoints(new JSONObject[] { new TrendPointBuilder.Builder()
                        .withpointId(5231)
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
        Integer point = pointId.orElse(5231);
        
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
