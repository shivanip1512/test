package com.eaton.rest.api.assets;

import static org.junit.Assert.assertTrue;

import com.eaton.rest.api.common.ApiCallHelper;
import com.eaton.rest.api.dr.PathParameters;

import io.restassured.response.ExtractableResponse;

public class AssetsCreateRequestAPI {
        public static ExtractableResponse<?> createCommChannelTCP(Object body) {
        String pathParam = PathParameters.getParam("createCommChannel");
        
        ExtractableResponse<?> createResponse = ApiCallHelper.post(pathParam, body);
        assertTrue("Error in create Comm Channel TCP", createResponse.statusCode() == 200);
        return createResponse;
        }
        
        public static ExtractableResponse<?> createCommChannelUDP(Object body) {
            String pathParam = PathParameters.getParam("createCommChannel");
            
            ExtractableResponse<?> createResponse = ApiCallHelper.post(pathParam, body);
            assertTrue("Error in create Comm Channel UDP", createResponse.statusCode() == 200);
            return createResponse;
    }
}
