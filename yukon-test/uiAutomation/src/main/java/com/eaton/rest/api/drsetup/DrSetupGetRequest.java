package com.eaton.rest.api.drsetup;

import static org.assertj.core.api.Assertions.assertThat;

import com.eaton.framework.APIs;
import com.eaton.rest.api.common.ApiCallHelper;

import io.restassured.response.ExtractableResponse;

public class DrSetupGetRequest {

    public static ExtractableResponse<?> getLoadGroup(int id) {
        String pathParam = APIs.DemandResponse.getLoadGroup + id;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertThat(response.statusCode()).as("Error in create load group \"%s\"").isEqualTo(200);
        return response;
    }

    public static ExtractableResponse<?> getLoadProgram(int id) {
        String pathParam = APIs.DemandResponse.getLoadProgram + id;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertThat(response.statusCode()).as("Error in create load program \"%s\"").isEqualTo(200);
        return response;
    }

    public static ExtractableResponse<?> getControlArea(int id) {
       
        String pathParam = APIs.DemandResponse.getControlArea + id;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertThat(response.statusCode()).as("Error in create control area \"%s\"").isEqualTo(200);
        return response;
    }

    public static ExtractableResponse<?> getControlScenario(int id) {
        String pathParam = APIs.DemandResponse.getProgramConstraint + id;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertThat(response.statusCode()).as("Error in create lcontrol scenario \"%s\"").isEqualTo(200);
        return response;
    }

    public static ExtractableResponse<?> getProgramConstraint(int id) {
        String pathParam = APIs.DemandResponse.getProgramConstraint + id;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertThat(response.statusCode()).as("Error in create program constraint \"%s\"").isEqualTo(200);
        return response;
    }
}