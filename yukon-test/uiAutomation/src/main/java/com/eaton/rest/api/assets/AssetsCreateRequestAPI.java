package com.eaton.rest.api.assets;

import static org.assertj.core.api.Assertions.assertThat;

import com.eaton.rest.api.common.ApiCallHelper;
import com.eaton.rest.api.dr.PathParameters;

import io.restassured.response.ExtractableResponse;

public class AssetsCreateRequestAPI {
        
        public static ExtractableResponse<?> createCommChannel(Object body) {
        String pathParam = PathParameters.getParam("createCommChannel");
        
        ExtractableResponse<?> createResponse = ApiCallHelper.post(pathParam, body);

        assertThat(createResponse.statusCode()).isEqualTo(200);
        
        return createResponse;
        }
}
