package com.eaton.rest.api.dbetoweb;

import static org.junit.Assert.assertTrue;

import com.eaton.rest.api.common.ApiCallHelper;

import io.restassured.response.ExtractableResponse;

public class DBEToWebCreateRequest {

    public static ExtractableResponse<?> createLoadGroup(Object body) {
        String pathParam = PathParametes.getParam("createLoadGroup");

        ExtractableResponse<?> createResponse = ApiCallHelper.post(pathParam, body);
        assertTrue("Error in create load group", createResponse.statusCode() == 200);
        return createResponse;
    }

    public static ExtractableResponse<?> createLoadProgram(int id) {
        String pathParam = PathParametes.getParam("createLoadProgram") + id;
        ExtractableResponse<?> createResponse = ApiCallHelper.get(pathParam);
        assertTrue("Error in create load program", createResponse.statusCode() == 200);
        return createResponse;
    }

    public static ExtractableResponse<?> createControlArea(int id) {
        String pathParam = PathParametes.getParam("createControlArea") + id;
        ExtractableResponse<?> createResponse = ApiCallHelper.get(pathParam);
        assertTrue("Error in create control area", createResponse.statusCode() == 200);
        return createResponse;
    }

    public static ExtractableResponse<?> createControlScenario(int id) {
        String pathParam = PathParametes.getParam("createControlScenario") + id;
        ExtractableResponse<?> createResponse = ApiCallHelper.get(pathParam);
        assertTrue("Error in create lcontrol scenario", createResponse.statusCode() == 200);
        return createResponse;
    }

    public static ExtractableResponse<?> createProgramConstraint(int id) {
        String pathParam = PathParametes.getParam("createProgramConstraint") + id;
        ExtractableResponse<?> createResponse = ApiCallHelper.get(pathParam);
        assertTrue("Error in create program constraint", createResponse.statusCode() == 200);
        return createResponse;
    }

}