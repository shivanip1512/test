package com.eaton.rest.api.assets;

import static org.junit.Assert.assertTrue;

import com.eaton.rest.api.common.ApiCallHelper;
import com.eaton.rest.api.dr.PathParameters;

import io.restassured.response.ExtractableResponse;

public class AssetsDeleteRequestAPI {
    public static ExtractableResponse<?> deleteCommChannelTCP(String id) {
        String pathParam = PathParameters.getParam("deleteCommChannel");

        ExtractableResponse<?> updateResponse = ApiCallHelper.delete(pathParam, id);
        assertTrue("Error in create Comm Channel TCP", updateResponse.statusCode() == 200);
        return updateResponse;
    }

    public static ExtractableResponse<?> deleteCommChannelUDP(String id) {
        String pathParam = PathParameters.getParam("deleteCommChannel");

        ExtractableResponse<?> updateResponse = ApiCallHelper.delete(pathParam, id);
        assertTrue("Error in create Comm Channel UDP", updateResponse.statusCode() == 200);
        return updateResponse;
    }
}
