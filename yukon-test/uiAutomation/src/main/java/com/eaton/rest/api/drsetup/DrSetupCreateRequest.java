package com.eaton.rest.api.drsetup;

import static org.assertj.core.api.Assertions.*;

import com.eaton.framework.APIs;
import com.eaton.rest.api.common.ApiCallHelper;

import io.restassured.response.ExtractableResponse;

public class DrSetupCreateRequest {

    public static ExtractableResponse<?> createLoadGroup(Object body) {
        String pathParam = APIs.DemandResponse.CREATE_LOAD_GROUP;
        ExtractableResponse<?> createResponse = ApiCallHelper.post(pathParam, body);
        assertThat(createResponse.statusCode()).as("Error in create load group \"%s\"").isEqualTo(200);
        return createResponse;
    }

    public static ExtractableResponse<?> createLoadProgram(Object body) {
        String pathParam = APIs.DemandResponse.CREATE_LOAD_PROGRAM;
        ExtractableResponse<?> createResponse = ApiCallHelper.post(pathParam, body);
        assertThat(createResponse.statusCode()).as("Error in create load program \"%s\"").isEqualTo(200);
        return createResponse;
    }

    public static ExtractableResponse<?> createControlArea(Object body) {
        String pathParam = APIs.DemandResponse.CREATE_CONTROL_AREA;
        ExtractableResponse<?> createResponse = ApiCallHelper.post(pathParam, body);
        assertThat(createResponse.statusCode()).as("Error in create control area \"%s\"").isEqualTo(200);
        return createResponse;
    }

    public static ExtractableResponse<?> createControlScenario(Object body) {
        String pathParam = APIs.DemandResponse.CREATE_CONTROL_SCENARIO;
        ExtractableResponse<?> createResponse = ApiCallHelper.post(pathParam, body);
        assertThat(createResponse.statusCode()).as("Error in create control scenario \"%s\"").isEqualTo(200);
        return createResponse;
    }

    public static ExtractableResponse<?> createProgramConstraint(Object body) {
        String pathParam = APIs.DemandResponse.CREATE_PROGRAM_CONSTRAINT;
        ExtractableResponse<?> createResponse = ApiCallHelper.post(pathParam, body);
        assertThat(createResponse.statusCode()).as("Error in create program constraint \"%s\"").isEqualTo(200);
        return createResponse;
    }

}