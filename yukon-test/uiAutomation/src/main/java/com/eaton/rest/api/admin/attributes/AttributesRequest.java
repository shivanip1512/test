package com.eaton.rest.api.admin.attributes;

import static org.assertj.core.api.Assertions.assertThat;

import com.eaton.framework.APIs;
import com.eaton.rest.api.common.ApiCallHelper;

import io.restassured.response.ExtractableResponse;

public class AttributesRequest {
    
    public static ExtractableResponse<?> createAttribute(Object body) {
        String pathParam = APIs.Attributes.CREATE_ATTRIBUTE;
        ExtractableResponse<?> createResponse = ApiCallHelper.post(pathParam, body);
        assertThat(createResponse.statusCode()).as("Error in create attribute \"%s\"").isEqualTo(201);
        return createResponse;
    }

    public static ExtractableResponse<?> getAttribute(String attributeId) {
        String pathParam = APIs.Attributes.GET_ATTRIBUTE + attributeId;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }
    
    public static ExtractableResponse<?> getAllAttributes() {
        String pathParam = APIs.Attributes.GET_ATTRIBUTE;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }

    public static ExtractableResponse<?> updateAttribute(String attributeId, Object body) {
        String pathParam = APIs.Attributes.UPDATE_ATTRIBUTE + attributeId;
        ExtractableResponse<?> response = ApiCallHelper.patch(pathParam, body);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }

    public static ExtractableResponse<?> deleteAttribute(String attributeId) {
        String pathParam = APIs.Attributes.DELETE_ATTRIBUTE + attributeId;
        ExtractableResponse<?> response = ApiCallHelper.delete(pathParam);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }
}
