package com.eaton.rest.api.admin.attributes;

import static org.assertj.core.api.Assertions.assertThat;

import com.eaton.framework.APIs;
import com.eaton.rest.api.common.ApiCallHelper;

import io.restassured.response.ExtractableResponse;

public class AttributeAssignmentRequest {
    
    public static ExtractableResponse<?> createAttributeAssignment(Object body) {
        String pathParam = APIs.AttributeAssignment.CREATE_ATTRIBUTE_ASGMT;
        ExtractableResponse<?> createResponse = ApiCallHelper.post(pathParam, body);
        assertThat(createResponse.statusCode()).as("Error creating attribute assignment " + body).isEqualTo(201);
        return createResponse;
    }

    public static ExtractableResponse<?> getAttributeAssignment(String attributeAsgmtId) {
        String pathParam = APIs.AttributeAssignment.GET_ATTRIBUTE_ASGMT + attributeAsgmtId;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }

    public static ExtractableResponse<?> updateAttributeAssignment(String attributeAsgmtId, Object body) {

        String pathParam = APIs.AttributeAssignment.UPDATE_ATTRIBUTE_ASGMT + attributeAsgmtId;
        ExtractableResponse<?> response = ApiCallHelper.put(pathParam, body);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }

    public static ExtractableResponse<?> deleteAttributeAssignment(String attributeAsgmtId) {

        String pathParam = APIs.AttributeAssignment.DELETE_ATTRIBUTE_ASGMT + attributeAsgmtId;
        ExtractableResponse<?> response = ApiCallHelper.delete(pathParam);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }
}
