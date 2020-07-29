package com.eaton.rest.api.trend;

import static org.assertj.core.api.Assertions.assertThat;

import com.eaton.framework.APIs;
import com.eaton.rest.api.common.ApiCallHelper;
import io.restassured.response.ExtractableResponse;

public class TrendRequest {
    
    public static ExtractableResponse<?> createTrend(Object body) {

        String pathParam = APIs.Trend.createTrend;
        ExtractableResponse<?> response = ApiCallHelper.post(pathParam, body);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }
    
    public static ExtractableResponse<?> getTrend(String trendId) {

        String pathParam = APIs.Trend.getTrend + trendId;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }

    public static ExtractableResponse<?> updateTrend(String trendId, Object body) {

        String pathParam = APIs.Trend.updateTrend + trendId;
        ExtractableResponse<?> response = ApiCallHelper.put(pathParam, body);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }
    
    public static ExtractableResponse<?> deleteTrend(String trendId) {

        String pathParam = APIs.Trend.deleteTrend + trendId;
        ExtractableResponse<?> response = ApiCallHelper.delete(pathParam, trendId);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }
    public static ExtractableResponse<?> resetPeak(String trendId, Object body) {

        String pathParam = APIs.Trend.updateTrend + trendId + "resetPeak";
        ExtractableResponse<?> response = ApiCallHelper.post(pathParam, body);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }
}
