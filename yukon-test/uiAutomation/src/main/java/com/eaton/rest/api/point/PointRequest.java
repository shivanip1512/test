package com.eaton.rest.api.point;

import static org.assertj.core.api.Assertions.assertThat;

import com.eaton.framework.APIs;
import com.eaton.rest.api.common.ApiCallHelper;

import io.restassured.response.ExtractableResponse;

public class PointRequest {
	
	public static ExtractableResponse<?> createPoint(Object body) {

        String pathParam = APIs.Point.CREATE_POINT;
        ExtractableResponse<?> response = ApiCallHelper.post(pathParam, body);
        assertThat(response.statusCode()).isEqualTo(201);
        return response;
    }

    public static ExtractableResponse<?> getPoint(String pointId) {

        String pathParam = APIs.Point.GET_POINT + pointId;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }

    public static ExtractableResponse<?> updatePoint(String pointId, Object body) {

        String pathParam = APIs.Point.UPDATE_POINT + pointId;
        ExtractableResponse<?> response = ApiCallHelper.put(pathParam, body);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }

    public static ExtractableResponse<?> deletePoint(String pointId) {

        String pathParam = APIs.Point.DELETE_POINT + pointId;
        ExtractableResponse<?> response = ApiCallHelper.delete(pathParam);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }
}
