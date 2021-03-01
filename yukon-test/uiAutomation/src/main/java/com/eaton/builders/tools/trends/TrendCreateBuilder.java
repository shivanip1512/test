package com.eaton.builders.tools.trends;

import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.eaton.rest.api.trend.TrendRequest;

import io.restassured.response.ExtractableResponse;

public class TrendCreateBuilder {

    public static class Builder {

        private String name;
        private JSONObject[] markers;
        private JSONObject[] points;

        public Builder(Optional<String> name) {
            String u = UUID.randomUUID().toString();
            String uuid = u.replace("-", "");

            this.name = name.orElse("Trend" + uuid);
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withMarkers(JSONObject[] markers) {
            this.markers = markers;

            return this;
        }

        public Builder withPoints(JSONObject[] points) {
            this.points = points;

            return this;
        }

        public JSONObject build() {
            JSONObject j = new JSONObject();
            j.put("name", this.name);

            if (this.markers != null || this.points != null) {
                JSONArray array = new JSONArray();

                if (this.markers != null) {
                    for (JSONObject marker : markers) {
                        array.put(marker);
                    }
                }

                if (this.points != null) {
                    for (JSONObject point : points) {
                        array.put(point);
                    }
                }

                j.put("trendSeries", array);
            }

            return j;
        }

        public Pair<JSONObject, JSONObject> create() {
            JSONObject request = build();
            ExtractableResponse<?> createResponse = TrendRequest.createTrend(request.toString());

            String res = createResponse.asString();
            JSONObject response = new JSONObject(res);

            return new Pair<>(request, response);
        }
    }
}
