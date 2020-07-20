package com.eaton.rest.api.drsetup;

import static org.assertj.core.api.Assertions.*;

import com.eaton.framework.APIs;
import com.eaton.rest.api.common.ApiCallHelper;

import io.restassured.response.ExtractableResponse;

public class DrSetupCreateRequest {

    public static ExtractableResponse<?> createLoadGroup(Object body) {
        String pathParam = APIs.DemandResponse.createLoadGroup;
        ExtractableResponse<?> createResponse = ApiCallHelper.post(pathParam, body);
        assertThat(createResponse.statusCode()).as("Error in create load group \"%s\"").isEqualTo(200);
        return createResponse;
    }

    public static ExtractableResponse<?> createLoadProgram(int id) {
        String pathParam = APIs.DemandResponse.createLoadProgram + id;
        ExtractableResponse<?> createResponse = ApiCallHelper.get(pathParam);
        assertThat(createResponse.statusCode()).as("Error in create load program \"%s\"").isEqualTo(200);
        return createResponse;
    }

    public static ExtractableResponse<?> createControlArea(int id) {
        String pathParam = APIs.DemandResponse.createControlArea + id;
        ExtractableResponse<?> createResponse = ApiCallHelper.get(pathParam);
        assertThat(createResponse.statusCode()).as("Error in create control area \"%s\"").isEqualTo(200);
        return createResponse;
    }

    public static ExtractableResponse<?> createControlScenario(int id) {
        String pathParam = APIs.DemandResponse.createControlScenario + id;
        ExtractableResponse<?> createResponse = ApiCallHelper.get(pathParam);
        assertThat(createResponse.statusCode()).as("Error in create lcontrol scenario \"%s\"").isEqualTo(200);
        return createResponse;
    }

    public static ExtractableResponse<?> createProgramConstraint(int id) {
        String pathParam = APIs.DemandResponse.createProgramConstraint + id;
        ExtractableResponse<?> createResponse = ApiCallHelper.get(pathParam);
        assertThat(createResponse.statusCode()).as("rror in create program constraint \"%s\"").isEqualTo(200);
        return createResponse;
    }

}