package com.eaton.builders.tools.trends;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

import io.restassured.response.ExtractableResponse;

public class TrendCreateService {

    public static Pair<JSONObject, ExtractableResponse<?>> BuildAndCreateTrendAllFields() {
        return new TrendCreateBuilder.Builder(Optional.empty())
                .withPoints(new JSONObject[] { new TrendPointBuilder.Builder()
                        .withpointId(4999)
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

    public static Pair<JSONObject, ExtractableResponse<?>> BuildAndCreateTrendWithPoint() {
        return new TrendCreateBuilder.Builder(Optional.empty())
                .withPoints(new JSONObject[] { new TrendPointBuilder.Builder()
                        .withpointId(4999)
                        .withLabel(Optional.empty())
                        .withColor(Optional.empty())
                        .withStyle(Optional.empty())
                        .withType(Optional.empty())
                        .withAxis(Optional.empty())
                        .withMultiplier(Optional.empty())
                        .withDate(Optional.empty())
                        .build() })
                .create();
    }

    public static Pair<JSONObject, ExtractableResponse<?>> BuildAndCreateTrendWithMarker() {
        return new TrendCreateBuilder.Builder(Optional.empty())
                .withMarkers(new JSONObject[] { new TrendMarkerBuilder.Builder()
                        .withAxis(Optional.empty())
                        .withColor(Optional.empty())
                        .withLabel(Optional.empty())
                        .withMultiplier(Optional.empty())
                        .build() })
                .create();
    }

    public static Pair<JSONObject, ExtractableResponse<?>> BuildAndCreateTrendRequiredFields() {
        return new TrendCreateBuilder.Builder(Optional.empty())
                .create();
    }
}
