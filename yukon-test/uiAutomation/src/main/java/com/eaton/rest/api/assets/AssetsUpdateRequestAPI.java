package com.eaton.rest.api.assets;

import static org.junit.Assert.assertTrue;

import com.eaton.rest.api.common.ApiCallHelper;
import com.eaton.rest.api.dr.PathParameters;

import io.restassured.response.ExtractableResponse;

public class AssetsUpdateRequestAPI {
    public static ExtractableResponse<?> updateCommChannelTCP(Object body, String id) {
        
        String pathParam = PathParameters.getParam("updateCommChannel");

        ExtractableResponse<?> updateResponse = ApiCallHelper.post(pathParam, body, id);
        assertTrue("Error in create Comm Channel TCP", updateResponse.statusCode() == 200);
        return updateResponse;
    }
}
