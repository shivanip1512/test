package com.eaton.rest.api.assets;

import static org.assertj.core.api.Assertions.assertThat;

import com.eaton.framework.APIs;
import com.eaton.rest.api.common.ApiCallHelper;
import io.restassured.response.ExtractableResponse;

public class AssetsCreateRequestAPI {

    public static ExtractableResponse<?> createCommChannel(Object body) {

        String pathParam = APIs.CommChannel.CREATE_COMM_CHANNEL;
        ExtractableResponse<?> createResponse = ApiCallHelper.post(pathParam, body);
        assertThat(createResponse.statusCode()).isEqualTo(201);
        return createResponse;
    }
}
