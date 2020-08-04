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

    public static ExtractableResponse<?> createLoadProgram(int id) {
        String pathParam = APIs.DemandResponse.CREATE_LOAD_PROGRAM + id;
        ExtractableResponse<?> createResponse = ApiCallHelper.get(pathParam);
        assertThat(createResponse.statusCode()).as("Error in create load program \"%s\"").isEqualTo(200);
        return createResponse;
    }

    public static ExtractableResponse<?> createControlArea(int id) {
        String pathParam = APIs.DemandResponse.CREATE_CONTROL_AREA + id;
        ExtractableResponse<?> createResponse = ApiCallHelper.get(pathParam);
        assertThat(createResponse.statusCode()).as("Error in create control area \"%s\"").isEqualTo(200);
        return createResponse;
    }

    public static ExtractableResponse<?> createControlScenario(int id) {
        String pathParam = APIs.DemandResponse.CREATE_CONTROL_SCENARIO + id;
        ExtractableResponse<?> createResponse = ApiCallHelper.get(pathParam);
        assertThat(createResponse.statusCode()).as("Error in create lcontrol scenario \"%s\"").isEqualTo(200);
        return createResponse;
    }

    public static ExtractableResponse<?> createProgramConstraint(int id) {
        String pathParam = APIs.DemandResponse.CREATE_PROGRAM_CONSTRAINT + id;
        ExtractableResponse<?> createResponse = ApiCallHelper.get(pathParam);
        assertThat(createResponse.statusCode()).as("rror in create program constraint \"%s\"").isEqualTo(200);
        return createResponse;
    }

}