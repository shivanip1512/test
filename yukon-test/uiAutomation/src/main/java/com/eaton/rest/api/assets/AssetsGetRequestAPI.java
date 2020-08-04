package com.eaton.rest.api.assets;

import static org.assertj.core.api.Assertions.assertThat;

import com.eaton.framework.APIs;
import com.eaton.rest.api.common.ApiCallHelper;
import io.restassured.response.ExtractableResponse;

public class AssetsGetRequestAPI {

    public static ExtractableResponse<?> getCommChannel(String commChannelId) {

        String pathParam = APIs.CommChannel.GET_COMM_CHANNEL + commChannelId;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertThat(response.statusCode()).as("Error in create control area \"%s\"").isEqualTo(200);
        return response;
    }
}