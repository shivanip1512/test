package com.eaton.rest.api.commchannel;

import static org.assertj.core.api.Assertions.assertThat;

import com.eaton.framework.APIs;
import com.eaton.rest.api.common.ApiCallHelper;
import io.restassured.response.ExtractableResponse;

public class CommChannelRequest {
	public static ExtractableResponse<?> createCommChannel(Object body) {

        String pathParam = APIs.CommChannel.CREATE_COMM_CHANNEL;
        ExtractableResponse<?> response = ApiCallHelper.post(pathParam, body);
        assertThat(response.statusCode()).isEqualTo(201);
        return response;
    }

    public static ExtractableResponse<?> getCommChannel(String commChannelId) {

        String pathParam = APIs.CommChannel.GET_COMM_CHANNEL + commChannelId;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }

    public static ExtractableResponse<?> updateCommChannel(String commChannelId, Object body) {

        String pathParam = APIs.CommChannel.UPDATE_COMM_CHANNEL + commChannelId;
        ExtractableResponse<?> response = ApiCallHelper.patch(pathParam, body);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }

    public static ExtractableResponse<?> deleteCommChannel(String commChannelId) {

        String pathParam = APIs.CommChannel.DELETE_COMM_CHANNEL + commChannelId;
        ExtractableResponse<?> response = ApiCallHelper.delete(pathParam);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }
}
