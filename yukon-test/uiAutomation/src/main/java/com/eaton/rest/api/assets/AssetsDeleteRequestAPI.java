package com.eaton.rest.api.assets;

import static org.junit.Assert.assertTrue;

import com.eaton.framework.APIs;
import com.eaton.rest.api.common.ApiCallHelper;

import io.restassured.response.ExtractableResponse;

public class AssetsDeleteRequestAPI {
    public static ExtractableResponse<?> deleteCommChannelTCP(String id) {
        String pathParam = APIs.CommChannel.DELETE_COMM_CHANNEL + id;
        ExtractableResponse<?> updateResponse = ApiCallHelper.delete(pathParam);
        assertTrue("Error in create Comm Channel TCP", updateResponse.statusCode() == 200);
        return updateResponse;
    }
}
